package fr.gouv.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.InputStreamBlob;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;

public class MoodleHtmlExportParser implements MoodleExportParser {
	private static final String NOTE_TYPE = "Note";

	private static final String NOTE_SCHEMA = "note";

	private static final String MT_FIELD = "mime_type";

	private static final String NOTE_MINE_TYPE = "text/html";

	private static String FORMAT_EXPORT = "html" ;

	private static String URD_ENCODING = "ISO-8859-1" ;
	
	protected String adrServer;
	
	private static final Log log = LogFactory.getLog(MoodleHtmlExportParser.class);
	
	public List<DocumentModel> getDocuments (CoreSession documentManager , String targetPath, ZipFile zip) throws ClientException, IOException {
		ZipEntry note = getNoteFile(zip) ;
		if(note == null){
			return null ;
		}
		setServer(zip);
		
		List<DocumentModel> documents = new ArrayList<DocumentModel>();
		String docName = note.getName() ;
		int extensionIndex = docName.indexOf(".");
		if(extensionIndex != -1)
			docName = docName.substring(0, extensionIndex);
		DocumentModel targetDoc = null;
		targetDoc = documentManager.createDocumentModel(targetPath, docName, NOTE_TYPE);

		targetDoc.setPropertyValue("dc:title", docName);
		targetDoc.setProperty(NOTE_SCHEMA, MT_FIELD, NOTE_MINE_TYPE);
		targetDoc.getAdapter(BlobHolder.class).setBlob(getBlob(note , zip));
		targetDoc.setProperty("files", "files", getBlobs(zip));
		documents.add(targetDoc); 
		return documents;
	}

	private void setServer(ZipFile zip) throws IOException {
		Enumeration<? extends ZipEntry> entries = zip.entries();
		for(;entries.hasMoreElements();) {
			ZipEntry entry = entries.nextElement() ;
			if(MoodleZIPImporter.isMoodleSignature(entry)) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));
				String line ;
				while((line = reader.readLine()) != null){
					String[] parts = line.split(MoodleZIPImporter.DELIMITER);
					if(parts.length == 2 && parts[0].compareTo("nuxeo-server")== 0){
						this.adrServer = parts[1];
						if(!(this.adrServer.endsWith("/")))
							this.adrServer = parts[1] + "/" ;
						return ;
					}
				}
			}
		}
	}

	private Blob getBlob(ZipEntry note, ZipFile zip) throws IOException {
		InputStream blobStream = zip.getInputStream(note);
		Blob blob = new InputStreamBlob(blobStream);
		String blobContent = encodeAllUrl(blob.getString(), URD_ENCODING) ;
		blob = new StringBlob(blobContent); 
		blob.setFilename(note.getName());
		blob.setMimeType(NOTE_MINE_TYPE);
		return blob ;

	}
	
	public static ZipEntry getNoteFile(ZipFile zip) {
		Enumeration<? extends ZipEntry> entries = zip.entries();
		for(;entries.hasMoreElements();) {
			ZipEntry entry = entries.nextElement() ;
			if(isNoteFile(entry)) 
				return entry ;
		}
		return null;
	}

	private List<Object> getBlobs (ZipFile zip) throws IOException {

		List<Object> files = new ArrayList<Object>();
		Map<String, Object> f;
		Blob blob ;
		List<ZipEntry> blobsToAttach = getEntryToAttach(zip);

		for(ZipEntry entry : blobsToAttach){
			blob = new InputStreamBlob(zip.getInputStream(entry));
			String name = entry.getName();
			f = new HashMap<String, Object>();
			f.put("filename", name.substring(name.lastIndexOf(File.separator) + 1 )); 
			f.put("file", blob);
			files.add(f);
		}

		return files ;
	}

	private List<ZipEntry> getEntryToAttach(ZipFile zip) {
		List<ZipEntry> files = new ArrayList<ZipEntry>();

		Enumeration<? extends ZipEntry> entries = zip.entries();
		for(;entries.hasMoreElements();) {
			ZipEntry entry = entries.nextElement() ;
			if(!entry.isDirectory() && !MoodleZIPImporter.isMoodleSignature(entry) && !isNoteFile(entry)){
				files.add(entry) ;
			}

		}
		return files;
	}

	protected static boolean isNoteFile (ZipEntry entry){
		if(entry.isDirectory())
			return false ;
		String name = entry.getName();
		if(name.contains("/") || name.contains("\\"))
			return false ;
		if(name.toLowerCase().endsWith("." + FORMAT_EXPORT))
			return true ;
		return false ;
	}

	protected Blob getParsedBlob(String content , Map<String, String> path) throws IOException{
		Document doc = Jsoup.parse(content);
		Elements links = doc.select("[href]");
		Elements media = doc.select("[src]");

		String nuxeoLink ;
		for (Element src : media) {
			String urlDeconded = URLDecoder.decode(src.attr("src"), URD_ENCODING);
			String parts [] = urlDeconded.split("/");
			nuxeoLink = path.get(parts[parts.length - 1]);
			if(nuxeoLink == null)
				nuxeoLink = "";
			src.attr("src", nuxeoLink);
		}

		for (Element link : links) {
			String urlDeconded = URLDecoder.decode(link.attr("href"), URD_ENCODING);
			String parts [] = urlDeconded.split("/");
			nuxeoLink = path.get(parts[parts.length - 1]);
			if(nuxeoLink == null)
				nuxeoLink = "";
			link.attr("href", nuxeoLink);
		}

		return new StringBlob(doc.html()) ;   
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateDocument(DocumentModel documentCreated) throws ClientException, IOException {
		//TODO : set urlBase
		String urlBase = this.adrServer ;

		String urlPattern = "nxfile/" + documentCreated.getRepositoryName() + "/" ;
		String docID = documentCreated.getId();
		List<Object>files  = (List<Object>) documentCreated.getProperty("files", "files");
		HashMap<String, String> filesPath = new HashMap<String, String>();

		int index = 0 ;
		for(Object f : files){
			HashMap<String, Object> content =  (HashMap<String, Object>) f;
			String fileName = (String) content.get("filename");
			String fileUrl = urlBase + urlPattern + docID + "/files:files/" + index +"/file/" + fileName;
			filesPath.put(fileName, fileUrl); 
			index ++ ;
		}
		String blobContent = (String) documentCreated.getProperty("note" , "note");
		documentCreated.getAdapter(BlobHolder.class).setBlob(getParsedBlob(blobContent , filesPath));
		
		if(getHtmlTitre(blobContent) != null)
			documentCreated.setPropertyValue("dc:title", getHtmlTitre(blobContent));
	}

	protected String encodeAllUrl(String content , String encoding) throws UnsupportedEncodingException{

		String test = null ;
		test = new String();
		Document doc = Jsoup.parse(content);
		Elements links = doc.select("[href]");
		Elements media = doc.select("[src]");

		if(test != null){

		}
		for (Element src : media) {
			src.attr("src", URLEncoder.encode(src.attr("src"), encoding));;
		}

		for (Element link : links) {
			link.attr("href", URLEncoder.encode(link.attr("href"), encoding));
		}

		return doc.html();
	}

	private String getHtmlTitre(String html){
		Document doc = Jsoup.parse(html);
		Elements classes = doc.getElementsByClass("header");
		try{
			if(classes != null){
				Element header = classes.get(0);
				Element topic = header.getElementsByAttributeValue("class", "topic starter").get(0);
				Element titre = topic.getElementsByAttributeValue("class", "subject").get(0) ;
				return titre.text();
			}
		}catch (IndexOutOfBoundsException e) {
			log.debug("bad html doc" , e);
			return null;

		}
		return null ;
	}
	
}
