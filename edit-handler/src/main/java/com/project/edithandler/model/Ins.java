package com.project.edithandler.model;

public class Ins extends Instruction {

	private char[] c;

	public Ins(String d, int p, char[] c) {
		super(d, p);
		this.c = c;
	}

	public char[] getC() {
		return c;
	}

	public void setC(char[] c) {
		this.c = c;
	}

}
