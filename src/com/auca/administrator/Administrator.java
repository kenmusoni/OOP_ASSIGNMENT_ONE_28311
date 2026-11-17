package com.auca.administrator;

import com.auca.abstracts.Person;
import com.auca.exceptions.ValidationException;

import java.time.LocalDate;

public class Administrator extends Person {
    // Section 3.7: Mandatory 6 declared attributes
    private String staffNumber;
    private String jobTitle;
    private String assignedOffice;
    private LocalDate employmentDate;
    private int accessLevel;
    private boolean active;

    public Administrator(String personId, String firstName, String lastName,
                         String email, String phoneNumber, LocalDate dateOfBirth,
                         String staffNumber, String jobTitle, String assignedOffice,
                         LocalDate employmentDate, int accessLevel, boolean active) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.staffNumber = staffNumber;
        this.jobTitle = jobTitle;
        this.assignedOffice = assignedOffice;
        this.employmentDate = employmentDate;
        this.accessLevel = accessLevel;
        this.active = active;
    }

    @Override
    public String getRoleDescription() {
        return "Administrator (" + jobTitle + ")";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void validate() throws ValidationException {
        super.validate(); // Validates Person attributes

        if (staffNumber == null || staffNumber.trim().isEmpty()) {
            throw new ValidationException("Staff number cannot be blank.");
        }
        String cleanStaff = staffNumber.trim();
        if (cleanStaff.length() < MIN_ID_LENGTH || cleanStaff.length() > MAX_ID_LENGTH) {
            throw new ValidationException("Staff number must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }

        if (jobTitle == null || jobTitle.trim().isEmpty()) {
            throw new ValidationException("Job title cannot be blank.");
        }

        if (assignedOffice == null || assignedOffice.trim().isEmpty()) {
            throw new ValidationException("Assigned office cannot be blank.");
        }

        if (employmentDate == null) {
            throw new ValidationException("Employment date cannot be null.");
        }
        if (employmentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Employment date cannot be in the future.");
        }

        if (accessLevel < 1 || accessLevel > 5) {
            throw new ValidationException("Access level must be between 1 and 5.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========================================");
        System.out.println("ADMINISTRATOR RECORD DETAILS");
        System.out.println("========================================");
        System.out.println("Person ID       : " + getPersonId());
        System.out.println("Full Name       : " + getDisplayName());
        System.out.println("Email           : " + getEmail());
        System.out.println("Phone           : " + getPhoneNumber());
        System.out.println("Date of Birth   : " + getDateOfBirth());
        System.out.println("Staff Number    : " + staffNumber);
        System.out.println("Job Title       : " + jobTitle);
        System.out.println("Assigned Office : " + assignedOffice);
        System.out.println("Employment Date : " + employmentDate);
        System.out.println("Access Level    : " + accessLevel);
        System.out.println("Active Status   : " + (active ? "ACTIVE" : "INACTIVE"));
        System.out.println("========================================");
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + getPersonId() + " / " + staffNumber + "] " + getDisplayName() + " - " + jobTitle;
    }

    // Getters and Setters
    public String getStaffNumber() { return staffNumber; }
    public void setStaffNumber(String staffNumber) { this.staffNumber = staffNumber; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getAssignedOffice() { return assignedOffice; }
    public void setAssignedOffice(String assignedOffice) { this.assignedOffice = assignedOffice; }

    public LocalDate getEmploymentDate() { return employmentDate; }
    public void setEmploymentDate(LocalDate employmentDate) { this.employmentDate = employmentDate; }

    public int getAccessLevel() { return accessLevel; }
    public void setAccessLevel(int accessLevel) { this.accessLevel = accessLevel; }

    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return super.toString() + " -> Administrator{staffNumber='" + staffNumber + "', jobTitle='" + jobTitle + "'}";
    }
}
