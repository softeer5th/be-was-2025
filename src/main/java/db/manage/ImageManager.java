package db.manage;

import db.Database;
import webserver.request.FileBody;

public class ImageManager {
    public static int addImage(String userId, FileBody fileBody) {
        if(fileBody == null) { throw new IllegalArgumentException("fileBody is null"); }

        try {
            return Database.addImage(userId, fileBody.contentType(), fileBody.data(), false);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
