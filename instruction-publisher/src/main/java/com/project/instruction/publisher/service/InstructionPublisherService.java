package com.project.instruction.publisher.service;

import com.project.instruction.publisher.entity.Document;
import com.project.instruction.publisher.model.Instruction;
import com.project.instruction.publisher.model.ResponseUser;

public interface InstructionPublisherService {

	String publishDocument(String uid, Document doc);

	String publishInstruction(String uid, Instruction ins);

	ResponseUser changeEditStatusByUsername(String username, String id);

	ResponseUser changeEditStatusByEmail(String email, String id);

	ResponseUser changeEditStatus(String uid, String id);

	ResponseUser getEditStatus(String uid, String id);

	ResponseUser getEditStatusByUsername(String username, String did);

	ResponseUser getEditStatusByEmail(String email, String did);

	// UserDocumentBasic editDocument(Document doc);

	// Map<String, Boolean> stopEditAndSaveStatus(String uid, String id);

}
