package com.hana4.keywordhanaro.model.mapper;

import com.hana4.keywordhanaro.model.dto.KeywordDto;
import com.hana4.keywordhanaro.model.entity.keyword.Keyword;

public class KeywordMapper {
	public static KeywordDto toDto(Keyword keyword) {
		return KeywordDto.builder()
			.id(keyword.getId())
			.user(keyword.getUser())
			.type(keyword.getType().name())
			.name(keyword.getName())
			.desc(keyword.getDescription())
			.seqOrder(keyword.getSeqOrder())
			.inquiryWord(keyword.getInquiryWord())
			.checkEveryTime(keyword.getCheckEveryTime())
			.amount(keyword.getAmount())
			.groupMember(keyword.getGroupMember())
			.branch(keyword.getBranch())
			.account(AccountMapper.toDTO(keyword.getAccount()))
			.subAccount(AccountMapper.toDTO(keyword.getSubAccount()))
			.isFavorite(keyword.isFavorite())
			.build();
	}
}
