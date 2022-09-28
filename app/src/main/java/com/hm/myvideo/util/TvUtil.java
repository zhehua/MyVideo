package com.hm.myvideo.util;

import com.hm.myvideo.MainActivity;
import com.hm.myvideo.beans.Menu;
import com.hm.myvideo.beans.PlayItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TvUtil {
    //tvb新闻台
    // https://prd-vcache.edge-global.akamai.tvb.com/__cl/slocalr2526/__c/ott_I-NEWS_h264/__op/bks/__f/index.m3u8?hdnea=ip=0.0.0.0~st=1654584878~exp=1654671278~acl=/__cl/slocalr2526/__c/ott_I-NEWS_h264/__op/bks/__f/*~hmac=263fcaabe871cbc3b19091982e7383f97c434300ae00a3c23fb091d710ed3071&p=3139

    public static List<Menu> getMenus() {
        if (MainActivity.menus.size() > 0)
            return MainActivity.menus;

        Map<String, String> list = new LinkedHashMap<>();
        list.put("港台", Constants.tvDomain + "?tid=gt");
        list.put("卫视", Constants.tvDomain + "?tid=ws");
        list.put("体育", Constants.tvDomain + "?tid=ty");
        list.put("央视", Constants.tvDomain + "?tid=ys");
        try {
            for (String key : list.keySet()) {
                String value = list.get(key);
                Menu menu = new Menu();
                menu.setCategory(key);
                Document ty = document(value);
                Elements aClass = ty.select("a");
                List<PlayItem> items = new ArrayList<>();
                for (Element element : aClass) {
                    String text = element.text();
                    String attr = element.attr("onclick");
                    if (attr.contains("act=play")) {
                        String m3u8Url = attr.replace("clicked('","").replace("');","");// getM3u8Url(domain + attr);
                        if (m3u8Url != null) {
                            PlayItem playItem = new PlayItem();
                            playItem.setName(text);
                            playItem.setKey(m3u8Url);
                            items.add(playItem);
                        }

                    }
                }
                menu.setItems(items);
                MainActivity.menus.add(menu);
                if (key.equals("港台")) {
                    Menu gd = TvUtil.tvChannels();
                    if (null != gd.getItems() && gd.getItems().size() > 0)
                        MainActivity.menus.add(gd);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MainActivity.menus;
    }

    public static String getM3u8Url(String url) {
        String str = null, key = null;
        System.out.println("--------------------->>  " + url);
        try {
            Document document = document(url);
            //todo 节目表
           /* List<PlayItem> program = program(document);
            System.out.println(JSON.toJSONString(program));*/
            Element playURL = document.getElementById("playURL");
            Elements scripts = document.select("script");
            String attr1 = playURL.select("option").attr("value");
            Map<String, String> map = new HashMap();
            for (Element script : scripts) {
                String data = script.data();
                if (data.contains("=bdecodeb")) {
                    if (data.contains("hken"))
                        continue;
                    String[] kvs = data.split(";");
                    for (String kv : kvs) {
                        int i = kv.indexOf("=");
                        if (i < 0)
                            continue;
                        String k = kv.substring(0, i);
                        String v = kv.substring(i + 1, kv.length() - 1);
                        if (v.contains("\""))
                            str = v.trim().replaceAll("\"", "");
                        else if (v.contains("bdecodeb")) {

                        } else {
                            v = kv.substring(i + 1, kv.length());
                            key = map.get(v);
                        }
                    }
                    break;
                } else {
                    if (data.contains("=") && !data.contains("window") && !data.contains("getCookieapp")) {
                        String[] ss = data.split(";");
                        for (String s : ss) {
                            String[] kv = s.split("=");
                            if(kv.length<2)
                                continue;
                            if (kv[1].contains("\"")) {
                                map.put(kv[0], kv[1].replaceAll("\"", ""));
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
            TVJsUtil tvJsUtil=new TVJsUtil();
            String m3u8Url=tvJsUtil.getPlayUrl(str,key,attr1);
            return m3u8Url;
           /* Connection.Response response = Jsoup.connect(m3u8Url).timeout(5_000).ignoreContentType(true).followRedirects(true).execute();
            return response.url().toString();*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* private static List<PlayItem> program(Document document) {
         try {
             Elements uls = document.select("ul");
             Element ul = uls.get(uls.size() - 1);
             if (ul != null) {
                 Elements lis = ul.select("li");
                 List<PlayItem> list = new ArrayList<>();
                 List<PlayItem> playList = new ArrayList<>();
                 boolean playing = false;
                 for (int i = lis.size() - 1; i >= 0; i--) {
                     Element li = lis.get(i);
                     if (li.hasText() && li.childNodeSize() > 0) {
                         String text = li.text();
                         if (null != text && text.contains(":")) {
                             PlayItem playItem = new PlayItem();
                             text = text.trim();
                             if (text.endsWith("回看")) {
                                 text = text.substring(0, text.length() - 2);
                                 playItem.setUrl("2");
                             } else if (text.endsWith("直播中")) {
                                 text = text.substring(0, text.length() - 3);
                                 playItem.setUrl("1");
                             } else {
                                 String s = text.split(" ")[0];
                                 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                                 String date = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
                                 Date time = dateFormat.parse(date + " " + s + ":00");
                                 if (time.before(dateFormat.parse(dateFormat.format(new Date())))) {
                                     if (playing) {
                                         playItem.setUrl("2");
                                     } else {
                                         playItem.setUrl("1");
                                         playing = true;
                                     }
                                 }
                             }
                             playItem.setName(text);
                             if ("2".equals(playItem.getUrl()))
                                 playList.add(playItem);
                             else
                                 list.add(playItem);
                         }
                     }
                 }
                 Collections.reverse(list);
                 Collections.reverse(playList);
                 list.addAll(playList);
                 return list;
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
         return new ArrayList<>();
     }
 */

    public static String getGd(Integer index) {
        Menu gd = tvChannels();
        Menu menu = MainActivity.menus.get(1);
        menu.setItems(gd.getItems());
        MainActivity.nameUrl = gd.getItems();
        return MainActivity.nameUrl.get(index).getUrl();
    }

    public static Menu tvChannels() {
        Menu menu = new Menu();
        menu.setCategory("广东");
        List<PlayItem> items = tvChannels(1);
        if (null == items || items.size() == 0)
            items = new ArrayList<>();
        List<PlayItem> items1 = tvChannels(2);
        if (null != items1 && items1.size() > 0)
            items.addAll(items1);
        List<PlayItem> gz = gz();
        if (null != gz && gz.size() > 0)
            items.addAll(gz);
        menu.setItems(items);
        return menu;
    }


    public static List<PlayItem> tvChannels(Integer pageNumber) {
        String url = "https://api.itouchtv.cn:8090/liveservice/v1/tvChannels?pageNumber=" + pageNumber + "&channelsSnapShotNumber=0&getPages=1&pageSize=10";
        Map<String, String> headers = HeaderUtil.getHeaders(url);
        headers.put("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-N976N Build/QP1A.190711.020)");
        headers.put("Connection", "Keep-Alive");
        headers.put("Accept-Encoding", "gzip");
        try {
            String body = Jsoup.connect(url).headers(headers).ignoreContentType(true).get().text();
            JSONObject jsonObject =new  JSONObject(body);
            JSONArray tvChannels = jsonObject.optJSONArray("tvChannels");
            List<PlayItem> items = setItems(tvChannels);
            return items;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<PlayItem> setItems(JSONArray recommendList) {
        List<PlayItem> items = new ArrayList<>();
        try {
            for (int i = 0; i < recommendList.length(); i++) {
                JSONObject object = recommendList.getJSONObject(i);
               /* Integer isTv = object.getInteger("isTv");
                if (isTv == 1) {*/
                String name = object.optString("name");
                String url;
                Integer sid = object.optInt("sid");
                if (sid == 1184)
                    url = "http://iptv.eatuo.com:9901/tsfile/live/1004_1.m3u8";
                else
                    url = object.optString("videoUrl");
                PlayItem playItem = new PlayItem();
                playItem.setName(name);
                playItem.setUrl(url);
                playItem.setKey("gd_" + sid);
                int videoValidTime = object.optInt("videoValidTime");
                playItem.setExpirationTime(System.currentTimeMillis() + videoValidTime);
                items.add(playItem);
                //  }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public static List<PlayItem> gz() {
        List<PlayItem> items = new ArrayList<>();
        Map<String, String> list = new HashMap<>();
        list.put("zhonghe", "广州综合");
        list.put("fazhi", "广州法制");
        list.put("xinwen", "广州新闻");
        list.put("jingsai", "广州竞赛");
        list.put("yingshi", "广州影视");
        list.put("shenghuo", "广州生活");
       /* PlayItem playItem=new PlayItem();
        playItem.setName("南国都市(4K)");
        playItem.setUrl("rtsp://183.59.156.50/PLTV/88888905/224/3221228179/10000100000000060000000008189131_0.smil?0&accountinfo=%7E%7EV2.0%7El0PMxMTr89VnbogvGkhHKQ%7EzJeEdQQK6GK7r2Q5jZjxjKYhZiHklGs8wg1XyXUSKdk5d8f2s2AYjzO2-qcyDeaExan3Pra4cuU_Whoju6BKGf666OGX7-GKwJrfpZjwyfM~ExtInfoWNHSPSTb+3AG0FnUkYLPMw=-1%2C0%2C1%2C%2C%2C2%2C%2C%2C%2C2%2CEND&GuardEncType=2");
        items.add(playItem);*/
        for (String key : list.keySet()) {
            String value = list.get(key);
            PlayItem item = new PlayItem();
            item.setKey("gz_" + key);
            item.setName(value);
            items.add(item);
        }
        return items;
    }

    public static String getGz(String key) {
        try {
            String json = Jsoup.connect("https://www.gztv.com/gztv/api/tv/" + key).timeout(5_000).ignoreContentType(true).header("User-Agent", "Mozilla/5.0 (iPad; CPU OS 13_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/87.0.4280.77 Mobile/15E148 Safari/604.1").get().text();
            JSONObject jsonObject =new JSONObject(json);
            return jsonObject.optString("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //卫视
    /*public static Menu ws() {
        Menu menu = new Menu();
        menu.setCategory("其他");
        List<PlayItem> items = new ArrayList<>();
        PlayItem item = new PlayItem();
        item.setUrl("http://hw-m-l.cztv.com/channels/lantian/channel01/720p.m3u8");
        //http://219.151.31.37/liveplay-kk.rtxapp.com/live/program/live/zjwshd/2300000/mnf.m3u8
        item.setName("浙江卫视");
        items.add(item);
        PlayItem item1 = new PlayItem();
        item1.setUrl("http://219.151.31.38/liveplay-kk.rtxapp.com/live/program/live/hnwshd/4000000/mnf.m3u8");
        item1.setName("湖南卫视");
        items.add(item1);
        PlayItem item2 = new PlayItem();
        String url = "https://live-hls.jstv.com/livezhuzhan/jsws.m3u8?upt=";
        String a = "/livezhuzhan/jsws.m3u8";
        String r = "jstvlivezhuzhan@2022cdn!@#124gg";
        long i = Math.round(System.currentTimeMillis()/ 1e3) + 300;
        String s = r + "&" + i + "&" + a;

        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(s.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int x = 0; x < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        String l = md5code.substring(12, 20);
        String d = l + i;
        System.out.println(url + d);
        item2.setUrl(url + d);
        //http://219.151.31.37/liveplay-kk.rtxapp.com/live/program/live/jswshd/4000000/mnf.m3u8
        item2.setName("江苏卫视");
        items.add(item2);

        items.addAll( JSON.parseArray("[{\"name\":\"凤凰卫视中文\",\"url\":\"https://playtv-live.ifeng.com/live/06OLEGEGM4G.m3u8\"},{\"name\":\"凤凰卫视资讯\",\"url\":\"https://playtv-live.ifeng.com/live/06OLEEWQKN4.m3u8\"},{\"name\":\"凤凰香港高清\",\"url\":\"http://183.207.249.35/PLTV/3/224/3221226975/index.m3u8\"},{\"name\":\"香港卫视HKS\",\"url\":\"http://zhibo.hkstv.tv/livestream/mutfysrq/playlist.m3u8\"},{\"name\":\"耀才财经\",\"url\":\"http://202.69.67.66:443/webcast/bshdlive-pc/playlist.m3u8\"},{\"name\":\"澳门莲花\",\"url\":\"http://2754879781.cloudvdn.com/a.m3u8?domain=live-hls.macaulotustv.com&player=A4kAAAbZjRagkO4W&secondToken=secondToken%3AC7Abok7gGd4mffIdlq96x25QqH4&streamid=lotustv%3Alotustv%2Fmacaulotustv&v3=1\"},{\"name\":\"澳门卫视\",\"url\":\"http://61.244.22.5/ch3/ch3.live/chunklist_w1228316132.m3u8\"},{\"name\":\"澳视资讯\",\"url\":\"http://61.244.22.5/ch5/info_ch5.live/master.m3u8\"},{\"name\":\"澳視澳門\",\"url\":\"http://61.244.22.5/ch1/ch1.live/playlist.m3u8\"},{\"name\":\"澳視卫星\",\"url\":\"http://61.244.22.5/ch3/ch3.live/chunklist_w30461384.m3u8\"},{\"name\":\"澳門Macau\",\"url\":\"http://61.244.22.5/ch2/_definst_/ch2.live/playlist.m3u8\"},{\"name\":\"澳视卫星\",\"url\":\"http://61.244.22.4/ch3/ch3.live/index.m3u8\"},{\"name\":\"澳视综艺\",\"url\":\"http://61.244.22.5/ch6/_definst_/hd_ch6.live/playlist.m3u8\"},{\"name\":\"澳视葡文\",\"url\":\"http://61.244.22.4/ch2/ch2.live/index.m3u8\"},{\"name\":\"澳門3台\",\"url\":\"http://61.244.22.4/ch3/ch3.live/playelist.m3u8\"},{\"name\":\"澳門微电影\",\"url\":\"http://61.244.22.4/ch3/ch3.live/chunklist_w1228316132.m3u8\"},{\"name\":\"澳门葡萄牙\",\"url\":\"http://61.244.22.5/ch2/ch2.live/chunklist_w1632175875.m3u8\"}]"
                , PlayItem.class));
        menu.setItems(items);

        return menu;
    }*/

    private static Document document(String url) throws Exception {
        Map<String, String> headers = new HashMap();
        headers.put("upgrade-insecure-requests", "1");
        headers.put("user-agent", "Mozilla/5.0 (Linux; Android 5.1.1; SM-N976N Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/74.0.3729.136 Mobile Safari/537.36 MicroMessengeriptv/1.4.0 VideoPlayer Html5Plus/1.0");
        headers.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=");
        headers.put("accept-encoding", "gzip, deflate");
        headers.put("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        headers.put("x-requested-with", "w2a.app.iptv800.com");
        return Jsoup.connect(url).timeout(5_000).headers(headers).get();
    }
}
