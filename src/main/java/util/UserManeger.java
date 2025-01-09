package util;

import db.Database;
import model.User;

public class UserManeger {
    public void addUser(String parameter) throws IllegalArgumentException {
        String[] tokens = parameter.split("&");
        String[] params = new String[tokens.length];
        try{
            for(int i = 0; i < tokens.length; i++){
                params[i] = tokens[i].split("=")[1];
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            throw new IllegalArgumentException("Invalid parameter: " + parameter);
        }
        User user = new User(params[0], params[1], params[2], "");
        Database.addUser(user);
    }



}
