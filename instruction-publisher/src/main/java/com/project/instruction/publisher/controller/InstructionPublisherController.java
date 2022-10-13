package com.project.instruction.publisher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.instruction.publisher.entity.Document;
import com.project.instruction.publisher.model.ResponseUser;
import com.project.instruction.publisher.model.SingleAccessRequest;
import com.project.instruction.publisher.service.InstructionPublisherService;

@RestController
@RequestMapping("/edit-publisher")
public class InstructionPublisherController {
	@Autowired
	private InstructionPublisherService is;

	@PostMapping("/publish-document/{uid}")
	public ResponseEntity<String> publishInstruction(@PathVariable String uid, @RequestBody Document doc) {
		return new ResponseEntity<>(is.publishDocument(uid, doc), HttpStatus.ACCEPTED);
	}

	@PutMapping("/change-edit-status")
	public ResponseEntity<ResponseUser> changeEditStatus(@RequestBody SingleAccessRequest accessRequest) {
		return ResponseEntity.ok(is.changeEditStatus(accessRequest.getOwner(), accessRequest.getDocId()));
	}

	@PutMapping("/change-edit-status-by-username")
	public ResponseEntity<ResponseUser> changeEditStatusByUsername(@RequestBody SingleAccessRequest accessRequest) {
		return ResponseEntity.ok(is.changeEditStatusByUsername(accessRequest.getOwner(), accessRequest.getDocId()));
	}

	@PutMapping("/change-edit-status-by-email")
	public ResponseEntity<ResponseUser> changeEditStatusByEmail(@RequestBody SingleAccessRequest accessRequest) {
		return ResponseEntity.ok(is.changeEditStatusByEmail(accessRequest.getOwner(), accessRequest.getDocId()));
	}

	@GetMapping("/get-edit-status/{id}/{uid}")
	public ResponseEntity<ResponseUser> getEditStatus(@PathVariable("id") String did, @PathVariable String uid) {
		return ResponseEntity.ok(is.getEditStatus(uid, did));
	}

	@GetMapping("/get-edit-status-by-username/{id}/{username}")
	public ResponseEntity<ResponseUser> getEditStatusByUsername(@PathVariable("id") String did,
			@PathVariable String username) {
		return ResponseEntity.ok(is.getEditStatusByUsername(username, did));
	}

	@GetMapping("/get-edit-status-by-email/{id}/{email}")
	public ResponseEntity<ResponseUser> getEditStatusByEmail(@PathVariable("id") String did,
			@PathVariable String email) {
		return ResponseEntity.ok(is.getEditStatusByEmail(email, did));
	}

//	@PutMapping("/stop-edit-save-status/{uid}")
//	public ResponseEntity<Map<String, Boolean>> stopEditAndSaveStatus(@RequestBody SingleAccessRequest accessRequest) {
//		return ResponseEntity.ok(is.stopEditAndSaveStatus(accessRequest.getOwnerUid(), accessRequest.getDocId()));
//	}
}
