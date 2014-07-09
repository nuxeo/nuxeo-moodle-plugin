package fr.gouv.importer;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipFile;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface MoodleExportParser {

	public List<DocumentModel> getDocuments (CoreSession documentManager , String targetPath, ZipFile zip) throws ClientException, IOException;

	public void updateDocument(DocumentModel documentCreated) throws ClientException, IOException;
	
}
