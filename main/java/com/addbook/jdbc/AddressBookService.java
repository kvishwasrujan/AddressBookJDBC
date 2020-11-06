package com.addbook.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vishw
 *
 */
public class AddressBookService<AddressBookDBServiceNew> {
	public enum IOService {
		DB_IO, REST_IO
	}
	private static Map<String, Integer> contactByCity = null;
	private static final Object contact = null;
	private static List<Contact> contactList;
	private static AddressBookDBService addressBookDBService;

	public AddressBookService(List<Contact> contactList) {
		this();
		this.contactList = contactList;
	}

	public AddressBookService() {
		addressBookDBService = AddressBookDBService.getInstance();
	}

	public static List<Contact> readContactData() {
		contactList = addressBookDBService.readData();
		return contactList;
	}

	public void updateContactDetails(String name, String address) {
		int result = addressBookDBService.updateEmployeeData(name, address);
		if (result == 0)
			return;
		Contact Contact = this.getContactData(name);
		if (Contact != null)
			Contact.address = address;
	}

	static Contact getContactData(String name) {
		return contactList.stream().filter(contact -> contact.firstName.equals(name)).findFirst().orElse(null);
	}

	public static boolean checkContactDetailsInSyncWithDB(String name) {
		List<Contact> contactList = addressBookDBService.getcontactData(name);
		return contactList.get(0).equals(getContactData(name));
	}

	public List<Contact> readContactDataForDateRange(LocalDate startDate, LocalDate endDate) {
		this.contactList = addressBookDBService.getContactForDateRange(startDate, endDate);
		return contactList;
	}

	public Map<String, Integer> readContactByCityOrState() {
		this.contactByCity = addressBookDBService.getContactByCity();
		return contactByCity;
	}


	public static Contact addNewContact(String date, String firstName, String lastName, String address, String city,
			String state, String zip, String phoneNo, String email)  {
		return AddressBookDBService.insertNewContactToDB(date, firstName, lastName, address, city, state, zip,
				phoneNo, email);
	}

	public static void addNewMultipleContacts(List<Contact> contacts) {
		Map<Integer, Boolean> status = new HashMap<>();
		contacts.forEach(contact -> {
			status.put(contact.hashCode(), false);
			Runnable task = () -> {
				 AddressBookDBService.insertNewContactToDB("2020-10-30", contact.getFirstName(),
							contact.getLastName(), contact.getAddress(), contact.getCity(), contact.getState(),
							contact.getZip(), contact.getPhoneNumber(), contact.getEmail());
					status.put(contact.hashCode(), true);
				
			};
			Thread thread = new Thread(task, contact.getFirstName());
			thread.start();
		});
		while (status.containsValue(false))
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
	}

	public void addEmployee(Contact contactJson, IOService ioService) {
		if (ioService.equals(IOService.DB_IO))
			this.addNewContact(contactJson.date,contactJson.firstName,contactJson.lastName,contactJson.address,contactJson.city,
					contactJson.state,contactJson.zip,contactJson.phoneNumber,contactJson.email);
		 contactList.add(contactJson);
		
	}

	public static long countEntries(IOService ioService) {
		return contactList.size();
	}

	public  void deleteEmployeePayroll(String firstName, IOService ioService) {
		if(ioService.equals(IOService.REST_IO))
			Contact contact = this.getContactData(firstName);
		    contactList.remove(contact);

		
	}
}
