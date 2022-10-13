package com.project.edithandler.customexceptions;

public class DocumentNotFoundException extends RuntimeException {
	public DocumentNotFoundException() {
		System.out.println("DocumentNotFoundException default constructor called.");
	}

	public DocumentNotFoundException(String msg) {
		super(msg);
		System.out.println("DocumentNotFoundException(msg) constructor called.");
	}
}
