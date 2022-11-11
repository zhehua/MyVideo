package com.hm.myvideo.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import java.util.HashMap;
import java.util.Map;

public class SportUtil {

    public String sportStr(String game) {
        if(game.equals("全部"))
            game="";
        try {
            String text = Jsoup.connect("https://70zhibo.com/api/web/indexMatchList?game=" + game).sslSocketFactory(new SSLSocketFactoryCompat()).header("cookie", "RQ0=d65cc98ae0778a179713a722e28377c5").ignoreContentType(true).get().text();
            return text;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println(JSON.toJSONString(sports));
        return null;
    }

    public JSONArray link(int id) {
        try {
            Map map = new HashMap<>();
            map.put("accept"," application/json, text/plain, */*");
            map.put("accept-encoding"," gzip, deflate, br");
            map.put("accept-language"," zh-CN,zh;q=0.9");
            map.put("referer"," https://70zhibo.com/");
            map.put("sec-ch-ua","\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"97\", \"Chromium\";v=\"97\"");
            map.put("sec-ch-ua-mobile"," ?0");
            map.put("sec-ch-ua-platform"," \"Windows\"");
            map.put("sec-fetch-dest"," empty");
            map.put("sec-fetch-mode"," cors");
            map.put("sec-fetch-site"," same-origin");
            map.put("user-agent"," Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36");


            Map cookies = new HashMap<>();
            cookies.put("Hm_lvt_3b6bc1de9ef0e8981c815510dc8a5d18","1652150030,1652150053,1652154263,1652236174");
            cookies.put("Hm_lpvt_3b6bc1de9ef0e8981c815510dc8a5d18","1652236382");
            String text = Jsoup.connect("http://70zhibo.com/api/web/match/" + id).headers(map).cookies(cookies).ignoreContentType(true).get().text();
            JSONObject object = new JSONObject(text);
            JSONArray lives = object.optJSONArray("lives");
            if (lives.length() > 0) {
                return lives;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
