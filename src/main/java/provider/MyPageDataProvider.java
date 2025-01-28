package provider;

import db.transaction.TransactionTemplate;
import manager.UserManager;
import model.User;

import java.util.Base64;
import java.util.Map;

public class MyPageDataProvider implements DynamicDataProvider{
    private final TransactionTemplate transactionTemplate = TransactionTemplate.getInstance();
    private final UserManager userManager = UserManager.getInstance();
    private static final String USERNAME = "USERNAME";
    private static final String PROFILE_IMAGE = "PROFILE_IMAGE";
    @Override
    public Model provideData(Map<String, Object> params) {
        Model model = new Model();

        User user = transactionTemplate.execute(userManager::getUser, params.get("userId"));

        if(user.getProfileImage() == null){
            model.put(PROFILE_IMAGE, createNullProfileImage());
        }else{
            String base64Image = Base64.getEncoder().encodeToString(user.getProfileImage());
            model.put(PROFILE_IMAGE, createProfileImage(base64Image));
        }

        model.put(USERNAME, user.getName());
        return model;
    }

    public String createNullProfileImage(){
        StringBuilder sb = new StringBuilder();
        sb.append("<img id=\"profile-image\" class=\"profile\" />");
        return sb.toString();
    }

    public String createProfileImage(String base64Image){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<img id=\"profile-image\" class=\"profile\" src=\"data:image/jpeg;base64,%s\"/>", base64Image));
        return sb.toString();
    }


}
