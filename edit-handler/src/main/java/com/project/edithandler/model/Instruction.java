package com.project.edithandler.model;

public class Instruction {
	private int p;
	private String did;

	public Instruction() {
		super();
	}

	public Instruction(String d, int p) {
		did = d;
		this.p = p;
	}

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public int getP() {
		return p;
	}

	public void setP(int p) {
		this.p = p;
	}

	@Override
	public String toString() {
		return "Instruction [p=" + p + ", did=" + did + "]";
	}

}
