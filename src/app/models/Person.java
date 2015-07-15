package app.models;

import java.sql.Date;

import javax.validation.constraints.*;

import org.hibernate.validator.constraints.NotEmpty;

public class Person {

	@NotEmpty
	@Size(max=100)
	private String name;
	
	@Min(1)
	private int age;
	
	private Date dob;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}
}
