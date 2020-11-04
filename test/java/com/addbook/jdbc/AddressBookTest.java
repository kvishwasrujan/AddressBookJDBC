package com.addbook.jdbc;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.addbook.jdbc.AddressBookService;
import com.addbook.jdbc.Contact;

import junit.framework.Assert;

public class AddressBookTest {
	@Test
	public void contactsWhenRetrievedFromDB_ShouldMatchCount() {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> contactList = addressBookService.readContactData();
		Assert.assertEquals(2, contactList.size());
	}
}
