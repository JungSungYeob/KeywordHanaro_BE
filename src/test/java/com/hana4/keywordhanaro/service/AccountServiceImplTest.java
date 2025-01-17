package com.hana4.keywordhanaro.service;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hana4.keywordhanaro.exception.AccountNotFoundException;
import com.hana4.keywordhanaro.model.entity.account.Account;
import com.hana4.keywordhanaro.repository.AccountRepository;

@SpringBootTest
class AccountServiceImplTest {

	@Autowired
	private AccountService accountService;

	@Autowired
	AccountRepository accountRepository;

	@Test
	void checkPasswordTest() throws AccountNotFoundException {
		assertThat(accountService.checkPassword("1231-1231-1231", "1234")).isTrue();
	}

	@Test
	void checkAccountNumberAndBankTest() throws AccountNotFoundException {
		Account account = accountRepository.findByAccountNumber("9999999").get();
		// BDDMockito.given(accountRepository.findUserUsernameByAccountNumberAndBank(accountNumber, bank))
		// 	.willReturn(Optional.of("Kim Hana"));

		Map<String, Object> response = new HashMap<>();
		response.put("name", "남인우");
		assertThat(accountService.checkAccountNumberAndBank(account.getAccountNumber(), account.getBank().getId())).isEqualTo(response);
	}
}
