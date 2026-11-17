package com.auca.assessment;

import com.auca.enrollment.Enrollment;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

import java.time.LocalDate;

public class Assessment implements Identifiable, Registrable {
    // Section 3.12: Mandatory 6 declared attributes
    private String assessmentId;
    private Enrollment enrollment;
    private String assessmentType;
    private double maximumScore;
    private double obtainedScore;
    private LocalDate assessmentDate;

    public Assessment(String assessmentId, Enrollment enrollment, String assessmentType,
                      double maximumScore, double obtainedScore, LocalDate assessmentDate) {
        this.assessmentId = assessmentId;
        this.enrollment = enrollment;
        this.assessmentType = assessmentType;
        this.maximumScore = maximumScore;
        this.obtainedScore = obtainedScore;
        this.assessmentDate = assessmentDate;
    }

    @Override
    public String getId() {
        return assessmentId;
    }

    @Override
    public String getDisplayName() {
        return assessmentType + " (" + enrollment.getCourse().getCourseCode() + ")";
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void validate() throws ValidationException {
        if (assessmentId == null || assessmentId.trim().isEmpty()) {
            throw new ValidationException("Assessment ID cannot be blank.");
        }
        String cleanId = assessmentId.trim();
        if (cleanId.length() < MIN_ID_LENGTH || cleanId.length() > MAX_ID_LENGTH) {
            throw new ValidationException("Assessment ID must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }

        if (enrollment == null) {
            throw new ValidationException("Assessment must reference an existing Enrollment.");
        }
        if (!enrollment.isActive()) {
            throw new ValidationException("Cannot record assessment for a non-active enrollment.");
        }

        if (assessmentType == null ||
            (!assessmentType.equalsIgnoreCase("QUIZ") &&
             !assessmentType.equalsIgnoreCase("ASSIGNMENT") &&
             !assessmentType.equalsIgnoreCase("MIDTERM") &&
             !assessmentType.equalsIgnoreCase("FINAL"))) {
            throw new ValidationException("Assessment type must be QUIZ, ASSIGNMENT, MIDTERM, or FINAL.");
        }

        if (maximumScore <= 0) {
            throw new ValidationException("Maximum score must be greater than zero.");
        }

        if (obtainedScore < 0 || obtainedScore > maximumScore) {
            throw new ValidationException("Obtained score must be between 0 and " + maximumScore + ".");
        }

        if (assessmentDate == null) {
            throw new ValidationException("Assessment date cannot be null.");
        }
        if (assessmentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Assessment date cannot be in the future.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========================================");
        System.out.println("ASSESSMENT RECORD DETAILS");
        System.out.println("========================================");
        System.out.println("Assessment ID   : " + assessmentId);
        System.out.println("Student         : " + enrollment.getStudent().getDisplayName());
        System.out.println("Course          : " + enrollment.getCourse().getDisplayName());
        System.out.println("Assessment Type : " + assessmentType);
        System.out.println("Score           : " + obtainedScore + " / " + maximumScore);
        System.out.println("Assessment Date : " + assessmentDate);
        System.out.println("========================================");
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + assessmentId + "] " + enrollment.getStudent().getDisplayName() + " | " +
               enrollment.getCourse().getCourseCode() + " " + assessmentType + ": " + obtainedScore + "/" + maximumScore;
    }

    // Getters and Setters
    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String assessmentId) { this.assessmentId = assessmentId; }

    public Enrollment getEnrollment() { return enrollment; }
    public void setEnrollment(Enrollment enrollment) { this.enrollment = enrollment; }

    public String getAssessmentType() { return assessmentType; }
    public void setAssessmentType(String assessmentType) { this.assessmentType = assessmentType; }

    public double getMaximumScore() { return maximumScore; }
    public void setMaximumScore(double maximumScore) { this.maximumScore = maximumScore; }

    public double getObtainedScore() { return obtainedScore; }
    public void setObtainedScore(double obtainedScore) { this.obtainedScore = obtainedScore; }

    public LocalDate getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(LocalDate assessmentDate) { this.assessmentDate = assessmentDate; }

    @Override
    public String toString() {
        return "Assessment{id='" + assessmentId + "', type='" + assessmentType + "', score=" + obtainedScore + "/" + maximumScore + "}";
    }
}
