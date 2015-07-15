package app.controller;

import app.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
 





import javax.net.ssl.HttpsURLConnection;
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
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
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
			logger.info(ex);
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
			logger.info(ex);
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
	public ModelAndView callback(@RequestParam("code") String code, 
			                                @RequestParam("session_state") String sessionState) throws IOException
	{
		String url = "https://www.googleapis.com/oauth2/v3/token";
		
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
 
		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		con.setRequestProperty("charset", "utf-8");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		
		StringBuffer sb = new StringBuffer();
		sb.append("code=" + code)
		.append("&client_id=767995890535.apps.googleusercontent.com")
		.append("&client_secret=MTnxddiDsRnuLJY6Rp9uCoo7")
		.append("&redirect_uri=http://localhost/webmvc/callback")
		.append("&grant_type=authorization_code");
 
		String urlParameters = sb.toString();
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		ObjectMapper m = new ObjectMapper();
		HashMap<String, Object> h = m.readValue(response.toString(), HashMap.class);
		
		url = "https://www.googleapis.com/plus/v1/people/me?access_token=" + h.get("access_token");
		
		obj = new URL(url);
		
		con = (HttpsURLConnection) obj.openConnection();
		
		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");

		responseCode = con.getResponseCode();
		
		in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		inputLine = null;
		response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		h = m.readValue(response.toString(), HashMap.class);
		
		ModelAndView model = new ModelAndView();
		HashMap<String, Object> image = (HashMap<String, Object>) h.get("image");
		HashMap<String, Object> name = (HashMap<String, Object>) h.get("name");
		ArrayList<HashMap<String, Object>> emails = (ArrayList<HashMap<String, Object>>) h.get("emails");
		
		model.addObject("image", image.get("url"));
		model.addObject("email", emails.get(0).get("value"));
		model.addObject("name", name.get("givenName").toString() + name.get("familyName").toString());
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
			logger.info("Cannot get connection: " + ex);
		}
		
		catch (SQLException ex)
		{
			logger.info("Cannot get connection: " + ex);
		}
		
		return con;
	}
}
