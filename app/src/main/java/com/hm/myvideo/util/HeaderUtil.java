package com.hm.myvideo.util;


import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HeaderUtil {
    public static Map<String, String> getHeaders(String url) {
        return getHeaders(url, null, "GET");
    }

    private static Map<String, String> getHeaders(String paramString1, String paramString2, String paramString3) {
        HashMap<String, String> hashMap = new HashMap<>();
        try {
            hashMap.put("X-ITOUCHTV-DEVICE-ID", "IMEI_351564863281986");
            if (paramString1.startsWith("https")) {
                hashMap.put("X-ITOUCHTV-Ca-Key", "04039368653554864194910691389924");
                long l = System.currentTimeMillis();
                hashMap.put("X-ITOUCHTV-Ca-Timestamp", l + "");
                if (paramString2 != null) {
                    if (!"".equals(paramString2)) {
                        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                        paramString1 = paramString3 + "\n" + paramString1 + "\n" + l + "\n" + new String(Base64.encodeBase64(messageDigest.digest(paramString2.getBytes())));
                    } else {
                        paramString1 = paramString3 + "\n" + paramString1 + "\n" + l + "\n";
                    }
                } else {
                    paramString1 = paramString3 + "\n" + paramString1 + "\n" + l + "\n";
                }
                hashMap.put("X-ITOUCHTV-Ca-Signature", b(paramString1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    private static String b(String paramString) {
        //System.out.println("stringToSigned=" + paramString);
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            String str = "ScW1pSGVCOWJLZ293SHF4UnYwcHJjMmNQTjJFd1hMMUhPWXUzRFBpWUNjYVl4eXhkRkl5VDVtQWZCbXIwVUtQTw==";
            String str1 = new String(Base64.decodeBase64(str.substring(1, str.length()).getBytes()));
            byte[] arrayOfByte = str1.getBytes();
            mac.init(new SecretKeySpec(arrayOfByte, 0, arrayOfByte.length, "HmacSHA256"));
            return new String(Base64.encodeBase64(mac.doFinal(paramString.getBytes())));
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

   public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("TLS");
            // trustAllCerts信任所有的证书
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }
}

