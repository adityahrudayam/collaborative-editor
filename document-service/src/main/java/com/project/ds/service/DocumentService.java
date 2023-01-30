package com.project.ds.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.project.ds.model.ResponseUser;
import com.project.ds.model.UserDocumentAdvanced;
import com.project.ds.model.UserDocumentBasic;
import com.project.ds.model.UserDocumentModel;

public interface DocumentService {

	UserDocumentBasic uploadFileDocument(String uid, MultipartFile file) throws IOException;

	UserDocumentBasic uploadJsonDocument(UserDocumentModel userDoc);

	UserDocumentBasic uploadJsonDocument(UserDocumentAdvanced userDoc);

	UserDocumentBasic updateDocumentName(UserDocumentBasic userDoc);

	UserDocumentAdvanced fetchDocumentDataById(String uid, String id);

	UserDocumentBasic getDocumentById(String uid, String id);

	Map<String, UserDocumentBasic> deleteDocumentById(String uid, String id);

	List<UserDocumentBasic> getDocumentsCreatedByUser(String uid, int page, int size);

	List<UserDocumentBasic> getSharedDocumentsOfUser(String uid, int page, int size);

	List<UserDocumentBasic> getDocumentsSharedToUser(String uid, int page, int size);

	List<UserDocumentBasic> getAllDocuments(String uid, int page, int size);

	ResponseUser changeEditStatus(String uid, String id);

	ResponseUser changeEditStatusByEmail(String email, String id);

	ResponseUser getEditStatus(String uid, String id);

	Map<String, Boolean> stopEditAndSaveStatus(String uid, String id);

//	Boolean distributeChangesToUsers(Instruction instruction);

	// Document saveDocument(Document doc);

	// Document editDocument(HashMap<String, Object> instructions);

}
