package com.auca.main;

import com.auca.administrator.Administrator;
import com.auca.assessment.Assessment;
import com.auca.bookloan.BookLoan;
import com.auca.classroom.Classroom;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.BookUnavailableException;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.ValidationException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.main.InputHelper.FormBackException;
import com.auca.main.InputHelper.FormCancelException;
import com.auca.student.Student;
import com.auca.teacher.Teacher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SmartForms {
    private final DataStore store;

    public SmartForms(DataStore store) {
        this.store = store;
    }

    private void printStepHeader(String title, int step, int totalSteps, String[] stepLabels) {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.printf("| %-58s |%n", "AUCA_ID / SCHOOL MANAGEMENT");
        System.out.printf("| %-42s Step %d of %d |%n", title, step, totalSteps);
        System.out.println("+------------------------------------------------------------+");
        for (int i = 0; i < stepLabels.length; i += 2) {
            String left = "[" + (i + 1) + "] " + stepLabels[i];
            String right = (i + 1 < stepLabels.length) ? "[" + (i + 2) + "] " + stepLabels[i + 1] : "";
            System.out.printf("| %-28s %-29s |%n", left, right);
        }
        System.out.println("+------------------------------------------------------------+");
        System.out.println("| Enter the requested value. Type BACK to return;            |");
        System.out.println("| CANCEL to discard the unsaved record.                      |");
        System.out.println("+------------------------------------------------------------+");
    }

    private String promptUniqueId(String prompt, boolean allowBack) throws FormBackException, FormCancelException {
        while (true) {
            String id = InputHelper.readFormString(prompt, allowBack);
            if (id.length() < Identifiable.MIN_ID_LENGTH || id.length() > Identifiable.MAX_ID_LENGTH) {
                System.out.println("! ID must be between " + Identifiable.MIN_ID_LENGTH + " and " + Identifiable.MAX_ID_LENGTH + " characters.");
                continue;
            }
            if (store.isIdTaken(id)) {
                System.out.println("! ID '" + id + "' already exists. Please enter a different ID.");
                continue;
            }
            return id;
        }
    }

    private String promptName(String prompt, int maxLen, boolean allowBack) throws FormBackException, FormCancelException {
        while (true) {
            String val = InputHelper.readFormString(prompt, allowBack);
            if (val.length() < Registrable.MIN_NAME_LENGTH || val.length() > maxLen) {
                System.out.println("! Must be between " + Registrable.MIN_NAME_LENGTH + " and " + maxLen + " characters.");
                continue;
            }
            return val;
        }
    }

    private String promptEmail(String prompt, boolean checkUnique, boolean allowBack) throws FormBackException, FormCancelException {
        while (true) {
            String val = InputHelper.readFormString(prompt, allowBack);
            if (val.length() > Registrable.MAX_EMAIL_LENGTH || !val.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                System.out.println("! Invalid email syntax or exceeds " + Registrable.MAX_EMAIL_LENGTH + " characters.");
                continue;
            }
            if (checkUnique && store.isEmailTaken(val)) {
                System.out.println("! Email '" + val + "' is already registered.");
                continue;
            }
            return val;
        }
    }

    private String promptPhone(String prompt, boolean allowBack) throws FormBackException, FormCancelException {
        while (true) {
            String val = InputHelper.readFormString(prompt, allowBack);
            if (!val.matches("^\\+?[0-9]{" + Registrable.MIN_PHONE_LENGTH + "," + Registrable.MAX_PHONE_LENGTH + "}$")) {
                System.out.println("! Phone must contain 10-15 digits with an optional leading '+'.");
                continue;
            }
            return val;
        }
    }

    private LocalDate promptPastDate(String prompt, boolean allowToday, boolean allowBack) throws FormBackException, FormCancelException {
        while (true) {
            LocalDate date = InputHelper.readFormDate(prompt, allowBack);
            if (allowToday && date.isAfter(LocalDate.now())) {
                System.out.println("! Date cannot be in the future.");
                continue;
            }
            if (!allowToday && !date.isBefore(LocalDate.now())) {
                System.out.println("! Date must be strictly in the past.");
                continue;
            }
            return date;
        }
    }

    private String promptChoiceFromList(String prompt, String[] allowed, boolean allowBack) throws FormBackException, FormCancelException {
        while (true) {
            String input = InputHelper.readFormString(prompt, allowBack);
            try {
                int idx = Integer.parseInt(input);
                if (idx >= 1 && idx <= allowed.length) {
                    return allowed[idx - 1];
                }
            } catch (NumberFormatException ignored) {
                for (String opt : allowed) {
                    if (opt.equalsIgnoreCase(input)) return opt;
                }
            }
            System.out.println("! Invalid choice. Enter a number from 1 to " + allowed.length + ".");
        }
    }

    //reg dep
        public Department registerDepartment() {
        String[] steps = {"Identity", "Office & Contact", "Head Teacher", "Review & Confirm"};
        String deptId = null, deptName = null, deptCode = null, officeLoc = null, contactEmail = null;
        Teacher headTeacher = null;
        int step = 1;

        while (true) {
            try {
                if (step == 1) {
                    printStepHeader("DEPARTMENT REGISTRATION", 1, 4, steps);
                    deptId = promptUniqueId("01 Department ID   [String | unique | 4-30 chars] : ", false);
                    deptName = promptName("02 Department name [String | 2-60 chars]          : ", 60, true);
                    while (true) {
                        String code = InputHelper.readFormString("03 Department code [String | unique | 2-15 chars] : ", true);
                        if (code.length() < 2 || code.length() > 15) {
                            System.out.println("! Code must be 2-15 characters.");
                            continue;
                        }
                        if (store.isDepartmentCodeTaken(code)) {
                            System.out.println("! Department code '" + code + "' already exists.");
                            continue;
                        }
                        deptCode = code;
                        break;
                    }
                    step = 2;
                } else if (step == 2) {
                    printStepHeader("DEPARTMENT REGISTRATION", 2, 4, steps);
                    officeLoc = InputHelper.readFormString("04 Office location [String | non-blank]           : ", true);
                    contactEmail = promptEmail("05 Contact email   [String | email]               : ", false, true);
                    step = 3;
                } else if (step == 3) {
                    printStepHeader("DEPARTMENT REGISTRATION", 3, 4, steps);
                    System.out.println("06 Head teacher    [Teacher | optional]");
                    if (store.getTeachers().isEmpty()) {
                        System.out.println("   (No teachers registered yet. Assigning null; you can assign later.)");
                        headTeacher = null;
                    } else {
                        System.out.println("   1. Leave unassigned for now");
                        for (int i = 0; i < store.getTeachers().size(); i++) {
                            System.out.println("   " + (i + 2) + ". " + store.getTeachers().get(i).getRegistrationSummary());
                        }
                        int sel = InputHelper.readMenuChoice("   Select option: ", 1, store.getTeachers().size() + 1);
                        headTeacher = (sel == 1) ? null : store.getTeachers().get(sel - 2);
                    }
                    step = 4;
                } else if (step == 4) {
                    while (true) {
                        System.out.println("\n+---------------- REVIEW DEPARTMENT -------------------------+");
                        System.out.println("| 01 Department ID   : " + deptId);
                        System.out.println("| 02 Department name : " + deptName);
                        System.out.println("| 03 Department code : " + deptCode);
                        System.out.println("| 04 Office location : " + officeLoc);
                        System.out.println("| 05 Contact email   : " + contactEmail);
                        System.out.println("| 06 Head teacher    : " + (headTeacher != null ? headTeacher.getDisplayName() : "Not Assigned"));
                        System.out.println("+------------------------------------------------------------+");
                        System.out.println("| [1] Save record   [2] Edit field   [0] Cancel              |");
                        System.out.println("+------------------------------------------------------------+");
                        int action = InputHelper.readMenuChoice("Select option: ", 0, 2);
                        if (action == 0) {
                            System.out.println("Department registration cancelled.");
                            return null;
                        } else if (action == 2) {
                            int f = InputHelper.readMenuChoice("Enter field number to edit (1-6, or 0 to return): ", 0, 6);
                            if (f == 1) deptId = promptUniqueId("New 01 Department ID: ", false);
                            else if (f == 2) deptName = promptName("New 02 Department name: ", 60, false);
                            else if (f == 3) {
                                String code = InputHelper.readFormString("New 03 Department code: ", false);
                                if (!code.equalsIgnoreCase(deptCode) && store.isDepartmentCodeTaken(code)) {
                                    System.out.println("! Code already exists.");
                                } else deptCode = code;
                            } else if (f == 4) officeLoc = InputHelper.readFormString("New 04 Office location: ", false);
                            else if (f == 5) contactEmail = promptEmail("New 05 Contact email: ", false, false);
                            else if (f == 6) {
                                if (store.getTeachers().isEmpty()) {
                                    System.out.println("! No teachers exist yet.");
                                } else {
                                    System.out.println("1. None");
                                    for (int i = 0; i < store.getTeachers().size(); i++) {
                                        System.out.println((i + 2) + ". " + store.getTeachers().get(i).getRegistrationSummary());
                                    }
                                    int s = InputHelper.readMenuChoice("Select: ", 1, store.getTeachers().size() + 1);
                                    headTeacher = (s == 1) ? null : store.getTeachers().get(s - 2);
                                }
                            }
                        } else {
                            Department dept = new Department(deptId, deptName, deptCode, officeLoc, contactEmail, headTeacher);
                            store.ensureUniqueId(deptId);
                            dept.validate();
                            store.getDepartments().add(dept);
                            System.out.println("+ Department " + dept.getDisplayName() + " registered successfully.");
                            return dept;
                        }
                    }
                }
            } catch (FormBackException e) {
                step = Math.max(1, step - 1);
            } catch (FormCancelException e) {
                System.out.println("Department registration cancelled.");
                return null;
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! Error: " + e.getMessage());
            }
        }
    }
    private Course selectOrCreateCourse(String contextName) throws FormBackException, FormCancelException {
        while (true) {
            System.out.println("\n+-------------------- CHOOSE COURSE -------------------------+");
            List<Course> list = store.getCourses();
            if (list.isEmpty()) {
                System.out.println("| No Course is registered yet.                               |");
            } else {
                for (int i = 0; i < list.size(); i++) {
                    System.out.printf("| %d %-56s |%n", (i + 1), list.get(i).getRegistrationSummary());
                }
            }
            int createOpt = list.size() + 1;
            System.out.printf("| %d [+] Register a new Course                                |%n", createOpt);
            System.out.printf("| 0 Cancel %-49s |%n", contextName);
            System.out.println("+------------------------------------------------------------+");

            String raw = InputHelper.readFormString("Select option: ", true);
            int choice;
            try {
                choice = Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("! Please enter a valid option number.");
                continue;
            }
            if (choice == 0) {
                throw new FormCancelException();
            } else if (choice == createOpt) {
                Course created = registerCourse();
                if (created != null) {
                    System.out.println("Course saved. Returning to " + contextName + "...");
                    return created;
                }
            } else if (choice >= 1 && choice <= list.size()) {
                return list.get(choice - 1);
            } else {
                System.out.println("! Selection out of range.");
            }
        }
    }


    // =========================================================================
    // SMART SELECTORS WITH NESTED REGISTRATION (Section 12.3 & 16.4)
    // =========================================================================
    private Department selectOrCreateDepartment(String contextName) throws FormBackException, FormCancelException {
        while (true) {
            System.out.println("\n+----------------- CHOOSE DEPARTMENT ------------------------+");
            List<Department> list = store.getDepartments();
            if (list.isEmpty()) {
                System.out.println("| No Department is registered yet.                           |");
            } else {
                for (int i = 0; i < list.size(); i++) {
                    System.out.printf("| %d %-56s |%n", (i + 1), list.get(i).getRegistrationSummary());
                }
            }
            int createOpt = list.size() + 1;
            System.out.printf("| %d [+] Register a new Department                            |%n", createOpt);
            System.out.printf("| 0 Cancel %-49s |%n", contextName);
            System.out.println("+------------------------------------------------------------+");

            String raw = InputHelper.readFormString("Select option: ", true);
            int choice;
            try {
                choice = Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("! Please enter a valid option number.");
                continue;
            }
            if (choice == 0) {
                throw new FormCancelException();
            } else if (choice == createOpt) {
                Department created = registerDepartment();
                if (created != null) {
                    System.out.println("Department saved. Returning to " + contextName + "...");
                    return created;
                }
            } else if (choice >= 1 && choice <= list.size()) {
                return list.get(choice - 1);
            } else {
                System.out.println("! Selection out of range.");
            }
        }
    }

    private Teacher selectOrCreateTeacher(String contextName) throws FormBackException, FormCancelException {
        while (true) {
            System.out.println("\n+------------------- CHOOSE TEACHER -------------------------+");
            List<Teacher> list = store.getTeachers();
            if (list.isEmpty()) {
                System.out.println("| No Teacher is registered yet.                              |");
            } else {
                for (int i = 0; i < list.size(); i++) {
                    System.out.printf("| %d %-56s |%n", (i + 1), list.get(i).getRegistrationSummary());
                }
            }
            int createOpt = list.size() + 1;
            System.out.printf("| %d [+] Register a new Teacher                               |%n", createOpt);
            System.out.printf("| 0 Cancel %-49s |%n", contextName);
            System.out.println("+------------------------------------------------------------+");

            String raw = InputHelper.readFormString("Select option: ", true);
            int choice;
            try {
                choice = Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("! Please enter a valid option number.");
                continue;
            }
            if (choice == 0) {
                throw new FormCancelException();
            } else if (choice == createOpt) {
                Teacher created = registerTeacher();
                if (created != null) {
                    System.out.println("Teacher saved. Returning to " + contextName + "...");
                    return created;
                }
            } else if (choice >= 1 && choice <= list.size()) {
                return list.get(choice - 1);
            } else {
                System.out.println("! Selection out of range.");
            }
        }
    }

    private Student selectOrCreateStudent(String contextName) throws FormBackException, FormCancelException {
        while (true) {
            System.out.println("\n+------------------- CHOOSE STUDENT -------------------------+");
            List<Student> list = store.getStudents();
            if (list.isEmpty()) {
                System.out.println("| No Student is registered yet.                              |");
            } else {
                for (int i = 0; i < list.size(); i++) {
                    System.out.printf("| %d %-56s |%n", (i + 1), list.get(i).getRegistrationSummary());
                }
            }
            int createOpt = list.size() + 1;
            System.out.printf("| %d [+] Register a new Student                               |%n", createOpt);
            System.out.printf("| 0 Cancel %-49s |%n", contextName);
            System.out.println("+------------------------------------------------------------+");

            String raw = InputHelper.readFormString("Select option: ", true);
            int choice;
            try {
                choice = Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("! Please enter a valid option number.");
                continue;
            }
            if (choice == 0) {
                throw new FormCancelException();
            } else if (choice == createOpt) {
                Student created = registerStudent();
                if (created != null) {
                    System.out.println("Student saved. Returning to " + contextName + "...");
                    return created;
                }
                      } else if (choice >= 1 && choice <= list.size()) {
                return list.get(choice - 1);
            } else {
                System.out.println("! Selection out of range.");
            }
        }
    }

    private Enrollment selectOrCreateActiveEnrollment(String contextName) throws FormBackException, FormCancelException {
        while (true) {
            List<Enrollment> activeList = store.getEnrollments().stream()
                    .filter(Enrollment::isActive)
                    .collect(Collectors.toList());

            System.out.println("\n+-------------- CHOOSE ACTIVE ENROLLMENT --------------------+");
            if (activeList.isEmpty()) {
                System.out.println("| No active Enrollment exists.                               |");
            } else {
                for (int i = 0; i < activeList.size(); i++) {
                    System.out.printf("| %d %-56s |%n", (i + 1), activeList.get(i).getRegistrationSummary());
                }
            }
            int createOpt = activeList.size() + 1;
            System.out.printf("| %d [+] Enroll a Student now                                 |%n", createOpt);
            System.out.printf("| 0 Cancel %-49s |%n", contextName);
            System.out.println("+------------------------------------------------------------+");

            String raw = InputHelper.readFormString("Select option: ", true);
            int choice;
            try {
                choice = Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("! Please enter a valid option number.");
                continue;
            }
            if (choice == 0) {
                throw new FormCancelException();
            } else if (choice == createOpt) {
                Enrollment created = registerEnrollment();
                if (created != null && created.isActive()) {
                    System.out.println("Enrollment saved. Returning to " + contextName + "...");
                    return created;
                }
            } else if (choice >= 1 && choice <= activeList.size()) {
                return activeList.get(choice - 1);
            } else {
                System.out.println("! Selection out of range.");
            }
        }
    }

    private LibraryBook selectOrCreateLibraryBook(String contextName) throws FormBackException, FormCancelException {
        while (true) {
            System.out.println("\n+----------------- CHOOSE LIBRARY BOOK ----------------------+");
            List<LibraryBook> list = store.getLibraryBooks();
            if (list.isEmpty()) {
                System.out.println("| No LibraryBook is registered yet.                          |");
            } else {
                for (int i = 0; i < list.size(); i++) {
                    System.out.printf("| %d %-56s |%n", (i + 1), list.get(i).getRegistrationSummary());
                }
            }
            int createOpt = list.size() + 1;
            System.out.printf("| %d [+] Register a new LibraryBook                           |%n", createOpt);
            System.out.printf("| 0 Cancel %-49s |%n", contextName);
            System.out.println("+------------------------------------------------------------+");

            String raw = InputHelper.readFormString("Select option: ", true);
            int choice;
            try {
                choice = Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("! Please enter a valid option number.");
                continue;
            }
            if (choice == 0) {
                throw new FormCancelException();
            } else if (choice == createOpt) {
                LibraryBook created = registerLibraryBook();
                if (created != null) {
                    System.out.println("LibraryBook saved. Returning to " + contextName + "...");
                    return created;
                }
            } else if (choice >= 1 && choice <= list.size()) {
                return list.get(choice - 1);
            } else {
                System.out.println("! Selection out of range.");
            }
        }
    }
    // =========================================================================
    // 2. REGISTER STUDENT (Section 16 & 17 Exact 4-Step 18-Field Console Design)
    // =========================================================================
    public Student registerStudent() {
        String[] steps = {"Personal details", "Academic details", "Student details", "Review & confirm"};
        String personId = null, firstName = null, lastName = null, email = null, phone = null;
        LocalDate dob = null;
        String memberNumber = null, academicStatus = null, instEmail = null, advisorName = null;
        Department dept = null;
        LocalDate joinDate = null;
        String studentNumber = null, programName = null, guardianPhone = null;
        LocalDate admissionDate = null;
        int studyYear = 1;
        boolean scholarship = false;

        int step = 1;
        while (true) {
            try {
                if (step == 1) {
                    printStepHeader("STUDENT REGISTRATION", 1, 4, steps);
                    System.out.println("[STEP 1/4] PERSONAL DETAILS");
                    personId = promptUniqueId("01 Person ID     [String | unique | 4-30 chars] : ", false);
                    firstName = promptName("02 First name    [String | 2-60 chars]          : ", 60, true);
                    lastName = promptName("03 Last name     [String | 2-60 chars]          : ", 60, true);
                    email = promptEmail("04 Email         [String | unique | email]      : ", true, true);
                    phone = promptPhone("05 Phone         [String | 10-15 digits]        : ", true);
                    dob = promptPastDate("06 Date of birth [LocalDate | YYYY-MM-DD]       : ", false, true);
                    step = 2;
                } else if (step == 2) {
                    printStepHeader("STUDENT REGISTRATION", 2, 4, steps);
                    System.out.println("[STEP 2/4] ACADEMIC DETAILS");
                    while (true) {
                        String mNum = InputHelper.readFormString("07 Member number       [String | unique | 4-30] : ", true);
                        if (mNum.length() < 4 || mNum.length() > 30) {
                            System.out.println("! Member number must be 4-30 characters.");
                            continue;
                        }
                        if (store.isMemberNumberTaken(mNum)) {
                            System.out.println("! Member number '" + mNum + "' already exists.");
                            continue;
                        }
                        memberNumber = mNum;
                        break;
                    }
                    System.out.println("08 Department          [Department | select]    :");
                    dept = selectOrCreateDepartment("student registration");
                    joinDate = promptPastDate("09 Join date           [LocalDate | YYYY-MM-DD] : ", true, true);
                    academicStatus = promptChoiceFromList("10 Academic status     [1 ACTIVE / 2 INACTIVE]  : ", new String[]{"ACTIVE", "INACTIVE"}, true);
                    instEmail = promptEmail("11 Institutional email [String | unique | email]: ", true, true);
                    advisorName = promptName("12 Advisor name        [String | 2-120 chars]   : ", 120, true);
                    step = 3;
                } else if (step == 3) {
                    printStepHeader("STUDENT REGISTRATION", 3, 4, steps);
                    System.out.println("[STEP 3/4] STUDENT DETAILS");
                    while (true) {
                        String sNum = InputHelper.readFormString("13 Student number [String | unique | 4-30]      : ", true);
                        if (sNum.length() < 4 || sNum.length() > 30) {
                            System.out.println("! Student number must be 4-30 characters.");
                            continue;
                        }
                        if (store.isStudentNumberTaken(sNum)) {
                            System.out.println("! Student number '" + sNum + "' already exists. Please enter a different student number.");
                            continue;
                        }
                        studentNumber = sNum;
                        break;
                    }
                    programName = promptName("14 Program name   [String | 2-120 chars]        : ", 120, true);
                    admissionDate = promptPastDate("15 Admission date [LocalDate | YYYY-MM-DD]      : ", true, true);
                    while (true) {
                        int yr = InputHelper.readFormInt("16 Study year     [int | 1-6]                   : ", true);
                        if (yr < 1 || yr > 6) {
                            System.out.println("! Study year must be between 1 and 6.");
                            continue;
                        }
                        studyYear = yr;
                        break;
                    }
                    guardianPhone = promptPhone("17 Guardian phone [String | 10-15 digits]       : ", true);
                    scholarship = InputHelper.readFormBooleanChoice("18 Scholarship    [1 YES / 2 NO]                : ", "YES", "NO", true);
                    step = 4;
                } else if (step == 4) {
                    while (true) {
                        System.out.println("\n+---------------- REVIEW STUDENT ---------------------------+");
                        System.out.println("| PERSON                                                     |");
                        System.out.println("| 01 Person ID           : " + personId);
                        System.out.println("| 02 First name          : " + firstName);
                        System.out.println("| 03 Last name           : " + lastName);
                        System.out.println("| 04 Email               : " + email);
                        System.out.println("| 05 Phone               : " + phone);
                        System.out.println("| 06 Date of birth       : " + dob);
                        System.out.println("| ACADEMIC MEMBER                                            |");
                        System.out.println("| 07 Member number       : " + memberNumber);
                        System.out.println("| 08 Department          : " + dept.getRegistrationSummary());
                        System.out.println("| 09 Join date           : " + joinDate);
                        System.out.println("| 10 Academic status     : " + academicStatus);
                        System.out.println("| 11 Institutional email : " + instEmail);
                        System.out.println("| 12 Advisor name        : " + advisorName);
                        System.out.println("| STUDENT                                                    |");
                        System.out.println("| 13 Student number      : " + studentNumber);
                        System.out.println("| 14 Program name        : " + programName);
                        System.out.println("| 15 Admission date      : " + admissionDate);
                        System.out.println("| 16 Study year          : " + studyYear);
                        System.out.println("| 17 Guardian phone      : " + guardianPhone);
                        System.out.println("| 18 Scholarship         : " + (scholarship ? "YES" : "NO"));
                        System.out.println("+------------------------------------------------------------+");
                        System.out.println("| [1] Save student  [2] Edit field  [0] Cancel             |");
                        System.out.println("+------------------------------------------------------------+");

                        int opt = InputHelper.readMenuChoice("Select option: ", 0, 2);
                        if (opt == 0) {
                            System.out.println("Student registration cancelled.");
                            return null;
                        } else if (opt == 2) {
                            int f = InputHelper.readMenuChoice("Enter field number to edit (1-18, or 0 to return): ", 0, 18);
                            switch (f) {
                                case 1 -> personId = promptUniqueId("New 01 Person ID: ", false);
                                case 2 -> firstName = promptName("New 02 First name: ", 60, false);
                                case 3 -> lastName = promptName("New 03 Last name: ", 60, false);
                                case 4 -> email = promptEmail("New 04 Email: ", true, false);
                                case 5 -> phone = promptPhone("New 05 Phone: ", false);
                                case 6 -> dob = promptPastDate("New 06 Date of birth: ", false, false);
                                case 7 -> {
                                    String m = InputHelper.readFormString("New 07 Member number: ", false);
                                    if (!m.equalsIgnoreCase(memberNumber) && store.isMemberNumberTaken(m)) {
                                        System.out.println("! Member number already exists.");
                                    } else memberNumber = m;
                                }
                                case 8 -> dept = selectOrCreateDepartment("student edit");
                                case 9 -> joinDate = promptPastDate("New 09 Join date: ", true, false);
                                case 10 -> academicStatus = promptChoiceFromList("New 10 Academic status [1 ACTIVE / 2 INACTIVE]: ", new String[]{"ACTIVE", "INACTIVE"}, false);
                                case 11 -> instEmail = promptEmail("New 11 Institutional email: ", true, false);
                                case 12 -> advisorName = promptName("New 12 Advisor name: ", 120, false);
                                case 13 -> {
                                    String s = InputHelper.readFormString("New 13 Student number: ", false);
                                    if (!s.equalsIgnoreCase(studentNumber) && store.isStudentNumberTaken(s)) {
                                        System.out.println("! Student number already exists.");
                                    } else studentNumber = s;
                                }
                                case 14 -> programName = promptName("New 14 Program name: ", 120, false);
                                case 15 -> admissionDate = promptPastDate("New 15 Admission date: ", true, false);
                                case 16 -> {
                                    int y = InputHelper.readFormInt("New 16 Study year (1-6): ", false);
                                    if (y >= 1 && y <= 6) studyYear = y;
                                    else System.out.println("! Study year must be 1-6.");
                                }
                                case 17 -> guardianPhone = promptPhone("New 17 Guardian phone: ", false);
                                case 18 -> scholarship = InputHelper.readFormBooleanChoice("New 18 Scholarship [1 YES / 2 NO]: ", "YES", "NO", false);
                            }
                        } else {
                            Student student = new Student(personId, firstName, lastName, email, phone, dob,
                                    memberNumber, dept, joinDate, academicStatus, instEmail, advisorName,
                                    studentNumber, programName, admissionDate, studyYear, guardianPhone, scholarship);
                            store.ensureUniqueId(personId);
                            if (store.isStudentNumberTaken(studentNumber)) {
                                throw new DuplicateRecordException("Student number " + studentNumber + " already exists.");
                            }
                            student.validate();
                            store.getStudents().add(student);
                            System.out.println("+ Student " + studentNumber + " registered successfully.");
                            return student;
                        }
                    }
                }
            } catch (FormBackException e) {
                step = Math.max(1, step - 1);
            } catch (FormCancelException e) {
                System.out.println("Student registration cancelled.");
                return null;
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! Validation error: " + e.getMessage());
            }
        }
    }
    // =========================================================================
    // 3. REGISTER TEACHER (4-Step 18-Field Guided Form)
    // =========================================================================
    public Teacher registerTeacher() {
        String[] steps = {"Personal details", "Academic details", "Teacher details", "Review & confirm"};
        String personId = null, firstName = null, lastName = null, email = null, phone = null;
        LocalDate dob = null;
        String memberNumber = null, academicStatus = null, instEmail = null, advisorName = null;
        Department dept = null;
        LocalDate joinDate = null;
        String empNumber = null, specialization = null, officeNumber = null, employmentType = null;
        LocalDate hireDate = null;
        BigDecimal salary = null;

        int step = 1;
        while (true) {
            try {
                if (step == 1) {
                    printStepHeader("TEACHER REGISTRATION", 1, 4, steps);
                    personId = promptUniqueId("01 Person ID     [String | unique | 4-30 chars] : ", false);
                    firstName = promptName("02 First name    [String | 2-60 chars]          : ", 60, true);
                    lastName = promptName("03 Last name     [String | 2-60 chars]          : ", 60, true);
                    email = promptEmail("04 Email         [String | unique | email]      : ", true, true);
                    phone = promptPhone("05 Phone         [String | 10-15 digits]        : ", true);
                    dob = promptPastDate("06 Date of birth [LocalDate | YYYY-MM-DD]       : ", false, true);
                    step = 2;
                } else if (step == 2) {
                    printStepHeader("TEACHER REGISTRATION", 2, 4, steps);
                    while (true) {
                        String mNum = InputHelper.readFormString("07 Member number       [String | unique | 4-30] : ", true);
                        if (mNum.length() < 4 || mNum.length() > 30) {
                            System.out.println("! Member number must be 4-30 characters.");
                            continue;
                        }
                        if (store.isMemberNumberTaken(mNum)) {
                            System.out.println("! Member number already exists.");
                            continue;
                        }
                        memberNumber = mNum;
                        break;
                    }
                    System.out.println("08 Department          [Department | select]    :");
                    dept = selectOrCreateDepartment("teacher registration");
                    joinDate = promptPastDate("09 Join date           [LocalDate | YYYY-MM-DD] : ", true, true);
                    academicStatus = promptChoiceFromList("10 Academic status     [1 ACTIVE / 2 INACTIVE]  : ", new String[]{"ACTIVE", "INACTIVE"}, true);
                    instEmail = promptEmail("11 Institutional email [String | unique | email]: ", true, true);
                    advisorName = promptName("12 Supervisor/Advisor  [String | 2-120 chars]   : ", 120, true);
                    step = 3;
                } else if (step == 3) {
                    printStepHeader("TEACHER REGISTRATION", 3, 4, steps);
                    while (true) {
                        String eNum = InputHelper.readFormString("13 Employee number [String | unique | 4-30]     : ", true);
                        if (eNum.length() < 4 || eNum.length() > 30) {
                            System.out.println("! Employee number must be 4-30 characters.");
                            continue;
                        }
                        if (store.isEmployeeNumberTaken(eNum)) {
                            System.out.println("! Employee number '" + eNum + "' already exists.");
                            continue;
                        }
                        empNumber = eNum;
                        break;
                    }
                    specialization = InputHelper.readFormString("14 Specialization  [String | non-blank]         : ", true);
                    hireDate = promptPastDate("15 Hire date       [LocalDate | YYYY-MM-DD]     : ", true, true);
                    while (true) {
                        BigDecimal sal = InputHelper.readFormBigDecimal("16 Salary          [BigDecimal | > 0]           : ", true);
                        if (sal.compareTo(BigDecimal.ZERO) <= 0) {
                            System.out.println("! Salary must be strictly positive.");
                            continue;
                        }
                        salary = sal;
                        break;
                    }
                    officeNumber = InputHelper.readFormString("17 Office number   [String | non-blank]         : ", true);
                    employmentType = promptChoiceFromList("18 Employment type [1 FULL-TIME / 2 PART-TIME / 3 VISITING]: ",
                            new String[]{"FULL-TIME", "PART-TIME", "VISITING"}, true);
                    step = 4;
                } else if (step == 4) {
                    while (true) {
                        System.out.println("\n+---------------- REVIEW TEACHER ---------------------------+");
                        System.out.println("| 01 Person ID           : " + personId);
                        System.out.println("| 02 First name          : " + firstName);
                        System.out.println("| 03 Last name           : " + lastName);
                        System.out.println("| 04 Email               : " + email);
                        System.out.println("| 05 Phone               : " + phone);
                        System.out.println("| 06 Date of birth       : " + dob);
                        System.out.println("| 07 Member number       : " + memberNumber);
                        System.out.println("| 08 Department          : " + dept.getRegistrationSummary());
                        System.out.println("| 09 Join date           : " + joinDate);
                        System.out.println("| 10 Academic status     : " + academicStatus);
                        System.out.println("| 11 Institutional email : " + instEmail);
                        System.out.println("| 12 Advisor name        : " + advisorName);
                        System.out.println("| 13 Employee number     : " + empNumber);
                        System.out.println("| 14 Specialization      : " + specialization);
                        System.out.println("| 15 Hire date           : " + hireDate);
                        System.out.println("| 16 Salary              : " + salary);
                        System.out.println("| 17 Office number       : " + officeNumber);
                        System.out.println("| 18 Employment type     : " + employmentType);
                        System.out.println("+------------------------------------------------------------+");
                        System.out.println("| [1] Save teacher  [2] Edit field  [0] Cancel               |");
                        System.out.println("+------------------------------------------------------------+");

                        int opt = InputHelper.readMenuChoice("Select option: ", 0, 2);
                        if (opt == 0) {
                            System.out.println("Teacher registration cancelled.");
                            return null;
                        } else if (opt == 2) {
                            int f = InputHelper.readMenuChoice("Enter field number to edit (1-18, or 0 to return): ", 0, 18);
                            switch (f) {
                                case 1 -> personId = promptUniqueId("New 01 Person ID: ", false);
                                case 2 -> firstName = promptName("New 02 First name: ", 60, false);
                                case 3 -> lastName = promptName("New 03 Last name: ", 60, false);
                                case 4 -> email = promptEmail("New 04 Email: ", true, false);
                                case 5 -> phone = promptPhone("New 05 Phone: ", false);
                                case 6 -> dob = promptPastDate("New 06 Date of birth: ", false, false);
                                case 7 -> memberNumber = InputHelper.readFormString("New 07 Member number: ", false);
                                case 8 -> dept = selectOrCreateDepartment("teacher edit");
                                case 9 -> joinDate = promptPastDate("New 09 Join date: ", true, false);
                                case 10 -> academicStatus = promptChoiceFromList("New 10 Academic status [1 ACTIVE / 2 INACTIVE]: ", new String[]{"ACTIVE", "INACTIVE"}, false);
                                case 11 -> instEmail = promptEmail("New 11 Institutional email: ", true, false);
                                case 12 -> advisorName = promptName("New 12 Advisor name: ", 120, false);
                                case 13 -> empNumber = InputHelper.readFormString("New 13 Employee number: ", false);
                                case 14 -> specialization = InputHelper.readFormString("New 14 Specialization: ", false);
                                case 15 -> hireDate = promptPastDate("New 15 Hire date: ", true, false);
                                case 16 -> salary = InputHelper.readFormBigDecimal("New 16 Salary: ", false);
                                case 17 -> officeNumber = InputHelper.readFormString("New 17 Office number: ", false);
                                case 18 -> employmentType = promptChoiceFromList("New 18 Employment type [1 FULL-TIME / 2 PART-TIME / 3 VISITING]: ",
                                        new String[]{"FULL-TIME", "PART-TIME", "VISITING"}, false);
                            }
                        } else {
                            Teacher teacher = new Teacher(personId, firstName, lastName, email, phone, dob,
                                    memberNumber, dept, joinDate, academicStatus, instEmail, advisorName,
                                    empNumber, specialization, hireDate, salary, officeNumber, employmentType);
                            store.ensureUniqueId(personId);
                            if (store.isEmployeeNumberTaken(empNumber)) {
                                throw new DuplicateRecordException("Employee number " + empNumber + " already exists.");
                            }
                            teacher.validate();
                            store.getTeachers().add(teacher);
                            System.out.println("+ Teacher " + empNumber + " registered successfully.");
                            return teacher;
                        }
                    }
                }
            } catch (FormBackException e) {
                step = Math.max(1, step - 1);
            } catch (FormCancelException e) {
                System.out.println("Teacher registration cancelled.");
                return null;
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! Validation error: " + e.getMessage());
            }
        }
    }
    // =========================================================================
    // 4. REGISTER ADMINISTRATOR (4-Step 12-Field Guided Form)
    // =========================================================================
    public Administrator registerAdministrator() {
        String[] steps = {"Personal details", "Staff identity", "Role & Access", "Review & confirm"};
        String personId = null, firstName = null, lastName = null, email = null, phone = null;
        LocalDate dob = null;
        String staffNumber = null, jobTitle = null, assignedOffice = null;
        LocalDate employmentDate = null;
        int accessLevel = 1;
        boolean active = true;

        int step = 1;
        while (true) {
            try {
                if (step == 1) {
                    printStepHeader("ADMINISTRATOR REGISTRATION", 1, 4, steps);
                    personId = promptUniqueId("01 Person ID     [String | unique | 4-30 chars] : ", false);
                    firstName = promptName("02 First name    [String | 2-60 chars]          : ", 60, true);
                    lastName = promptName("03 Last name     [String | 2-60 chars]          : ", 60, true);
                    email = promptEmail("04 Email         [String | unique | email]      : ", true, true);
                    phone = promptPhone("05 Phone         [String | 10-15 digits]        : ", true);
                    dob = promptPastDate("06 Date of birth [LocalDate | YYYY-MM-DD]       : ", false, true);
                    step = 2;
                } else if (step == 2) {
                    printStepHeader("ADMINISTRATOR REGISTRATION", 2, 4, steps);
                    while (true) {
                        String sNum = InputHelper.readFormString("07 Staff number    [String | unique | 4-30]     : ", true);
                        if (sNum.length() < 4 || sNum.length() > 30) {
                            System.out.println("! Staff number must be 4-30 characters.");
                            continue;
                        }
                        if (store.isStaffNumberTaken(sNum)) {
                            System.out.println("! Staff number already exists.");
                            continue;
                        }
                        staffNumber = sNum;
                        break;
                    }
                    jobTitle = InputHelper.readFormString("08 Job title       [String | non-blank]         : ", true);
                    assignedOffice = InputHelper.readFormString("09 Assigned office [String | non-blank]         : ", true);
                    step = 3;
                } else if (step == 3) {
                    printStepHeader("ADMINISTRATOR REGISTRATION", 3, 4, steps);
                    employmentDate = promptPastDate("10 Employment date [LocalDate | YYYY-MM-DD]     : ", true, true);
                    while (true) {
                        int lvl = InputHelper.readFormInt("11 Access level    [int | 1-5]                  : ", true);
                        if (lvl < 1 || lvl > 5) {
                            System.out.println("! Access level must be between 1 and 5.");
                            continue;
                        }
                        accessLevel = lvl;
                        break;
                    }
                    active = InputHelper.readFormBooleanChoice("12 Active status   [1 ACTIVE / 2 INACTIVE]      : ", "ACTIVE", "INACTIVE", true);
                    step = 4;
                } else if (step == 4) {
                    while (true) {
                        System.out.println("\n+-------------- REVIEW ADMINISTRATOR ------------------------+");
                        System.out.println("| 01 Person ID       : " + personId);
                        System.out.println("| 02 First name      : " + firstName);
                        System.out.println("| 03 Last name       : " + lastName);
                        System.out.println("| 04 Email           : " + email);
                        System.out.println("| 05 Phone           : " + phone);
                        System.out.println("| 06 Date of birth   : " + dob);
                        System.out.println("| 07 Staff number    : " + staffNumber);
                        System.out.println("| 08 Job title       : " + jobTitle);
                        System.out.println("| 09 Assigned office : " + assignedOffice);
                        System.out.println("| 10 Employment date : " + employmentDate);
                        System.out.println("| 11 Access level    : " + accessLevel);
                        System.out.println("| 12 Active status   : " + (active ? "ACTIVE" : "INACTIVE"));
                        System.out.println("+------------------------------------------------------------+");
                        System.out.println("| Save administrator  Edit field  Cancel         |");
                        System.out.println("+------------------------------------------------------------+");

                        int opt = InputHelper.readMenuChoice("Select option: ", 0, 2);
                        if (opt == 0) {
                            System.out.println("Administrator registration cancelled.");
                            return null;
                        } else if (opt == 2) {
                            int f = InputHelper.readMenuChoice("Enter field number to edit (1-12, or 0 to return): ", 0, 12);
                            switch (f) {
                                case 1 -> personId = promptUniqueId("New 01 Person ID: ", false);
                                case 2 -> firstName = promptName("New 02 First name: ", 60, false);
                                case 3 -> lastName = promptName("New 03 Last name: ", 60, false);
                                case 4 -> email = promptEmail("New 04 Email: ", true, false);
                                case 5 -> phone = promptPhone("New 05 Phone: ", false);
                                case 6 -> dob = promptPastDate("New 06 Date of birth: ", false, false);
                                case 7 -> staffNumber = InputHelper.readFormString("New 07 Staff number: ", false);
                                case 8 -> jobTitle = InputHelper.readFormString("New 08 Job title: ", false);
                                case 9 -> assignedOffice = InputHelper.readFormString("New 09 Assigned office: ", false);
                                case 10 -> employmentDate = promptPastDate("New 10 Employment date: ", true, false);
                                case 11 -> {
                                    int l = InputHelper.readFormInt("New 11 Access level (1-5): ", false);
                                    if (l >= 1 && l <= 5) accessLevel = l;
                                    else System.out.println("! Must be 1-5.");
                                }
                                case 12 -> active = InputHelper.readFormBooleanChoice("New 12 Active [1 ACTIVE / 2 INACTIVE]: ", "ACTIVE", "INACTIVE", false);
                            }
                        } else {
                            Administrator admin = new Administrator(personId, firstName, lastName, email, phone, dob,
                                    staffNumber, jobTitle, assignedOffice, employmentDate, accessLevel, active);
                            store.ensureUniqueId(personId);
                            admin.validate();
                            store.getAdministrators().add(admin);
                            System.out.println("+ Administrator " + staffNumber + " registered successfully.");
                            return admin;
                        }
                    }
                }
            } catch (FormBackException e) {
                step = Math.max(1, step - 1);
            } catch (FormCancelException e) {
                System.out.println("Administrator registration cancelled.");
                return null;
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! Validation error: " + e.getMessage());
            }
        }
    }
    // =========================================================================
    // 5. REGISTER COURSE (4-Step Guided Form with Nested Dept & Teacher)
    // =========================================================================
    public Course registerCourse() {
        String[] steps = {"Course Identity", "Credits", "Department & Teacher", "Review & Confirm"};
        String courseId = null, courseCode = null, courseTitle = null;
        int creditHours = 3;
        Department dept = null;
        Teacher teacher = null;

        int step = 1;
        while (true) {
            try {
                if (step == 1) {
                    printStepHeader("COURSE REGISTRATION", 1, 4, steps);
                    courseId = promptUniqueId("01 Course ID    [String | unique | 4-30 chars] : ", false);
                    while (true) {
                        String code = InputHelper.readFormString("02 Course code  [String | unique | 3-15 chars] : ", true);
                        if (code.length() < 3 || code.length() > 15) {
                            System.out.println("! Course code must be 3-15 characters.");
                            continue;
                        }
                        if (store.isCourseCodeTaken(code)) {
                            System.out.println("! Course code '" + code + "' already exists.");
                            continue;
                        }
                        courseCode = code;
                        break;
                    }
                    courseTitle = promptName("03 Course title [String | 2-100 chars]         : ", 100, true);
                    step = 2;
                } else if (step == 2) {
                    printStepHeader("COURSE REGISTRATION", 2, 4, steps);
                    while (true) {
                        int ch = InputHelper.readFormInt("04 Credit hours [int | 1-6]                    : ", true);
                        if (ch < 1 || ch > 6) {
                            System.out.println("! Credit hours must be between 1 and 6.");
                            continue;
                        }
                        creditHours = ch;
                        break;
                    }
                    step = 3;
                } else if (step == 3) {
                    printStepHeader("COURSE REGISTRATION", 3, 4, steps);
                    System.out.println("05 Department       [Department | select]");
                    dept = selectOrCreateDepartment("course registration");
                    System.out.println("06 Assigned teacher [Teacher | select]");
                    teacher = selectOrCreateTeacher("course registration");
                    step = 4;
                } else if (step == 4) {
                    while (true) {
                        System.out.println("\n+------------------ REVIEW COURSE ---------------------------+");
                        System.out.println("| 01 Course ID        : " + courseId);
                        System.out.println("| 02 Course code      : " + courseCode);
                        System.out.println("| 03 Course title     : " + courseTitle);
                        System.out.println("| 04 Credit hours     : " + creditHours);
                        System.out.println("| 05 Department       : " + dept.getRegistrationSummary());
                        System.out.println("| 06 Assigned teacher : " + teacher.getRegistrationSummary());
                        System.out.println("+------------------------------------------------------------+");
                        System.out.println("| [1] Save course  [2] Edit field  [0] Cancel              |");
                        System.out.println("+------------------------------------------------------------+");

                        int opt = InputHelper.readMenuChoice("Select option: ", 0, 2);
                        if (opt == 0) {
                            System.out.println("Course registration cancelled.");
                            return null;
                        } else if (opt == 2) {
                            int f = InputHelper.readMenuChoice("Enter field number to edit (1-6, or 0 to return): ", 0, 6);
                            switch (f) {
                                case 1 -> courseId = promptUniqueId("New 01 Course ID: ", false);
                                case 2 -> courseCode = InputHelper.readFormString("New 02 Course code: ", false);
                                case 3 -> courseTitle = promptName("New 03 Course title: ", 100, false);
                                case 4 -> {
                                    int ch = InputHelper.readFormInt("New 04 Credit hours (1-6): ", false);
                                    if (ch >= 1 && ch <= 6) creditHours = ch;
                                    else System.out.println("! Must be 1-6.");
                                }
                                case 5 -> dept = selectOrCreateDepartment("course edit");
                                case 6 -> teacher = selectOrCreateTeacher("course edit");
                            }
                        } else {
                            Course course = new Course(courseId, courseCode, courseTitle, creditHours, dept, teacher);
                            store.ensureUniqueId(courseId);
                            course.validate();
                            store.getCourses().add(course);
                            System.out.println("+ Course " + course.getDisplayName() + " registered successfully.");
                            return course;
                        }
                    }
                }
            } catch (FormBackException e) {
                step = Math.max(1, step - 1);
            } catch (FormCancelException e) {
                System.out.println("Course registration cancelled.");
                return null;
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! Validation error: " + e.getMessage());
            }
        }
    }
    // =========================================================================
    // 6. REGISTER CLASSROOM (4-Step Guided Form)
    // =========================================================================
    public Classroom registerClassroom() {
        String[] steps = {"Room Identity", "Capacity & Floor", "Availability", "Review & Confirm"};
        String classroomId = null, buildingName = null, roomNumber = null;
        int capacity = 30, floorNumber = 0;
        boolean available = true;

        int step = 1;
        while (true) {
            try {
                if (step == 1) {
                    printStepHeader("CLASSROOM REGISTRATION", 1, 4, steps);
                    classroomId = promptUniqueId("01 Classroom ID  [String | unique | 4-30 chars] : ", false);
                    buildingName = promptName("02 Building name [String | 2-60 chars]          : ", 60, true);
                    roomNumber = InputHelper.readFormString("03 Room number   [String | non-blank]           : ", true);
                    step = 2;
                } else if (step == 2) {
                    printStepHeader("CLASSROOM REGISTRATION", 2, 4, steps);
                    while (true) {
                        int cap = InputHelper.readFormInt("04 Capacity      [int | > 0]                    : ", true);
                        if (cap <= 0) {
                            System.out.println("! Capacity must be greater than zero.");
                            continue;
                        }
                        capacity = cap;
                        break;
                    }
                    while (true) {
                        int flr = InputHelper.readFormInt("05 Floor number  [int | >= 0]                   : ", true);
                        if (flr < 0) {
                            System.out.println("! Floor number must be 0 or greater.");
                            continue;
                        }
                        floorNumber = flr;
                        break;
                    }
                    step = 3;
                } else if (step == 3) {
                    printStepHeader("CLASSROOM REGISTRATION", 3, 4, steps);
                    available = InputHelper.readFormBooleanChoice("06 Available     [1 YES / 2 NO]                 : ", "YES", "NO", true);
                    step = 4;
                } else if (step == 4) {
                    while (true) {
                        System.out.println("\n+---------------- REVIEW CLASSROOM --------------------------+");
                        System.out.println("| 01 Classroom ID  : " + classroomId);
                        System.out.println("| 02 Building name : " + buildingName);
                        System.out.println("| 03 Room number   : " + roomNumber);
                        System.out.println("| 04 Capacity      : " + capacity);
                        System.out.println("| 05 Floor number  : " + floorNumber);
                        System.out.println("| 06 Available     : " + (available ? "YES" : "NO"));
                        System.out.println("+------------------------------------------------------------+");
                        System.out.println("| Save classroom  Edit field  Cancel           |");
                        System.out.println("+------------------------------------------------------------+");

                        int opt = InputHelper.readMenuChoice("Select option: ", 0, 2);
                        if (opt == 0) {
                            System.out.println("Classroom registration cancelled.");
                            return null;
                        } else if (opt == 2) {
                            int f = InputHelper.readMenuChoice("Enter field number to edit (1-6, or 0 to return): ", 0, 6);
                            switch (f) {
                                case 1 -> classroomId = promptUniqueId("New 01 Classroom ID: ", false);
                                case 2 -> buildingName = promptName("New 02 Building name: ", 60, false);
                                case 3 -> roomNumber = InputHelper.readFormString("New 03 Room number: ", false);
                                case 4 -> capacity = InputHelper.readFormInt("New 04 Capacity (>0): ", false);
                                case 5 -> floorNumber = InputHelper.readFormInt("New 05 Floor number (>=0): ", false);
                                case 6 -> available = InputHelper.readFormBooleanChoice("New 06 Available [1 YES / 2 NO]: ", "YES", "NO", false);
                            }
                        } else {
                            Classroom room = new Classroom(classroomId, buildingName, roomNumber, capacity, floorNumber, available);
                            store.ensureUniqueId(classroomId);
                            room.validate();
                            store.getClassrooms().add(room);
                            System.out.println("+ Classroom " + room.getDisplayName() + " registered successfully.");
                            return room;
                        }
                    }
                }
            } catch (FormBackException e) {
                step = Math.max(1, step - 1);
            } catch (FormCancelException e) {
                System.out.println("Classroom registration cancelled.");
                return null;
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! Validation error: " + e.getMessage());
            }
        }
    }
    // =========================================================================
    // 7. ENROLL STUDENT (4-Step Guided Form with Prerequisite & Duplicate Guard)
    // =========================================================================
    public Enrollment registerEnrollment() {
        String[] steps = {"Enrollment ID", "Student & Course", "Term & Status", "Review & Confirm"};
        String enrollmentId = null, semester = null, status = null;
        Student student = null;
        Course course = null;
        LocalDate enrollmentDate = null;

        int step = 1;
        while (true) {
            try {
                if (step == 1) {
                    printStepHeader("STUDENT ENROLLMENT", 1, 4, steps);
                    enrollmentId = promptUniqueId("01 Enrollment ID [String | unique | 4-30 chars] : ", false);
                    step = 2;
                } else if (step == 2) {
                    printStepHeader("STUDENT ENROLLMENT", 2, 4, steps);
                    System.out.println("02 Student [Student | select]");
                    student = selectOrCreateStudent("enrollment");
                    System.out.println("03 Course  [Course | select]");
                    course = selectOrCreateCourse("enrollment");
                    step = 3;
                } else if (step == 3) {
                    printStepHeader("STUDENT ENROLLMENT", 3, 4, steps);
                    enrollmentDate = promptPastDate("04 Enrollment date   [LocalDate | YYYY-MM-DD]   : ", true, true);
                    semester = promptChoiceFromList("05 Semester          [1 SEMESTER 1 / 2 SEMESTER 2 / 3 SUMMER]: ",
                            new String[]{"SEMESTER 1", "SEMESTER 2", "SUMMER"}, true);
                    status = promptChoiceFromList("06 Enrollment status [1 ACTIVE / 2 DROPPED / 3 COMPLETED]: ",
                            new String[]{"ACTIVE", "DROPPED", "COMPLETED"}, true);

                    if ("ACTIVE".equalsIgnoreCase(status) && store.hasActiveEnrollment(student, course, semester)) {
                        System.out.println("! Duplicate active enrollment: " + student.getDisplayName() +
                                " is already actively enrolled in " + course.getCourseCode() + " for " + semester + ".");
                        continue;
                    }
                    step = 4;
                } else if (step == 4) {
                    while (true) {
                        System.out.println("\n+---------------- REVIEW ENROLLMENT -------------------------+");
                        System.out.println("| 01 Enrollment ID     : " + enrollmentId);
                        System.out.println("| 02 Student           : " + student.getRegistrationSummary());
                        System.out.println("| 03 Course            : " + course.getRegistrationSummary());
                        System.out.println("| 04 Enrollment date   : " + enrollmentDate);
                        System.out.println("| 05 Semester          : " + semester);
                        System.out.println("| 06 Enrollment status : " + status);
                        System.out.println("+------------------------------------------------------------+");
                        System.out.println("| [1] Save enrollment  [2] Edit field  [0] Cancel          |");
                        System.out.println("+------------------------------------------------------------+");

                        int opt = InputHelper.readMenuChoice("Select option: ", 0, 2);
                        if (opt == 0) {
                            System.out.println("Enrollment cancelled.");
                            return null;
                        } else if (opt == 2) {
                            int f = InputHelper.readMenuChoice("Enter field number to edit (1-6, or 0 to return): ", 0, 6);
                            switch (f) {
                                case 1 -> enrollmentId = promptUniqueId("New 01 Enrollment ID: ", false);
                                case 2 -> student = selectOrCreateStudent("enrollment edit");
                                case 3 -> course = selectOrCreateCourse("enrollment edit");
                                case 4 -> enrollmentDate = promptPastDate("New 04 Enrollment date: ", true, false);
                                case 5 -> semester = promptChoiceFromList("New 05 Semester [1 SEMESTER 1 / 2 SEMESTER 2 / 3 SUMMER]: ",
                                        new String[]{"SEMESTER 1", "SEMESTER 2", "SUMMER"}, false);
                                case 6 -> status = promptChoiceFromList("New 06 Status [1 ACTIVE / 2 DROPPED / 3 COMPLETED]: ",
                                        new String[]{"ACTIVE", "DROPPED", "COMPLETED"}, false);
                            }
                        } else {
                            if ("ACTIVE".equalsIgnoreCase(status) && store.hasActiveEnrollment(student, course, semester)) {
                                throw new DuplicateRecordException("Duplicate active enrollment for the same student, course, and semester.");
                            }
                            Enrollment enrollment = new Enrollment(enrollmentId, student, course, enrollmentDate, semester, status);
                            store.ensureUniqueId(enrollmentId);
                            enrollment.validate();
                            store.getEnrollments().add(enrollment);
                            System.out.println("+ Enrollment " + enrollmentId + " saved successfully.");
                            return enrollment;
                        }
                    }
                }
            } catch (FormBackException e) {
                step = Math.max(1, step - 1);
            } catch (FormCancelException e) {
                System.out.println("Enrollment cancelled.");
                return null;
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! Validation error: " + e.getMessage());
            }
        }
    }
    // =========================================================================
    // 8. RECORD ASSESSMENT (4-Step Guided Form - Active Enrollments Only)
    // =========================================================================
    public Assessment registerAssessment() {
        String[] steps = {"Assessment ID", "Active Enrollment", "Scores & Date", "Review & Confirm"};
        String assessmentId = null, assessmentType = null;
        Enrollment enrollment = null;
        double maxScore = 100.0, obtainedScore = 0.0;
        LocalDate assessmentDate = null;

        int step = 1;
        while (true) {
            try {
                if (step == 1) {
                    printStepHeader("RECORD ASSESSMENT", 1, 4, steps);
                    assessmentId = promptUniqueId("01 Assessment ID [String | unique | 4-30 chars] : ", false);
                    step = 2;
                } else if (step == 2) {
                    printStepHeader("RECORD ASSESSMENT", 2, 4, steps);
                    System.out.println("02 Active Enrollment [Enrollment | select]");
                    enrollment = selectOrCreateActiveEnrollment("assessment recording");
                    step = 3;
                } else if (step == 3) {
                    printStepHeader("RECORD ASSESSMENT", 3, 4, steps);
                    assessmentType = promptChoiceFromList("03 Assessment type [1 QUIZ / 2 ASSIGNMENT / 3 MIDTERM / 4 FINAL]: ",
                            new String[]{"QUIZ", "ASSIGNMENT", "MIDTERM", "FINAL"}, true);
                    while (true) {
                        double max = InputHelper.readFormDouble("04 Maximum score   [double | > 0]                : ", true);
                        if (max <= 0) {
                            System.out.println("! Maximum score must be greater than zero.");
                            continue;
                        }
                        maxScore = max;
                        break;
                    }
                    while (true) {
                        double obt = InputHelper.readFormDouble("05 Obtained score  [double | 0 to " + maxScore + "]     : ", true);
                        if (obt < 0 || obt > maxScore) {
                            System.out.println("! Obtained score must be between 0 and " + maxScore + ".");
                            continue;
                        }
                        obtainedScore = obt;
                        break;
                    }
                    assessmentDate = promptPastDate("06 Assessment date [LocalDate | YYYY-MM-DD]      : ", true, true);
                    step = 4;
                } else if (step == 4) {
                    while (true) {
                        System.out.println("\n+---------------- REVIEW ASSESSMENT -------------------------+");
                        System.out.println("| 01 Assessment ID   : " + assessmentId);
                        System.out.println("| 02 Enrollment      : " + enrollment.getRegistrationSummary());
                        System.out.println("| 03 Assessment type : " + assessmentType);
                        System.out.println("| 04 Maximum score   : " + maxScore);
                        System.out.println("| 05 Obtained score  : " + obtainedScore);
                        System.out.println("| 06 Assessment date : " + assessmentDate);
                        System.out.println("+------------------------------------------------------------+");
                        System.out.println("| Save assessment  Edit field  Cancel          |");
                        System.out.println("+------------------------------------------------------------+");

                        int opt = InputHelper.readMenuChoice("Select option: ", 0, 2);
                        if (opt == 0) {
                            System.out.println("Assessment recording cancelled.");
                            return null;
                        } else if (opt == 2) {
                            int f = InputHelper.readMenuChoice("Enter field number to edit (1-6, or 0 to return): ", 0, 6);
                            switch (f) {
                                case 1 -> assessmentId = promptUniqueId("New 01 Assessment ID: ", false);
                                case 2 -> enrollment = selectOrCreateActiveEnrollment("assessment edit");
                                case 3 -> assessmentType = promptChoiceFromList("New 03 Type [1 QUIZ / 2 ASSIGNMENT / 3 MIDTERM / 4 FINAL]: ",
                                        new String[]{"QUIZ", "ASSIGNMENT", "MIDTERM", "FINAL"}, false);
                                case 4 -> maxScore = InputHelper.readFormDouble("New 04 Maximum score (>0): ", false);
                                case 5 -> obtainedScore = InputHelper.readFormDouble("New 05 Obtained score: ", false);
                                case 6 -> assessmentDate = promptPastDate("New 06 Assessment date: ", true, false);
                            }
                        } else {
                            Assessment assessment = new Assessment(assessmentId, enrollment, assessmentType, maxScore, obtainedScore, assessmentDate);
                            store.ensureUniqueId(assessmentId);
                            assessment.validate();
                            store.getAssessments().add(assessment);
                            System.out.println("+ Assessment " + assessmentId + " recorded successfully.");
                            return assessment;
                        }
                    }
                }
            } catch (FormBackException e) {
                step = Math.max(1, step - 1);
            } catch (FormCancelException e) {
                System.out.println("Assessment recording cancelled.");
                return null;
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! Validation error: " + e.getMessage());
            }
        }
    }
    // =========================================================================
    // 9. RECORD FEE PAYMENT (4-Step Guided Form with Receipt Uniqueness)
    // =========================================================================
    public FeePayment registerFeePayment() {
        String[] steps = {"Payment ID", "Student Selection", "Amount & Receipt", "Review & Confirm"};
        String paymentId = null, paymentMethod = null, receiptNumber = null;
        Student student = null;
        BigDecimal amount = null;
        LocalDate paymentDate = null;

        int step = 1;
        while (true) {
            try {
                if (step == 1) {
                    printStepHeader("RECORD FEE PAYMENT", 1, 4, steps);
                    paymentId = promptUniqueId("01 Payment ID [String | unique | 4-30 chars] : ", false);
                    step = 2;
                } else if (step == 2) {
                    printStepHeader("RECORD FEE PAYMENT", 2, 4, steps);
                    System.out.println("02 Student [Student | select]");
                    student = selectOrCreateStudent("fee payment");
                    step = 3;
                } else if (step == 3) {
                    printStepHeader("RECORD FEE PAYMENT", 3, 4, steps);
                    while (true) {
                        BigDecimal amt = InputHelper.readFormBigDecimal("03 Amount         [BigDecimal | > 0]           : ", true);
                        if (amt.compareTo(BigDecimal.ZERO) <= 0) {
                            System.out.println("! Payment amount must be strictly positive.");
                            continue;
                        }
                        amount = amt;
                        break;
                    }
                    paymentDate = promptPastDate("04 Payment date   [LocalDate | YYYY-MM-DD]     : ", true, true);
                    paymentMethod = promptChoiceFromList("05 Payment method [1 CASH / 2 BANK TRANSFER / 3 MOBILE MONEY / 4 CARD]: ",
                            new String[]{"CASH", "BANK TRANSFER", "MOBILE MONEY", "CARD"}, true);
                    while (true) {
                        String rcpt = InputHelper.readFormString("06 Receipt number [String | unique | non-blank]: ", true);
                        if (store.isReceiptNumberTaken(rcpt)) {
                            System.out.println("! Receipt number '" + rcpt + "' already exists.");
                            continue;
                        }
                        receiptNumber = rcpt;
                        break;
                    }
                    step = 4;
                } else if (step == 4) {
                    while (true) {
                        System.out.println("\n+---------------- REVIEW FEE PAYMENT ------------------------+");
                        System.out.println("| 01 Payment ID     : " + paymentId);
                        System.out.println("| 02 Student        : " + student.getRegistrationSummary());
                        System.out.println("| 03 Amount         : " + amount);
                        System.out.println("| 04 Payment date   : " + paymentDate);
                        System.out.println("| 05 Payment method : " + paymentMethod);
                        System.out.println("| 06 Receipt number : " + receiptNumber);
                        System.out.println("+------------------------------------------------------------+");
                        System.out.println("| Save payment  Edit field  Cancel               |");
                        System.out.println("+------------------------------------------------------------+");

                        int opt = InputHelper.readMenuChoice("Select option: ", 0, 2);
                        if (opt == 0) {
                            System.out.println("Fee payment cancelled.");
                            return null;
                        } else if (opt == 2) {
                            int f = InputHelper.readMenuChoice("Enter field number to edit (1-6, or 0 to return): ", 0, 6);
                            switch (f) {
                                case 1 -> paymentId = promptUniqueId("New 01 Payment ID: ", false);
                                case 2 -> student = selectOrCreateStudent("fee payment edit");
                                case 3 -> amount = InputHelper.readFormBigDecimal("New 03 Amount (>0): ", false);
                                case 4 -> paymentDate = promptPastDate("New 04 Payment date: ", true, false);
                                case 5 -> paymentMethod = promptChoiceFromList("New 05 Method [1 CASH / 2 BANK TRANSFER / 3 MOBILE MONEY / 4 CARD]: ",
                                        new String[]{"CASH", "BANK TRANSFER", "MOBILE MONEY", "CARD"}, false);
                                case 6 -> {
                                    String r = InputHelper.readFormString("New 06 Receipt number: ", false);
                                    if (!r.equalsIgnoreCase(receiptNumber) && store.isReceiptNumberTaken(r)) {
                                        System.out.println("! Receipt number already exists.");
                                    } else receiptNumber = r;
                                }
                            }
                        } else {
                            if (store.isReceiptNumberTaken(receiptNumber)) {
                                throw new DuplicateRecordException("Receipt number " + receiptNumber + " already exists.");
                            }
                            FeePayment payment = new FeePayment(paymentId, student, amount, paymentDate, paymentMethod, receiptNumber);
                            store.ensureUniqueId(paymentId);
                            payment.validate();
                            store.getFeePayments().add(payment);
                            System.out.println("+ Fee payment " + paymentId + " (Receipt #" + receiptNumber + ") saved successfully.");
                            return payment;
                        }
                    }
                }
            } catch (FormBackException e) {
                step = Math.max(1, step - 1);
            } catch (FormCancelException e) {
                System.out.println("Fee payment cancelled.");
                return null;
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! Validation error: " + e.getMessage());
            }
        }
    }
    // =========================================================================
    // 10. REGISTER LIBRARY BOOK (4-Step Guided Form)
    // =========================================================================
    public LibraryBook registerLibraryBook() {
        String[] steps = {"Book Identity", "Title & Author", "Inventory Copies", "Review & Confirm"};
        String bookId = null, isbn = null, title = null, author = null;
        int totalCopies = 1, availableCopies = 1;

        int step = 1;
        while (true) {
            try {
                if (step == 1) {
                    printStepHeader("LIBRARY BOOK REGISTRATION", 1, 4, steps);
                    bookId = promptUniqueId("01 Book ID [String | unique | 4-30 chars] : ", false);
                    isbn = InputHelper.readFormString("02 ISBN    [String | non-blank]           : ", true);
                    step = 2;
                } else if (step == 2) {
                    printStepHeader("LIBRARY BOOK REGISTRATION", 2, 4, steps);
                    title = InputHelper.readFormString("03 Title   [String | non-blank]           : ", true);
                    author = InputHelper.readFormString("04 Author  [String | non-blank]           : ", true);
                    step = 3;
                } else if (step == 3) {
                    printStepHeader("LIBRARY BOOK REGISTRATION", 3, 4, steps);
                    while (true) {
                        int tot = InputHelper.readFormInt("05 Total copies     [int | >= 0]          : ", true);
                        if (tot < 0) {
                            System.out.println("! Total copies cannot be negative.");
                            continue;
                        }
                        totalCopies = tot;
                        break;
                    }
                    while (true) {
                        int avail = InputHelper.readFormInt("06 Available copies [int | 0 to " + totalCopies + "]       : ", true);
                        if (avail < 0 || avail > totalCopies) {
                            System.out.println("! Available copies must be between 0 and " + totalCopies + ".");
                            continue;
                        }
                        availableCopies = avail;
                        break;
                    }
                    step = 4;
                } else if (step == 4) {
                    while (true) {
                        System.out.println("\n+---------------- REVIEW LIBRARY BOOK -----------------------+");
                        System.out.println("| 01 Book ID          : " + bookId);
                        System.out.println("| 02 ISBN             : " + isbn);
                        System.out.println("| 03 Title            : " + title);
                        System.out.println("| 04 Author           : " + author);
                        System.out.println("| 05 Total copies     : " + totalCopies);
                        System.out.println("| 06 Available copies : " + availableCopies);
                        System.out.println("+------------------------------------------------------------+");
                        System.out.println("| Save book  Edit field  Cancel                |");
                        System.out.println("+------------------------------------------------------------+");

                        int opt = InputHelper.readMenuChoice("Select option: ", 0, 2);
                        if (opt == 0) {
                            System.out.println("Library book registration cancelled.");
                            return null;
                        } else if (opt == 2) {
                            int f = InputHelper.readMenuChoice("Enter field number to edit (1-6, or 0 to return): ", 0, 6);
                            switch (f) {
                                case 1 -> bookId = promptUniqueId("New 01 Book ID: ", false);
                                case 2 -> isbn = InputHelper.readFormString("New 02 ISBN: ", false);
                                case 3 -> title = InputHelper.readFormString("New 03 Title: ", false);
                                case 4 -> author = InputHelper.readFormString("New 04 Author: ", false);
                                case 5 -> totalCopies = InputHelper.readFormInt("New 05 Total copies (>=0): ", false);
                                case 6 -> availableCopies = InputHelper.readFormInt("New 06 Available copies: ", false);
                            }
                        } else {
                            LibraryBook book = new LibraryBook(bookId, isbn, title, author, totalCopies, availableCopies);
                            store.ensureUniqueId(bookId);
                            book.validate();
                            store.getLibraryBooks().add(book);
                            System.out.println("+ LibraryBook \"" + title + "\" registered successfully.");
                            return book;
                        }
                    }
                }
            } catch (FormBackException e) {
                step = Math.max(1, step - 1);
            } catch (FormCancelException e) {
                System.out.println("Library book registration cancelled.");
                return null;
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! Validation error: " + e.getMessage());
            }
        }
    }
    // =========================================================================
    // 11. BORROW LIBRARY BOOK -> CREATES BookLoan (Decrements stock atomically)
    // =========================================================================
    public BookLoan registerBookLoan() {
        String[] steps = {"Loan ID", "Borrower & Book", "Loan Dates", "Review & Confirm"};
        String loanId = null;
        Student student = null;
        LibraryBook book = null;
        LocalDate borrowedDate = null, dueDate = null;

        int step = 1;
        while (true) {
            try {
                if (step == 1) {
                    printStepHeader("BORROW LIBRARY BOOK", 1, 4, steps);
                    loanId = promptUniqueId("01 Loan ID [String | unique | 4-30 chars] : ", false);
                    step = 2;
                } else if (step == 2) {
                    printStepHeader("BORROW LIBRARY BOOK", 2, 4, steps);
                    System.out.println("02 Student     [Student | select]");
                    student = selectOrCreateStudent("book borrowing");
                    while (true) {
                        System.out.println("03 LibraryBook [LibraryBook | select]");
                        LibraryBook chosen = selectOrCreateLibraryBook("book borrowing");
                        if (chosen.getAvailableCopies() <= 0) {
                            System.out.println("! BookUnavailableException: '" + chosen.getTitle() + "' has 0 available copies. Choose another book or type CANCEL.");
                            continue;
                        }
                        book = chosen;
                        break;
                    }
                    step = 3;
                } else if (step == 3) {
                    printStepHeader("BORROW LIBRARY BOOK", 3, 4, steps);
                    borrowedDate = promptPastDate("04 Borrowed date [LocalDate | YYYY-MM-DD] : ", true, true);
                    while (true) {
                        LocalDate due = InputHelper.readFormDate("05 Due date      [LocalDate | after borrow]: ", true);
                        if (!due.isAfter(borrowedDate)) {
                            System.out.println("! Due date must be strictly after borrowed date (" + borrowedDate + ").");
                            continue;
                        }
                        dueDate = due;
                        break;
                    }
                    System.out.println("06 Returned date : Not Returned (Auto-set to null until returned)");
                    step = 4;
                } else if (step == 4) {
                    while (true) {
                        System.out.println("\n+------------------ REVIEW BOOK LOAN ------------------------+");
                        System.out.println("| 01 Loan ID       : " + loanId);
                        System.out.println("| 02 Student       : " + student.getRegistrationSummary());
                        System.out.println("| 03 Book          : " + book.getRegistrationSummary());
                        System.out.println("| 04 Borrowed date : " + borrowedDate);
                        System.out.println("| 05 Due date      : " + dueDate);
                        System.out.println("| 06 Returned date : null (Active Loan)");
                        System.out.println("+------------------------------------------------------------+");
                        System.out.println("| Confirm loan  Edit field  Cancel               |");
                        System.out.println("+------------------------------------------------------------+");

                        int opt = InputHelper.readMenuChoice("Select option: ", 0, 2);
                        if (opt == 0) {
                            System.out.println("Book loan cancelled. Stock unchanged.");
                            return null;
                        } else if (opt == 2) {
                            int f = InputHelper.readMenuChoice("Enter field number to edit (1-5, or 0 to return): ", 0, 5);
                            switch (f) {
                                case 1 -> loanId = promptUniqueId("New 01 Loan ID: ", false);
                                case 2 -> student = selectOrCreateStudent("loan edit");
                                case 3 -> book = selectOrCreateLibraryBook("loan edit");
                                case 4 -> borrowedDate = promptPastDate("New 04 Borrowed date: ", true, false);
                                case 5 -> dueDate = InputHelper.readFormDate("New 05 Due date: ", false);
                            }
                        } else {
                            if (book.getAvailableCopies() <= 0) {
                                throw new BookUnavailableException("No copies remaining for '" + book.getTitle() + "'.");
                            }
                            BookLoan loan = new BookLoan(loanId, student, book, borrowedDate, dueDate, null);
                            store.ensureUniqueId(loanId);
                            loan.validate();

                            book.borrowCopy();
                            store.getBookLoans().add(loan);
                            System.out.println("+ Book loan " + loanId + " confirmed. Remaining copies of '" +
                                    book.getTitle() + "': " + book.getAvailableCopies() + "/" + book.getTotalCopies());
                            return loan;
                        }
                    }
                }
            } catch (FormBackException e) {
                step = Math.max(1, step - 1);
            } catch (FormCancelException e) {
                System.out.println("Book loan cancelled. Stock unchanged.");
                return null;
            } catch (ValidationException | DuplicateRecordException | BookUnavailableException e) {
                System.out.println("! Error: " + e.getMessage());
            }
        }
    }
}

