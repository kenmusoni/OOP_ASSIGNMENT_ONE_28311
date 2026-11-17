package com.auca.abstracts;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;

import java.time.LocalDate;

public abstract class AcademicMember extends Person {
    // Section 3.4: Mandatory 6 declared attributes
    private String memberNumber;
    private Department department;
    private LocalDate joinDate;
    private String academicStatus;
    private String institutionalEmail;
    private String advisorName;

    // Parameterized constructor chaining to Person via super(...)
    public AcademicMember(String personId, String firstName, String lastName,
                          String email, String phoneNumber, LocalDate dateOfBirth,
                          String memberNumber, Department department, LocalDate joinDate,
                          String academicStatus, String institutionalEmail, String advisorName) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.memberNumber = memberNumber;
        this.department = department;
        this.joinDate = joinDate;
        this.academicStatus = academicStatus;
        this.institutionalEmail = institutionalEmail;
        this.advisorName = advisorName;
    }

    // Section 4: Required abstract method
    @Override
    public abstract String getAcademicCategory();

    @Override
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(academicStatus);
    }

    @Override
    public void validate() throws ValidationException {
        // Validate inherited Person attributes first
        super.validate();

        if (memberNumber == null || memberNumber.trim().isEmpty()) {
            throw new ValidationException("Member number cannot be blank.");
        }
        String cleanMemberNum = memberNumber.trim();
        if (cleanMemberNum.length() < MIN_ID_LENGTH || cleanMemberNum.length() > MAX_ID_LENGTH) {
            throw new ValidationException("Member number must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }

        if (department == null) {
            throw new ValidationException("Academic member must belong to an existing Department.");
        }

        if (joinDate == null) {
            throw new ValidationException("Join date cannot be null.");
        }
        if (joinDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Join date cannot be in the future.");
        }

        if (academicStatus == null ||
            (!academicStatus.equalsIgnoreCase("ACTIVE") && !academicStatus.equalsIgnoreCase("INACTIVE"))) {
            throw new ValidationException("Academic status must be ACTIVE or INACTIVE.");
        }

        validateEmailFormat(institutionalEmail, "Institutional email");

        if (advisorName == null || advisorName.trim().isEmpty()) {
            throw new ValidationException("Advisor name cannot be blank.");
        }
        String trimmedAdvisor = advisorName.trim();
        if (trimmedAdvisor.length() < MIN_NAME_LENGTH || trimmedAdvisor.length() > 120) {
            throw new ValidationException("Advisor name must be between " + MIN_NAME_LENGTH + " and 120 characters.");
        }
    }

    @Override
    public String getRegistrationSummary() {
        String deptLabel = (department != null) ? department.getDepartmentName() : "No Department";
        return "[" + memberNumber + "] " + getDisplayName() + " | " + getAcademicCategory() + " | Dept: " + deptLabel;
    }

    // Getters and Setters
    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public String getAcademicStatus() {
        return academicStatus;
    }

    public void setAcademicStatus(String academicStatus) {
        this.academicStatus = academicStatus;
    }

    public String getInstitutionalEmail() {
        return institutionalEmail;
    }

    public void setInstitutionalEmail(String institutionalEmail) {
        this.institutionalEmail = institutionalEmail;
    }

    public String getAdvisorName() {
        return advisorName;
    }

    public void setAdvisorName(String advisorName) {
        this.advisorName = advisorName;
    }

    @Override
    public String toString() {
        return super.toString() + " -> AcademicMember{memberNumber='" + memberNumber +
               "', department=" + (department != null ? department.getDepartmentCode() : "null") +
               ", status='" + academicStatus + "'}";
    }
}
