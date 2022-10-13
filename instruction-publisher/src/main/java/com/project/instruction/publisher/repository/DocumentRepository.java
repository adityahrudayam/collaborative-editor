package com.project.instruction.publisher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.instruction.publisher.entity.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

}
