package com.auca.main;

import com.auca.abstracts.Person;
import com.auca.administrator.Administrator;
import com.auca.assessment.Assessment;
import com.auca.bookloan.BookLoan;
import com.auca.classroom.Classroom;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;

import java.util.ArrayList;
import java.util.List;

public class DataStore {
    // Section 6: Typed in-memory collections for all 11 concrete entities
    private final List<Department> departments = new ArrayList<>();
    private final List<Student> students = new ArrayList<>();
    private final List<Teacher> teachers = new ArrayList<>();
    private final List<Administrator> administrators = new ArrayList<>();
    private final List<Course> courses = new ArrayList<>();
    private final List<Classroom> classrooms = new ArrayList<>();
    private final List<Enrollment> enrollments = new ArrayList<>();
    private final List<Assessment> assessments = new ArrayList<>();
    private final List<FeePayment> feePayments = new ArrayList<>();
    private final List<LibraryBook> libraryBooks = new ArrayList<>();
    private final List<BookLoan> bookLoans = new ArrayList<>();

    // Collection Getters
    public List<Department> getDepartments() { return departments; }
    public List<Student> getStudents() { return students; }
    public List<Teacher> getTeachers() { return teachers; }
    public List<Administrator> getAdministrators() { return administrators; }
    public List<Course> getCourses() { return courses; }
    public List<Classroom> getClassrooms() { return classrooms; }
    public List<Enrollment> getEnrollments() { return enrollments; }
    public List<Assessment> getAssessments() { return assessments; }
    public List<FeePayment> getFeePayments() { return feePayments; }
    public List<LibraryBook> getLibraryBooks() { return libraryBooks; }
    public List<BookLoan> getBookLoans() { return bookLoans; }

    // Polymorphic Aggregate Views (Section 5)
    public List<Person> getAllPersons() {
        List<Person> people = new ArrayList<>();
        people.addAll(students);
        people.addAll(teachers);
        people.addAll(administrators);
        return people;
    }

    public List<Identifiable> getAllIdentifiables() {
        List<Identifiable> all = new ArrayList<>();
        all.addAll(departments);
        all.addAll(students);
        all.addAll(teachers);
        all.addAll(administrators);
        all.addAll(courses);
        all.addAll(classrooms);
        all.addAll(enrollments);
        all.addAll(assessments);
        all.addAll(feePayments);
        all.addAll(libraryBooks);
        all.addAll(bookLoans);
        return all;
    }

    // Global ID Uniqueness Check
    public boolean isIdTaken(String id) {
        if (id == null) return false;
        for (Identifiable item : getAllIdentifiables()) {
            if (item.getId().equalsIgnoreCase(id.trim())) {
                return true;
            }
        }
        return false;
    }

    public void ensureUniqueId(String id) throws DuplicateRecordException {
        if (isIdTaken(id)) {
            throw new DuplicateRecordException("ID '" + id + "' already exists in the system.");
        }
    }

    // Domain-Specific Uniqueness Helpers (Section 8)
    public boolean isDepartmentCodeTaken(String code) {
        return departments.stream().anyMatch(d -> d.getDepartmentCode().equalsIgnoreCase(code.trim()));
    }

    public boolean isMemberNumberTaken(String memberNumber) {
        boolean inStudents = students.stream().anyMatch(s -> s.getMemberNumber().equalsIgnoreCase(memberNumber.trim()));
        boolean inTeachers = teachers.stream().anyMatch(t -> t.getMemberNumber().equalsIgnoreCase(memberNumber.trim()));
        return inStudents || inTeachers;
    }

    public boolean isStudentNumberTaken(String studentNumber) {
        return students.stream().anyMatch(s -> s.getStudentNumber().equalsIgnoreCase(studentNumber.trim()));
    }

    public boolean isEmployeeNumberTaken(String empNumber) {
        return teachers.stream().anyMatch(t -> t.getEmployeeNumber().equalsIgnoreCase(empNumber.trim()));
    }

    public boolean isStaffNumberTaken(String staffNumber) {
        return administrators.stream().anyMatch(a -> a.getStaffNumber().equalsIgnoreCase(staffNumber.trim()));
    }

    public boolean isCourseCodeTaken(String courseCode) {
        return courses.stream().anyMatch(c -> c.getCourseCode().equalsIgnoreCase(courseCode.trim()));
    }

    public boolean isEmailTaken(String email) {
        for (Person p : getAllPersons()) {
            if (p.getEmail().equalsIgnoreCase(email.trim())) return true;
            if (p instanceof com.auca.abstracts.AcademicMember am) {
                if (am.getInstitutionalEmail().equalsIgnoreCase(email.trim())) return true;
            }
        }
        return false;
    }

    public boolean isReceiptNumberTaken(String receiptNumber) {
        return feePayments.stream().anyMatch(f -> f.getReceiptNumber().equalsIgnoreCase(receiptNumber.trim()));
    }

    public boolean hasActiveEnrollment(Student student, Course course, String semester) {
        return enrollments.stream().anyMatch(e ->
                e.getStudent().getId().equalsIgnoreCase(student.getId()) &&
                e.getCourse().getId().equalsIgnoreCase(course.getId()) &&
                e.getSemester().equalsIgnoreCase(semester.trim()) &&
                e.isActive());
    }

    // Unified Search by ID (Section 7 Option 14)
    public Registrable findById(String id) throws RecordNotFoundException {
        for (Identifiable item : getAllIdentifiables()) {
            if (item.getId().equalsIgnoreCase(id.trim()) && item instanceof Registrable reg) {
                return reg;
            }
        }
        throw new RecordNotFoundException("No record found with ID: " + id);
    }
}
