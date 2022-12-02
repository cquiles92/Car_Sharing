package carsharing.daoimpl;

import carsharing.dao.CustomerDao;
import carsharing.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements CustomerDao {

    private final Connection connection;

    public CustomerDaoImpl(Connection connection) {
        this.connection = connection;
        initializeTable();
    }

    @Override
    public boolean addCustomer(Customer customer) {
        boolean ifSuccessful = false;
        String sqlStatement = "INSERT INTO CUSTOMER(name) VALUES(?)";

        if (!ifCustomerExists(customer)) {
            try {
                PreparedStatement statement = connection.prepareStatement(sqlStatement);
                statement.setString(1, customer.getName());
                statement.execute();
                ifSuccessful = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ifSuccessful;
    }

    @Override
    public boolean ifCustomerExists(Customer customer) {
        boolean ifSuccessful = false;
        String sqlStatement = "SELECT * FROM CUSTOMER WHERE NAME = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            statement.setString(1, customer.getName());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String savedCustomerName = resultSet.getString(2);
                if (customer.getName().equals(savedCustomerName)) {
                    ifSuccessful = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ifSuccessful;
    }

    @Override
    public List<Customer> getCustomerList() {
        List<Customer> customers = new ArrayList<>();
        String sqlStatement = "SELECT * FROM CUSTOMER";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                int rentedCarId = resultSet.getInt(3);
                Customer customer = new Customer(name);
                customer.setId(id);
                customer.setRentedCarId(rentedCarId);
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        boolean ifSuccessful = false;
        String sqlStatement = "UPDATE CUSTOMER SET NAME = ?, RENTED_CAR_ID = ? WHERE ID = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            statement.setString(1, customer.getName());
            if (customer.getRentedCarId() == 0) {
                statement.setNull(2, 4);
            } else {
                statement.setInt(2, customer.getRentedCarId());
            }
            statement.setInt(3, customer.getId());
            statement.execute();
            ifSuccessful = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ifSuccessful;
    }

    private void initializeTable() {
        final String sqlCreateTableStatement = "CREATE TABLE IF NOT EXISTS CUSTOMER " +
                "(ID INTEGER NOT NULL AUTO_INCREMENT, " +
                " NAME VARCHAR(255) NOT NULL UNIQUE, " +
                " RENTED_CAR_ID INTEGER NULL," +
                " PRIMARY KEY ( ID )," +
                " FOREIGN KEY (RENTED_CAR_ID)" +
                " REFERENCES CAR(ID))";

        try {
            connection.prepareStatement(sqlCreateTableStatement).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}