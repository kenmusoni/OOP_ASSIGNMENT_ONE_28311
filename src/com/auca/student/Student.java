package com.auca.student;

import com.auca.abstracts.AcademicMember;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;

import java.time.LocalDate;

public class Student extends AcademicMember {
    // Section 3.5: Mandatory 6 declared attributes
    private String studentNumber;
    private String programName;
    private LocalDate admissionDate;
    private int studyYear;
    private String guardianPhone;
    private boolean scholarshipStatus;

    public Student(String personId, String firstName, String lastName,
                   String email, String phoneNumber, LocalDate dateOfBirth,
                   String memberNumber, Department department, LocalDate joinDate,
                   String academicStatus, String institutionalEmail, String advisorName,
                   String studentNumber, String programName, LocalDate admissionDate,
                   int studyYear, String guardianPhone, boolean scholarshipStatus) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth,
              memberNumber, department, joinDate, academicStatus, institutionalEmail, advisorName);
        this.studentNumber = studentNumber;
        this.programName = programName;
        this.admissionDate = admissionDate;
        this.studyYear = studyYear;
        this.guardianPhone = guardianPhone;
        this.scholarshipStatus = scholarshipStatus;
    }

    @Override
    public String getRoleDescription() {
        return "Student";
    }

    @Override
    public String getAcademicCategory() {
        return "Undergraduate / Enrolled Student";
    }

    @Override
    public void validate() throws ValidationException {
        super.validate(); // Validates Person & AcademicMember attributes

        if (studentNumber == null || studentNumber.trim().isEmpty()) {
            throw new ValidationException("Student number cannot be blank.");
        }
        String cleanStuNum = studentNumber.trim();
        if (cleanStuNum.length() < MIN_ID_LENGTH || cleanStuNum.length() > MAX_ID_LENGTH) {
            throw new ValidationException("Student number must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }

        if (programName == null || programName.trim().isEmpty()) {
            throw new ValidationException("Program name cannot be blank.");
        }
        String cleanProg = programName.trim();
        if (cleanProg.length() < MIN_NAME_LENGTH || cleanProg.length() > 120) {
            throw new ValidationException("Program name must be between " + MIN_NAME_LENGTH + " and 120 characters.");
        }

        if (admissionDate == null) {
            throw new ValidationException("Admission date cannot be null.");
        }
        if (admissionDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Admission date cannot be in the future.");
        }

        if (studyYear < 1 || studyYear > 6) {
            throw new ValidationException("Study year must be between 1 and 6.");
        }

        validatePhoneFormat(guardianPhone, "Guardian phone");
    }

    @Override
    public void printDetails() {
        System.out.println("========================================");
        System.out.println("STUDENT RECORD DETAILS");
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
        System.out.println("Student Number      : " + studentNumber);
        System.out.println("Program Name        : " + programName);
        System.out.println("Admission Date      : " + admissionDate);
        System.out.println("Study Year          : " + studyYear);
        System.out.println("Guardian Phone      : " + guardianPhone);
        System.out.println("Scholarship Status  : " + (scholarshipStatus ? "YES" : "NO"));
        System.out.println("========================================");
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + getPersonId() + " / " + studentNumber + "] " + getDisplayName() + " - " + programName + " (Year " + studyYear + ")";
    }

    // Getters and Setters
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }

    public LocalDate getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }

    public int getStudyYear() { return studyYear; }
    public void setStudyYear(int studyYear) { this.studyYear = studyYear; }

    public String getGuardianPhone() { return guardianPhone; }
    public void setGuardianPhone(String guardianPhone) { this.guardianPhone = guardianPhone; }

    public boolean isScholarshipStatus() { return scholarshipStatus; }
    public void setScholarshipStatus(boolean scholarshipStatus) { this.scholarshipStatus = scholarshipStatus; }

    @Override
    public String toString() {
        return super.toString() + " -> Student{studentNumber='" + studentNumber + "', program='" + programName + "', year=" + studyYear + "}";
    }
}
