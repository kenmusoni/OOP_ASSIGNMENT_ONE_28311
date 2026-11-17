package com.auca.classroom;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

public class Classroom implements Identifiable, Registrable {
    // Section 3.10: Mandatory 6 declared attributes
    private String classroomId;
    private String buildingName;
    private String roomNumber;
    private int capacity;
    private int floorNumber;
    private boolean available;

    public Classroom(String classroomId, String buildingName, String roomNumber,
                     int capacity, int floorNumber, boolean available) {
        this.classroomId = classroomId;
        this.buildingName = buildingName;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.floorNumber = floorNumber;
        this.available = available;
    }

    @Override
    public String getId() {
        return classroomId;
    }

    @Override
    public String getDisplayName() {
        return buildingName + " - Room " + roomNumber;
    }

    @Override
    public boolean isActive() {
        return available;
    }

    @Override
    public void validate() throws ValidationException {
        if (classroomId == null || classroomId.trim().isEmpty()) {
            throw new ValidationException("Classroom ID cannot be blank.");
        }
        String cleanId = classroomId.trim();
        if (cleanId.length() < MIN_ID_LENGTH || cleanId.length() > MAX_ID_LENGTH) {
            throw new ValidationException("Classroom ID must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }

        if (buildingName == null || buildingName.trim().isEmpty()) {
            throw new ValidationException("Building name cannot be blank.");
        }
        String cleanBuilding = buildingName.trim();
        if (cleanBuilding.length() < MIN_NAME_LENGTH || cleanBuilding.length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Building name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
        }

        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new ValidationException("Room number cannot be blank.");
        }

        if (capacity <= 0) {
            throw new ValidationException("Classroom capacity must be greater than zero.");
        }

        if (floorNumber < 0) {
            throw new ValidationException("Floor number must be zero or greater.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========================================");
        System.out.println("CLASSROOM RECORD DETAILS");
        System.out.println("========================================");
        System.out.println("Classroom ID  : " + classroomId);
        System.out.println("Building Name : " + buildingName);
        System.out.println("Room Number   : " + roomNumber);
        System.out.println("Capacity      : " + capacity);
        System.out.println("Floor Number  : " + floorNumber);
        System.out.println("Available     : " + (available ? "YES" : "NO"));
        System.out.println("========================================");
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + classroomId + "] " + getDisplayName() + " (Cap: " + capacity + ", Floor: " + floorNumber + ")";
    }

    // Getters and Setters
    public String getClassroomId() { return classroomId; }
    public void setClassroomId(String classroomId) { this.classroomId = classroomId; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getFloorNumber() { return floorNumber; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return "Classroom{id='" + classroomId + "', building='" + buildingName + "', room='" + roomNumber + "'}";
    }
}
