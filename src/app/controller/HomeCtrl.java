package app.controller;

import app.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
 

import javax.net.ssl.HttpsURLConnection;
import javax.naming.*;
import javax.sql.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.apache.log4j.Logger;

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
	public Map<String, Object> callback(@RequestParam("code") String code, 
			                            @RequestParam("session_state") String sessionState) throws IOException
	{
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("success", 1);
		
		String url = "https://www.googleapis.com/oauth2/v3/token";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		
		con.setRequestMethod("POST");
		
		String urlParameters = "code=" + code + "&client_id=67995890535.apps.googleusercontent.com&client_secret=MTnxddiDsRnuLJY6Rp9uCoo7&redirect_uri=http://localhost:8080/webmvc/callback2&grant_type=authorization_code";
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
		
		m.put("response", response.toString());
		
		return m;
	}
	
	@RequestMapping("/callback2")
	@ResponseBody
	public String callback2()
	{
		return "success";
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
