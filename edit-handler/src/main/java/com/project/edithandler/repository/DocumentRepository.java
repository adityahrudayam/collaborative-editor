package com.project.edithandler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.edithandler.entity.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

}
