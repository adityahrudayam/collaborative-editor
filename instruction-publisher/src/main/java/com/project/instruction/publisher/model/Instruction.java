package com.project.instruction.publisher.model;

public class Instruction {
	private int p;
	private String uid;
	private String did;
	private boolean type;

	public Instruction() {
		super();
	}

	public Instruction(int p, String uid, String did, boolean type) {
		super();
		this.p = p;
		this.uid = uid;
		this.did = did;
		this.type = type;
	}

	public int getP() {
		return p;
	}

	public void setP(int p) {
		this.p = p;
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

	public boolean getType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Instruction [p=" + p + ", uid=" + uid + ", did=" + did + ", type=" + type + "]";
	}

}
