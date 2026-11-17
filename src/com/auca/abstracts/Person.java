package com.auca.abstracts;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

import java.time.LocalDate;

public abstract class Person implements Identifiable, Registrable {
    // Section 3.3: Mandatory 6 declared attributes
    private String personId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;

    // Parameterized constructor
    public Person(String personId, String firstName, String lastName,
                  String email, String phoneNumber, LocalDate dateOfBirth) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    // Section 4: Required abstract method
    @Override
    public abstract String getRoleDescription();

    // Identifiable implementations
    @Override
    public String getId() {
        return personId;
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean isActive() {
        return true; // Default active state; overridden by subclasses with status flags
    }

    // Registrable validation implementation (Section 8 rules)
    @Override
    public void validate() throws ValidationException {
        if (personId == null || personId.trim().isEmpty()) {
            throw new ValidationException("Person ID cannot be blank.");
        }
        String cleanId = personId.trim();
        if (cleanId.length() < MIN_ID_LENGTH || cleanId.length() > MAX_ID_LENGTH) {
            throw new ValidationException("Person ID must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }

        validateName(firstName, "First name");
        validateName(lastName, "Last name");
        validateEmailFormat(email, "Email");
        validatePhoneFormat(phoneNumber, "Phone number");

        if (dateOfBirth == null) {
            throw new ValidationException("Date of birth cannot be null.");
        }
        if (!dateOfBirth.isBefore(LocalDate.now())) {
            throw new ValidationException("Date of birth must be in the past.");
        }
    }

    // Protected helper methods so subclasses can reuse standard validation cleanly
    protected void validateName(String value, String fieldLabel) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldLabel + " cannot be blank.");
        }
        String trimmed = value.trim();
        if (trimmed.length() < MIN_NAME_LENGTH || trimmed.length() > MAX_NAME_LENGTH) {
            throw new ValidationException(fieldLabel + " must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
        }
    }

    protected void validateEmailFormat(String value, String fieldLabel) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldLabel + " cannot be blank.");
        }
        String trimmed = value.trim();
        if (trimmed.length() > MAX_EMAIL_LENGTH) {
            throw new ValidationException(fieldLabel + " cannot exceed " + MAX_EMAIL_LENGTH + " characters.");
        }
        if (!trimmed.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Invalid format for " + fieldLabel + ".");
        }
    }

    protected void validatePhoneFormat(String value, String fieldLabel) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldLabel + " cannot be blank.");
        }
        String trimmed = value.trim();
        if (!trimmed.matches("^\\+?[0-9]{" + MIN_PHONE_LENGTH + "," + MAX_PHONE_LENGTH + "}$")) {
            throw new ValidationException(fieldLabel + " must contain 10 to 15 digits with an optional leading '+'.");
        }
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + personId + "] " + getDisplayName() + " (" + getRoleDescription() + ")";
    }

    // Getters and Setters
    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "Person{personId='" + personId + "', name='" + getDisplayName() +
               "', email='" + email + "', phone='" + phoneNumber + "', dob=" + dateOfBirth + "}";
    }
}
