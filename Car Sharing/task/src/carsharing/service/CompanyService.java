package carsharing.service;

import carsharing.dao.CompanyDao;
import carsharing.daoimpl.CompanyDaoImpl;
import carsharing.model.Company;

import java.sql.Connection;
import java.util.List;

public class CompanyService {
    private CompanyDao companyDao;

    public CompanyService(Connection connection) {
        companyDao = new CompanyDaoImpl(connection);
    }

    /**
     * @param companyId
     * @return Returns a Company based off the ID or throws an error
     */
    public Company getCompany(int companyId) {
        return companyDao.getCompany(companyId).orElseThrow();
    }

    /**
     * @return Returns a list of Companies from the Company database.
     */
    public List<Company> getCompanyList() {
        return companyDao.getAllCompanies();
    }

    /**
     * @param companyName
     * @return Creates a Company in the Company database and returns true if it does not exist.
     */
    public boolean createCompany(String companyName) {
        return companyDao.addCompany(new Company(companyName));
    }
}
