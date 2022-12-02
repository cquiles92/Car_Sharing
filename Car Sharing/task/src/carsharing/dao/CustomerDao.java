package carsharing.dao;

import carsharing.model.Customer;

import java.util.List;

public interface CustomerDao {
    boolean addCustomer(Customer customer);

    boolean ifCustomerExists(Customer customer);

    List<Customer> getCustomerList();

    boolean updateCustomer(Customer customer);
}
