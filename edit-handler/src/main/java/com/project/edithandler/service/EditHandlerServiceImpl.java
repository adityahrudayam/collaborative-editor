package com.project.edithandler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.edithandler.customexceptions.DocumentNotFoundException;
import com.project.edithandler.customexceptions.InvalidRequestDataException;
import com.project.edithandler.customexceptions.UnauthorizedAccessException;
import com.project.edithandler.entity.Document;
import com.project.edithandler.repository.DocumentRepository;

@Service
@Transactional
public class EditHandlerServiceImpl implements EditHandlerService {

	@Autowired
	private DocumentRepository dr;

	@Override
	public void saveDocumentAfterEditing(Document doc)
			throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
		if (doc == null || doc.getEditor() == null || doc.getDid() == null || doc.getData() == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		Document document = dr.findById(doc.getDid()).orElseThrow(
				() -> new DocumentNotFoundException("No document found with the given id: " + doc.getDid()));
		System.out.println(document.getEditor());
		if (document.getEditor() == null)
			throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
		document.setData(doc.getData());
		document.getEditor().setEditingDoc(null);
		document.setEditor(null);
		dr.saveAndFlush(document);
	}

}
