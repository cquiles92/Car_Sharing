package carsharing.dao;

import carsharing.model.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyDao {

    boolean addCompany(Company company);

    boolean ifCompanyExists(Company company);

    Optional<Company> getCompany(int companyId);

    List<Company> getAllCompanies();

    boolean updateCompany(Company oldCompany, Company newCompany);

    boolean deleteCompany(Company company);

}
