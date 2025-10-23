package com.spring.getready.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.getready.model.UserDetail;

@Repository
public interface UserDetailRepository extends JpaRepository<UserDetail, Integer> {

	UserDetail findByEmailEquals(String email);
	
	List<UserDetail> findByEmailNot(String email);
	
	UserDetail findByUserUuidEquals(String uuid);
	
	List<UserDetail> findByUserGroupShortGroupEquals(String shortGroup);
	
}
