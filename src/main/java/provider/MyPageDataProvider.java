package provider;

import db.transaction.TransactionTemplate;
import manager.UserManager;
import model.User;

import java.util.Map;

public class MyPageDataProvider implements DynamicDataProvider{
    private final TransactionTemplate transactionTemplate = TransactionTemplate.getInstance();
    private final UserManager userManager = UserManager.getInstance();
    private static final String USERNAME = "USERNAME";
    @Override
    public Model provideData(Map<String, Object> params) {
        Model model = new Model();

        User user = transactionTemplate.execute(userManager::getUser, params.get("userId"));

        model.put(USERNAME, user.getName());
        return model;
    }
}
