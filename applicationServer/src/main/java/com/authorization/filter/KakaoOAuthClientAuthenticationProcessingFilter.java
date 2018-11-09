package com.authorization.filter;

import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;

/**
 * Kakao 인증 sso Filter 등록
 * @author han.yong.woo
 *
 */
public class KakaoOAuthClientAuthenticationProcessingFilter extends OAuth2ClientAuthenticationProcessingFilter {

	public KakaoOAuthClientAuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
		// TODO Auto-generated constructor stub
	}

}
