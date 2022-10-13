package com.project.edithandler.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.edithandler.entity.Document;
import com.project.edithandler.model.TextDocument;
import com.project.edithandler.repository.TextDocumentDao;

@Service
public class TextDocumentServiceImpl implements TextDocumentService {

	@Autowired
	private TextDocumentDao td;

	@Override
	public boolean saveDocument(Document doc) {
		return td.saveDocument(doc);
	}

	@Override
	public List<TextDocument> fetchAllDocuments() {
		return td.fetchAllDocuments();
	}

	@Override
	public boolean containsDocumentById(String did) {
		return td.containsDocumentById(did);
	}

	@Override
	public TextDocument fetchDocumentById(String did) {
		return td.fetchDocumentById(did);
	}

	@Override
	public boolean deleteDocumentById(String did) {
		return td.deleteDocumentById(did);
	}

	@Override
	public TextDocument updateDocument(Document doc) {
		return td.updateDocument(doc);
	}

}
