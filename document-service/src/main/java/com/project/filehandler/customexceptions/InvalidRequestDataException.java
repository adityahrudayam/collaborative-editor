package com.project.filehandler.customexceptions;

public class InvalidRequestDataException extends RuntimeException {
	public InvalidRequestDataException() {
		System.out.println("InvalidRequestDataException default constructor called.");
	}

	public InvalidRequestDataException(String msg) {
		super(msg);
		System.out.println("InvalidRequestDataException(msg) constructor called.");
	}
}
