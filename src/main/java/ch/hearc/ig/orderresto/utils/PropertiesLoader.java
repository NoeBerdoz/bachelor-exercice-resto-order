package ch.hearc.ig.orderresto.utils;

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
            error.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}