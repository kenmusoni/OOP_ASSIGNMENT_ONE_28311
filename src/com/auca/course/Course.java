package com.auca.course;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Course implements Identifiable, Registrable {
    // Section 3.9: Mandatory 6 declared attributes
    private String courseId;
    private String courseCode;
    private String courseTitle;
    private int creditHours;
    private Department department;
    private Teacher assignedTeacher;

    public Course(String courseId, String courseCode, String courseTitle,
                  int creditHours, Department department, Teacher assignedTeacher) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditHours = creditHours;
        this.department = department;
        this.assignedTeacher = assignedTeacher;
    }

    @Override
    public String getId() {
        return courseId;
    }

    @Override
    public String getDisplayName() {
        return courseCode + " - " + courseTitle;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void validate() throws ValidationException {
        if (courseId == null || courseId.trim().isEmpty()) {
            throw new ValidationException("Course ID cannot be blank.");
        }
        String cleanId = courseId.trim();
        if (cleanId.length() < MIN_ID_LENGTH || cleanId.length() > MAX_ID_LENGTH) {
            throw new ValidationException("Course ID must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }

        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new ValidationException("Course code cannot be blank.");
        }
        String cleanCode = courseCode.trim();
        if (cleanCode.length() < 3 || cleanCode.length() > 15) {
            throw new ValidationException("Course code must be between 3 and 15 characters.");
        }

        if (courseTitle == null || courseTitle.trim().isEmpty()) {
            throw new ValidationException("Course title cannot be blank.");
        }
        String cleanTitle = courseTitle.trim();
        if (cleanTitle.length() < MIN_NAME_LENGTH || cleanTitle.length() > 100) {
            throw new ValidationException("Course title must be between " + MIN_NAME_LENGTH + " and 100 characters.");
        }

        if (creditHours < 1 || creditHours > 6) {
            throw new ValidationException("Credit hours must be between 1 and 6.");
        }

        if (department == null) {
            throw new ValidationException("Course must belong to a registered Department.");
        }

        if (assignedTeacher == null) {
            throw new ValidationException("Course must have an assigned Teacher.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========================================");
        System.out.println("COURSE RECORD DETAILS");
        System.out.println("========================================");
        System.out.println("Course ID        : " + courseId);
        System.out.println("Course Code      : " + courseCode);
        System.out.println("Course Title     : " + courseTitle);
        System.out.println("Credit Hours     : " + creditHours);
        System.out.println("Department       : " + department.getDisplayName());
        System.out.println("Assigned Teacher : " + assignedTeacher.getDisplayName());
        System.out.println("========================================");
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + courseId + " / " + courseCode + "] " + courseTitle + " (" + creditHours + " credits)";
    }

    // Getters and Setters
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public int getCreditHours() { return creditHours; }
    public void setCreditHours(int creditHours) { this.creditHours = creditHours; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Teacher getAssignedTeacher() { return assignedTeacher; }
    public void setAssignedTeacher(Teacher assignedTeacher) { this.assignedTeacher = assignedTeacher; }

    @Override
    public String toString() {
        return "Course{id='" + courseId + "', code='" + courseCode + "', title='" + courseTitle + "'}";
    }
}
