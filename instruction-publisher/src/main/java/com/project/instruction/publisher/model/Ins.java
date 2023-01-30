package com.project.instruction.publisher.model;

public final class Ins extends Instruction {

	private char[] c;

	public Ins(String d, int p, char[] c) {
		super(d, p, false);
		this.c = c;
	}

	public Ins(String did, int p, byte[] data) {
		this(did, p, new String(data).toCharArray());
	}

	public char[] getC() {
		return c;
	}

	public void setC(char[] c) {
		this.c = c;
	}

}
