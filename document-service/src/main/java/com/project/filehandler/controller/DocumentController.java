package com.project.filehandler.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.filehandler.model.ResponseUser;
import com.project.filehandler.model.SingleAccessRequest;
import com.project.filehandler.model.UserDocumentAdvanced;
import com.project.filehandler.model.UserDocumentBasic;
import com.project.filehandler.model.UserDocumentModel;
import com.project.filehandler.service.DocumentService;

@RestController
@RequestMapping("/document-service")
public class DocumentController {
	private static final int PAGE_SIZE = 5;

	@Autowired
	private DocumentService ds;

	@PostMapping("/upload-file-document/{uid}")
	public ResponseEntity<UserDocumentBasic> uploadFileDocument(@PathVariable("uid") String uid,
			@RequestParam("file") MultipartFile file) throws IOException {
		return ResponseEntity.ok(ds.uploadFileDocument(uid, file));
	}

	@PostMapping("/upload-json-document")
	public ResponseEntity<UserDocumentBasic> uploadJsonDocument(@RequestBody UserDocumentModel userDoc) {
		return ResponseEntity.ok(ds.uploadJsonDocument(userDoc));
	}

	@PostMapping("/upload-json")
	public ResponseEntity<UserDocumentBasic> uploadJsonDocument(@RequestBody UserDocumentAdvanced userDoc) {
		return ResponseEntity.ok(ds.uploadJsonDocument(userDoc));
	}

	@PutMapping("/update-document-name")
	public ResponseEntity<UserDocumentBasic> updateDocumentName(@RequestBody UserDocumentBasic userDoc) {
		return ResponseEntity.ok(ds.updateDocumentName(userDoc));
	}

	@GetMapping("/download-document/{id}/{uid}")
	public ResponseEntity<Resource> downloadDocument(@PathVariable("id") String did, @PathVariable("uid") String uid) {
		UserDocumentAdvanced doc = ds.fetchDocumentDataById(uid, did);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(doc.getDocType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getDocName() + "\"")
				.body(new ByteArrayResource(doc.getDocData()));
	}

	@GetMapping("/document/{id}/{uid}")
	public ResponseEntity<UserDocumentBasic> fetchDocumentById(@PathVariable("id") String did,
			@PathVariable("uid") String uid) {
		return ResponseEntity.ok(ds.fetchDocumentDataById(uid, did));
	}

	@GetMapping("/document-only/{id}/{uid}")
	public ResponseEntity<UserDocumentBasic> fetchOnlyDocById(@PathVariable("id") String did,
			@PathVariable("uid") String uid) {
		return ResponseEntity.ok(ds.getDocumentById(uid, did));
	}

	@DeleteMapping("/delete-document/{id}/{uid}")
	public ResponseEntity<Map<String, UserDocumentBasic>> deleteDocById(@PathVariable("id") String did,
			@PathVariable("uid") String uid) {
		return ResponseEntity.ok(ds.deleteDocumentById(uid, did));
	}

	@GetMapping("/documents-made-by-user/{uid}")
	public ResponseEntity<List<UserDocumentBasic>> fetchDocsCreatedByUser(@PathVariable String uid,
			@RequestParam(name = "page", required = false) Integer page) {
		page = page == null ? 1 : page;
		return ResponseEntity.ok(ds.getDocumentsCreatedByUser(uid, page, PAGE_SIZE));
	}

	@GetMapping("/documents-shared-by-user/{uid}")
	public ResponseEntity<List<UserDocumentBasic>> fetchSharedDocsOfUser(@PathVariable String uid,
			@RequestParam(name = "page", required = false) Integer page) {
		page = page == null ? 1 : page;
		return ResponseEntity.ok(ds.getSharedDocumentsOfUser(uid, page, PAGE_SIZE));
	}

	@GetMapping("/documents-shared-to-user/{uid}")
	public ResponseEntity<List<UserDocumentBasic>> fetchDocsSharedToUser(@PathVariable String uid,
			@RequestParam(name = "page", required = false) Integer page) {
		page = page == null ? 1 : page;
		return ResponseEntity.ok(ds.getDocumentsSharedToUser(uid, page, PAGE_SIZE));
	}

	@GetMapping("/documents/{uid}")
	public ResponseEntity<List<UserDocumentBasic>> fetchDocs(@PathVariable String uid,
			@RequestParam(name = "page", required = false) Integer page) {
		page = page == null ? 1 : page;
		return ResponseEntity.ok(ds.getAllDocuments(uid, page, PAGE_SIZE));
	}

	@PutMapping("/change-edit-status")
	public ResponseEntity<ResponseUser> changeEditStatus(@RequestBody SingleAccessRequest accessRequest) {
		return ResponseEntity.ok(ds.changeEditStatus(accessRequest.getOwnerUid(), accessRequest.getDocId()));
	}

	@GetMapping("/get-edit-status/{id}/{uid}")
	public ResponseEntity<ResponseUser> getEditStatus(@PathVariable("id") String did, @PathVariable String uid) {
		return ResponseEntity.ok(ds.getEditStatus(uid, did));
	}

	@PutMapping("/stop-edit-save-status")
	public ResponseEntity<Map<String, Boolean>> stopEditAndSaveStatus(@RequestBody SingleAccessRequest accessRequest) {
		return ResponseEntity.ok(ds.stopEditAndSaveStatus(accessRequest.getOwnerUid(), accessRequest.getDocId()));
	}

}
