package com.addbook.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import junit.framework.Assert;

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

}
