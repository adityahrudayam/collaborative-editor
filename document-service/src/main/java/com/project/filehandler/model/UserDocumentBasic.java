package com.project.filehandler.model;

public class UserDocumentBasic {
	private String uid, did, docName, docType;

	public UserDocumentBasic() {
		super();
	}

	public UserDocumentBasic(String did, String docName, String docType) {
		super();
		this.did = did;
		this.docName = docName;
		this.docType = docType;
	}

	public UserDocumentBasic(String uid, String did, String docName, String docType) {
		super();
		this.uid = uid;
		this.did = did;
		this.docName = docName;
		this.docType = docType;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	@Override
	public String toString() {
		return "UserDocumentBasic [uid=" + uid + ", did=" + did + ", docName=" + docName + ", docType=" + docType + "]";
	}

}
