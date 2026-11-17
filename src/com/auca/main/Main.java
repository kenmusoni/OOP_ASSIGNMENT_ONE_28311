package com.auca.main;

import com.auca.abstracts.AcademicMember;
import com.auca.abstracts.Person;
import com.auca.administrator.Administrator;
import com.auca.assessment.Assessment;
import com.auca.bookloan.BookLoan;
import com.auca.classroom.Classroom;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.exceptions.ValidationException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static final DataStore store = new DataStore();
    private static final SmartForms forms = new SmartForms(store);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = InputHelper.readMenuChoice("Select an option: ", 0, 19);
            switch (choice) {
                case 1 -> forms.registerDepartment();
                case 2 -> forms.registerStudent();
                case 3 -> forms.registerTeacher();
                case 4 -> forms.registerAdministrator();
                case 5 -> forms.registerCourse();
                case 6 -> forms.registerClassroom();
                case 7 -> forms.registerEnrollment();
                case 8 -> forms.registerAssessment();
                case 9 -> forms.registerFeePayment();
                case 10 -> forms.registerLibraryBook();
                case 11 -> forms.registerBookLoan();
                case 12 -> returnLibraryBook();
                case 13 -> viewAllRecords();
                case 14 -> searchRecordById();
                case 15 -> updateExistingRecord();
                case 16 -> showStudentAcademicSummary();
                case 17 -> showStudentPaymentHistory();
                case 18 -> viewAvailableBooks();
                case 19 -> demonstratePolymorphism();
                case 0 -> {
                    System.out.println("Exiting AUCA_ID School Management System. Goodbye!");
                    running = false;
                }
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n============== AUCA_ID SCHOOL MANAGEMENT ==============");
        System.out.println(" 1. Register Department         11. Borrow Library Book");
        System.out.println(" 2. Register Student            12. Return Library Book");
        System.out.println(" 3. Register Teacher            13. View All Records");
        System.out.println(" 4. Register Administrator      14. Search Record by ID");
        System.out.println(" 5. Register Course             15. Update Existing Record");
        System.out.println(" 6. Register Classroom          16. Student Academic Summary");
        System.out.println(" 7. Enroll Student              17. Student Payment History");
        System.out.println(" 8. Record Assessment           18. View Available Books");
        System.out.println(" 9. Record Fee Payment          19. Demonstrate Polymorphism");
        System.out.println("10. Register Library Book        0. Exit");
        System.out.println("=======================================================");
    }
    // =========================================================================
    // OPTION 12: RETURN LIBRARY BOOK (Updates existing BookLoan & restores stock)
    // =========================================================================
    private static void returnLibraryBook() {
        List<BookLoan> activeLoans = store.getBookLoans().stream()
                .filter(BookLoan::isActive)
                .collect(Collectors.toList());

        if (activeLoans.isEmpty()) {
            System.out.println("\n! No active book loans found to return.");
            return;
        }

        System.out.println("\n+----------------- ACTIVE BOOK LOANS ------------------------+");
        for (int i = 0; i < activeLoans.size(); i++) {
            System.out.printf("| %d %-56s |%n", (i + 1), activeLoans.get(i).getRegistrationSummary());
        }
        System.out.println("| 0 Cancel and return to menu                                |");
        System.out.println("+------------------------------------------------------------+");

        int sel = InputHelper.readMenuChoice("Select loan to return: ", 0, activeLoans.size());
        if (sel == 0) return;

        BookLoan loan = activeLoans.get(sel - 1);
        while (true) {
            String rawDate = InputHelper.readRawLine("Enter returned date [YYYY-MM-DD] (or 0 to cancel): ");
            if (rawDate.equals("0")) return;
            try {
                LocalDate retDate = LocalDate.parse(rawDate);
                if (retDate.isBefore(loan.getBorrowedDate())) {
                    System.out.println("! Returned date cannot be before borrowed date (" + loan.getBorrowedDate() + ").");
                    continue;
                }
                if (retDate.isAfter(LocalDate.now())) {
                    System.out.println("! Returned date cannot be in the future.");
                    continue;
                }
                if (!InputHelper.confirmAction("Confirm return of '" + loan.getBook().getTitle() + "'?")) {
                    System.out.println("Return cancelled.");
                    return;
                }
                loan.setReturnedDate(retDate);
                loan.validate();
                loan.getBook().returnCopy();
                System.out.println("+ Book returned successfully. Available copies: " +
                        loan.getBook().getAvailableCopies() + "/" + loan.getBook().getTotalCopies());
                return;
            } catch (DateTimeParseException e) {
                System.out.println("! Invalid date: use YYYY-MM-DD.");
            } catch (ValidationException e) {
                loan.setReturnedDate(null); // Rollback on failure
                System.out.println("! Validation error: " + e.getMessage());
            }
        }
    }

    // =========================================================================
    // OPTION 13: VIEW ALL RECORDS
    // =========================================================================
    private static void viewAllRecords() {
        List<Identifiable> all = store.getAllIdentifiables();
        if (all.isEmpty()) {
            System.out.println("\n! No records registered in AUCA_ID yet.");
            return;
        }
        System.out.println("\n================ ALL REGISTERED RECORDS (" + all.size() + ") ================");
        for (int i = 0; i < all.size(); i++) {
            Identifiable item = all.get(i);
            String summary = (item instanceof Registrable reg) ? reg.getRegistrationSummary() : item.getDisplayName();
            System.out.printf("%2d. [%-13s] %s%n", (i + 1), item.getClass().getSimpleName(), summary);
        }
        System.out.println("===========================================================");
    }

    // =========================================================================
    // OPTION 14: SEARCH RECORD BY ID
    // =========================================================================
    private static void searchRecordById() {
        String id = InputHelper.readRawLine("\nEnter Record ID to search (or 0 to cancel): ");
        if (id.equals("0") || id.isEmpty()) return;

        try {
            Registrable found = store.findById(id);
            found.printDetails();
        } catch (RecordNotFoundException e) {
            System.out.println("! " + e.getMessage());
        }
    }
    // =========================================================================
    // OPTION 15: UPDATE EXISTING RECORD (Mutable fields + confirmation)
    // =========================================================================
    private static void updateExistingRecord() {
        String id = InputHelper.readRawLine("\nEnter Record ID to update (or 0 to cancel): ");
        if (id.equals("0") || id.isEmpty()) return;

        try {
            Registrable record = store.findById(id);
            record.printDetails();

            if (record instanceof Department dept) {
                System.out.println("Editable fields: 1. Office Location | 2. Contact Email | 3. Assign Head Teacher | 0. Cancel");
                int opt = InputHelper.readMenuChoice("Select field: ", 0, 3);
                if (opt == 0) return;
                if (opt == 1) {
                    String oldVal = dept.getOfficeLocation();
                    String val = InputHelper.readRawLine("New Office Location: ");
                    dept.setOfficeLocation(val);
                    try {
                        dept.validate();
                        if (!InputHelper.confirmAction("Save change?")) dept.setOfficeLocation(oldVal);
                        else System.out.println("+ Department updated.");
                    } catch (ValidationException e) {
                        dept.setOfficeLocation(oldVal);
                        System.out.println("! Update failed: " + e.getMessage());
                    }
                } else if (opt == 2) {
                    String oldVal = dept.getContactEmail();
                    String val = InputHelper.readRawLine("New Contact Email: ");
                    dept.setContactEmail(val);
                    try {
                        dept.validate();
                        if (!InputHelper.confirmAction("Save change?")) dept.setContactEmail(oldVal);
                        else System.out.println("+ Department updated.");
                    } catch (ValidationException e) {
                        dept.setContactEmail(oldVal);
                        System.out.println("! Update failed: " + e.getMessage());
                    }
                } else if (opt == 3) {
                    if (store.getTeachers().isEmpty()) {
                        System.out.println("! No Teachers registered yet. Register a Teacher first.");
                        return;
                    }
                    for (int i = 0; i < store.getTeachers().size(); i++) {
                        System.out.println((i + 1) + ". " + store.getTeachers().get(i).getRegistrationSummary());
                    }
                    int t = InputHelper.readMenuChoice("Select Head Teacher (0 to cancel): ", 0, store.getTeachers().size());
                    if (t == 0) return;
                    if (InputHelper.confirmAction("Assign selected teacher as Head Teacher?")) {
                        dept.setHeadTeacher(store.getTeachers().get(t - 1));
                        System.out.println("+ Head Teacher assigned successfully.");
                    }
                }
            } else if (record instanceof Student stu) {
                System.out.println("Editable fields: 1. Study Year (1-6) | 2. Academic Status | 3. Guardian Phone | 0. Cancel");
                int opt = InputHelper.readMenuChoice("Select field: ", 0, 3);
                if (opt == 0) return;
                if (opt == 1) {
                    int oldYr = stu.getStudyYear();
                    String raw = InputHelper.readRawLine("New Study Year (1-6): ");
                    try {
                        stu.setStudyYear(Integer.parseInt(raw));
                        stu.validate();
                        if (!InputHelper.confirmAction("Save change?")) stu.setStudyYear(oldYr);
                        else System.out.println("+ Student updated.");
                    } catch (Exception e) {
                        stu.setStudyYear(oldYr);
                        System.out.println("! Update failed: " + e.getMessage());
                    }
                } else if (opt == 2) {
                    String oldSt = stu.getAcademicStatus();
                    String val = InputHelper.readRawLine("New Status (ACTIVE / INACTIVE): ").toUpperCase();
                    stu.setAcademicStatus(val);
                    try {
                        stu.validate();
                        if (!InputHelper.confirmAction("Save change?")) stu.setAcademicStatus(oldSt);
                        else System.out.println("+ Student updated.");
                    } catch (ValidationException e) {
                        stu.setAcademicStatus(oldSt);
                        System.out.println("! Update failed: " + e.getMessage());
                    }
                } else if (opt == 3) {
                    String oldPh = stu.getGuardianPhone();
                    String val = InputHelper.readRawLine("New Guardian Phone: ");
                    stu.setGuardianPhone(val);
                    try {
                        stu.validate();
                        if (!InputHelper.confirmAction("Save change?")) stu.setGuardianPhone(oldPh);
                        else System.out.println("+ Student updated.");
                    } catch (ValidationException e) {
                        stu.setGuardianPhone(oldPh);
                        System.out.println("! Update failed: " + e.getMessage());
                    }
                }
            } else if (record instanceof Teacher tea) {
                System.out.println("Editable fields: 1. Salary | 2. Office Number | 3. Academic Status | 0. Cancel");
                int opt = InputHelper.readMenuChoice("Select field: ", 0, 3);
                if (opt == 1) {
                    BigDecimal oldSal = tea.getSalary();
                    try {
                        tea.setSalary(new BigDecimal(InputHelper.readRawLine("New Salary (>0): ")));
                        tea.validate();
                        if (!InputHelper.confirmAction("Save change?")) tea.setSalary(oldSal);
                        else System.out.println("+ Teacher updated.");
                    } catch (Exception e) {
                        tea.setSalary(oldSal);
                        System.out.println("! Update failed: " + e.getMessage());
                    }
                } else if (opt == 2) {
                    String oldOff = tea.getOfficeNumber();
                    tea.setOfficeNumber(InputHelper.readRawLine("New Office Number: "));
                    try {
                        tea.validate();
                        if (!InputHelper.confirmAction("Save change?")) tea.setOfficeNumber(oldOff);
                        else System.out.println("+ Teacher updated.");
                    } catch (ValidationException e) {
                        tea.setOfficeNumber(oldOff);
                        System.out.println("! Update failed: " + e.getMessage());
                    }
                } else if (opt == 3) {
                    String oldSt = tea.getAcademicStatus();
                    tea.setAcademicStatus(InputHelper.readRawLine("New Status (ACTIVE / INACTIVE): ").toUpperCase());
                    try {
                        tea.validate();
                        if (!InputHelper.confirmAction("Save change?")) tea.setAcademicStatus(oldSt);
                        else System.out.println("+ Teacher updated.");
                    } catch (ValidationException e) {
                        tea.setAcademicStatus(oldSt);
                        System.out.println("! Update failed: " + e.getMessage());
                    }
                }
            } else if (record instanceof Enrollment enr) {
                String oldSt = enr.getEnrollmentStatus();
                enr.setEnrollmentStatus(InputHelper.readRawLine("New Status (ACTIVE / DROPPED / COMPLETED): ").toUpperCase());
                try {
                    enr.validate();
                    if (!InputHelper.confirmAction("Save change?")) enr.setEnrollmentStatus(oldSt);
                    else System.out.println("+ Enrollment status updated.");
                } catch (ValidationException e) {
                    enr.setEnrollmentStatus(oldSt);
                    System.out.println("! Update failed: " + e.getMessage());
                }
            } else if (record instanceof Classroom cls) {
                boolean oldAvail = cls.isAvailable();
                cls.setAvailable(!oldAvail);
                if (InputHelper.confirmAction("Toggle classroom availability to " + (!oldAvail ? "AVAILABLE" : "UNAVAILABLE") + "?")) {
                    System.out.println("+ Classroom availability updated.");
                } else {
                    cls.setAvailable(oldAvail);
                }
            } else {
                System.out.println("Record summary verified. Use dedicated menu workflows (e.g., Return Book) for transactional updates.");
            }
        } catch (RecordNotFoundException e) {
            System.out.println("! " + e.getMessage());
        }
    }
    // =========================================================================
    // OPTION 16: STUDENT ACADEMIC SUMMARY
    // =========================================================================
    private static void showStudentAcademicSummary() {
        if (store.getStudents().isEmpty()) {
            System.out.println("\n! No students registered yet.");
            return;
        }
        System.out.println("\n--- SELECT STUDENT FOR ACADEMIC SUMMARY ---");
        for (int i = 0; i < store.getStudents().size(); i++) {
            System.out.println((i + 1) + ". " + store.getStudents().get(i).getRegistrationSummary());
        }
        int sel = InputHelper.readMenuChoice("Select student (0 to cancel): ", 0, store.getStudents().size());
        if (sel == 0) return;

        Student stu = store.getStudents().get(sel - 1);
        stu.printDetails();

        List<Enrollment> stuEnrollments = store.getEnrollments().stream()
                .filter(e -> e.getStudent().getId().equalsIgnoreCase(stu.getId()))
                .collect(Collectors.toList());

        System.out.println("ENROLLMENTS & ASSESSMENTS:");
        if (stuEnrollments.isEmpty()) {
            System.out.println("  No course enrollments found.");
        } else {
            for (Enrollment enr : stuEnrollments) {
                System.out.println("  * " + enr.getRegistrationSummary());
                List<Assessment> scores = store.getAssessments().stream()
                        .filter(a -> a.getEnrollment().getId().equalsIgnoreCase(enr.getId()))
                        .collect(Collectors.toList());
                for (Assessment a : scores) {
                    System.out.println("      -> " + a.getAssessmentType() + ": " + a.getObtainedScore() + "/" + a.getMaximumScore());
                }
            }
        }
    }

    // =========================================================================
    // OPTION 17: STUDENT PAYMENT HISTORY
    // =========================================================================
    private static void showStudentPaymentHistory() {
        if (store.getStudents().isEmpty()) {
            System.out.println("\n! No students registered yet.");
            return;
        }
        System.out.println("\n--- SELECT STUDENT FOR PAYMENT HISTORY ---");
        for (int i = 0; i < store.getStudents().size(); i++) {
            System.out.println((i + 1) + ". " + store.getStudents().get(i).getRegistrationSummary());
        }
        int sel = InputHelper.readMenuChoice("Select student (0 to cancel): ", 0, store.getStudents().size());
        if (sel == 0) return;

        Student stu = store.getStudents().get(sel - 1);
        List<FeePayment> payments = store.getFeePayments().stream()
                .filter(p -> p.getStudent().getId().equalsIgnoreCase(stu.getId()))
                .collect(Collectors.toList());

        System.out.println("\nPAYMENT HISTORY FOR " + stu.getDisplayName() + " [" + stu.getStudentNumber() + "]:");
        if (payments.isEmpty()) {
            System.out.println("  No fee payments recorded for this student.");
            return;
        }
        BigDecimal totalPaid = BigDecimal.ZERO;
        for (FeePayment p : payments) {
            System.out.println("  * " + p.getPaymentDate() + " | Receipt #" + p.getReceiptNumber() +
                    " | " + p.getPaymentMethod() + " | Amount: " + p.getAmount());
            totalPaid = totalPaid.add(p.getAmount());
        }
        System.out.println("TOTAL AMOUNT PAID: " + totalPaid);
    }

    // =========================================================================
    // OPTION 18: VIEW AVAILABLE BOOKS
    // =========================================================================
    private static void viewAvailableBooks() {
        List<LibraryBook> available = store.getLibraryBooks().stream()
                .filter(LibraryBook::isActive)
                .collect(Collectors.toList());

        System.out.println("\n================ AVAILABLE LIBRARY BOOKS ================");
        if (available.isEmpty()) {
            System.out.println("No books currently available in stock.");
        } else {
            for (int i = 0; i < available.size(); i++) {
                System.out.println((i + 1) + ". " + available.get(i).getRegistrationSummary());
            }
        }
        System.out.println("=========================================================");
    }

    // =========================================================================
    // OPTION 19: DEMONSTRATE POLYMORPHISM (Section 5 & 11 Requirement)
    // =========================================================================
    private static void demonstratePolymorphism() {
        System.out.println("\n================ POLYMORPHISM DEMONSTRATION ================");
        System.out.println("1. SUPERCLASS POLYMORPHISM via List<Person>:");
        System.out.println("   (Note: Person and AcademicMember are abstract and cannot be instantiated directly.)");

        List<Person> people = store.getAllPersons();
        if (people.isEmpty()) {
            System.out.println("   [No Student, Teacher, or Administrator records registered yet.]");
        } else {
            for (Person p : people) {
                System.out.println("   -> Reference Type: Person | Runtime Type: " + p.getClass().getSimpleName());
                System.out.println("      getRoleDescription() : " + p.getRoleDescription());
                if (p instanceof AcademicMember am) {
                    System.out.println("      getAcademicCategory(): " + am.getAcademicCategory());
                }
                System.out.println("      getDisplayName()     : " + p.getDisplayName());
            }
        }

        System.out.println("\n2. INTERFACE POLYMORPHISM via List<Identifiable>:");
        List<Identifiable> entities = store.getAllIdentifiables();
        if (entities.isEmpty()) {
            System.out.println("   [No entities registered yet.]");
        } else {
            for (Identifiable idObj : entities) {
                System.out.println("   -> [" + idObj.getClass().getSimpleName() + "] ID=" + idObj.getId() +
                        " | Name=" + idObj.getDisplayName() + " | Active=" + idObj.isActive());
            }
        }
        System.out.println("============================================================");
    }
}
