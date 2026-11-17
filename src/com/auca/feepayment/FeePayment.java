package com.auca.feepayment;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FeePayment implements Identifiable, Registrable {
    // Section 3.13: Mandatory 6 declared attributes
    private String paymentId;
    private Student student;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String receiptNumber;

    public FeePayment(String paymentId, Student student, BigDecimal amount,
                      LocalDate paymentDate, String paymentMethod, String receiptNumber) {
        this.paymentId = paymentId;
        this.student = student;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
    }

    @Override
    public String getId() {
        return paymentId;
    }

    @Override
    public String getDisplayName() {
        return "Receipt #" + receiptNumber + " (" + student.getDisplayName() + ")";
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void validate() throws ValidationException {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new ValidationException("Payment ID cannot be blank.");
        }
        String cleanId = paymentId.trim();
        if (cleanId.length() < MIN_ID_LENGTH || cleanId.length() > MAX_ID_LENGTH) {
            throw new ValidationException("Payment ID must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }

        if (student == null) {
            throw new ValidationException("Fee payment must reference an existing Student.");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Payment amount must be strictly positive.");
        }

        if (paymentDate == null) {
            throw new ValidationException("Payment date cannot be null.");
        }
        if (paymentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Payment date cannot be in the future.");
        }

        if (paymentMethod == null ||
            (!paymentMethod.equalsIgnoreCase("CASH") &&
             !paymentMethod.equalsIgnoreCase("BANK TRANSFER") &&
             !paymentMethod.equalsIgnoreCase("MOBILE MONEY") &&
             !paymentMethod.equalsIgnoreCase("CARD"))) {
            throw new ValidationException("Payment method must be CASH, BANK TRANSFER, MOBILE MONEY, or CARD.");
        }

        if (receiptNumber == null || receiptNumber.trim().isEmpty()) {
            throw new ValidationException("Receipt number cannot be blank.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========================================");
        System.out.println("FEE PAYMENT RECORD DETAILS");
        System.out.println("========================================");
        System.out.println("Payment ID     : " + paymentId);
        System.out.println("Student        : " + student.getDisplayName() + " [" + student.getStudentNumber() + "]");
        System.out.println("Amount         : " + amount);
        System.out.println("Payment Date   : " + paymentDate);
        System.out.println("Payment Method : " + paymentMethod);
        System.out.println("Receipt Number : " + receiptNumber);
        System.out.println("========================================");
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + paymentId + " / Rcpt: " + receiptNumber + "] " + student.getDisplayName() + " - Amount: " + amount;
    }

    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    @Override
    public String toString() {
        return "FeePayment{id='" + paymentId + "', receipt='" + receiptNumber + "', amount=" + amount + "}";
    }
}
