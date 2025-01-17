package com.hana4.keywordhanaro.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hana4.keywordhanaro.exception.AccountNotFoundException;
import com.hana4.keywordhanaro.model.dto.AccountDto;
import com.hana4.keywordhanaro.model.entity.Bank;
import com.hana4.keywordhanaro.model.entity.account.Account;
import com.hana4.keywordhanaro.model.mapper.AccountMapper;
import com.hana4.keywordhanaro.repository.AccountRepository;
import com.hana4.keywordhanaro.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public AccountDto getAccount(Long id) throws AccountNotFoundException {
		return AccountMapper.toDto(accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException("cannot find account by id")));
	}

	@Override
	public List<AccountDto> getAccounts() {
		return accountRepository.findAll().stream().map(AccountMapper::toDto).toList();
	}

	@Override
	public boolean checkPassword(String accountNumber, String password) throws AccountNotFoundException {
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountNotFoundException("cannot find account by accountNumber"));

		return passwordEncoder.matches(password, account.getPassword());
	}

	@Override
	public List<AccountDto> getAccountsByUsername(String username) {
		return accountRepository.findAllByUserUsername(username).stream().map(AccountMapper::toDto).toList();
	}

	@Override
	public Map<String, Object> checkAccountNumberAndBank(String accountNumber, Short bankId) throws AccountNotFoundException {
		accountNumber = accountNumber.replaceAll("-", "");
		Account account = accountRepository.findByAccountNumberAndBankId(accountNumber, bankId)
			.orElseThrow(() -> new AccountNotFoundException("cannot find account by accountNumber"));
		Map<String, Object> response = new HashMap<>();
		response.put("name", account.getUser().getName());
		return response;
	}
}
