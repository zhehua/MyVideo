package com.hm.myvideo.util;

public class Constants {
    public static boolean useApp=false;
    public final static int videoValidTime = 5 * 60_000;
    public static String  userAgent(){
        if(useApp)
            return "Mozilla/5.0 (Linux; Android 5.1.1; SM-N976N Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/74.0.3729.136 Mobile Safari/537.36 MicroMessengeriptv/1.4.0 VideoPlayer Html5Plus/1.0";
        else
            return "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1";
    }

    public static String  tvDomain(){
        if(useApp)
            return "https://player.ggiptv.com/iptv.php";
        else
            return "http://iptv807.com";
    }

    public final static float menuTextSize=20;
    public final static float itemTextSize=15;
}