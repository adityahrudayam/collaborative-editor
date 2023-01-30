package com.project.us.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String did;
    @Column(nullable = false)
    private String docName;
    @Column(nullable = false)
    private String docType;
    @Lob
    private byte[] data;
    @ManyToMany(mappedBy = "docs", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_uid", referencedColumnName = "uid", nullable = false, updatable = false)
    private User owner;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "editor_uid", referencedColumnName = "uid")
    private User editor;

    public boolean containsUser(User user) {
        return users.contains(user);
    }

    public void addUser(User user) {
        if (!containsUser(user))
            users.add(user);
    }

    public void addAllUsers(List<User> users) {
        users.forEach(u -> {
            if (!containsUser(u))
                this.users.add(u);
        });
    }

    public Document() {
        System.out.println("Document default constructor called.");
    }

    public Document(String docName, String docType) {
        System.out.println("Document(args) constructor called.");
        this.docName = docName;
        this.docType = docType;
    }

    public Document(String docName, String docType, byte[] data) {
        System.out.println("Document(args) constructor called.");
        this.docName = docName;
        this.docType = docType;
        this.data = data;
    }

    public Document(String did, String docName, String docType, byte[] data) {
        System.out.println("Document(args) constructor called.");
        this.did = did;
        this.docName = docName;
        this.docType = docType;
        this.data = data;
    }

    public Document(String docName, String docType, byte[] data, Set<User> users) {
        System.out.println("Document(args) constructor called.");
        this.docName = docName;
        this.docType = docType;
        this.data = data;
        this.users = users;
    }

    public Document(String did, String docName, String docType, byte[] data, Set<User> users) {
        System.out.println("Document(args) constructor called.");
        this.did = did;
        this.docName = docName;
        this.docType = docType;
        this.data = data;
        this.users = users;
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
        Document other = (Document) obj;
        return Objects.equals(did, other.did);
    }

    @Override
    public String toString() {
        return "Document [did=" + did + ", docName=" + docName + ", docType=" + docType + "]";
    }

}
