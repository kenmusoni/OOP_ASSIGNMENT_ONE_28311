package com.auca.interfaces;

public interface Identifiable {
    //  Mandatory 6 Interface Constants
    public static final String ID_PREFIX = "AUCA_";
    public static final int MIN_ID_LENGTH = 4;
    public static final int MAX_ID_LENGTH = 30;
    public static final String ID_SEPARATOR = "-";
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final int ENTITY_VERSION = 1;

    //  Required Method Contracts
    String getId();
    String getDisplayName();
    boolean isActive();
}
