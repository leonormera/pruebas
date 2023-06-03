package com.capmation.challenge1;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BankAccountRepository extends CrudRepository<BankAccount, Long>, PagingAndSortingRepository<BankAccount, Long> {
	BankAccount findByIdAndOwner(Long id, String owner);
    Page<BankAccount> findByOwner(String owner, PageRequest amount);
}
