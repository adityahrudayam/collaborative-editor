package com.project.us.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String uid;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "user_docs", joinColumns = @JoinColumn(name = "uid", referencedColumnName = "uid", nullable = false), inverseJoinColumns = @JoinColumn(referencedColumnName = "did", name = "did"))
    private Set<Document> docs = new HashSet<>();
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Document> ownedDocs = new HashSet<>();
    @OneToOne(mappedBy = "editor", cascade = CascadeType.ALL)
    private Document editingDoc;

    public boolean containsDocument(Document doc) {
        return docs.contains(doc);
    }

    public void addDocument(Document doc) {
        if (!containsDocument(doc))
            docs.add(doc);
    }

    public void removeDocument(Document doc) {
        if (!containsDocument(doc))
            return;
        docs.remove(doc);
    }

    public User() {
        System.out.println("User default constructor called.");
    }

    public User(String name, String username, String email, String password) {
        super();
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String uid, String name, String username, String email, String password) {
        super();
        this.uid = uid;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String name, String username, String email, String password, Set<Document> docs) {
        super();
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.docs = docs;
    }

    public User(String uid, String name, String username, String email, String password, Set<Document> docs) {
        super();
        this.uid = uid;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.docs = docs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return Objects.equals(uid, other.uid);
    }

    @Override
    public String toString() {
        return "User [uid=" + uid + ", name=" + name + ", username=" + username + ", email=" + email + ", password="
                + password + "]";
    }

}
