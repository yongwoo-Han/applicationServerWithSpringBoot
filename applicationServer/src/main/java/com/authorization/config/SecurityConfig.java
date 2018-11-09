package com.authorization.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CompositeFilter;

import com.authorization.filter.FacebookOAuthClientAuthenticationProcessingFilter;
import com.authorization.filter.GoogleOAuthClientAuthenticationProcessingFilter;
import com.authorization.resource.ClientResources;
import com.authorization.service.SocialService;

import lombok.AllArgsConstructor;

/**
 * ClientResources에서 properties 정보 가지고 온 것을 빈으로 등록해준다.
 * NestedConfigurationProperty을 이용하여 쉽게 프로퍼티 정보를 가져온다 
 * @author han.yong.woo
 *
 */
@EnableWebSecurity
@EnableOAuth2Client
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private final OAuth2ClientContext oAuth2ClientContext;
    private final SocialService socialService;
    
	/**
	 * http Configuration
	 * 스프링 시큐리티 설정
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		
		
		
		// @formatter:off
		http.csrf().disable();
		
		http.antMatcher("/**").authorizeRequests().antMatchers("/", "/login**").permitAll().anyRequest()
		.authenticated().and().exceptionHandling()
		.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/")).and()
		.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
		// @formatter:on

		//log out
		http.logout()
			.invalidateHttpSession(true)
			.clearAuthentication(true)
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/")
			.permitAll();
	}
	
	@Bean
	@ConfigurationProperties("facebook")
	public ClientResources facebook(){
		return new ClientResources();
	}
	
	@Bean
	@ConfigurationProperties("google")
	public ClientResources google(){
		return new ClientResources();
	}
	
	/**
	 * 인증 요청에 따른 리다이렉션 빈 등록
	 * @return
	 */
	@Bean
	public FilterRegistrationBean<Filter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter){
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
		registration.setFilter(filter);
		registration.setOrder(-100); //Spring Security보다 우선순위를 낮게 설정
		return registration;
	}

	/**
	 * 각각의 정의된 ssoFilter를 filter(Composite pattern)로 setFilter
	 * @return
	 */
	private Filter ssoFilter() {
		// TODO Auto-generated method stub
		CompositeFilter filter = new CompositeFilter();
		List<Filter> filters = new ArrayList<>();
		filters.add(ssoFilter(google(), new GoogleOAuthClientAuthenticationProcessingFilter(socialService))); // 이전에 등록했던 리다이렉트 URL
		filters.add(ssoFilter(facebook(), new FacebookOAuthClientAuthenticationProcessingFilter(socialService)));
		filter.setFilters(filters);
		return filter;
	}

	private Filter ssoFilter(ClientResources client, OAuth2ClientAuthenticationProcessingFilter filters) {
		// TODO Auto-generated method stub
		
		// OAuth2ClientAuthenticationProcessingFilter 필터는 인증 서버에서 OAuth2 엑세스 토큰을 획득하고 인증 객체를 
		// SecurityContext에 로드하는데 사용할 수 있는 OAuth2 클라이언트 필터
		//OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
		OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext); //OAuth2 전용 RestTemplate : Access Token 값을 가져오고 해당 토큰에 사용자 정보를 가져온다.
		filters.setRestTemplate(restTemplate);
		UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId());
		tokenServices.setRestTemplate(restTemplate);
		filters.setTokenServices(tokenServices);
		return filters;
	}
	
	
	
	
	
}
