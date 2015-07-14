package app.models;

import javax.validation.constraints.*;

import org.hibernate.validator.constraints.NotEmpty;

public class Person {

	@NotEmpty
	@Size(max=100)
	private String name;
	
	@Min(1)
	private int age;

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
}
