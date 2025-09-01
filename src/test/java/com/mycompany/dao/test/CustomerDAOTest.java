package com.mycompany.dao.test;

import com.mycompany.dao.CustomerDAO;
import com.mycompany.models.Customer;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerDAOTest {

    private CustomerDAO customerDAO;

    @BeforeEach
    void setUp() {
        customerDAO = new CustomerDAO();
    }

    @Test
    void testAddAndFetchCustomer() {
        Customer customer = new Customer();
        customer.setCustomerName("Test User");
        customer.setAddress("123 Test Street");
        customer.setPhone("0771234567");
        customer.setUnitsConsumed(100);

        boolean added = customerDAO.addCustomer(customer);
        assertTrue(added, "Customer should be added successfully");
        assertTrue(customer.getAccountNo() > 0, "Generated accountNo should be set");

        // Fetch by ID
        Customer fetched = customerDAO.getCustomerById(customer.getAccountNo());
        assertNotNull(fetched, "Fetched customer should not be null");
        assertEquals("Test User", fetched.getCustomerName());
    }

    @Test
    void testGetAllCustomers() {
        List<Customer> customers = customerDAO.getAllCustomers();
        assertNotNull(customers, "Should return a list (even if empty)");
    }

    @Test
    void testGetCustomersByName() {
        List<Customer> matches = customerDAO.getCustomersByName("Test");
        assertNotNull(matches);
        // Should at least contain customers with "Test" in their name
        matches.forEach(c -> assertTrue(c.getCustomerName().contains("Test")));
    }

    @Test
    void testUpdateCustomer() {
        // Add new customer first
        Customer customer = new Customer();
        customer.setCustomerName("Update Me");
        customer.setAddress("Old Address");
        customer.setPhone("0779876543");
        customer.setUnitsConsumed(50);
        customerDAO.addCustomer(customer);

        customer.setCustomerName("Updated Name");
        customer.setAddress("New Address");
        customer.setUnitsConsumed(75);

        boolean updated = customerDAO.updateCustomer(customer);
        assertTrue(updated, "Customer should be updated");

        Customer updatedCustomer = customerDAO.getCustomerById(customer.getAccountNo());
        assertEquals("Updated Name", updatedCustomer.getCustomerName());
        assertEquals("New Address", updatedCustomer.getAddress());
    }

    @Test
    void testDeleteCustomer() {
        // Add temporary customer
        Customer temp = new Customer();
        temp.setCustomerName("Temp Delete");
        temp.setAddress("Nowhere");
        temp.setPhone("0780000000");
        temp.setUnitsConsumed(10);
        customerDAO.addCustomer(temp);

        boolean deleted = customerDAO.deleteCustomer(temp.getAccountNo());
        assertTrue(deleted, "Customer should be deleted");

        Customer deletedCustomer = customerDAO.getCustomerById(temp.getAccountNo());
        assertNull(deletedCustomer, "Deleted customer should not be found");
    }

    @Test
    void testPhoneExists() {
        Customer customer = new Customer();
        customer.setCustomerName("Phone Test");
        customer.setAddress("Test City");
        customer.setPhone("0751111111");
        customer.setUnitsConsumed(5);
        customerDAO.addCustomer(customer);

        assertTrue(customerDAO.phoneExists("0751111111"), "Phone number should exist");
        assertFalse(customerDAO.phoneExists("0999999999"), "Non-existing phone should return false");
    }

    @Test
    void testPhoneExistsExcludeId() {
        Customer customer = new Customer();
        customer.setCustomerName("Exclude Test");
        customer.setAddress("Exclude City");
        customer.setPhone("0712222222");
        customer.setUnitsConsumed(12);
        customerDAO.addCustomer(customer);

        // Should return false if checking with same accountNo
        assertFalse(customerDAO.phoneExists("0712222222", customer.getAccountNo()));
        // Should return true if checking without exclusion
        assertTrue(customerDAO.phoneExists("0712222222"));
    }

    @Test
    void testGetCustomerCount() {
        int countBefore = customerDAO.getCustomerCount();

        Customer c = new Customer();
        c.setCustomerName("Count Test");
        c.setAddress("Count Address");
        c.setPhone("0723333333");
        c.setUnitsConsumed(25);
        customerDAO.addCustomer(c);

        int countAfter = customerDAO.getCustomerCount();
        assertTrue(countAfter >= countBefore + 1, "Customer count should increase after adding");
    }
}
