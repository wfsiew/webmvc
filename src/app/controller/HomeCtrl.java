package app.controller;

import app.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;

import javax.naming.*;
import javax.sql.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeCtrl
{
	@RequestMapping("/users")
	@ResponseBody
	public ArrayList<User> getUsers() throws Exception
	{
		ArrayList<User> l = new ArrayList<User>();
		PreparedStatement ps = null;
		ResultSet res = null;
		Connection con = getConnection();
		
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
			ex.printStackTrace();
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
	public ModelAndView home() throws SQLException
	{
		return new ModelAndView("home");
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addUser(@RequestBody User user)
	{
		log("id = " + user.getId());
		log("useremail = " + user.getUserEmail());
		
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
	
	private Connection getConnection()
	{
		String dscontext = "java:/MSSQLDS";
		
		Connection con = null;
		
		try
		{
			Context context = new InitialContext();
			DataSource dataSource = (DataSource) context.lookup(dscontext);
			
			if (dataSource != null)
				con = dataSource.getConnection();
			
			else
				log("Failed to lookup datasource.");
		}
		
		catch (NamingException ex)
		{
			log("Cannot get connection: " + ex);
		}
		
		catch (SQLException ex)
		{
			log("Cannot get connection: " + ex);
		}
		
		return con;
	}
	
	private void log(String a)
	{
		System.out.println(a);
	}
}
