package uk.ac.mmu.electricchargingproject;


public class MyDataModel {


    private int id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String buildingName;
    private String buildingNumber;
    private String thoroughfare;
    private Double distance;


    public boolean isPaymentRequired() {
        return paymentRequired;
    }

    public void setPaymentRequired(boolean paymentRequired) {
        this.paymentRequired = paymentRequired;
    }

    private boolean paymentRequired;


    public MyDataModel(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public MyDataModel(String name, Double latitude, Double longitude, String buildingName, String buildingNumber, String thoroughfare, String street, String town, String county, String postcode, String locationLongDescription, String locationType) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.buildingName = buildingName;
        this.buildingNumber = buildingNumber;
        this.thoroughfare = thoroughfare;
        this.street = street;
        this.town = town;
        this.county = county;
        this.postcode = postcode;
        this.locationLongDescription = locationLongDescription;
        this.locationType = locationType;
    }

    public MyDataModel(int id, String name, Double latitude, Double longitude, String buildingName, String buildingNumber, String thoroughfare, String street, String town, String county, String postcode, String locationLongDescription, String locationType) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.buildingName = buildingName;
        this.buildingNumber = buildingNumber;
        this.thoroughfare = thoroughfare;
        this.street = street;
        this.town = town;
        this.county = county;
        this.postcode = postcode;
        this.locationLongDescription = locationLongDescription;
        this.locationType = locationType;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getThoroughfare() {
        return thoroughfare;
    }

    public void setThoroughfare(String thoroughfare) {
        this.thoroughfare = thoroughfare;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getLocationLongDescription() {
        return locationLongDescription;
    }

    public void setLocationLongDescription(String locationLongDescription) {
        this.locationLongDescription = locationLongDescription;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    private String street;
    private String town;
    private String county;
    private String postcode;
    private String locationLongDescription;
    private String locationType;

    /// Empty constructor
    public MyDataModel() {

    }
}


