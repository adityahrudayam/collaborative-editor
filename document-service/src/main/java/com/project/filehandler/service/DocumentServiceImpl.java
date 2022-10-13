package com.project.filehandler.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.project.filehandler.customexceptions.DocumentNotFoundException;
import com.project.filehandler.customexceptions.InvalidRequestDataException;
import com.project.filehandler.customexceptions.UnauthorizedAccessException;
import com.project.filehandler.customexceptions.UserNotFoundException;
import com.project.filehandler.entity.Document;
import com.project.filehandler.entity.User;
import com.project.filehandler.model.ResponseUser;
import com.project.filehandler.model.UserDocumentAdvanced;
import com.project.filehandler.model.UserDocumentBasic;
import com.project.filehandler.model.UserDocumentModel;
import com.project.filehandler.repository.DocumentRepository;
import com.project.filehandler.repository.UserRepository;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

	@Autowired
	private DocumentRepository dr;

	@Autowired
	private UserRepository ur;

	public Document createDocument(MultipartFile file) throws InvalidRequestDataException, IOException {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		if (fileName.contains(".."))
			throw new InvalidRequestDataException("Invalid file name. Please check the request & try again.");
		Document doc = new Document(fileName, file.getContentType(), file.getBytes());
		return doc;
	}

	@Override
	public UserDocumentBasic uploadFileDocument(String uid, MultipartFile file)
			throws InvalidRequestDataException, UserNotFoundException, IOException {
		if (uid == null || file == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		User user = ur.findById(uid)
				.orElseThrow(() -> new UserNotFoundException("No user found with the given user id: " + uid));
		Document doc = createDocument(file);
		doc.setOwner(user);
		doc.addUser(user);
		user.addDocument(doc);
		Document dR = dr.saveAndFlush(doc);
		return new UserDocumentBasic(uid, dR.getDid(), dR.getDocName(), dR.getDocType());
	}

	@Override
	public UserDocumentBasic uploadJsonDocument(UserDocumentModel userDoc)
			throws InvalidRequestDataException, UserNotFoundException {
		if (userDoc == null || userDoc.getUid() == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		String uid = userDoc.getUid();
		User user = ur.findById(uid)
				.orElseThrow(() -> new UserNotFoundException("No user found with the given user id: " + uid));
		Document doc = userDoc.getDocument();
		if (doc == null || doc.getDocName() == null || doc.getDocType() == null || doc.getData() == null)
			throw new InvalidRequestDataException("Invalid document data. Please check the request & try again.");
		doc.setOwner(user);
		doc.addUser(user);
		user.addDocument(doc);
		Document rD = dr.saveAndFlush(doc);
		return new UserDocumentBasic(uid, rD.getDid(), rD.getDocName(), rD.getDocType());
	}

	@Override
	public UserDocumentBasic uploadJsonDocument(UserDocumentAdvanced userDoc)
			throws InvalidRequestDataException, UserNotFoundException {
		if (userDoc == null || userDoc.getUid() == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		String uid = userDoc.getUid();
		User user = ur.findById(uid)
				.orElseThrow(() -> new UserNotFoundException("No user found with the given user id: " + uid));
		if (userDoc.getDocName() == null || userDoc.getDocType() == null || userDoc.getDocData() == null)
			throw new InvalidRequestDataException("Invalid document data. Please check the request & try again.");
		Document doc = new Document(userDoc.getDocName(), userDoc.getDocType(), userDoc.getDocData());
		doc.setOwner(user);
		doc.addUser(user);
		user.addDocument(doc);
		Document rD = dr.saveAndFlush(doc);
		return new UserDocumentBasic(uid, rD.getDid(), rD.getDocName(), rD.getDocType());
	}

	@Override
	public UserDocumentBasic updateDocumentName(UserDocumentBasic userDoc)
			throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
		if (userDoc == null || userDoc.getDid() == null || userDoc.getUid() == null || userDoc.getDocName() == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		String uid = userDoc.getUid(), did = userDoc.getDid();
		Document doc = dr.findById(did).orElseThrow(
				() -> new DocumentNotFoundException("No document found with the given document id: " + did));
		if (!doc.getOwner().getUid().equals(uid))
			throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
		doc.setDocName(userDoc.getDocName());
		Document saveDoc = dr.save(doc);
		return new UserDocumentBasic(uid, did, saveDoc.getDocName(), saveDoc.getDocType());
	}

	@Override
	public UserDocumentAdvanced fetchDocumentDataById(String uid, String id)
			throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
		if (uid == null || id == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		Document doc = dr.findById(id).orElseThrow(
				() -> new DocumentNotFoundException("No document found with the given document id: " + id));
		User test = new User();
		test.setUid(uid);
		if (!doc.containsUser(test))
			throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
		System.out.println("fetchCompleteDocumentById(id): " + doc + " by uid: " + uid);
		return new UserDocumentAdvanced(uid, id, doc.getDocName(), doc.getDocType(), doc.getData());
	}

	@Override
	public UserDocumentBasic getDocumentById(String uid, String id)
			throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
		if (uid == null || id == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		Document doc = dr.findById(id).orElseThrow(
				() -> new DocumentNotFoundException("No document found with the given document id: " + id));
		User test = new User();
		test.setUid(uid);
		if (!doc.containsUser(test))
			throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
		System.out.println("getDocumentById(id): " + doc + " by uid: " + uid);
		return new UserDocumentBasic(uid, id, doc.getDocName(), doc.getDocType());
	}

	@Override
	public Map<String, UserDocumentBasic> deleteDocumentById(String uid, String id)
			throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
		if (uid == null || id == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		Document doc = dr.findById(id).orElseThrow(
				() -> new DocumentNotFoundException("No document found with the given document id: " + id));
		if (!doc.getOwner().getUid().equals(uid))
			throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
		doc.getUsers().forEach(user -> user.removeDocument(doc));
		dr.delete(doc);
		return Map.of("document deleted", new UserDocumentBasic(uid, id, doc.getDocName(), doc.getDocType()));
	}

	@Override
	public List<UserDocumentBasic> getDocumentsCreatedByUser(String uid, int page, int size)
			throws InvalidRequestDataException, UserNotFoundException {
		if (uid == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		User user = ur.findById(uid)
				.orElseThrow(() -> new UserNotFoundException("No user found with the given user id: " + uid));
		return user.getOwnedDocs().stream().skip((page - 1) * size).limit(size)
				.map(doc -> new UserDocumentBasic(doc.getDid(), doc.getDocName(), doc.getDocType()))
				.collect(Collectors.toList());
	}

	@Override
	public List<UserDocumentBasic> getSharedDocumentsOfUser(String uid, int page, int size)
			throws InvalidRequestDataException, UserNotFoundException {
		if (uid == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		User user = ur.findById(uid)
				.orElseThrow(() -> new UserNotFoundException("No user found with the given user id: " + uid));
		return user.getOwnedDocs().stream().filter(doc -> doc.getUsers().size() > 1).skip((page - 1) * size).limit(size)
				.map(doc -> new UserDocumentBasic(doc.getDid(), doc.getDocName(), doc.getDocType()))
				.collect(Collectors.toList());
	}

	@Override
	public List<UserDocumentBasic> getDocumentsSharedToUser(String uid, int page, int size)
			throws InvalidRequestDataException, UserNotFoundException {
		if (uid == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		User user = ur.findById(uid)
				.orElseThrow(() -> new UserNotFoundException("No user found with the given user id: " + uid));
		return user.getDocs().stream().filter(doc -> !doc.getOwner().equals(user)).skip((page - 1) * size).limit(size)
				.map(doc -> new UserDocumentBasic(doc.getOwner().getUid(), doc.getDid(), doc.getDocName(),
						doc.getDocType()))
				.collect(Collectors.toList());
	}

	@Override
	public List<UserDocumentBasic> getAllDocuments(String uid, int page, int size) throws UserNotFoundException {
		return ur.findById(uid)
				.orElseThrow(() -> new UserNotFoundException("No user found with the given user id: " + uid)).getDocs()
				.stream().map(doc -> new UserDocumentBasic(doc.getDid(), doc.getDocName(), doc.getDocType()))
				.skip((page - 1) * size).limit(size).collect(Collectors.toList());
	}

	@Override
	public ResponseUser changeEditStatus(String uid, String id)
			throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
		if (id == null || uid == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		Document doc = dr.findById(id)
				.orElseThrow(() -> new DocumentNotFoundException("No document found with the given id: " + id));
		User user = ur.findById(uid)
				.orElseThrow(() -> new UserNotFoundException("No user found with the given user id: " + uid));
		if (!doc.containsUser(user) || doc.getEditor() != null)
			throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
		doc.setEditor(user);
		dr.saveAndFlush(doc);
		return new ResponseUser(uid, user.getName(), user.getUsername(), user.getEmail());
	}

	@Override
	public ResponseUser changeEditStatusByEmail(String email, String id)
			throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
		if (id == null || email == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		Document doc = dr.findById(id)
				.orElseThrow(() -> new DocumentNotFoundException("No document found with the given id: " + id));
		Optional<User> userL = ur.findByEmail(email);
		if (userL == null || userL.isEmpty())
			throw new UserNotFoundException("No user found with the given email-id: " + email);
		User user = userL.get();
		if (!doc.containsUser(user) || doc.getEditor() != null)
			throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
		doc.setEditor(user);
		dr.saveAndFlush(doc);
		return new ResponseUser(user.getName(), user.getUsername(), email);
	}

	@Override
	public ResponseUser getEditStatus(String uid, String id)
			throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
		if (id == null || uid == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		Document doc = dr.findById(id)
				.orElseThrow(() -> new DocumentNotFoundException("No document found with the given id: " + id));
		User test = new User();
		test.setUid(uid);
		if (!doc.containsUser(test))
			throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
		User user = doc.getEditor();
		return user != null ? new ResponseUser(user.getName(), user.getUsername(), user.getEmail())
				: new ResponseUser();
	}

	@Override
	public Map<String, Boolean> stopEditAndSaveStatus(String uid, String id)
			throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
		if (id == null || uid == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		Document doc = dr.findById(id)
				.orElseThrow(() -> new DocumentNotFoundException("No document found with the given id: " + id));
		User user = ur.findById(uid)
				.orElseThrow(() -> new UserNotFoundException("No user found with the given user id: " + uid));
		if (!doc.containsUser(user) || doc.getEditor() == null)
			throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
		doc.setEditor(null);
		dr.saveAndFlush(doc);
		return Map.of("canEdit", true);
	}

//	@Override
//	public Boolean distributeChangesToUsers(Instruction instruction) {
//		// TODO Auto-generated method stub
//		return false;
//	}

//	@Override
//	public ResponseEntity<Document> saveDocument(Document doc) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ResponseEntity<Document> editDocument(HashMap<String, Object> instructions) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
