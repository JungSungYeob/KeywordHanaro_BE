package com.hana4.keywordhanaro.service;

import com.hana4.keywordhanaro.exception.TicketNotFoundException;
import com.hana4.keywordhanaro.exception.UserNotFoundException;
import com.hana4.keywordhanaro.model.dto.TicketDto;
import com.hana4.keywordhanaro.model.dto.TicketRequestDto;
import com.hana4.keywordhanaro.model.dto.UserDto;

public interface TicketService {
	TicketDto createTicket(TicketRequestDto ticketRequestDto, UserDto userDto) throws Exception;

	void updatePermission(Short location, UserDto userDto) throws UserNotFoundException;

	TicketDto getTicket(Long id) throws TicketNotFoundException;
}
