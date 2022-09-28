package com.hm.myvideo.util;


import java.util.HashMap;
import java.util.Map;

public class TVJsUtil {
    public  String getPlayUrl(String str,String key,String attr1) {
        TVJsUtil gd = new TVJsUtil();
        String bdecodeb = gd.bdecodeb(str, key);
        String replace = bdecodeb.toString().replace("<script>", "").replace("</script>", "");
        Map<String, String> tmap = new HashMap();
        //  System.out.println(replace);
        String[] split = replace.split(";");
        for (String ss : split) {
            String[] kv = ss.split("=");
            String k = kv[0].trim();
            k = k.replace("var", "");
            String v = kv[1].trim();
            if (v.contains("\"")) {
                if (v.contains("split") || v.contains("reverse")) {
                    StringBuilder sb = new StringBuilder();
                    String[] split1 = v.split("\\+");
                    for (String s : split1) {
                        String val = s.replace(".split(\"\").reverse().join(\"\")", "");
                        if (val.contains("\"")) {
                            val = val.replace("\"", "");
                            if (s.contains("reverse"))
                                sb.append(new StringBuilder(val).reverse());
                            else
                                sb.append(new StringBuilder(val));
                        } else {
                            sb.append(new StringBuilder(tmap.get(val)).reverse());
                        }
                    }
                    tmap.put(k.trim(), sb.toString());
                } else {
                    v = v.replace("\"", "");
                    tmap.put(k.trim(), v.trim());
                }

            } else {
                if (tmap.containsKey(v)) {
                    String o = tmap.get(v);
                    tmap.put(k.trim(), o.trim());
                }
            }
        }
        String startPlayer = gd.startPlayer(attr1, tmap);
        System.out.println(startPlayer);
        String playUrl = startPlayer.toString().replace("player", "play");
        return playUrl;
    }

    String m3u8Uri;

    public String startPlayer(String uri, Map<String, String> tmap) {
        System.out.println(uri);
        String hken = tmap.get("hken");
        String hkens = tmap.get("hkens");
        String token = tmap.get("token");
        uri = new StringBuilder(uri).reverse().toString();
        uri = bdecodeb(uri, hken);
        uri = uri.replace("token=123", "token=" + token);
        uri = uri.replace("token=" + hkens, "token=" + token);
        uri = uri.replace(hken, "");
        m3u8Uri = "";
        if (uri != null) {
            return urlParsing(uri);
        }
        return null;
    }

    private String urlParsing(String uri) {
        if (uri != null && uri.indexOf("://") > 0) {
            String vid = uri.substring(uri.indexOf("://") + 3);
            if (uri.indexOf("http://") == 0) {
                if (uri.indexOf("") > -1) {
                }
                return setPlayerUri(uri);
            } else if (uri.indexOf("://") > 2) {
                return setPlayerUri(uri);
            }
        }
        return null;
    }

    private String setPlayerUri(String uri) {
        m3u8Uri = uri;
        return m3u8Uri;
    }

    private String bdecodeb(String str, String key) {
        String string = bdecode(str);
        int len = key.length();
        String code = "";
        for (int i = 0; i < string.length(); i++) {
            int k = i % len;
            code += (char) (string.charAt(i) ^ key.charAt(k));
        }
        return bdecode(code);
    }

    private String bdecode(String data) {
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
            a1 = bits >> 16 & 0xff;
            a2 = bits >> 8 & 0xff;
            a3 = bits & 0xff;
            if (h3 == 64) {
                tmp_arr[ac++] = (char) (a1) + "";
            } else if (h4 == 64) {
                tmp_arr[ac++] = (char) (a1) + "" + (char) (a2);
            } else {
                tmp_arr[ac++] = (char) (a1) + "" + (char) (a2) + "" + (char) (a3);
            }
        } while (i < data.length());
        dec = join(tmp_arr);
        return dec;
    }

    String join(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (Object o : arr) {
            sb.append(o);
        }
        return sb.toString();
    }

}
