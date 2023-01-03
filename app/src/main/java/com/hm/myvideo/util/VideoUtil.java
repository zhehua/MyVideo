package com.hm.myvideo.util;

import android.text.TextUtils;

import com.hm.myvideo.beans.Menu;
import com.hm.myvideo.beans.PlayItem;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoUtil {
    public static Long timestamp = null;
    public static Integer pageCount = null;

    public static List<Menu> getList(Integer key, String keyWord, Integer pageNo, String item) throws Exception {
        Map<String, String> urls = new HashMap<>();
        urls.put("量子","http://lzizy3.com/api.php/provide/vod/from/lzm3u8");
        urls.put("U酷", "https://api.ukuapi.com/api.php/provide/vod/from/ukm3u8");  //1080
        urls.put("星海", "https://www.xhzy01.com/api.php/provide/vod/from/xhm3u8"); //1080
        urls.put("优质", "https://api.1080zyku.com/inc/apijson.php/from/1080zyk");//少广告
        urls.put("快车", "https://caiji.kczyapi.com/api.php/provide/vod/from/kcm3u8");
        urls.put("fox", "https://api.foxzyapi.com/api.php/provide/vod/from/foxm3u8");
        urls.put("tom", "https://api.tomcaiji.com/api.php/provide/vod/from/tomm3u8");


        urls.put("飞速", "https://www.feisuzyapi.com/api.php/provide/vod/from/fsm3u8");
        urls.put("天空", "https://api.tiankongapi.com/api.php/provide/vod/from/tkm3u8");
        urls.put("无尽", "https://api.wujinapi.net/api.php/provide/vod/from/wjm3u8"); //不能搜索
        //urls.put("想看", "https://m3u8.xiangkanapi.com/api.php/provide/vod/from/xkm3u8");//失效
        //urls.put("酷点", "https://kudian10.com/api.php/provide/vod/from/kdm3u8");//慢
        urls.put("百度", "https://api.apibdzy.com/api.php/provide/vod/from/dbm3u8");
        urls.put("鱼乐", "https://api.ylzy1.com/api.php/provide/vod/from/lem3u8/at/json");
        urls.put("樱花", "https://m3u8.apiyhzy.com/api.php/provide/vod/from/yhm3u8");
        urls.put("闪电", "https://sdzyapi.com/api.php/provide/vod/from/sdm3u8");
        urls.put("新浪", "https://api.xinlangapi.com/xinlangapi.php/provide/vod/from/xlm3u8/at/json");
        urls.put("快播", "http://www.kuaibozy.com/api.php/provide/vod/at/json/kbm3u8");


        List<Menu> Menus = new ArrayList<>();
        String surl = urls.get("量子");
        String s = "";
        if (!StringUtil.isBlank(keyWord)) {
            s = "wd=" + keyWord;
            if (!TextUtils.isEmpty(item))
                surl = urls.get(item);
        } else if (null != key) {
            s = "t=" + key;
        }

        surl += "?ac=detail&" + s + "&pg=" + pageNo;
        //System.out.println(s);
        String text = Jsoup.connect(surl).timeout(10_000).sslSocketFactory(new SSLSocketFactoryCompat()).ignoreContentType(true).get().text();
        text = string2Json(text);
        // text = text.replaceAll("[\\t\\n\\r]", "");
        JSONObject object =new JSONObject(text);
        pageCount = object.getInt("pagecount");
        JSONArray array = object.getJSONArray("list");
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            //Integer vodId = jsonObject.getInteger("vod_id");
            String vodPic = jsonObject.getString("vod_pic");
            String vodName = jsonObject.getString("vod_name");
            String vodRemarks = jsonObject.getString("vod_remarks");
            //   String vodTime = jsonObject.getString("vod_time");
            String vod_year = jsonObject.getString("vod_year");
            String vod_play_url = jsonObject.getString("vod_play_url");
            String[] date_urls = vod_play_url.split("#");
            List<PlayItem> list = new ArrayList<>();
            for (int i1 = date_urls.length - 1; i1 >= 0; i1--) {
                String date_url = date_urls[i1];
                String[] split = date_url.split("\\$");
                if (split.length > 1) {
                    String date = split[0];
                    String url = split[1];
                    PlayItem playItem = new PlayItem();
                    playItem.setUrl(url);
                    playItem.setName(date);
                    list.add(playItem);
                }
            }
            Menu menu = new Menu();
            menu.setItems(list);
            menu.setCategory(vodName);
            menu.setVodPic(vodPic);
            menu.setVodRemarks(vodRemarks);
            Menus.add(menu);
        }

        return Menus;
    }


    private static String string2Json(String s) {
        char[] tempArr = s.toCharArray();
        int tempLength = tempArr.length;
        for (int i = 0; i < tempLength; i++) {
            if (tempArr[i] == '"') {
                if (tempArr[i - 1] == ':' || tempArr[i - 1] == '{' || tempArr[i - 1] == ',') {

                } else if (tempArr[i + 1] == ':' || (tempArr[i + 1] == ',' && tempArr[i + 2] == '"') || tempArr[i + 1] == '}') {

                } else
                    tempArr[i] = '“';
            }
        }
        return new String(tempArr);
    }


}
