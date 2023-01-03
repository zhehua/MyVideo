package com.hm.myvideo.util;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TvJsUtil2 {

    public String ntkrk(String data) {
        String keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        int a1, a2, a3, h1, h2, h3, h4, bits, i = 0, ac = 0;
        String dec = "";
        int length = (int) Math.ceil(data.length() * 1.0 / 4);
        String[] tmp_arr = new String[length];
        if (data == null) {
            return data;
        }
        data += "";
        do {
            h1 = keyStr.indexOf(data.charAt(i++));
            h2 = keyStr.indexOf(data.charAt(i++));
            h3 = keyStr.indexOf(data.charAt(i++));
            h4 = keyStr.indexOf(data.charAt(i++));
            bits = h1 << 18 | h2 << 12 | h3 << 6 | h4;
            a1 = bits >> 16 & 255;
            a2 = bits >> 8 & 255;
            a3 = bits & 255;
            if (h3 == 64) {
                tmp_arr[ac++] = (char) (a1) + "";
            } else if (h4 == 64) {
                tmp_arr[ac++] = (char) (a1) + "" + (char) (a2);
            } else {
                tmp_arr[ac++] = (char) (a1) + "" + (char) (a2) + "" + (char) (a3);
            }
        } while (i < data.length());
        TVJsUtil util=new TVJsUtil();
        dec = util.join(tmp_arr);
        return dec;
    }

    public static String getM3u8Url(String url) {
        try {
            Document document = Jsoup.connect(url).userAgent(Constants.userAgent()).get();
            TVJsUtil tvJsUtil = new TVJsUtil();
            TvJsUtil2 jsUtil2=new TvJsUtil2();
            // Element playURL = document.getElementById("playURL");
            String attr1 = document.selectFirst("option").attr("value");
            Map<String, String> map = new HashMap();
            Elements scripts = document.select("script");
            //查找包换 keyStr
            for (Element script : scripts) {
                String data = script.html();
                if (data.contains("keyStr")) {
                    int index = data.lastIndexOf("dec}");
                    String substring = data.substring(index + 4);
                    String[] kvs = substring.split(";");
                    String kv1 = kvs[1];
                    int i1 = kv1.indexOf("=");
                    String jKey = kv1.substring(i1 + 2, kv1.length() - 1).trim();
                    String kv2 = kvs[2];
                    int i2 = kv2.indexOf("=");
                    String jText = kv2.substring(i2 + 2, kv2.length() - 1).trim();
                    String jiemi = jsUtil2.jiemi(jText, jKey);

                    String js=null ;
                    Document parse = Jsoup.parse(jiemi);
                    Elements ss = parse.select("script");
                    for (Element s : ss) {
                        String dat = s.data();
                        if(dat.contains("document.write")){
                            js=dat;
                            break;
                        }
                    }

                    /*int i = jiemi.indexOf("</script>");
                    String js = jiemi.substring(8, i);*/
                    String[] split = js.split(";");


                    String s0 = split[0];
                    int i0 = s0.indexOf("=");
                    String t0 = s0.substring(i0 + 1).trim().replaceAll("\"", "");


                    String s = split[1];
                    int ii = s.indexOf("=");
                    String t = s.substring(ii + 1).trim().replaceAll("\"", "");


                    String key =null;
                   // String s =null;
                    if(t0.length()<10){
                        key = map.get(t0);
                    }else {
                        key=map.get(t);
                        t=t0;
                    }
                    String m3u8Url = tvJsUtil.getPlayUrl(t, key, attr1);
                   // if(!Constants.useApp)
                        return  m3u8Url;
                    /*Connection connection = Jsoup.connect(m3u8Url).userAgent(Constants.userAgent()).ignoreContentType(true);
                    if(!Constants.useApp){
                        connection.cookies(TvUtil.getCookies()).headers(TvUtil.getHeaders());
                    }
                    Connection.Response response = connection.execute();
                    System.out.println(response.url());
                    return response.url().toString();*/

                } else {
                    if (data.contains("=") && !data.contains("window") && !data.contains("getCookieapp")) {
                        String[] ss = data.split(";");
                        for (String s : ss) {
                            String[] kv = s.split("=");
                            if (kv.length < 2)
                                continue;
                            if (kv[1].contains("\"")) {
                                map.put(kv[0].trim(), kv[1].trim().replaceAll("\"", ""));
                            } else {
                                String key1 = kv[1];
                                if (map.containsKey(key1)) {
                                    String o = map.get(kv[1]);
                                    map.put(kv[0], o);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  String jiemi(String ogkwu, String cxvrj) {
        ogkwu = ntkrk(ogkwu);
        int len = cxvrj.length();
        String ugzgx = "";
        for (int i = 0; i < ogkwu.length(); i++) {
            int k = i % len;
            ugzgx += (char) (ogkwu.charAt(i) ^ cxvrj.charAt(k));
        }
        String kpwan = ntkrk(ugzgx);
        return kpwan;
    }
}
