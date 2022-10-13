package com.project.edithandler.model;

import java.util.List;

public class MultiAccessRequest {
	private String ownerUid, docId;
	private List<String> receiverUids;

	public MultiAccessRequest() {
		super();
	}

	public MultiAccessRequest(String ownerUid, String docId, List<String> receiverUids) {
		super();
		this.ownerUid = ownerUid;
		this.docId = docId;
		this.receiverUids = receiverUids;
	}

	public String getOwnerUid() {
		return ownerUid;
	}

	public void setOwnerUid(String ownerUid) {
		this.ownerUid = ownerUid;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public List<String> getReceiverUids() {
		return receiverUids;
	}

	public void setReceiverUids(List<String> receiverUids) {
		this.receiverUids = receiverUids;
	}

}
