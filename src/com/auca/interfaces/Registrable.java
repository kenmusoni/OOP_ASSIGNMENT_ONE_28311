package com.auca.interfaces;

import com.auca.exceptions.ValidationException;

public interface Registrable {
    // Section 3.2: Mandatory 6 Interface Constants
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 60;
    public static final int MAX_EMAIL_LENGTH = 50;
    public static final int MIN_PHONE_LENGTH = 10;
    public static final int MAX_PHONE_LENGTH = 15;
    public static final String DATE_PATTERN = "YYYY-MM-DD";

    // Section 4: Required Method Contracts
    void validate() throws ValidationException;
    void printDetails();
    String getRegistrationSummary();

    // Section 4 Cleaner Alternative: Permitted default implementations 
    // so non-person entities do not have to write dummy logic.
    default String getRoleDescription() {
        return "System Entity";
    }

    default String getAcademicCategory() {
        return "Non-Academic";
    }
}
