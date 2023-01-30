package com.project.us.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.project.us.constants.KafkaConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.us.customexceptions.DocumentNotFoundException;
import com.project.us.customexceptions.InvalidRequestDataException;
import com.project.us.customexceptions.UnauthorizedAccessException;
import com.project.us.customexceptions.UserNotFoundException;
import com.project.us.entity.Document;
import com.project.us.entity.User;
import com.project.us.model.ResponseUser;
import com.project.us.repository.DocumentRepository;
import com.project.us.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private KafkaTemplate<String, Event> kafkaTemplate;

    @Autowired
    private UserRepository ur;

    @Autowired
    private DocumentRepository dr;

    @Override
    public ResponseUser saveUser(User user) throws InvalidRequestDataException {
        System.out.println(user);
        if (user == null || user.getName() == null || user.getEmail() == null || user.getUsername() == null
                || user.getPassword() == null)
            throw new InvalidRequestDataException("Invalid request data. Please check it & try again.");
        // System.out.println(user);
        String username = user.getUsername().trim(), email = user.getEmail().trim().toLowerCase(),
                name = user.getName().trim(), password = user.getPassword().trim();
        if (email.isBlank() || !email.endsWith(".com") || !email.contains("@") || username.length() < 3
                || (username.contains("@") && username.contains(".com")) || name.isBlank() || password.isBlank()
                || password.length() < 6)
            throw new InvalidRequestDataException("Invalid request data. Please check it & try again.");
        User save = new User(name, username, email, password);
        User u = ur.save(save);
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(u, false, KafkaConstants.UNIQUE_SERVER_ID));
        return new ResponseUser(u.getUid(), u.getName(), u.getUsername(), u.getEmail());
    }

    @Override
    public void deleteUserByUid(String uid) throws UserNotFoundException {
        User user = ur.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("No user found with the given user id: " + uid));
        List<Document> docs = user.getOwnedDocs().stream().peek(doc -> {
            doc.getUsers().forEach(u -> u.removeDocument(doc));
            doc.setUsers(null);
        }).collect(Collectors.toList());
        dr.saveAllAndFlush(docs);
        ur.delete(user);
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(user, true, KafkaConstants.UNIQUE_SERVER_ID));
    }

    @Override
    public void deleteUserByUsername(String username) throws UserNotFoundException {
        Optional<User> find = ur.findByUsername(username);
        if (find.isEmpty())
            throw new UserNotFoundException("No user found with the given username: " + username);
        User user = find.get();
        List<Document> docs = user.getOwnedDocs().stream().peek(doc -> {
            doc.getUsers().forEach(u -> u.removeDocument(doc));
            doc.setUsers(null);
        }).collect(Collectors.toList());
        dr.saveAllAndFlush(docs);
        ur.delete(user);
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(user, true, KafkaConstants.UNIQUE_SERVER_ID));
    }

    @Override
    public void deleteUserByEmail(String email) throws UserNotFoundException {
        Optional<User> find = ur.findByEmail(email);
        if (find.isEmpty())
            throw new UserNotFoundException("No user found with the given email id: " + email);
        User user = find.get();
        List<Document> docs = user.getOwnedDocs().stream().peek(doc -> {
            doc.getUsers().forEach(u -> u.removeDocument(doc));
            doc.setUsers(null);
        }).collect(Collectors.toList());
        dr.saveAllAndFlush(docs);
        ur.delete(user);
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(user, true, KafkaConstants.UNIQUE_SERVER_ID));
    }

    @Override
    public ResponseUser getAccountDetailsByUid(String uid) throws InvalidRequestDataException, UserNotFoundException {
        if (uid == null)
            throw new InvalidRequestDataException("Invalid request data. Please check it & try again.");
        User user = ur.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("No user found with the given user id: " + uid));
        return new ResponseUser(user.getName(), user.getUsername(), user.getEmail());
    }

    @Override
    public ResponseUser getAccountDetailsByUsername(String username)
            throws InvalidRequestDataException, UserNotFoundException {
        if (username == null)
            throw new InvalidRequestDataException("Invalid request data. Please check it & try again.");
        Optional<User> user = ur.findByUsername(username);
        if (user.isEmpty())
            throw new UserNotFoundException("No user found with the given username: " + username);
        User res = user.get();
        return new ResponseUser(res.getName(), res.getUsername(), res.getEmail());
    }

    @Override
    public ResponseUser getAccountDetailsByEmail(String email)
            throws InvalidRequestDataException, UserNotFoundException {
        if (email == null)
            throw new InvalidRequestDataException("Invalid request data. Please check it & try again.");
        Optional<User> user = ur.findByEmail(email);
        if (user.isEmpty())
            throw new UserNotFoundException("No user found with the given email-id: " + email);
        User res = user.get();
        return new ResponseUser(res.getName(), res.getUsername(), res.getEmail());
    }

    @Override
    public ResponseUser editUser(User user) throws InvalidRequestDataException, UserNotFoundException {
        if (user == null || user.getUid() == null)
            throw new InvalidRequestDataException("Invalid request data. Please check it & try again.");
        User findUser = ur.findById(user.getUid())
                .orElseThrow(() -> new UserNotFoundException("No user found with the given user id: " + user.getUid()));
        if (user.getName() != null)
            findUser.setName(user.getName());
        if (user.getUsername() != null)
            findUser.setUsername(user.getUsername());
        if (user.getEmail() != null)
            findUser.setEmail(user.getEmail());
        if (user.getPassword() != null)
            findUser.setEmail(user.getPassword());
        User saveUser = ur.saveAndFlush(findUser);
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(user, false, KafkaConstants.UNIQUE_SERVER_ID));
        return new ResponseUser(saveUser.getName(), saveUser.getUsername(), saveUser.getEmail());
    }

    @Override
    public List<ResponseUser> getUsersWithAccess(String uid, String id)
            throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
        if (id == null || uid == null)
            throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
        Document doc = dr.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("No document found with the given id: " + id));
        User test = new User();
        test.setUid(uid);
        if (!doc.containsUser(test))
            throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
        return doc.getUsers().stream()
                .map(u -> new ResponseUser(u.getName(), u.getUsername(), u.getEmail())).collect(Collectors.toList());
    }

    @Override
    public List<ResponseUser> getUsersWithAccessByUsername(String username, String id)
            throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
        if (id == null || username == null)
            throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
        Document doc = dr.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("No document found with the given id: " + id));
        if (doc.getUsers().stream().noneMatch(u -> u.getUsername().equals(username)))
            throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
        return doc.getUsers().stream().filter(u -> !u.getUsername().equals(username))
                .map(u -> new ResponseUser(u.getName(), u.getUsername(), u.getEmail())).collect(Collectors.toList());
    }

    @Override
    public ResponseUser shareAccessToUser(String ownerUid, String receiverUid, String id)
            throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException,
            UserNotFoundException {
        if (ownerUid == null || receiverUid == null || id == null)
            throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
        Document doc = dr.findById(id).orElseThrow(
                () -> new DocumentNotFoundException("No document found with the given document id: " + id));
        if (!doc.getOwner().getUid().equals(ownerUid))
            throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
        User receivingUser = ur.findById(receiverUid)
                .orElseThrow(() -> new UserNotFoundException("No user found with the given user id: " + receiverUid));
        if (receivingUser.containsDocument(doc))
            throw new InvalidRequestDataException(
                    "User already has access to the document. Please check the request & try again.");
        doc.addUser(receivingUser);
        receivingUser.addDocument(doc);
        Document savedDoc = dr.saveAndFlush(doc);
        System.out.println(receivingUser.getEditingDoc());
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(savedDoc, false, KafkaConstants.UNIQUE_SERVER_ID));
        return new ResponseUser(receivingUser.getName(), receivingUser.getUsername(), receivingUser.getEmail());
    }

    @Override
    public List<ResponseUser> shareAccessToUsers(String ownerUid, List<String> receiverUids, String id)
            throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
        if (ownerUid == null || receiverUids == null || id == null)
            throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
        Document doc = dr.findById(id).orElseThrow(
                () -> new DocumentNotFoundException("No document found with the given document id: " + id));
        if (!doc.getOwner().getUid().equals(ownerUid))
            throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
        List<String> nonNullReceiverUids = receiverUids.stream().filter(uid -> uid != null && !uid.equals(ownerUid))
                .collect(Collectors.toList());
        List<User> receivers = ur.findAllById(nonNullReceiverUids);
        receivers.forEach(u -> u.addDocument(doc));
        doc.addAllUsers(receivers);
        Document savedDoc = dr.saveAndFlush(doc);
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(savedDoc, false, KafkaConstants.UNIQUE_SERVER_ID));
        return savedDoc.getUsers().stream().filter(u -> !u.equals(savedDoc.getOwner()))
                .map(u -> new ResponseUser(u.getName(), u.getUsername(), u.getEmail())).collect(Collectors.toList());
    }

    @Override
    public List<ResponseUser> removeAccessToUsers(String owner, List<String> receivers, String docId)
            throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
        if (owner == null || receivers == null || docId == null)
            throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
        Document doc = dr.findById(docId).orElseThrow(
                () -> new DocumentNotFoundException("No document found with the given document id: " + docId));
        if (!doc.getOwner().getUid().equals(owner))
            throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
        Set<String> uidsToRemove = new HashSet<>(receivers);
        uidsToRemove.remove(owner);
        Set<User> users = doc.getUsers().stream().filter(u -> !uidsToRemove.contains(u.getUid()))
                .collect(Collectors.toSet());
        doc.setUsers(users);
        Document savedDoc = dr.saveAndFlush(doc);
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(savedDoc, false, KafkaConstants.UNIQUE_SERVER_ID));
        return savedDoc.getUsers().stream().filter(u -> !u.equals(savedDoc.getOwner()))
                .map(u -> new ResponseUser(u.getName(), u.getUsername(), u.getEmail())).collect(Collectors.toList());
    }

    @Override
    public List<ResponseUser> shareAccessToUsersByUsername(String ownerUsername, List<String> receiverUsernames,
                                                           String id) throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
        if (ownerUsername == null || receiverUsernames == null || id == null)
            throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
        Document doc = dr.findById(id).orElseThrow(
                () -> new DocumentNotFoundException("No document found with the given document id: " + id));
        if (!doc.getOwner().getUsername().equals(ownerUsername))
            throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
        List<User> receivers = receiverUsernames.stream()
                .filter(un -> un != null && !un.equals(doc.getOwner().getUsername())).map(un -> ur.findByUsername(un))
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        receivers.forEach(u -> u.addDocument(doc));
        doc.addAllUsers(receivers);
        Document savedDoc = dr.saveAndFlush(doc);
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(savedDoc, false, KafkaConstants.UNIQUE_SERVER_ID));
        return savedDoc.getUsers().stream().filter(u -> !u.equals(savedDoc.getOwner()))
                .map(u -> new ResponseUser(u.getName(), u.getUsername(), u.getEmail())).collect(Collectors.toList());
    }

    @Override
    public List<ResponseUser> removeAccessToUsersByUsername(String ownerUsername, List<String> receiverUsernames,
                                                            String id) throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
        if (ownerUsername == null || receiverUsernames == null || id == null)
            throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
        Document doc = dr.findById(id).orElseThrow(
                () -> new DocumentNotFoundException("No document found with the given document id: " + id));
        if (!doc.getOwner().getUsername().equals(ownerUsername))
            throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
        Set<String> usersToRemove = new HashSet<>(receiverUsernames);
        usersToRemove.remove(ownerUsername);
        Set<User> users = doc.getUsers().stream().filter(u -> !usersToRemove.contains(u.getUsername()))
                .collect(Collectors.toSet());
        doc.setUsers(users);
        Document savedDoc = dr.saveAndFlush(doc);
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(savedDoc, false, KafkaConstants.UNIQUE_SERVER_ID));
        return savedDoc.getUsers().stream().filter(u -> !u.equals(savedDoc.getOwner()))
                .map(u -> new ResponseUser(u.getName(), u.getUsername(), u.getEmail())).collect(Collectors.toList());
    }

    @Override
    public List<ResponseUser> shareAccessToUsersByEmail(String ownerEmail, List<String> receiverEmails, String id)
            throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
        if (ownerEmail == null || receiverEmails == null || id == null)
            throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
        Document doc = dr.findById(id).orElseThrow(
                () -> new DocumentNotFoundException("No document found with the given document id: " + id));
        if (!doc.getOwner().getEmail().equals(ownerEmail))
            throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
        List<User> receivers = receiverEmails.stream()
                .filter(email -> email != null && !email.equals(doc.getOwner().getEmail()))
                .map(email -> ur.findByEmail(email)).filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
        receivers.forEach(u -> u.addDocument(doc));
        doc.addAllUsers(receivers);
        Document savedDoc = dr.saveAndFlush(doc);
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(savedDoc, false, KafkaConstants.UNIQUE_SERVER_ID));
        return savedDoc.getUsers().stream().filter(u -> !u.equals(savedDoc.getOwner()))
                .map(u -> new ResponseUser(u.getName(), u.getUsername(), u.getEmail())).collect(Collectors.toList());
    }

    @Override
    public List<ResponseUser> removeAccessToUsersByEmail(String ownerEmail, List<String> receiverEmails, String id)
            throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
        if (ownerEmail == null || receiverEmails == null || id == null)
            throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
        Document doc = dr.findById(id).orElseThrow(
                () -> new DocumentNotFoundException("No document found with the given document id: " + id));
        if (!doc.getOwner().getEmail().equals(ownerEmail))
            throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
        Set<String> usersToRemove = new HashSet<>(receiverEmails);
        usersToRemove.remove(ownerEmail);
        Set<User> users = doc.getUsers().stream().filter(u -> !usersToRemove.contains(u.getEmail()))
                .collect(Collectors.toSet());
        doc.setUsers(users);
        Document savedDoc = dr.saveAndFlush(doc);
        kafkaTemplate.send(KafkaConstants.TOPIC, new Event<>(savedDoc, false, KafkaConstants.UNIQUE_SERVER_ID));
        return savedDoc.getUsers().stream().filter(u -> !u.equals(savedDoc.getOwner()))
                .map(u -> new ResponseUser(u.getName(), u.getUsername(), u.getEmail())).collect(Collectors.toList());
    }

    @Override
    public List<String> getFilteredUsersByUsername(String username) throws InvalidRequestDataException {
        if (username == null)
            throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
        return ur.findByUsernameStartsWith(username).stream().map(User::getUsername).collect(Collectors.toList());
    }

    @Override
    public List<String> getFilteredUsersByUsername(String username, Set<String> selectedUsernames)
            throws InvalidRequestDataException {
        if (username == null || selectedUsernames == null)
            throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
        return ur.findByUsernameStartsWith(username).stream()
                .map(User::getUsername).filter(uUsername -> !selectedUsernames.contains(uUsername))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getFilteredUsersByEmail(String email) throws InvalidRequestDataException {
        if (email == null)
            throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
        return ur.findByEmailStartsWith(email).stream().map(User::getEmail).collect(Collectors.toList());
    }

    @Override
    public List<String> getFilteredUsersByEmail(String email, Set<String> selectedEmails)
            throws InvalidRequestDataException {
        if (email == null || selectedEmails == null)
            throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
        return ur.findByEmailStartsWith(email).stream()
                .map(User::getEmail).filter(uEmail -> !selectedEmails.contains(uEmail))
                .collect(Collectors.toList());
    }

}
