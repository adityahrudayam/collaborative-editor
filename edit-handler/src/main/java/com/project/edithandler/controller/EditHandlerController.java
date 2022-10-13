package com.project.edithandler.controller;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.edithandler.config.MQConfig;
import com.project.edithandler.entity.Document;
import com.project.edithandler.model.ResponseUser;
import com.project.edithandler.model.SingleAccessRequest;
import com.project.edithandler.model.TextDocument;
import com.project.edithandler.service.EditHandlerService;
import com.project.edithandler.service.TextDocumentService;

@Component
@RestController
public class EditHandlerController {
	@Autowired
	private EditHandlerService es;

	@Autowired
	private TextDocumentService tds;

	@RabbitListener(queues = MQConfig.QUEUE)
	public void listener(Document doc) {
		System.out.println(doc);
		// write-through & write-back combined
		if (tds.containsDocumentById(doc.getDid())) {
			try {
				TextDocument textDoc = tds.updateDocument(doc);
				
			} finally {
				es.saveDocumentAfterEditing(doc);
			}
		}else {

			es.saveDocument(doc);
		}
	}

//	@PostMapping("/save-document")
//	public ResponseEntity<Map<String, Boolean>> saveDocument() {
//		
//	}

//	@PutMapping("/change-edit-status/{uid}")
//	public ResponseEntity<ResponseUser> changeEditStatus(@RequestBody SingleAccessRequest accessRequest) {
//		return ResponseEntity.ok(es.changeEditStatus(accessRequest.getOwnerUid(), accessRequest.getDocId()));
//	}

//	@PutMapping("/change-edit-status-by-email/{uemail}/{did}")
//	public ResponseEntity<ResponseUser> changeEditStatusByEmail(@PathVariable String uemail,
//			@PathVariable String receiver, @PathVariable String did) {
//		return ResponseEntity.ok(es.changeEditStatusByEmail(uemail, did));
//	}

//	@GetMapping("/get-edit-status/{id}/{uid}")
//	public ResponseEntity<ResponseUser> getEditStatus(@PathVariable("id") String did, @PathVariable String uid) {
//		return ResponseEntity.ok(es.getEditStatus(uid, did));
//	}
//
//	@PutMapping("/stop-edit-save-status/{uid}")
//	public ResponseEntity<Map<String, Boolean>> stopEditAndSaveStatus(@RequestBody SingleAccessRequest accessRequest) {
//		return ResponseEntity.ok(es.stopEditAndSaveStatus(accessRequest.getOwnerUid(), accessRequest.getDocId()));
//	}
}
