package objects;

public enum TaskType {

    PARTYMONITOR("Party Monitoring"), CLEAN("Cleaning Up Event"), SETUP("Setting Up Event"), SPECIAL("Special Event Attendance");
    // declaring private variable for getting values
    private String formName;

    // getter method
    public String getFormName()
    {
        return this.formName;
    }

    public String toString() {
        return this.formName;
    }

    // enum constructor - cannot be public or protected
    TaskType(String name)
    {
        this.formName = name;
    }

}
