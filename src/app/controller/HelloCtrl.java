package app.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import sun.net.www.content.audio.x_aiff;
import sun.text.normalizer.ICUBinary.Authenticate;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.sun.javafx.collections.MappingChange.Map;
import com.sun.mail.handlers.message_rfc822;

@Controller
public class HelloCtrl {
	
	@RequestMapping(value = { "/", "/welcome**" }, method = RequestMethod.GET)
	public ModelAndView welcomePage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security Custom Login Form");
		model.addObject("message", "This is welcome page!");
		model.setViewName("hello");
		
		return model;
	}
	
	@RequestMapping(value = "/admin**", method = RequestMethod.GET)
	public ModelAndView adminPage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security Custom Login Form");
		model.addObject("message", "This is protected page!");
		model.setViewName("admin");

		return model;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(@RequestParam(value = "login_error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout) {

		ModelAndView model = new ModelAndView();
		if (error != null) {
			model.addObject("error", "Invalid username and password!");
		}

		if (logout != null) {
			model.addObject("msg", "You've been logged out successfully.");
		}
		model.setViewName("login");

		return model;
	}
	
	@RequestMapping(value = "/auth", method = RequestMethod.GET)
	@ResponseBody
	public HashMap<String, Object> auth() {
		HashMap<String, Object> m = new HashMap<String, Object>();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> c = authentication.getAuthorities();
		
		for (GrantedAuthority o : c) {
			m.put(authentication.getName(), o.getAuthority());
		}
		
		return m;
	}
}
