package com.project.ds.model;

public class ResponseUser {
	private String uid;
	private String name;
	private String username;
	private String email;

	public ResponseUser() {
		System.out.println("ResponseUser default constructor called.");
	}

	public ResponseUser(String name, String username, String email) {
		this.name = name;
		this.username = username;
		this.email = email;
	}

	public ResponseUser(String uid, String name, String username, String email) {
		this.uid = uid;
		this.name = name;
		this.username = username;
		this.email = email;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
