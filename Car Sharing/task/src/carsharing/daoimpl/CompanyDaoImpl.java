package carsharing.daoimpl;

import carsharing.dao.CompanyDao;
import carsharing.model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompanyDaoImpl implements CompanyDao {

    private final Connection connection;

    public CompanyDaoImpl(Connection connection) {
        this.connection = connection;
        initializeTable();
    }

    @Override
    public boolean addCompany(Company company) {
        boolean ifSuccessful = false;
        String sqlStatement = "INSERT INTO COMPANY(name) VALUES(?)";
        if (!ifCompanyExists(company)) {
            try {
                PreparedStatement statement = connection.prepareStatement(sqlStatement);
                statement.setString(1, company.getName());
                statement.execute();
                ifSuccessful = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ifSuccessful;
    }

    @Override
    public boolean ifCompanyExists(Company company) {
        boolean ifSuccessful = false;
        String sqlStatement = "SELECT * FROM COMPANY WHERE name = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, company.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String savedCompanyName = resultSet.getString(2);
                if (company.getName().equals(savedCompanyName)) {
                    ifSuccessful = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ifSuccessful;
    }

    @Override
    public Optional<Company> getCompany(int companyId) {
        Optional<Company> result = Optional.empty();
        String sqlStatement = "SELECT * FROM COMPANY WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, companyId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int savedId = resultSet.getInt(1);
                String name = resultSet.getString(2);
                Company savedCompany = new Company(name);
                if (companyId == savedId) {
                    result = Optional.of(savedCompany);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<Company> getAllCompanies() {
        List<Company> companies = new ArrayList<>();
        String sqlStatement = "SELECT * FROM COMPANY";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                Company company = new Company(name);
                company.setId(id);
                companies.add(company);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return companies;
    }

    @Override
    public boolean updateCompany(Company oldCompany, Company newCompany) {
        boolean ifSuccessful = false;
        String sqlStatement = "UPDATE COMPANY SET NAME = ? WHERE NAME = ?";
        if (ifCompanyExists(oldCompany)) {
            try {
                PreparedStatement statement = connection.prepareStatement(sqlStatement);
                statement.setString(1, oldCompany.getName());
                statement.setString(2, newCompany.getName());
                statement.execute();
                ifSuccessful = true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return ifSuccessful;
    }

    @Override
    public boolean deleteCompany(Company company) {
        boolean ifSuccessful = false;
        String sqlStatement = "DELETE * FROM COMPANY WHERE name = ?";
        if (ifCompanyExists(company)) {
            try {
                PreparedStatement statement = connection.prepareStatement(sqlStatement);
                statement.setString(1, company.getName());
                statement.execute();
                ifSuccessful = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ifSuccessful;
    }

    private void initializeTable() {
        final String sqlCreateTableStatement = "CREATE TABLE IF NOT EXISTS COMPANY " +
                "(id INTEGER NOT NULL AUTO_INCREMENT, " +
                " name VARCHAR(255) NOT NULL UNIQUE, " +
                " PRIMARY KEY ( id ))";

        try {
            connection.prepareStatement(sqlCreateTableStatement).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
