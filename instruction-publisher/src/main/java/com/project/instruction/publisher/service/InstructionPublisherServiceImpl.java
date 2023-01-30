package com.project.instruction.publisher.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.instruction.publisher.config.MQConfig;
import com.project.instruction.publisher.customexceptions.DocumentNotFoundException;
import com.project.instruction.publisher.customexceptions.InvalidRequestDataException;
import com.project.instruction.publisher.customexceptions.UnauthorizedAccessException;
import com.project.instruction.publisher.customexceptions.UserNotFoundException;
import com.project.instruction.publisher.entity.Document;
import com.project.instruction.publisher.entity.User;
import com.project.instruction.publisher.model.Instruction;
import com.project.instruction.publisher.model.ResponseUser;
import com.project.instruction.publisher.repository.DocumentRepository;
import com.project.instruction.publisher.repository.UserRepository;

@Service
@Transactional
public class InstructionPublisherServiceImpl implements InstructionPublisherService {

	@Autowired
	private DocumentRepository dr;

	@Autowired
	private UserRepository ur;

	@Autowired
	private RabbitTemplate template;

	@Override
	public String publishDocument(String uid, Document doc)
			throws InvalidRequestDataException, UnauthorizedAccessException {
		if (uid == null || doc == null || doc.getEditor() == null || doc.getData() == null)
			throw new InvalidRequestDataException("Invalid request. Please check the request data & try again.");
		if (!doc.getEditor().getUid().equals(uid))
			throw new UnauthorizedAccessException("Unauthorized access not permitted! Enter valid credentials.");
		template.convertAndSend(MQConfig.EXCHANGE, MQConfig.ROUTING_KEY, doc);
		return "document published: " + doc;
	}
	
	@Override
	public String publishInstruction(String uid, Instruction ins)
			throws InvalidRequestDataException, UnauthorizedAccessException {
		if (uid == null || ins == null)
			throw new InvalidRequestDataException("Invalid request. Please check the request data & try again.");
		if (!ins.getUid().equals())
			throw new UnauthorizedAccessException("Unauthorized access not permitted! Enter valid credentials.");
		template.convertAndSend(MQConfig.EXCHANGE, MQConfig.ROUTING_KEY, ins);
		return "document published: " + ins;
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
		user.setEditingDoc(doc);
		dr.saveAndFlush(doc);
		return new ResponseUser(user.getUsername(), user.getEmail());
	}

	@Override
	public ResponseUser changeEditStatusByUsername(String username, String id)
			throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
		if (id == null || username == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		Document doc = dr.findById(id)
				.orElseThrow(() -> new DocumentNotFoundException("No document found with the given id: " + id));
		User user = ur.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("No user found with the given username: " + username));
		if (!doc.containsUser(user) || doc.getEditor() != null)
			throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
		doc.setEditor(user);
		user.setEditingDoc(doc);
		dr.saveAndFlush(doc);
		return new ResponseUser(user.getUsername(), username);
	}

	@Override
	public ResponseUser changeEditStatusByEmail(String email, String id)
			throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
		if (id == null || email == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		Document doc = dr.findById(id)
				.orElseThrow(() -> new DocumentNotFoundException("No document found with the given id: " + id));
		User user = ur.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("No user found with the given email-id: " + email));
		if (!doc.containsUser(user) || doc.getEditor() != null)
			throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
		doc.setEditor(user);
		user.setEditingDoc(doc);
		dr.saveAndFlush(doc);
		return new ResponseUser(user.getUsername(), email);
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
		return user != null ? new ResponseUser(user.getUsername(), user.getEmail()) : null;
	}

	@Override
	public ResponseUser getEditStatusByUsername(String username, String id) throws InvalidRequestDataException,
			DocumentNotFoundException, UserNotFoundException, UnauthorizedAccessException {
		if (id == null || username == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		Document doc = dr.findById(id)
				.orElseThrow(() -> new DocumentNotFoundException("No document found with the given id: " + id));
		User user = ur.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("No user found with the given username: " + username));
		if (!doc.containsUser(user))
			throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
		user = doc.getEditor();
		return user != null ? new ResponseUser(user.getUsername(), user.getEmail()) : null;
	}

	@Override
	public ResponseUser getEditStatusByEmail(String email, String id)
			throws InvalidRequestDataException, DocumentNotFoundException, UnauthorizedAccessException {
		if (id == null || email == null)
			throw new InvalidRequestDataException("Invalid request data. Please check the request & try again.");
		Document doc = dr.findById(id)
				.orElseThrow(() -> new DocumentNotFoundException("No document found with the given id: " + id));
		User user = ur.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("No user found with the given email: " + email));
		if (!doc.containsUser(user))
			throw new UnauthorizedAccessException("Unauthorized access request. Please check the request & try again.");
		user = doc.getEditor();
		return user != null ? new ResponseUser(user.getUsername(), user.getEmail()) : null;
	}

}
