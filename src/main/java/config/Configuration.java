package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static final Properties properties = new Properties();
    private static String secretKey;

    static {
        try (InputStream input = Configuration.class.getClassLoader().getResourceAsStream(".env")) {
            if (input == null) {
                throw new IllegalArgumentException(".env file not found in classpath.");
            }
            properties.load(input);
            secretKey = properties.getProperty("jwt.secret");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load .env file", e);
        }
    }

    public static String getSecretKey() {
        return secretKey;
    }
}