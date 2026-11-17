package com.auca.department;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Department implements Identifiable, Registrable {
    // Section 3.8: Mandatory 6 declared attributes
    private String departmentId;
    private String departmentName;
    private String departmentCode;
    private String officeLocation;
    private String contactEmail;
    private Teacher headTeacher; // Optional on creation; assignable after Teacher exists

    public Department(String departmentId, String departmentName, String departmentCode,
                      String officeLocation, String contactEmail, Teacher headTeacher) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.officeLocation = officeLocation;
        this.contactEmail = contactEmail;
        this.headTeacher = headTeacher;
    }

    // Overloaded constructor for initial creation without a headTeacher (Constructor chaining)
    public Department(String departmentId, String departmentName, String departmentCode,
                      String officeLocation, String contactEmail) {
        this(departmentId, departmentName, departmentCode, officeLocation, contactEmail, null);
    }

    @Override
    public String getId() {
        return departmentId;
    }

    @Override
    public String getDisplayName() {
        return departmentName + " (" + departmentCode + ")";
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void validate() throws ValidationException {
        if (departmentId == null || departmentId.trim().isEmpty()) {
            throw new ValidationException("Department ID cannot be blank.");
        }
        String cleanId = departmentId.trim();
        if (cleanId.length() < MIN_ID_LENGTH || cleanId.length() > MAX_ID_LENGTH) {
            throw new ValidationException("Department ID must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }

        if (departmentName == null || departmentName.trim().isEmpty()) {
            throw new ValidationException("Department name cannot be blank.");
        }
        String cleanName = departmentName.trim();
        if (cleanName.length() < MIN_NAME_LENGTH || cleanName.length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Department name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
        }

        if (departmentCode == null || departmentCode.trim().isEmpty()) {
            throw new ValidationException("Department code cannot be blank.");
        }
        String cleanCode = departmentCode.trim();
        if (cleanCode.length() < 2 || cleanCode.length() > 15) {
            throw new ValidationException("Department code must be between 2 and 15 characters.");
        }

        if (officeLocation == null || officeLocation.trim().isEmpty()) {
            throw new ValidationException("Office location cannot be blank.");
        }

        if (contactEmail == null || contactEmail.trim().isEmpty()) {
            throw new ValidationException("Contact email cannot be blank.");
        }
        String cleanEmail = contactEmail.trim();
        if (cleanEmail.length() > MAX_EMAIL_LENGTH || !cleanEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Invalid contact email format or length.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========================================");
        System.out.println("DEPARTMENT DETAILS");
        System.out.println("========================================");
        System.out.println("Department ID   : " + departmentId);
        System.out.println("Department Name : " + departmentName);
        System.out.println("Department Code : " + departmentCode);
        System.out.println("Office Location : " + officeLocation);
        System.out.println("Contact Email   : " + contactEmail);
        System.out.println("Head Teacher    : " + (headTeacher != null ? headTeacher.getDisplayName() + " [" + headTeacher.getEmployeeNumber() + "]" : "Not Assigned"));
        System.out.println("========================================");
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + departmentId + "] " + departmentName + " (" + departmentCode + ")";
    }

    // Getters and Setters
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }

    public String getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public Teacher getHeadTeacher() { return headTeacher; }
    public void setHeadTeacher(Teacher headTeacher) { this.headTeacher = headTeacher; }

    @Override
    public String toString() {
        return "Department{id='" + departmentId + "', name='" + departmentName + "', code='" + departmentCode + "'}";
    }
}
