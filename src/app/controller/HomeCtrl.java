package app.controller;

import app.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.naming.*;
import javax.sql.*;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class HomeCtrl
{
	private static Logger logger = Logger.getLogger(HomeCtrl.class);;
	
	private final String ds1 = "java:/MSSQLDS";
	private final String ds2 = "java:/MSSQLDS-120";
	
	@RequestMapping("/customers")
	@ResponseBody
	public ArrayList<Customer> getCustomers() throws Exception
	{
		ArrayList<Customer>  l = new ArrayList<Customer>();
		PreparedStatement ps = null;
		ResultSet res = null;
		Connection con = getConnection(ds2);
		
		try
		{
			String q = "select top 2000 custid, name from customer";
			ps = con.prepareStatement(q);
			res = ps.executeQuery();
			
			while (res.next())
			{
				Customer o = new Customer();
				o.setId(res.getInt(1));
				o.setName(res.getString(2));
				
				l.add(o);
			}
		}
		
		catch (Exception ex)
		{
			logger.error(ex);
		}
		
		finally
		{
			if (res != null)
				res.close();
			
			if (ps != null)
				ps.close();
			
			if (con != null)
				con.close();
		}
		
		return l;
	}
	
	@RequestMapping("/users")
	@ResponseBody
	public ArrayList<User> getUsers() throws Exception
	{
		ArrayList<User> l = new ArrayList<User>();
		PreparedStatement ps = null;
		ResultSet res = null;
		Connection con = getConnection(ds1);
		
		try
		{
			String q = "select id, useremail from [user] where useremail like ?";
			ps = con.prepareStatement(q);
			ps.setString(1, "%iew%");
			res = ps.executeQuery();
			
			while (res.next())
			{
				User o = new User();
				o.setId(res.getInt("id"));
				o.setUserEmail(res.getString("useremail"));
				
				l.add(o);
			}
		}
		
		catch (Exception ex)
		{
			logger.error(ex);
		}
		
		finally
		{
			if (res != null)
				res.close();
			
			if (ps != null)
				ps.close();
			
			if (con != null)
				con.close();
		}
		
		return l;
	}
	
	@RequestMapping("/home")
	public ModelAndView home()
	{
		ModelAndView model = new ModelAndView();
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("success", 1);
		m.put("msg", "hello");
		model.addObject("dic", m);
		model.setViewName("home");
		
		return model;
	}
	
	@RequestMapping(value = "/add/person", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addPerson(@RequestBody @Valid Person person, BindingResult res)
	{
		Map<String, Object> m = new HashMap<String, Object>();
		
		if (res.hasErrors())
		{
			List<FieldError> l = res.getFieldErrors();
			for (FieldError o : l)
			{
				m.put(o.getField(), o.getDefaultMessage());
			}
		}
		
		else
		{
			m.put("age", person.getAge());
			m.put("name", person.getName());
		}
		
		return m;
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addUser(@RequestBody User user)
	{
		//logger.info("id = " + user.getId());
		//logger.info("useremail = " + user.getUserEmail());
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("id", user.getId());
		m.put("useremail", user.getUserEmail());
		
		return m;
	}
	
	@RequestMapping("/data")
	@ResponseBody
	public Map<String, Object> data()
	{
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("success", 1);
		m.put("message", "hello");
		
		return m;
	}
	
	@RequestMapping("/callback")
	@ResponseBody
	public ModelAndView callback(@RequestParam("code") String code, 
			                     @RequestParam("session_state") String sessionState)
	{
		HashMap<String, Object> h = getAccessToken(code);
		GoogleProfile g = getProfile(h);
		
		ModelAndView model = new ModelAndView();
		
		model.addObject("profile", g);
		model.setViewName("callback");
		
		return model;
	}
	
	private Connection getConnection(String ds)
	{
		String dscontext = ds;
		
		Connection con = null;
		
		try
		{
			Context context = new InitialContext();
			DataSource dataSource = (DataSource) context.lookup(dscontext);
			
			if (dataSource != null)
				con = dataSource.getConnection();
			
			else
				logger.info("Failed to lookup datasource.");
		}
		
		catch (NamingException ex)
		{
			logger.error("Cannot get connection: " + ex);
		}
		
		catch (SQLException ex)
		{
			logger.error("Cannot get connection: " + ex);
		}
		
		return con;
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
