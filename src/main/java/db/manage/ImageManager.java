package db.manage;

import db.Database;
import model.Image;
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

    public static Image getImage(int imageId) {
        return Database.getImageById(imageId);
    }
}
