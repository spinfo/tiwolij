package tiwolij.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Exposes request details to the templates, since Thymeleaf 3.1 no longer
 * provides #httpServletRequest.
 */
@ControllerAdvice
public class RequestInfo {

	@ModelAttribute("requestURI")
	public String requestURI(HttpServletRequest request) {
		return request.getRequestURI();
	}

	@ModelAttribute("contextPath")
	public String contextPath(HttpServletRequest request) {
		return request.getContextPath();
	}

	@ModelAttribute("queryString")
	public String queryString(HttpServletRequest request) {
		return (request.getQueryString() != null) ? request.getQueryString() : "";
	}

}
