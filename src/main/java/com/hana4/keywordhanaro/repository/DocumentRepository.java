package com.hana4.keywordhanaro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.keywordhanaro.model.entity.document.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
