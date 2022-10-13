package com.project.us.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.us.entity.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

}
