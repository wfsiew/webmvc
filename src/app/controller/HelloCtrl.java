package app.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.models.GoogleProfile;

@Controller
public class HelloCtrl {
	
	private static Logger logger = Logger.getLogger(HomeCtrl.class);;
	
	@RequestMapping(value = { "/", "/welcome**" }, method = RequestMethod.GET)
	public ModelAndView welcomePage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security Custom Login Form");
		model.addObject("message", "This is welcome page!");
		model.setViewName("hello");
		
		return model;
	}
	
	@PreAuthorize("hasAuthority('role1') and hasAuthority('role2')")
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
	
	@RequestMapping("/callback")
	@ResponseBody
	public ModelAndView callback(@RequestParam("code") String code, 
			                     @RequestParam("session_state") String sessionState,
			                     HttpServletRequest req)
	{
		HashMap<String, Object> h = getAccessToken(code);
		GoogleProfile g = getProfile(h);
		ModelAndView model = new ModelAndView();
		
		if (g.emails.length > 0)
		{
			String email = g.emails[0].value;
			//model.addObject("googleEmail", email);
			
			Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("role1","role2","role3");
			Authentication authentication = new UsernamePasswordAuthenticationToken(email, "###", authorities);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			model.setViewName("redirect:admin");
			
			return model;
		}
		
		model.addObject("profile", g);
		model.setViewName("callback");
		
		return model;
	}
	
	@RequestMapping("/googlelogin")
	public ModelAndView googleLogin()
	{
		ModelAndView model = new ModelAndView();
		model.setViewName("googlelogin");
		
		return model;
	}
	
	@PreAuthorize("hasAuthority('ROLE_ANONYMOUS')")
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
	
	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getAccessToken(String code)
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		
		try
		{
			String url = "https://www.googleapis.com/oauth2/v3/token";
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			
			post.setHeader("User-Agent", "Mozilla/5.0");
			
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("code", code));
			urlParameters.add(new BasicNameValuePair("client_id", "767995890535.apps.googleusercontent.com"));
			urlParameters.add(new BasicNameValuePair("client_secret", "MTnxddiDsRnuLJY6Rp9uCoo7"));
			urlParameters.add(new BasicNameValuePair("redirect_uri", "http://localhost/webmvc/callback"));
			urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
			
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			
			HttpResponse response = client.execute(post);
			
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			ObjectMapper m = new ObjectMapper();
			h = m.readValue(result.toString(), HashMap.class);
		}
		
		catch (Exception ex)
		{
			logger.error(ex);
		}
		
		return h;
	}
	
	private GoogleProfile getProfile(HashMap<String, Object> h)
	{
		GoogleProfile g = null;
		
		try
		{
			String url = "https://www.googleapis.com/plus/v1/people/me?access_token=" + h.get("access_token");
			
			HttpClient client = new DefaultHttpClient();
			HttpGet req = new HttpGet(url);
			
			req.addHeader("User-Agent", "Mozilla/5.0");
			 
			HttpResponse response = client.execute(req);
			
			BufferedReader rd = new BufferedReader(
	                new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			ObjectMapper m = new ObjectMapper();
			g = m.readValue(result.toString(), GoogleProfile.class);
		}
		
		catch (Exception ex)
		{
			logger.error(ex);
		}
		
		return g;
	}
}
