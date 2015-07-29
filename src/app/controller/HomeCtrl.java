package app.controller;

import app.models.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

import javax.naming.*;
import javax.sql.*;
import javax.validation.Valid;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Controller
@SessionAttributes("googleEmail")
public class HomeCtrl extends AppCtrl
{
	private static Logger logger = Logger.getLogger(HomeCtrl.class);
	
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
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd");
			String dt = df.format(person.getDob());
			m.put("dob", dt);
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
	
	@RequestMapping(value="/list", method=RequestMethod.POST)
	@ResponseBody
	public String list(@RequestBody ArrayList<Integer> list) {
		StringBuffer sb = new StringBuffer();
		
		for (Integer x : list) {
			sb.append("" + x);
		}
		
		return sb.toString();
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
	
	@RequestMapping("/excelfile")
	public HttpEntity<byte[]> getExcelFile()
	{
		HttpEntity<byte[]> o = null;
		
		try
		{
			Workbook wb = new XSSFWorkbook();
			Sheet sheet = wb.createSheet("new");
			
			DataFormat format = wb.createDataFormat();
			short x = format.getFormat("#,##0.00000");
			
			Row row = sheet.createRow(0);
			
			CellStyle cellStyle = wb.createCellStyle();
			cellStyle.setDataFormat(x);
			
			Cell c = row.createCell(0);
			c.setCellStyle(cellStyle);
			c.setCellValue(12345678.12345);
			
			row.createCell(1).setCellValue("Hello");
			
			CreationHelper createHelper = wb.getCreationHelper();
			cellStyle = wb.createCellStyle();
		    cellStyle.setDataFormat(
		        createHelper.createDataFormat().getFormat("yyyy-mm-dd"));
			
			c = row.createCell(2);
			c.setCellStyle(cellStyle);
			c.setCellValue(Calendar.getInstance());
			
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			wb.write(bos);
			byte[] b = bos.toByteArray();
			bos.close();
			
			wb.close();
			
			HttpHeaders header = new HttpHeaders();
		    header.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		    header.set("Content-Disposition",
		    		"attachment; filename=testexcel.xlsx");
		    header.setContentLength(b.length);

		    o = new HttpEntity<byte[]>(b, header);
		}
		
		catch (Exception ex)
		{
			logger.error(ex);
		}
		
		return o;
	}
	
	@RequestMapping("/file")
	public HttpEntity<byte[]> getFile()
	{
		String p = "C:\\nginx-1.9.2\\conf\\nginx.conf";
		HttpEntity<byte[]> o = null;
		
		try
		{
			Path file = Paths.get(p);
			byte[] b = Files.readAllBytes(file);
			
			HttpHeaders header = new HttpHeaders();
		    header.setContentType(new MediaType("application", "text"));
		    header.set("Content-Disposition",
		    		"attachment; filename=abc.txt");
		    header.setContentLength(b.length);

		    o = new HttpEntity<byte[]>(b, header);
		}
		
		catch (Exception ex)
		{
			logger.error(ex);
		}
		
		return o;
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
}
