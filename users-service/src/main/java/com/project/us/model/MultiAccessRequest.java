package com.project.us.model;

import java.util.List;

public class MultiAccessRequest {
	private String owner, docId;
	private List<String> receivers;

	public MultiAccessRequest() {
		super();
	}

	public MultiAccessRequest(String ownerUid, String docId, List<String> receiverUids) {
		super();
		this.owner = ownerUid;
		this.docId = docId;
		this.receivers = receiverUids;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public List<String> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<String> receivers) {
		this.receivers = receivers;
	}

}
