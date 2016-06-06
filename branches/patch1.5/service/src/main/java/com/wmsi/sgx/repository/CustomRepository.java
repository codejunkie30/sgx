package com.wmsi.sgx.repository;

import java.io.Serializable;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface CustomRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

	//TODO Change to CrudRepository if paging is not needed
}
