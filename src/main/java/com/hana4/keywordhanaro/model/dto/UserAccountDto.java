package com.hana4.keywordhanaro.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserAccountDto {
	private String id;
	private String name;
}
