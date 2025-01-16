package util.enums;

public enum CookieName {
    SESSION_COOKIE("sid");

    private final String name;

    CookieName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
