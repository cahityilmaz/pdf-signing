package com.capp.pdfsigning;

import java.io.IOException;
import java.security.GeneralSecurityException;

public final class Main {

    public static void main(String[] args) {
        checkArgs(args);
        var app = new Signer(args[0], args[1]);
        try {
            app.sign();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Error occurred while singing pdf file.", e);
        }
    }

    private static void checkArgs(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Source and / or target file paths are not specified.");
        }
    }
}
