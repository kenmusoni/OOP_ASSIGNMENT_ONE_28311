package com.auca.bookloan;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;

import java.time.LocalDate;

public class BookLoan implements Identifiable, Registrable {
    // Section 3.15: Mandatory 6 declared attributes
    private String loanId;
    private Student student;
    private LibraryBook book;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;

    public BookLoan(String loanId, Student student, LibraryBook book,
                    LocalDate borrowedDate, LocalDate dueDate, LocalDate returnedDate) {
        this.loanId = loanId;
        this.student = student;
        this.book = book;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnedDate = returnedDate;
    }

    // Overloaded constructor for active new loans where returnedDate is null
    public BookLoan(String loanId, Student student, LibraryBook book,
                    LocalDate borrowedDate, LocalDate dueDate) {
        this(loanId, student, book, borrowedDate, dueDate, null);
    }

    @Override
    public String getId() {
        return loanId;
    }

    @Override
    public String getDisplayName() {
        return book.getTitle() + " -> " + student.getDisplayName();
    }

    @Override
    public boolean isActive() {
        return returnedDate == null;
    }

    @Override
    public void validate() throws ValidationException {
        if (loanId == null || loanId.trim().isEmpty()) {
            throw new ValidationException("Loan ID cannot be blank.");
        }
        String cleanId = loanId.trim();
        if (cleanId.length() < MIN_ID_LENGTH || cleanId.length() > MAX_ID_LENGTH) {
            throw new ValidationException("Loan ID must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }

        if (student == null) {
            throw new ValidationException("Book loan must reference an existing Student.");
        }

        if (book == null) {
            throw new ValidationException("Book loan must reference an existing LibraryBook.");
        }

        if (borrowedDate == null) {
            throw new ValidationException("Borrowed date cannot be null.");
        }
        if (borrowedDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Borrowed date cannot be in the future.");
        }

        if (dueDate == null || !dueDate.isAfter(borrowedDate)) {
            throw new ValidationException("Due date must be strictly after the borrowed date.");
        }

        if (returnedDate != null && returnedDate.isBefore(borrowedDate)) {
            throw new ValidationException("Returned date cannot be before the borrowed date.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========================================");
        System.out.println("BOOK LOAN RECORD DETAILS");
        System.out.println("========================================");
        System.out.println("Loan ID       : " + loanId);
        System.out.println("Student       : " + student.getDisplayName() + " [" + student.getStudentNumber() + "]");
        System.out.println("Book          : " + book.getTitle() + " [" + book.getBookId() + "]");
        System.out.println("Borrowed Date : " + borrowedDate);
        System.out.println("Due Date      : " + dueDate);
        System.out.println("Returned Date : " + (returnedDate != null ? returnedDate : "Not Returned (Active Loan)"));
        System.out.println("========================================");
    }

    @Override
    public String getRegistrationSummary() {
        String status = (returnedDate == null) ? "ACTIVE (Due: " + dueDate + ")" : "RETURNED (" + returnedDate + ")";
        return "[" + loanId + "] \"" + book.getTitle() + "\" -> " + student.getDisplayName() + " [" + status + "]";
    }

    // Getters and Setters
    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public LibraryBook getBook() { return book; }
    public void setBook(LibraryBook book) { this.book = book; }

    public LocalDate getBorrowedDate() { return borrowedDate; }
    public void setBorrowedDate(LocalDate borrowedDate) { this.borrowedDate = borrowedDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnedDate() { return returnedDate; }
    public void setReturnedDate(LocalDate returnedDate) { this.returnedDate = returnedDate; }

    @Override
    public String toString() {
        return "BookLoan{id='" + loanId + "', book='" + book.getTitle() + "', student='" + student.getStudentNumber() + "'}";
    }
}
