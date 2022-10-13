package com.project.us.service;

import java.util.List;
import java.util.Set;

import com.project.us.entity.User;
import com.project.us.model.ResponseUser;

public interface UserService {
	ResponseUser saveUser(User user);

	void deleteUserByUid(String uid);

	void deleteUserByUsername(String username);

	void deleteUserByEmail(String email);

	ResponseUser getAccountDetailsByUid(String uid);

	ResponseUser getAccountDetailsByUsername(String username);

	ResponseUser getAccountDetailsByEmail(String email);

	ResponseUser editUser(User user);

	List<ResponseUser> getUsersWithAccess(String uid, String id);

	List<ResponseUser> getUsersWithAccessByUsername(String username, String id);

	ResponseUser shareAccessToUser(String ownerUid, String receiverUid, String id);

	List<ResponseUser> shareAccessToUsers(String ownerUid, List<String> receiverUids, String id);

	List<ResponseUser> removeAccessToUsers(String owner, List<String> receivers, String docId);

	List<ResponseUser> shareAccessToUsersByUsername(String ownerUsername, List<String> receiverUsernames, String docId);

	List<ResponseUser> removeAccessToUsersByUsername(String ownerUsername, List<String> receiverUsernames,
			String docId);

	List<ResponseUser> shareAccessToUsersByEmail(String ownerEmail, List<String> receiverEmails, String docId);

	List<ResponseUser> removeAccessToUsersByEmail(String ownerEmail, List<String> receiverEmails, String docId);

	List<String> getFilteredUsersByUsername(String username);

	List<String> getFilteredUsersByUsername(String username, Set<String> selectedUsernames);

	List<String> getFilteredUsersByEmail(String email);

	List<String> getFilteredUsersByEmail(String email, Set<String> selectedEmails);

}
