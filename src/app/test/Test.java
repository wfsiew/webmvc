package app.test;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Test {

	public static void main(String[] args) {
		String ms = "hello";
		
		Properties prop = new Properties();
		prop.setProperty("mail.smtp.host", "mail.redtone.com");
		Session session = Session.getDefaultInstance(prop, null);
		
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("admin@example.com", "Example.com Admin"));
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress("siewwingfei@hotmail.com", "Mr. User"));
            msg.setSubject("Your Example.com account has been activated");
            msg.setText(ms);
            Transport.send(msg);
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
