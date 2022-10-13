package com.project.edithandler.repository;

import java.util.List;

import com.project.edithandler.entity.Document;
import com.project.edithandler.model.TextDocument;

public interface TextDocumentDao {

	boolean saveDocument(Document doc);

	List<TextDocument> fetchAllDocuments();

	TextDocument fetchDocumentById(String did);

	boolean deleteDocumentById(String did);

	TextDocument updateDocument(Document doc);

	boolean containsDocumentById(String did);

}
