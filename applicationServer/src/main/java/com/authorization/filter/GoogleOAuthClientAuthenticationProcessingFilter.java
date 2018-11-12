package com.authorization.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.authorization.connection.UserConnection;
import com.authorization.detail.GoogleUserDetails;
import com.authorization.service.SocialService;
import com.constant.CmmConsts;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Google 인증 sso Filter 등록
 * @author han.yong.woo
 *
 */
public class GoogleOAuthClientAuthenticationProcessingFilter extends OAuth2ClientAuthenticationProcessingFilter {

	private ObjectMapper mapper = new ObjectMapper();
	private SocialService socialService;
	
	public GoogleOAuthClientAuthenticationProcessingFilter(SocialService socialService) {
		super(CmmConsts.GOOGLE_AUTH_URL);
		this.socialService = socialService;
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	/**
	 * Access Token으로 인증 성공 시 호출되는 메서드
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
		final OAuth2AccessToken accessToken = restTemplate.getAccessToken(); // 토큰 정보 가져온다.
		final OAuth2Authentication auth = (OAuth2Authentication) authResult; // 인증된 Client와 User 정보를 가져온다.
		final Object details = auth.getUserAuthentication().getDetails(); // 소셜에서 JSON으로 떨어진 데이터(상세정보)를 저장
		
		final GoogleUserDetails googleDetails = mapper.convertValue(details, GoogleUserDetails.class);
		googleDetails.setAccessToken(accessToken);
		
		final UserConnection userConnection = UserConnection.valueOf(googleDetails); // UserConnection을 userDetails 기반으로 생성
		final UsernamePasswordAuthenticationToken userPasswordToken = socialService.doAuthentication(userConnection); //SocialService를 이용하여 인증절차 진행
		super.successfulAuthentication(request, response, chain, userPasswordToken);
	}

}
