package com.hana4.keywordhanaro.service;

import java.time.LocalDate;
import java.util.List;

import com.hana4.keywordhanaro.model.dto.TransactionDTO;

public interface InquiryService {
	List<TransactionDTO> getAccountTransactions(Long accountId, LocalDate startDate, LocalDate endDate,
		String transactionType, String sortOrder, String searchWord);
}
