package com.dw.drone.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dw.drone.service.MainService;

/*
 * 프론트를 이클립스에서 관리하면 Controller ex) JSP
 * 프론트가 분리되어 있으면 RestController ex) React, Angular ...
 */
//페이지 호출할 때는 RestController(X), Controller(O)
@Controller
public class MainController {

	@Autowired
	private MainService mainService;
	
	//throws Exception 모든 오류를 다 잡음(try/catch기능)
	@GetMapping("/home")
	public String loadHomePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		/*
		 * return 타입은 String
		 * html 페이지 이름을 리턴
		 */
		
		//* 세션 == 데이터베이스 (임시 저장하는 데이터)
		HttpSession session = request.getSession();
		//세션 키를 이용해서 데이터를 가져옴
		if(session.getAttribute("Korea") == null) { //세션에 데이터가 없을때
			//1. 로그인 없이 바로 /home 경로로 들어올 때
			//2. 세션 만료 되었을때(스프링 default 1시간)
			//tip. 세션처리는 interceptor에서 전역으로 처리하자! 
			//지금은 연습이라 메서드로 함. emp-interceptor에 있음..
			
			response.sendRedirect("/");
		}else {
			boolean sessionData = (boolean) session.getAttribute("Korea");
			if(!sessionData) {
				response.sendRedirect("/");
			}
		}
		return "index";
	}
	
	@GetMapping("/")
	public String loadIndexPage() {
		return "login";
	}
	
	@GetMapping("/drone/detail/{uuid}")
	public String loadDetailPage(@PathVariable int uuid, ModelMap map) {
		//ModelMap은 주소로 넘어오는 @PathVariable or @RequestParam 데이터를 View(HTML)에 전달한다.
		map.addAttribute("droneUUID", uuid);
		return "detail";
	}
	
	
	//제이슨으로 변환한 값을 받기위해 @ResponseBody 를 썼다.
	//@ResponseBody는 어디에 써도 상관이 없다. @GetMapping 밑에 써도 괜찮다. 개발자 취향 차이
	@PostMapping("/valid-recaptcha")
	public @ResponseBody Boolean validRecaptcha(HttpServletRequest request, HttpSession session){
		String recaptchaResponse = request.getParameter("g-recaptcha-response");
		boolean isRecaptcha = mainService.verifyRecaptcha(recaptchaResponse);
		if(isRecaptcha) {
			//session에 데이터 추가
			//세션은 메모리에 데이터를 저장
			session.setAttribute("Korea", true);
		}else {
			session.setAttribute("Korea", false); 
		}
		
		return isRecaptcha;
	}
	
	
	@GetMapping("/join")
	public String callJoinPage() {
		return "join";
	}
	
}