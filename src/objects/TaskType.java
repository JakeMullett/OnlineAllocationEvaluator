package objects;

public enum TaskType {

    PARTYMONITOR("Party Monitoring"), CLEAN("Cleaning"), SETUP("Setting Up");
    // declaring private variable for getting values
    private String formName;

    // getter method
    public String getFormName()
    {
        return this.formName;
    }

    // enum constructor - cannot be public or protected
    TaskType(String name)
    {
        this.formName = name;
    }

}
