package com.habibi.core.mapper;

import com.habibi.core.dto.AccountDto;
import com.habibi.core.entity.Account;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDto accountToAccountDto(Account account);
    List<AccountDto> accountsToAccountDtos(List<Account> accounts);
}