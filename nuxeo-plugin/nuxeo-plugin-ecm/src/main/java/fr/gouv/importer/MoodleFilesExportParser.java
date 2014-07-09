package fr.gouv.importer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.InputStreamBlob;

public class MoodleFilesExportParser implements MoodleExportParser {
	private static final String File_TYPE = "File";


	public MoodleFilesExportParser ( ZipFile zip) {
		//		this.zip = zip ;
	}

	public List<DocumentModel> getDocuments (CoreSession documentManager , String targetPath, ZipFile zip) throws ClientException, IOException {
		List<DocumentModel> documents = new ArrayList<DocumentModel>();
		
		Enumeration<? extends ZipEntry> entries = zip.entries();
		for(;entries.hasMoreElements();) {
			ZipEntry entry = entries.nextElement() ;
			if(entry.isDirectory() || MoodleZIPImporter.isMoodleSignature(entry))
				continue;
			
			InputStream blobStream = zip.getInputStream(entry);
			Blob blob = new InputStreamBlob(blobStream);
			DocumentModel targetDoc = null;
			targetDoc = documentManager.createDocumentModel(targetPath, entry.getName(), File_TYPE);

			targetDoc.setPropertyValue("dc:title", entry.getName());
			targetDoc.setPropertyValue("file:filename", entry.getName());
			
			targetDoc.getAdapter(BlobHolder.class).setBlob(blob);
			documents.add(targetDoc); 
		}
		
		return documents;
	}

	@Override
	public void updateDocument(DocumentModel documentCreated) throws ClientException, IOException {
	}

}
