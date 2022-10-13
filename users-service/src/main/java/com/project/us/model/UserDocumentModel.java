package com.project.us.model;

import com.project.us.entity.Document;

public class UserDocumentModel {
	private String uid;
	private Document document;

	public UserDocumentModel() {
		System.out.println("UserDocumentModel default constructor called.");
	}

	public UserDocumentModel(String uid, Document document) {
		super();
		this.uid = uid;
		this.document = document;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

}
