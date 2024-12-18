package com.hana4.keywordhanaro.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.keywordhanaro.model.dto.KeywordDto;
import com.hana4.keywordhanaro.service.KeywordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/keyword")
@RequiredArgsConstructor
public class KeywordController {

	private final KeywordService keywordService;

	@PostMapping
	public ResponseEntity<KeywordDto> createKeyword(@RequestBody KeywordDto keywordDto) {
		return ResponseEntity.ok(keywordService.createKeyword(keywordDto));
	}
}
