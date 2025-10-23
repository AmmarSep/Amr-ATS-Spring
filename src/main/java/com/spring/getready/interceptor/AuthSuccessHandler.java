package com.spring.getready.interceptor;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		handleRedirects(request, response, authentication);
	}

	protected void handleRedirects(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		String targetUrl = null;

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			String role = grantedAuthority.getAuthority();
			if (role.equals("ROLE_ADMIN")) {
				targetUrl = "/admin";
				break;
			} else if (role.equals("ROLE_RECRUITER") || role.equals("ROLE_REC")) {
				targetUrl = "/admin";
				break;
			} else if (role.equals("ROLE_CANDIDATE") || role.equals("ROLE_CAN")) {
				targetUrl = "/recruitment/jobs";
				break;
			} else if (role.equals("ROLE_USER")) {
				targetUrl = "/home";
				break;
			}
		}

		if (targetUrl == null) {
			targetUrl = "/recruitment/jobs";
		}

		if (response.isCommitted()) {
			// debug code here
			return;
		}

		redirectStrategy.sendRedirect(request, response, targetUrl);
	}

//	protected void clearAuthenticationAttributes(HttpServletRequest request) {
//		HttpSession session = request.getSession(false);
//		if (session == null) {
//			return;
//		}
//		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
//	}

}
