package com.project.us.customexceptions;

public class UnauthorizedAccessException extends RuntimeException {
	public UnauthorizedAccessException() {
		System.out.println("UnauthorizedAccessException default constructor called.");
	}

	public UnauthorizedAccessException(String msg) {
		super(msg);
		System.out.println("UnauthorizedAccessException(msg) constructor called.");
	}
}
