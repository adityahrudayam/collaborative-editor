package com.project.edithandler.repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.project.edithandler.entity.Document;
import com.project.edithandler.entity.User;
import com.project.edithandler.model.ResponseUser;
import com.project.edithandler.model.TextDocument;

@Repository
public class TextDocumentDaoImpl implements TextDocumentDao {

	@Autowired
	private RedisTemplate<String, TextDocument> redisTemplate;

	private final static String KEY = "TEXT-DOCUMENTS";

	@Override
	public boolean saveDocument(Document doc) {
		try {
			User editor = doc.getEditor();
			Set<ResponseUser> usersWithAccess = doc.getUsers().stream()
					.map(u -> new ResponseUser(u.getUsername(), u.getEmail())).collect(Collectors.toSet());
			TextDocument text = new TextDocument(doc.getDid(), doc.getDocName(), doc.getData(), usersWithAccess,
					new ResponseUser(editor.getUsername(), editor.getEmail()));
			redisTemplate.opsForHash().put(KEY, doc.getDid(), text);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<TextDocument> fetchAllDocuments() {
		List<TextDocument> textDocs = redisTemplate.opsForHash().values(KEY).stream().map(e -> (TextDocument) e)
				.collect(Collectors.toList());
		return textDocs;
	}

	@Override
	public TextDocument fetchDocumentById(String did) {
		return (TextDocument) redisTemplate.opsForHash().get(KEY, did);
	}

	@Override
	public boolean containsDocumentById(String did) {
		return redisTemplate.opsForHash().hasKey(KEY, did);
	}

	@Override
	public boolean deleteDocumentById(String did) {
		try {
			redisTemplate.opsForHash().delete(KEY, did);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public TextDocument updateDocument(Document doc) {
		try {
			User editor = doc.getEditor();
			Set<ResponseUser> usersWithAccess = doc.getUsers().stream()
					.map(u -> new ResponseUser(u.getUsername(), u.getEmail())).collect(Collectors.toSet());
			TextDocument text = new TextDocument(doc.getDid(), doc.getDocName(), doc.getData(), usersWithAccess,
					new ResponseUser(editor.getUsername(), editor.getEmail()));
			redisTemplate.opsForHash().put(KEY, doc.getDid(), text);
			return text;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
