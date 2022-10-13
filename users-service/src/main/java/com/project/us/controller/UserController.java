package com.project.us.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.us.entity.User;
import com.project.us.model.Filter;
import com.project.us.model.MultiAccessRequest;
import com.project.us.model.ResponseUser;
import com.project.us.model.SingleAccessRequest;
import com.project.us.service.UserService;

@RestController
@RequestMapping("/user-service")
public class UserController {

	@Autowired
	private UserService us;

	@PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseUser> saveUser(@RequestBody User user) {
		return ResponseEntity.ok(us.saveUser(user));
	}

	@PutMapping("/edit-user/{uid}")
	public ResponseEntity<ResponseUser> editUserAccount(@RequestBody User user) {
		return ResponseEntity.ok(us.editUser(user));
	}

	@DeleteMapping("/delete-user-by-id/{uid}")
	public ResponseEntity<String> deleteUserByUid(@PathVariable String uid) {
		us.deleteUserByUid(uid);
		return ResponseEntity.ok("Successfully deleted the user with id: " + uid);
	}

	@DeleteMapping("/delete-user-by-username/{username}")
	public ResponseEntity<String> deleteUserByUsername(@PathVariable String username) {
		us.deleteUserByUsername(username);
		return ResponseEntity.ok("Successfully deleted the user with username: " + username);
	}

	@DeleteMapping("/delete-user-by-email/{email}")
	public ResponseEntity<String> deleteUserByEmail(@PathVariable String email) {
		us.deleteUserByEmail(email);
		return ResponseEntity.ok("Successfully deleted the user with email-id: " + email);
	}

	@GetMapping("/account-details-by-id/{uid}")
	public ResponseEntity<ResponseUser> getAccountDetailsByUid(@PathVariable String uid) {
		return ResponseEntity.ok(us.getAccountDetailsByUid(uid));
	}

	@GetMapping("/account-details-by-username/{username}")
	public ResponseEntity<ResponseUser> getAccountDetailsByUsername(@PathVariable String username) {
		return ResponseEntity.ok(us.getAccountDetailsByUsername(username));
	}

	@GetMapping("/account-details-by-email/{email}")
	public ResponseEntity<ResponseUser> getAccountDetailsByEmail(@PathVariable String email) {
		return ResponseEntity.ok(us.getAccountDetailsByEmail(email));
	}

	@GetMapping("/users-by-document-id/{id}/{uid}")
	public ResponseEntity<List<ResponseUser>> getUsersByDid(@PathVariable("id") String did,
			@PathVariable("uid") String uid) {
		return ResponseEntity.ok(us.getUsersWithAccess(uid, did));
	}

	@GetMapping("/users-by-document-id-username/{id}/{username}")
	public ResponseEntity<List<ResponseUser>> getUsersByUsername(@PathVariable("id") String did,
			@PathVariable String username) {
		return ResponseEntity.ok(us.getUsersWithAccessByUsername(username, did));
	}

	@PostMapping("/share-access-to-user")
	public ResponseEntity<ResponseUser> shareAccessToUser(@RequestBody SingleAccessRequest accessRequest) {
		return ResponseEntity.ok(us.shareAccessToUser(accessRequest.getOwnerUid(), accessRequest.getReceiverUid(),
				accessRequest.getDocId()));
	}

	@PostMapping("/share-access-to-users")
	public ResponseEntity<List<ResponseUser>> shareAccessToUsers(@RequestBody MultiAccessRequest accessRequest) {
		return ResponseEntity.ok(us.shareAccessToUsers(accessRequest.getOwner(), accessRequest.getReceivers(),
				accessRequest.getDocId()));
	}

	@PostMapping("/remove-access-to-users")
	public ResponseEntity<List<ResponseUser>> remomveAccessToUsers(@RequestBody MultiAccessRequest accessRequest) {
		return ResponseEntity.ok(us.removeAccessToUsers(accessRequest.getOwner(), accessRequest.getReceivers(),
				accessRequest.getDocId()));
	}

	@PostMapping("/share-access-to-users-by-username")
	public ResponseEntity<List<ResponseUser>> shareAccessToUsersByUsername(
			@RequestBody MultiAccessRequest accessRequest) {
		return ResponseEntity.ok(us.shareAccessToUsersByUsername(accessRequest.getOwner(), accessRequest.getReceivers(),
				accessRequest.getDocId()));
	}

	@PostMapping("/remove-access-to-users-by-username")
	public ResponseEntity<List<ResponseUser>> removeAccessToUsersByUsername(
			@RequestBody MultiAccessRequest accessRequest) {
		return ResponseEntity.ok(us.removeAccessToUsersByUsername(accessRequest.getOwner(),
				accessRequest.getReceivers(), accessRequest.getDocId()));
	}

	@PostMapping("/share-access-to-users-by-email")
	public ResponseEntity<List<ResponseUser>> shareAccessToUsersByEmail(@RequestBody MultiAccessRequest accessRequest) {
		return ResponseEntity.ok(us.shareAccessToUsersByEmail(accessRequest.getOwner(), accessRequest.getReceivers(),
				accessRequest.getDocId()));
	}

	@PostMapping("/remove-access-to-users-by-email")
	public ResponseEntity<List<ResponseUser>> removeAccessToUsersByEmail(
			@RequestBody MultiAccessRequest accessRequest) {
		return ResponseEntity.ok(us.removeAccessToUsersByEmail(accessRequest.getOwner(), accessRequest.getReceivers(),
				accessRequest.getDocId()));
	}

	@GetMapping("/users-by-username-search/{search}")
	public ResponseEntity<List<String>> getFilteredEmailsByUsername(@PathVariable("search") String username) {
		return ResponseEntity.ok(us.getFilteredUsersByUsername(username));
	}

	@PostMapping("/users-by-username-filter")
	public ResponseEntity<List<String>> getFilteredEmailsByUsername(Filter filter) {
		return ResponseEntity.ok(us.getFilteredUsersByUsername(filter.getSearch(), filter.getSelectedUsers()));
	}

	@GetMapping("/users-by-email-search/{search}")
	public ResponseEntity<List<String>> getFilteredEmailsByEmail(@PathVariable("search") String email) {
		return ResponseEntity.ok(us.getFilteredUsersByEmail(email));
	}

	@PostMapping("/users-by-email-filter")
	public ResponseEntity<List<String>> getFilteredEmailsByEmail(Filter filter) {
		return ResponseEntity.ok(us.getFilteredUsersByEmail(filter.getSearch(), filter.getSelectedUsers()));
	}
}
