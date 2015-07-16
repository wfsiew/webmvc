package app.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
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
		
		StringBuffer sb = new StringBuffer("https://accounts.google.com/o/oauth2/auth");
		sb.append("?response_type=code")
		.append("&client_id=767995890535.apps.googleusercontent.com")
		.append("&redirect_uri=http://localhost/webmvc/callback")
		.append("&scope=openid%20profile%20email")
		.append("&login_hint=email");
		
		model.addObject("uri", sb.toString());
		
		model.setViewName("login");

		return model;
	}
	
	@RequestMapping(value = "/auth", method = RequestMethod.GET)
	@ResponseBody
	public HashMap<String, Object> auth() {
		HashMap<String, Object> m = new HashMap<String, Object>();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> c = authentication.getAuthorities();
		ArrayList<String> l = new ArrayList<String>();
		
		for (GrantedAuthority o : c) {
			l.add(o.getAuthority());
		}
		
		String roles = String.join(",", l);
		m.put(authentication.getName(), roles);
		
		return m;
	}
}
