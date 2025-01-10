package util;

import db.Database;
import model.User;

public class UserManager {
    public void addUser(String parameter) throws IllegalArgumentException {
        String[] tokens = parameter.split("&");
        String[] params = new String[tokens.length];
        try{
            for(int i = 0; i < tokens.length; i++){
                params[i] = tokens[i].split("=")[1];
            }

            if(Database.findUserById(params[0]) == null){
                User user = new User(params[0], params[1], params[2], "");
                Database.addUser(user);
            }
            else throw new IllegalArgumentException("id: " + params[0] + " is already exists");
        }
        catch (ArrayIndexOutOfBoundsException e){
            throw new IllegalArgumentException("Invalid parameter: " + parameter);
        }
    }



}
