package com.project.us.model;

import java.util.Set;

public class Filter {
	private String search;
	private Set<String> selectedUsers;

	public Filter() {
		super();
	}

	public Filter(String search, Set<String> selectedUsers) {
		super();
		this.search = search;
		this.selectedUsers = selectedUsers;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public Set<String> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(Set<String> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}

}
