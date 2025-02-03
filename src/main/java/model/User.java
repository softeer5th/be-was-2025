package model;

public class User {
    private Long id;
    private String loginId;
    private String password;
    private String name;
    private String email;
    private byte[] profileImage;

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, String loginId, String password, String name, String email, byte[] profileImage) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
    }

    public Long getId() {return id;}
    public String getLoginId() {
        return loginId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public byte[] getProfileImage(){ return profileImage; }
}
