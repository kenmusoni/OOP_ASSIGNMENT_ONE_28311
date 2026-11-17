package com.auca.librarybook;

import com.auca.exceptions.BookUnavailableException;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

public class LibraryBook implements Identifiable, Registrable {
    // Section 3.14: Mandatory 6 declared attributes
    private String bookId;
    private String isbn;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;

    public LibraryBook(String bookId, String isbn, String title,
                       String author, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    @Override
    public String getId() {
        return bookId;
    }

    @Override
    public String getDisplayName() {
        return title + " by " + author;
    }

    @Override
    public boolean isActive() {
        return availableCopies > 0;
    }

    // Controlled inventory helpers for BookLoan workflows
    public void borrowCopy() throws BookUnavailableException {
        if (availableCopies <= 0) {
            throw new BookUnavailableException("No available copies remaining for book: " + title);
        }
        availableCopies--;
    }

    public void returnCopy() throws ValidationException {
        if (availableCopies >= totalCopies) {
            throw new ValidationException("All copies of '" + title + "' are already in stock.");
        }
        availableCopies++;
    }

    @Override
    public void validate() throws ValidationException {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new ValidationException("Book ID cannot be blank.");
        }
        String cleanId = bookId.trim();
        if (cleanId.length() < MIN_ID_LENGTH || cleanId.length() > MAX_ID_LENGTH) {
            throw new ValidationException("Book ID must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }

        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("ISBN cannot be blank.");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Book title cannot be blank.");
        }

        if (author == null || author.trim().isEmpty()) {
            throw new ValidationException("Author name cannot be blank.");
        }

        if (totalCopies < 0) {
            throw new ValidationException("Total copies cannot be negative.");
        }

        if (availableCopies < 0 || availableCopies > totalCopies) {
            throw new ValidationException("Available copies must be between 0 and total copies (" + totalCopies + ").");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========================================");
        System.out.println("LIBRARY BOOK RECORD DETAILS");
        System.out.println("========================================");
        System.out.println("Book ID          : " + bookId);
        System.out.println("ISBN             : " + isbn);
        System.out.println("Title            : " + title);
        System.out.println("Author           : " + author);
        System.out.println("Total Copies     : " + totalCopies);
        System.out.println("Available Copies : " + availableCopies);
        System.out.println("========================================");
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + bookId + "] \"" + title + "\" by " + author + " (Available: " + availableCopies + "/" + totalCopies + ")";
    }

    // Getters and Setters
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    @Override
    public String toString() {
        return "LibraryBook{id='" + bookId + "', title='" + title + "', available=" + availableCopies + "/" + totalCopies + "}";
    }
}
