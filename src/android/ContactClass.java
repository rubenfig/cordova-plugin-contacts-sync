package com.rubenfig.plugin.contacts;

public class ContactClass {
	
	public String name;
	public String lastName;
	public String phone;
	public long id;
	
	public ContactClass(long id, String name, String phone) {
		this.name = name;
		this.phone = phone;
		this.id = id;
	}
}
