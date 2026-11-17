package com.auca.enrollment;

import com.auca.course.Course;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;

import java.time.LocalDate;

public class Enrollment implements Identifiable, Registrable {
    // Section 3.11: Mandatory 6 declared attributes
    private String enrollmentId;
    private Student student;
    private Course course;
    private LocalDate enrollmentDate;
    private String semester;
    private String enrollmentStatus;

    public Enrollment(String enrollmentId, Student student, Course course,
                      LocalDate enrollmentDate, String semester, String enrollmentStatus) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.semester = semester;
        this.enrollmentStatus = enrollmentStatus;
    }

    @Override
    public String getId() {
        return enrollmentId;
    }

    @Override
    public String getDisplayName() {
        return student.getDisplayName() + " -> " + course.getCourseCode();
    }

    @Override
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(enrollmentStatus);
    }

    @Override
    public void validate() throws ValidationException {
        if (enrollmentId == null || enrollmentId.trim().isEmpty()) {
            throw new ValidationException("Enrollment ID cannot be blank.");
        }
        String cleanId = enrollmentId.trim();
        if (cleanId.length() < MIN_ID_LENGTH || cleanId.length() > MAX_ID_LENGTH) {
            throw new ValidationException("Enrollment ID must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }

        if (student == null) {
            throw new ValidationException("Enrollment must reference an existing Student.");
        }

        if (course == null) {
            throw new ValidationException("Enrollment must reference an existing Course.");
        }

        if (enrollmentDate == null) {
            throw new ValidationException("Enrollment date cannot be null.");
        }
        if (enrollmentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Enrollment date cannot be in the future.");
        }

        if (semester == null ||
            (!semester.equalsIgnoreCase("SEMESTER 1") &&
             !semester.equalsIgnoreCase("SEMESTER 2") &&
             !semester.equalsIgnoreCase("SUMMER"))) {
            throw new ValidationException("Semester must be SEMESTER 1, SEMESTER 2, or SUMMER.");
        }

        if (enrollmentStatus == null ||
            (!enrollmentStatus.equalsIgnoreCase("ACTIVE") &&
             !enrollmentStatus.equalsIgnoreCase("DROPPED") &&
             !enrollmentStatus.equalsIgnoreCase("COMPLETED"))) {
            throw new ValidationException("Enrollment status must be ACTIVE, DROPPED, or COMPLETED.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========================================");
        System.out.println("ENROLLMENT RECORD DETAILS");
        System.out.println("========================================");
        System.out.println("Enrollment ID     : " + enrollmentId);
        System.out.println("Student           : " + student.getDisplayName() + " [" + student.getStudentNumber() + "]");
        System.out.println("Course            : " + course.getDisplayName());
        System.out.println("Enrollment Date   : " + enrollmentDate);
        System.out.println("Semester          : " + semester);
        System.out.println("Enrollment Status : " + enrollmentStatus);
        System.out.println("========================================");
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + enrollmentId + "] " + student.getDisplayName() + " | " + course.getCourseCode() + " (" + semester + " - " + enrollmentStatus + ")";
    }

    // Getters and Setters
    public String getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(String enrollmentId) { this.enrollmentId = enrollmentId; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }

    @Override
    public String toString() {
        return "Enrollment{id='" + enrollmentId + "', student='" + student.getStudentNumber() + "', course='" + course.getCourseCode() + "'}";
    }
}
