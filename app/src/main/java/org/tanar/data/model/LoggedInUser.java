package org.tanar.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private Boolean isTutor;
    private Double latitude;


    private String classNumber;
    private Double longitude;
    private Double altitude;

    public LoggedInUser(String userId, String displayName, Boolean isTutor, Double latitude, Double longitude, Double altitude, String classNumber) {
        this.userId = userId;
        this.displayName = displayName;
        this.isTutor = isTutor;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.classNumber=classNumber;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public Boolean isTutor() {
        return isTutor;
    }

    public String getClassNumber() {
        return classNumber;
    }

}