package fr.gouv.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.filemanager.service.extension.AbstractFileImporter;
import org.nuxeo.ecm.platform.types.TypeManager;

public class MoodleZIPImporter extends AbstractFileImporter {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4135238398628894739L;
	private static final Log log = LogFactory.getLog(MoodleZIPImporter.class);
	public static final String MOOGLE_SIGNATURE = ".moodle_export" ;
	public static final String DELIMITER = "=" ;

	public static ZipFile getArchiveFileIfValid(File file) throws IOException {
		ZipFile zip;

		try {
			zip = new ZipFile(file);
		} catch (ZipException e) {
			log.debug("file is not a zipfile ! ", e);
			return null;
		} catch (IOException e) {
			log.debug("can not open zipfile ! ", e);
			return null;
		}

		ZipEntry marker = zip.getEntry(MOOGLE_SIGNATURE);

		if (marker == null) {
			zip.close();
			return null;
		} else {
			return zip;
		}
	}

	public DocumentModel create(CoreSession documentManager, Blob content,
			String path, boolean overwrite, String filename,
			TypeManager typeService) throws ClientException, IOException {
		//init moodle parser:
		
		
		File tmp = File.createTempFile("moodle-importer", null);

		content.transferTo(tmp);

		ZipFile zip = getArchiveFileIfValid(tmp);

		if (zip == null) {
			tmp.delete();
			return null;
		}
		MoodleExportParser parser = getParser(zip);
		if(parser == null){
			return null ;
		}
		
		DocumentModel container = documentManager.getDocument(new PathRef(path));
		List<DocumentModel> documents = parser.getDocuments(documentManager, path , zip);

		if(documents == null || documents.size() == 0)
			return null ;

		for (DocumentModel document : documents){
			DocumentModel documentCreted = documentManager.createDocument(document);
			parser.updateDocument (documentCreted) ;
			documentManager.saveDocument(documentCreted);
		}
		return  container ;
	}

	public static MoodleExportParser getParser(ZipFile zip) throws IOException{
		MoodleExportParser parser = null ;
		Enumeration<? extends ZipEntry> entries = zip.entries();
		for(;entries.hasMoreElements();) {
			ZipEntry entry = entries.nextElement() ;
			if(isMoodleSignature(entry)) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));
				String line = reader.readLine();
				if(line  != null){
					String[] parts = line.split(DELIMITER);
					if(parts.length == 2 && parts[0].compareTo("format-export")== 0){
						if(parts[1].contains("leap2a") && MoodleXMLExportParser.getNoteFile(zip) != null)
							parser = new MoodleXMLExportParser();
						else if(parts[1].contains("html") && MoodleHtmlExportParser.getNoteFile(zip) != null){
								parser = new MoodleHtmlExportParser();
						}
						else //default we import file by file
							parser = new MoodleFilesExportParser(zip);
					}
				}
			}
		}
			return parser ;
	}

	public static boolean isMoodleSignature(ZipEntry entry){

		String name = entry.getName();
		if(name.compareTo(MOOGLE_SIGNATURE) == 0)
			return true ;
		return false ;
	}

}
