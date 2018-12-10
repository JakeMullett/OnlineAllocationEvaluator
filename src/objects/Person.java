package objects;

public class Person {
    private String name;
    private Preferences prefs;

    public Person(String newName, Preferences preferences) {
        name = newName;
        prefs = preferences;
    }

    public Preferences getPrefs() {
        return prefs;
    }

    public String getName() {
        return name;
    }
}
