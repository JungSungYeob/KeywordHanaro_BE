package com.hana4.keywordhanaro.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.keywordhanaro.model.dto.TransactionDto;
import com.hana4.keywordhanaro.service.InquiryServiceImpl;

@RestController
@RequestMapping("/inquiry")
public class InquiryController {
	private final InquiryServiceImpl inquiryService;

	public InquiryController(InquiryServiceImpl inquiryService) {
		this.inquiryService = inquiryService;
	}

	@GetMapping("/{accountId}")
	public List<TransactionDto> getAccountTransactions(
		@PathVariable Long accountId,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
		@RequestParam(defaultValue = "all") String transactionType,
		@RequestParam(defaultValue = "latest") String sortOrder,
		@RequestParam(required = false) String searchWord) {

		return inquiryService.getAccountTransactions(
			accountId, startDate, endDate, transactionType, sortOrder, searchWord);
	}
}
