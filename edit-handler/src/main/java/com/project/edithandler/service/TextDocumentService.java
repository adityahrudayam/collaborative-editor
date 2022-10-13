package com.project.edithandler.service;

import java.util.List;

import com.project.edithandler.entity.Document;
import com.project.edithandler.model.TextDocument;

public interface TextDocumentService {
	boolean saveDocument(Document doc);

	List<TextDocument> fetchAllDocuments();

	TextDocument fetchDocumentById(String did);

	boolean containsDocumentById(String did);

	boolean deleteDocumentById(String did);

	TextDocument updateDocument(Document doc);
}
