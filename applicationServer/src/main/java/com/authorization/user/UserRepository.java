package com.authorization.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.authorization.connection.UserConnection;

public interface UserRepository extends JpaRepository<User, Long> {

	User findBySocial(UserConnection userConnection);
}
