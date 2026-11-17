package com.auca.teacher;

import com.auca.abstracts.AcademicMember;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Teacher extends AcademicMember {
    // Section 3.6: Mandatory 6 declared attributes
    private String employeeNumber;
    private String specialization;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String officeNumber;
    private String employmentType;

    public Teacher(String personId, String firstName, String lastName,
                   String email, String phoneNumber, LocalDate dateOfBirth,
                   String memberNumber, Department department, LocalDate joinDate,
                   String academicStatus, String institutionalEmail, String advisorName,
                   String employeeNumber, String specialization, LocalDate hireDate,
                   BigDecimal salary, String officeNumber, String employmentType) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth,
              memberNumber, department, joinDate, academicStatus, institutionalEmail, advisorName);
        this.employeeNumber = employeeNumber;
        this.specialization = specialization;
        this.hireDate = hireDate;
        this.salary = salary;
        this.officeNumber = officeNumber;
        this.employmentType = employmentType;
    }

    @Override
    public String getRoleDescription() {
        return "Teacher / Faculty";
    }

    @Override
    public String getAcademicCategory() {
        return "Academic Faculty (" + specialization + ")";
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();

        if (employeeNumber == null || employeeNumber.trim().isEmpty()) {
            throw new ValidationException("Employee number cannot be blank.");
        }
        String cleanEmpNum = employeeNumber.trim();
        if (cleanEmpNum.length() < MIN_ID_LENGTH || cleanEmpNum.length() > MAX_ID_LENGTH) {
            throw new ValidationException("Employee number must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }

        if (specialization == null || specialization.trim().isEmpty()) {
            throw new ValidationException("Specialization cannot be blank.");
        }

        if (hireDate == null) {
            throw new ValidationException("Hire date cannot be null.");
        }
        if (hireDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Hire date cannot be in the future.");
        }

        if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Teacher salary must be strictly positive.");
        }

        if (officeNumber == null || officeNumber.trim().isEmpty()) {
            throw new ValidationException("Office number cannot be blank.");
        }

        if (employmentType == null ||
            (!employmentType.equalsIgnoreCase("FULL-TIME") &&
             !employmentType.equalsIgnoreCase("PART-TIME") &&
             !employmentType.equalsIgnoreCase("VISITING"))) {
            throw new ValidationException("Employment type must be FULL-TIME, PART-TIME, or VISITING.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========================================");
        System.out.println("TEACHER RECORD DETAILS");
        System.out.println("========================================");
        System.out.println("Person ID           : " + getPersonId());
        System.out.println("Full Name           : " + getDisplayName());
        System.out.println("Email               : " + getEmail());
        System.out.println("Phone               : " + getPhoneNumber());
        System.out.println("Date of Birth       : " + getDateOfBirth());
        System.out.println("Member Number       : " + getMemberNumber());
        System.out.println("Department          : " + getDepartment().getDisplayName());
        System.out.println("Join Date           : " + getJoinDate());
        System.out.println("Academic Status     : " + getAcademicStatus());
        System.out.println("Institutional Email : " + getInstitutionalEmail());
        System.out.println("Advisor Name        : " + getAdvisorName());
        System.out.println("Employee Number     : " + employeeNumber);
        System.out.println("Specialization      : " + specialization);
        System.out.println("Hire Date           : " + hireDate);
        System.out.println("Salary              : " + salary);
        System.out.println("Office Number       : " + officeNumber);
        System.out.println("Employment Type     : " + employmentType);
        System.out.println("========================================");
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + getPersonId() + " / " + employeeNumber + "] " + getDisplayName() + " - " + specialization;
    }

    // Getters and Setters
    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public String getOfficeNumber() { return officeNumber; }
    public void setOfficeNumber(String officeNumber) { this.officeNumber = officeNumber; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    @Override
    public String toString() {
        return super.toString() + " -> Teacher{employeeNumber='" + employeeNumber + "', specialization='" + specialization + "'}";
    }
}
