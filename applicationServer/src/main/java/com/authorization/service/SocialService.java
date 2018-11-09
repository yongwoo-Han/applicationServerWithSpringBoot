package com.authorization.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.authorization.connection.UserConnection;
import com.authorization.user.User;
import com.authorization.user.UserService;

import lombok.AllArgsConstructor;

/**
 * 가입 및 로그인 처리하는 서비스
 * @author han
 *
 */
@Component
@AllArgsConstructor
public class SocialService {

	private final UserService userService;
	
	public UsernamePasswordAuthenticationToken doAuthentication(UserConnection userConnection){
		
		if(userService.isExistUser(userConnection)){
			// 기존 회원일 경우 데이터베이스 조회하여 인증처리
			final User user = userService.findBySocial(userConnection);
			return setAuthentication(user);
		} else {
			// 새 회원일 경우 인증처리
			final User user = userService.signUp(userConnection);
			return setAuthentication(user);
		}
	}
	
	private UsernamePasswordAuthenticationToken setAuthentication(Object user){
		return new UsernamePasswordAuthenticationToken(user, null, getAuthentication("ROLE_USER"));
	}
	
	private Collection<? extends GrantedAuthority> getAuthentication(String role){
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(role));
		return authorities;
	}
}
