package com.project.filehandler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.filehandler.entity.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

}
