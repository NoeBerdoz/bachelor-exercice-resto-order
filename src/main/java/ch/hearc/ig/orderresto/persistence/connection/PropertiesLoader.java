package ch.hearc.ig.orderresto.persistence.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesLoader {

    private Properties properties;

    public PropertiesLoader(String filePath) {
        properties = new Properties();
        try (FileInputStream file = new FileInputStream(filePath)) {
            properties.load(file);
        } catch (IOException error) {
            System.err.println("[-] Failed to load properties file: " + filePath);
            throw new RuntimeException("Properties file loading error: " + error.getMessage());        }
    }

    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            System.err.println("[-] Missing property: " + key);
            throw new RuntimeException("Missing required property: " + key);
        }
        return value;
    }
}