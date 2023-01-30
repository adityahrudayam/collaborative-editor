package com.project.ds.service;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.ds.constants.KafkaConstants;
import com.project.ds.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.project.ds.customexceptions.DocumentNotFoundException;
import com.project.ds.customexceptions.InvalidRequestDataException;
import com.project.ds.customexceptions.UnauthorizedAccessException;
import com.project.ds.customexceptions.UserNotFoundException;
import com.project.ds.entity.Document;
import com.project.ds.entity.User;
import com.project.ds.repository.DocumentRepository;
import com.project.ds.repository.UserRepository;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private KafkaTemplate<String, Event> kafkaTemplate;

    @Autowired
    private DocumentRepository dr;

    @Autowired
    private UserRepository ur;

    public Document createDocument(MultipartFile file) throws NullPointerException, InvalidRequestDataException, IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (fileName.contains(".."))
            throw new InvalidRequestDataException("Invalid file name. Please check the request & try again.");
        return new Document(fileName, file.getContentType(), file.getBytes());
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
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(dR.getOwner(), false, KafkaConstants.UNIQUE_SERVER_ID));
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(dR, false, KafkaConstants.UNIQUE_SERVER_ID));
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
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(rD.getOwner(), false, KafkaConstants.UNIQUE_SERVER_ID));
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(rD, false, KafkaConstants.UNIQUE_SERVER_ID));
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
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(rD.getOwner(), false, KafkaConstants.UNIQUE_SERVER_ID));
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(rD, false, KafkaConstants.UNIQUE_SERVER_ID));
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
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(saveDoc, false, KafkaConstants.UNIQUE_SERVER_ID));
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
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(doc, true, KafkaConstants.UNIQUE_SERVER_ID));
        return Map.of("document deleted", new UserDocumentBasic(uid, id, doc.getDocName(), doc.getDocType()));
    }

    @Override
    public List<UserDocumentBasic> getDocumentsCreatedByUser(String uid, int page, int size)
            throws InvalidRequestDataException, UserNotFoundException {
        if (uid == null)
            throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
        User user = ur.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("No user found with the given user id: " + uid));
        return user.getOwnedDocs().stream().skip((long) (page - 1) * size).limit(size)
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
        return user.getOwnedDocs().stream().filter(doc -> doc.getUsers().size() > 1).skip((long) (page - 1) * size).limit(size)
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
        return user.getDocs().stream().filter(doc -> !doc.getOwner().equals(user)).skip((long) (page - 1) * size).limit(size)
                .map(doc -> new UserDocumentBasic(doc.getOwner().getUid(), doc.getDid(), doc.getDocName(),
                        doc.getDocType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDocumentBasic> getAllDocuments(String uid, int page, int size) throws UserNotFoundException {
        return ur.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("No user found with the given user id: " + uid)).getDocs()
                .stream().map(doc -> new UserDocumentBasic(doc.getDid(), doc.getDocName(), doc.getDocType()))
                .skip((long) (page - 1) * size).limit(size).collect(Collectors.toList());
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
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(doc, false, KafkaConstants.UNIQUE_SERVER_ID));
        //kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(doc.getOwner(), false));
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
        if (userL.isEmpty())
            throw new UserNotFoundException("No user found with the given email-id: " + email);
        User user = userL.get();
        if (!doc.containsUser(user) || doc.getEditor() != null)
            throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
        doc.setEditor(user);
        dr.saveAndFlush(doc);
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(doc, false, KafkaConstants.UNIQUE_SERVER_ID));
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
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(doc, false, KafkaConstants.UNIQUE_SERVER_ID));
        return Map.of("canEdit", true);
    }

//    public int copyFile(String source, String target) {
//        try (
//                final FileChannel input = new FileInputStream(source).getChannel();
//                final FileChannel output = new FileOutputStream(target).getChannel();
//                InputStream is = new BufferedInputStream(new FileInputStream(source));
//                OutputStream os = new BufferedOutputStream(new FileOutputStream(target))
//        ) {
//            //Now FileChannel
//            int checkSum1 = 0;
//            ByteBuffer buffer1 = ByteBuffer.allocate(4 * 1024);
//            while (input.read(buffer1) != -1) {
//                checkSum1 += buffer1.position();
//                output.write(buffer1);
//                buffer1.clear();
//            }
//            //Now BufferStream
//            int checkSum2 = 0;
//            int read = -1;
//            byte[] buffer2 = new byte[1024 * 4];
//            while ((read = is.read(buffer2, 0, buffer2.length)) != -1) {
//                checkSum2 += read;
//                os.write(buffer2, 0, read);
//            }
//            System.out.println("Ops performed by FileChannel: " + checkSum1);
//            System.out.println("Ops performed by BufferStream: " + checkSum2);
//            return checkSum1 + checkSum2;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

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
