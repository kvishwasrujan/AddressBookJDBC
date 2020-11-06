package com.addbook.jdbc;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import com.addbook.jdbc.AddressBookService.IOService;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AddressBookTest {
	@Test
	public void contactsWhenRetrievedFromDB_ShouldMatchCount() {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> contactList = addressBookService.readContactData();
		Assert.assertEquals(2, contactList.size());
	}

	@Test
	public void givenNewSalaryForEmployee_WhenUpdatedUsingPreparedStatement_ShouldSyncWithDB() {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> contactList = addressBookService.readContactData();
		addressBookService.updateContactDetails("srujan", "konda");
		boolean result = addressBookService.checkContactDetailsInSyncWithDB("srujan");
		Assert.assertTrue(result);
	}

	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		LocalDate startDate = LocalDate.of(2018, 02, 06);
		LocalDate endDate = LocalDate.now();
		List<Contact> contactList = addressBookService.readContactDataForDateRange(startDate, endDate);
		Assert.assertEquals(2, contactList.size());
	}

	@Test
	public void givenContacts_RetrieveNumberOfContacts_ByCityOrState() {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		Map<String, Integer> contactByCityMap = addressBookService.readContactByCityOrState();
		Integer count = 2;
		Assert.assertEquals(count, contactByCityMap.get("hnk"));
	}

	@Test
	public void givenNewContact_WhenAdded_ShouldSyncWithDB() {
		AddressBookService.addNewContact("2018-08-08", "srujan", "konda", "Gandhinagar", "Hnk", "wgl", "873485",
				"7289472389", "vishwasr@gmail.com");
		boolean isSynced = AddressBookService.checkContactDetailsInSyncWithDB("srujan");
		assertTrue(isSynced);
	}

	@Test
	public void givenMultipeContacts_WhenAddedToDBWithMultiThreads_ShouldSyncWithDB() {
		List<Contact> contacts = new ArrayList<>() {
			{
				add(new Contact("srujan", "konda", "gandhinagar", "hnk", "wgl", "682011", "8725120000",
						"vishwasr@gmail.com"));
				add(new Contact("rohith", "goud", "bank colony", "karimanagar", "telangana", "683022", "8725120022",
						"rohithgoud@gmail.com"));
			}
		};
		AddressBookService.addNewMultipleContacts(contacts);
		List<Contact> contactList = AddressBookService.readContactData();
		Assert.assertEquals(7, contactList.size());
	}

	@Before
	public void setUp() {
		RestAssured.baseURI = "https://localhost";
		RestAssured.port = 3000;
	}

	@Test
	public void givenEmployee_readFromJsonServer_ShouldMatch() {
		AddressBookService addressBookService;
		Contact[] arrayOfContacts = getContactList();
		addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
		Contact contactJson = new Contact("2018-08-08", "srujan", "konda", "gandhinagar", "Hnk", "wgl", "873485",
				"7289472389", "vishwasr@gmail.com", "Friends");
		Response response1 = addEmployeeToJsonServer(contactJson);
		int statusCode = response1.getStatusCode();
		Assert.assertEquals(201, statusCode);
		addressBookService.add(contactJson, IOService.REST_IO);
		long entries = addressBookService.countEntries(IOService.REST_IO);
		Assert.assertEquals(2, entries);
	}

	private Response addEmployeeToJsonServer(Contact contactJson) {
		String empJson = new Gson().toJson(contactJson);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		return request.post("/contactsDB");
	}

	private Contact[] getContactList() {
		Response response = RestAssured.get("/contacts");
		System.out.println("Contacts entries in JSONserver" + response.asString());
		Contact[] arrayOfcontacts = new Gson().fromJson(response.asString(), Contact[].class);
		return arrayOfcontacts;
	}

	@Test
	public void givenMultipleEmployees_readFromJsonServer_ShouldMatch() {
		AddressBookService addressBookService;
		Contact[] arrayOfContacts = getContactList();
		addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
		Contact[] arrayOfContactsJson = {
				new Contact("2018-08-08", "aarush", "Reddy", "Naimnagar", "Hnk", "wgl", "98765", "9356237239",
						"aarush@gmail.com", "Friends"),
				new Contact("2018-02-02", "Anil", "Varma", "Bank Colony", "karimnagar", "telangana", "8738299",
						"1234567890", "anilvarma@gmail.com", "Family"),
				new Contact("2018-05-01", "Krishna", "Burra", "Madhavnagar", "bhopal", "Madhya pradesh", "6343738",
						"9087654321", "krishnaburra@gmail.com", "Friends")

		};
		for (Contact contact : arrayOfContactsJson) {
			Response response = addEmployeeToJsonServer(contact);
			int statusCode = response.getStatusCode();
			Assert.assertEquals(201, statusCode);
			contact = new Gson().fromJson(response.asString(), Contact.class);
			addressBookService.addEmployee(contact, IOService.REST_IO);
		}
		long entries = addressBookService.countEntries(IOService.REST_IO);
		Assert.assertEquals(5, entries);
	}

	@Test
	public void givenNewContactFor_WhenUpdated_ShouldMatch200Respnse() {
		AddressBookService addressBookService;
		Contact[] arrayOfContacts = getContactList();
		addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
		addressBookService.updateContact("srujan", "hnk", IOService.REST_IO);
		Contact contact = addressBookService.getContactData("srujan");
		String empJson = new Gson().toJson(contact);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		Response response = request.put("/contactsDB/" + contact.id);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(200, statusCode);

	}

}
