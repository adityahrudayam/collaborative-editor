package com.project.us.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.us.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);

	List<User> findByEmailStartsWith(String email);

	List<User> findByUsernameStartsWith(String username);

}
