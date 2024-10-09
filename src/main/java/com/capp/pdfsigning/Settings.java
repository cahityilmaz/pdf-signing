package com.capp.pdfsigning;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public final class Settings extends Properties {

    public static final String STORE_PASSWORD = "store.password";
    public static final String PKCS11_LIBRARY_PATH = "pkcs11.library.path";
    public static final String TSA_SERVER_URL = "tsa.server.url";
    public static final String TSA_USERNAME = "tsa.username";
    public static final String TSA_PASSWORD = "tsa.password";

    private static Settings INSTANCE = null;

    public static Settings getInstance() {
        if (INSTANCE == null) {
            try {
                INSTANCE = createInstance();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return INSTANCE;
    }

    public String getStorePassword() {
        return getInstance().getProperty(STORE_PASSWORD);
    }

    public String getPkcs11LibraryPath() {
        return getInstance().getProperty(PKCS11_LIBRARY_PATH);
    }

    public String getTsaServerUrl() {
        return getInstance().getProperty(TSA_SERVER_URL);
    }

    public String getTsaServerUsername() {
        return getInstance().getProperty(TSA_USERNAME);
    }

    public String getTsaServerPassword() {
        return getInstance().getProperty(TSA_PASSWORD);
    }

    private static Settings createInstance() throws IOException {
        var settings = new Settings();
        settings.load(new FileReader("settings.properties"));
        return settings;
    }

    @Override
    public String toString() {
        return "Settings{" +
                STORE_PASSWORD + " = " + getProperty(STORE_PASSWORD) +
                PKCS11_LIBRARY_PATH + " = " + getProperty(PKCS11_LIBRARY_PATH) +
                TSA_SERVER_URL + " = " + getProperty(TSA_SERVER_URL) +
                TSA_USERNAME + " = " + getProperty(TSA_USERNAME) +
                TSA_PASSWORD + " = " + getProperty(TSA_PASSWORD) +
                '}';
    }
}
