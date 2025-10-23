package com.spring.getready.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

	@RequestMapping("/error")
	public String handleError(HttpServletRequest request, Model model) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
		Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		
		model.addAttribute("status", status);
		model.addAttribute("error", request.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE));
		model.addAttribute("message", message);
		model.addAttribute("path", request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
		
		if (exception instanceof Throwable) {
			StringBuilder trace = new StringBuilder();
			Throwable throwable = (Throwable) exception;
			trace.append(throwable.toString()).append("\n");
			for (StackTraceElement element : throwable.getStackTrace()) {
				trace.append("\tat ").append(element.toString()).append("\n");
			}
			model.addAttribute("trace", trace.toString());
		}
		
		return "error";
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

}
