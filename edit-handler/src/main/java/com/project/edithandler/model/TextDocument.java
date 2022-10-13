package com.project.edithandler.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class TextDocument implements Serializable {
	private String did, docName;
	private byte[] data;
	private Set<ResponseUser> usersWithAccess;
	private ResponseUser editor;

	public TextDocument() {
		super();
	}

	public TextDocument(String did, String docName, byte[] data, Set<ResponseUser> usersWithAccess,
			ResponseUser editor) {
		super();
		this.did = did;
		this.docName = docName;
		this.data = data;
		this.usersWithAccess = usersWithAccess;
		this.editor = editor;
	}

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Set<ResponseUser> getUsersWithAccess() {
		return usersWithAccess;
	}

	public void setUsersWithAccess(Set<ResponseUser> usersWithAccess) {
		this.usersWithAccess = usersWithAccess;
	}

	public ResponseUser getEditor() {
		return editor;
	}

	public void setEditor(ResponseUser editor) {
		this.editor = editor;
	}

	@Override
	public int hashCode() {
		return Objects.hash(did);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextDocument other = (TextDocument) obj;
		return Objects.equals(did, other.did);
	}

	@Override
	public String toString() {
		return "TextDocument [did=" + did + ", docName=" + docName + ", editor=" + editor + "]";
	}

}
