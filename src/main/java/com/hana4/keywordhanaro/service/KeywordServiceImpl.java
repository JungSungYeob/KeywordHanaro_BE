package com.hana4.keywordhanaro.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hana4.keywordhanaro.exception.InvalidRequestException;
import com.hana4.keywordhanaro.model.dto.KeywordDto;
import com.hana4.keywordhanaro.model.entity.account.Account;
import com.hana4.keywordhanaro.model.entity.keyword.Keyword;
import com.hana4.keywordhanaro.model.entity.keyword.KeywordType;
import com.hana4.keywordhanaro.model.entity.user.User;
import com.hana4.keywordhanaro.model.mapper.KeywordMapper;
import com.hana4.keywordhanaro.repository.AccountRepository;
import com.hana4.keywordhanaro.repository.KeywordRepository;
import com.hana4.keywordhanaro.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {

	private final KeywordRepository keywordRepository;
	private final UserRepository userRepository;
	private final AccountRepository accountRepository;

	private static final Long SEQ_ORDER_INTERVAL = 100L;

	@Override
	public KeywordDto createKeyword(KeywordDto keywordDto) {
		User user = userRepository.findById(keywordDto.getUser().getId())
			.orElseThrow(() -> new NullPointerException("User not found"));

		Account account = null;
		Account subAccount = null;

		validateCommonRequest(keywordDto);

		// 리스트 순서
		Long newSeqOrder = keywordRepository.findTopByUserIdOrderBySeqOrderDesc(keywordDto.getUser().getId())
			.map(keyword -> keyword.getSeqOrder() + SEQ_ORDER_INTERVAL)
			.orElse(SEQ_ORDER_INTERVAL);

		Keyword keyword;

		switch (keywordDto.getType()) {
			case "INQUIRY":
				validateInquiryKeyword(keywordDto);
				account = getAccount(keywordDto.getAccount().getId());
				keyword = new Keyword(user, KeywordType.INQUIRY, keywordDto.getName(), keywordDto.getDesc(),
					newSeqOrder, account, keywordDto.getInquiryWord());
				break;

			case "TRANSFER":
				validateTransferKeyword(keywordDto);
				account = getAccount(keywordDto.getAccount().getId());
				subAccount = getSubAccount(keywordDto.getSubAccount().getAccountNumber());
				keyword = new Keyword(user, KeywordType.TRANSFER, keywordDto.getName(), keywordDto.getDesc(),
					newSeqOrder, account, subAccount, keywordDto.getAmount(), keywordDto.getCheckEveryTime());
				break;

			case "TICKET":
				validateTicketKeyword(keywordDto);
				keyword = new Keyword(user, KeywordType.TICKET, keywordDto.getName(), keywordDto.getDesc(),
					newSeqOrder, keywordDto.getBranch());
				break;

			case "SETTLEMENT":
				validateSettlementKeyword(keywordDto);
				account = getAccount(keywordDto.getAccount().getId());
				keyword = new Keyword(user, KeywordType.SETTLEMENT, keywordDto.getName(), keywordDto.getDesc(),
					newSeqOrder, account, keywordDto.getGroupMember(), keywordDto.getAmount(),
					keywordDto.getCheckEveryTime());
				break;

			default:
				throw new InvalidRequestException("Invalid keyword type");
		}

		keyword = keywordRepository.save(keyword);
		return KeywordMapper.toDto(keyword);
	}

	private Account getAccount(Long accountId) {
		System.out.println("!!!!!!accountRepository = " + accountRepository.findById(accountId));
		return accountRepository.findById(accountId)
			.orElseThrow(() -> new NullPointerException("Account not found"));
	}

	private Account getSubAccount(String accountNumber) {
		return accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new NullPointerException("Receiving account not found"));
	}

	@Override
	public KeywordDto updateKeyword(Long id, KeywordDto keywordDto) {
		Keyword existingKeyword = keywordRepository.findById(id)
			.orElseThrow(() -> new NullPointerException("Keyword not found"));

		// 기본 정보 업데이트
		existingKeyword.setName(keywordDto.getName());
		existingKeyword.setDescription(keywordDto.getDesc());
		existingKeyword.setFavorite(keywordDto.isFavorite());

		// 계좌 정보 업데이트
		if (keywordDto.getAccount() != null) {
			Account account = accountRepository.findById(keywordDto.getAccount().getId())
				.orElseThrow(() -> new NullPointerException("Account not found"));
			existingKeyword.setAccount(account);
		}

		if (keywordDto.getSubAccount() != null) {
			Account subAccount = accountRepository.findByAccountNumber(keywordDto.getSubAccount().getAccountNumber())
				.orElseThrow(() -> new NullPointerException("Account not found"));
			existingKeyword.setSubAccount(subAccount);
		}

		// 타입별 특정 필드 업데이트
		switch (existingKeyword.getType()) {
			case INQUIRY:
				existingKeyword.setInquiryWord(keywordDto.getInquiryWord());
				break;
			case TRANSFER:
				validateAmountAndCheckEveryTime(keywordDto);
				existingKeyword.setAmount(keywordDto.getAmount());
				existingKeyword.setCheckEveryTime(keywordDto.getCheckEveryTime());
				break;
			case TICKET:
				existingKeyword.setBranch(keywordDto.getBranch());
				break;
			case SETTLEMENT:
				validateAmountAndCheckEveryTime(keywordDto);
				existingKeyword.setGroupMember(keywordDto.getGroupMember());
				existingKeyword.setAmount(keywordDto.getAmount());
				existingKeyword.setCheckEveryTime(keywordDto.getCheckEveryTime());
				break;
		}

		Keyword updatedKeyword = keywordRepository.save(existingKeyword);
		return KeywordMapper.toDto(updatedKeyword);
	}

	@Override
	public ResponseEntity<KeywordMapper.DeleteResponse> removeKeyword(Long id) {
		Optional<Keyword> keyword = keywordRepository.findById(id);
		if (keyword.isPresent()) {
			keywordRepository.delete(keyword.get());
			return ResponseEntity.ok(new KeywordMapper.DeleteResponse(true, "Keyword deleted successfully"));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new KeywordMapper.DeleteResponse(false, "Keyword not found"));
		}
	}

	private void validateCommonRequest(KeywordDto keywordDto) {
		if (keywordDto.getType() == null) {
			throw new InvalidRequestException("Keyword type is required");
		}
		if (keywordDto.getName() == null || keywordDto.getName().trim().isEmpty()) {
			throw new InvalidRequestException("Keyword name is required");
		}
		if (keywordDto.getDesc() == null || keywordDto.getDesc().trim().isEmpty()) {
			throw new InvalidRequestException("Keyword description is required");
		}
	}

	private void validateInquiryKeyword(KeywordDto keywordDto) {
		if (keywordDto.getAccount() == null) {
			throw new InvalidRequestException("Account is required for INQUIRY keyword");
		}
		if (keywordDto.getInquiryWord() == null || keywordDto.getInquiryWord().trim().isEmpty()) {
			throw new InvalidRequestException("Inquiry word is required for INQUIRY keyword");
		}
	}

	private void validateTransferKeyword(KeywordDto keywordDto) {
		if (keywordDto.getAccount() == null) {
			throw new InvalidRequestException("Account is required for TRANSFER keyword");
		}
		if (keywordDto.getSubAccount() == null) {
			throw new InvalidRequestException("Sub-account is required for TRANSFER keyword");
		}
		validateAmountAndCheckEveryTime(keywordDto);
	}

	private void validateTicketKeyword(KeywordDto keywordDto) {
		if (keywordDto.getBranch() == null || keywordDto.getBranch().trim().isEmpty()) {
			throw new InvalidRequestException("Branch information is required for TICKET keyword");
		}
	}

	private void validateSettlementKeyword(KeywordDto keywordDto) {
		if (keywordDto.getAccount() == null) {
			throw new InvalidRequestException("Account is required for SETTLEMENT keyword");
		}
		if (keywordDto.getGroupMember() == null || keywordDto.getGroupMember().trim().isEmpty()) {
			throw new InvalidRequestException("Group member information is required for SETTLEMENT keyword");
		}
		validateAmountAndCheckEveryTime(keywordDto);
	}

	private void validateAmountAndCheckEveryTime(KeywordDto keywordDto) {
		if (Boolean.TRUE.equals(keywordDto.getCheckEveryTime()) && keywordDto.getAmount() != null) {
			throw new InvalidRequestException("Amount should not be provided when checkEveryTime is true");
		}
		if (Boolean.FALSE.equals(keywordDto.getCheckEveryTime()) && keywordDto.getAmount() == null) {
			throw new InvalidRequestException("Valid amount is required when checkEveryTime is false");
		}
	}
}
