package com.project.us.model;

public class SingleAccessRequest {
	private String ownerUid, receiverUid, docId;

	public SingleAccessRequest() {
		super();
	}

	public SingleAccessRequest(String ownerUid, String receiverUid, String docId) {
		super();
		this.ownerUid = ownerUid;
		this.receiverUid = receiverUid;
		this.docId = docId;
	}

	public String getOwnerUid() {
		return ownerUid;
	}

	public void setOwnerUid(String ownerUid) {
		this.ownerUid = ownerUid;
	}

	public String getReceiverUid() {
		return receiverUid;
	}

	public void setReceiverUid(String receiverUid) {
		this.receiverUid = receiverUid;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

}
