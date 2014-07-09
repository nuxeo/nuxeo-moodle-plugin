package fr.gouv.importer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MoodleXMLExportParser extends MoodleHtmlExportParser{
	private static final String FORMAT_EXPORT = "xml";
	private static final Log log = LogFactory.getLog(MoodleXMLExportParser.class);

	protected Blob getParsedBlob(String content , Map<String, String> path) throws IOException{

		String string=new String();
		try {
			string = getEntry(content);
		} catch (Exception e) {
			log.debug("can not parse this xml document" , e);
		}

		return new StringBlob(string);

	}

	private String getEntry(String content) throws ParserConfigurationException, SAXException, IOException{
		String html = new String();
		InputStream is =new ByteArrayInputStream(content.getBytes());
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		doc.getDocumentElement().normalize();
		Node root = doc.getDocumentElement();
		NodeList nodeRoot = root.getChildNodes();

		if(nodeRoot.getLength()!=0){
			html += "<table> <tr><td>"+
					nodeRoot.item(1).getTextContent()
					+"</td>"
					+"<tr><td>"
					+nodeRoot.item(4).getChildNodes().item(0).getTextContent()+"</td></tr></table>";

		}
		int lNodeRoot=  doc.getElementsByTagName("entry").getLength();
		//les entrees
		NodeList nodes = doc.getElementsByTagName("entry");
		if(lNodeRoot!=0){
			html +="<table>";
			for(int i=0;i<lNodeRoot;i++)
			{	html+="<tr>";
			NodeList childs =nodes.item(i).getChildNodes();
			int l=childs.getLength();
			for(int j=0; j<l;j++){
				if(contains(childs.item(j).getNodeName())){
					html += "<tr><td>" 
							+childs.item(j).getTextContent()
							+" </td></tr>";
				}
			}
			html+="</tr>";
			}
			html+="</table>";
		}

		return html;
	}
	public static boolean contains(String test) {

		for (NodeName c : NodeName.values()) {
			if (c.name().equals(test)) {
				return true;
			}
		}

		return false;
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

	protected String encodeAllUrl(String content , String encoding) throws UnsupportedEncodingException{
		return content;
	}

	private enum NodeName {
		content;

	}
}
