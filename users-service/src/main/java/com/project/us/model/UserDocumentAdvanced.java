package com.project.us.model;

public class UserDocumentAdvanced extends UserDocumentBasic {

	private byte[] docData;

	public UserDocumentAdvanced() {
		super();
	}

	public UserDocumentAdvanced(String did, String docName, String docType, byte[] data) {
		super(did, docName, docType);
		this.docData = data;
	}

	public UserDocumentAdvanced(String uid, String did, String docName, String docType, byte[] data) {
		super(uid, did, docName, docType);
		this.docData = data;
	}

	public byte[] getDocData() {
		return docData;
	}

	public void setDocData(byte[] data) {
		this.docData = data;
	}

}
