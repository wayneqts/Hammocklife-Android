package com.app.hammocklife.custom;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.auth.oauth2.GoogleCredentials;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import android.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;

public class Utils {
    public static String FCM_KEY = "AAAAEJBBUec:APA91bErbnZrDrrVTRbFG-v5Qiik_VdlrNVewbxGbeLLYDeQ2gYT59LkkHLZ4UR6ZeWPC0RjureOr8wr8wkisaJEkId1fJSUG09ogD5qjvavQNG0m3EV7spPpvP5d5WwGwS9sb1hHByw";
    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = { MESSAGING_SCOPE };
    public static int dp2px (int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public static void checkPermission (Fragment fragment, String permissionString, int permissionCode) {
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || fragment.getContext() == null) return;
        int existingPermissionStatus = ContextCompat.checkSelfPermission(fragment.getContext(),
                permissionString);
        if (existingPermissionStatus == PackageManager.PERMISSION_GRANTED) return;
        fragment.requestPermissions(new String[]{permissionString}, permissionCode);
    }

    public static boolean isReadStorageGranted (Context context) {
        int storagePermissionGranted = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        return storagePermissionGranted == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isWriteStorageGranted (Context context) {
        int storagePermissionGranted = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return storagePermissionGranted == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isCameraGranted (Context context) {
        int cameraPermissionGranted = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        return cameraPermissionGranted == PackageManager.PERMISSION_GRANTED;
    }

    public static String createJwt(Context context) {
        try {
            // Load service-account.json from assets
            InputStream is = context.getAssets().open("service-account.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject sa = new JSONObject(json);

            String clientEmail = sa.getString("client_email");
            String privateKeyPem = sa.getString("private_key")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            // Decode private key
            byte[] pkcs8Bytes = Base64.decode(privateKeyPem, Base64.DEFAULT);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8Bytes);
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

            long now = System.currentTimeMillis();
            long oneHour = 3600 * 1000;

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(clientEmail)
                    .audience("https://oauth2.googleapis.com/token")
                    .claim("scope", "https://www.googleapis.com/auth/firebase.messaging")
                    .issueTime(new Date(now))
                    .expirationTime(new Date(now + oneHour))
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.RS256),
                    claimsSet
            );

            signedJWT.sign(new RSASSASigner(privateKey));

            return signedJWT.serialize();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
