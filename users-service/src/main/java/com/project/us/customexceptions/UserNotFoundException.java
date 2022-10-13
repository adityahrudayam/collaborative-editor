package com.project.us.customexceptions;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException() {
		System.out.println("UserNotFoundException default constructor called.");
	}

	public UserNotFoundException(String msg) {
		super(msg);
		System.out.println("UserNotFoundException(msg) constructor called.");
	}
}
