package com.project.instruction.publisher.model;

public class SingleAccessRequest {
	private String owner, receiver, docId;

	public SingleAccessRequest() {
		super();
	}

	public SingleAccessRequest(String owner, String receiver, String docId) {
		super();
		this.owner = owner;
		this.receiver = receiver;
		this.docId = docId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

}
