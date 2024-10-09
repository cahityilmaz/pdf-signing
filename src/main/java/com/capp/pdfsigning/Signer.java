package com.capp.pdfsigning;

import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class Signer {

    public static final String SIGNATURE_NAME = "Signature";

    private final String srcFile;
    private final String destFile;

    public Signer(String srcFile, String destFile) {
        this.srcFile = srcFile;
        this.destFile = destFile;
    }

    public void sign() throws GeneralSecurityException, IOException {
        Settings settings = Settings.getInstance();

        Provider bouncyCastleProvider = getBouncyCastleProvider();
        Provider sunPKCS11Provider = getSunPKCS11Provider(settings);

        KeyStore ks = getKeyStore(settings);

        var alias = ks.aliases().nextElement();
        var pk = (PrivateKey) ks.getKey(alias, getKeyStorePassword(settings).toCharArray());
        var chain = ks.getCertificateChain(alias);

        var providerName = sunPKCS11Provider == null ? bouncyCastleProvider.getName() : sunPKCS11Provider.getName();
        var pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA512, providerName);

        var pdfPadesSigner = new PdfPadesSigner(new PdfReader(srcFile), new FileOutputStream(destFile));
        pdfPadesSigner.signWithBaselineLTProfile(createSignerProperties(), chain, pks, createTSAClient(settings));
    }

    private static SignerProperties createSignerProperties() {
        return new SignerProperties().setFieldName(SIGNATURE_NAME);
    }

    private static KeyStore getKeyStore(Settings settings) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {
        var password = getKeyStorePassword(settings);

        var ks = KeyStore.getInstance("PKCS11");
        var protectionParameter = new KeyStore.PasswordProtection(password.toCharArray());
        ks.load(() -> protectionParameter);
        return ks;
    }

    private static String getKeyStorePassword(Settings settings) {
        return settings.getStorePassword();
    }

    private static Provider getBouncyCastleProvider() {
        var providerBC = new BouncyCastleProvider();
        Security.addProvider(providerBC);
        return providerBC;
    }

    private static Provider getSunPKCS11Provider(Settings settings) {
        var sunPKCS11 = Security.getProvider("SunPKCS11");
        sunPKCS11 = sunPKCS11.configure(createPkcs11InlineConf(settings));
        Security.addProvider(sunPKCS11);
        return sunPKCS11;
    }

    private static String createPkcs11InlineConf(Settings settings) {
        var name = "default";
        var slotListIndex = 0;
        return String.format("--name = %s\nlibrary = %s\nslotListIndex = %d", name,
                settings.getPkcs11LibraryPath(), slotListIndex);
    }

    private static ITSAClient createTSAClient(Settings settings) {
        return new TSAClientBouncyCastle(settings.getTsaServerUrl(), settings.getTsaServerUsername(),
                settings.getTsaServerPassword());
    }

}
