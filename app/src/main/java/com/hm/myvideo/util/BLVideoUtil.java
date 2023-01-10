package com.hm.myvideo.util;

import android.text.TextUtils;


import com.hm.myvideo.beans.Menu;
import com.hm.myvideo.beans.PlayItem;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class BLVideoUtil {
    public static Long timestamp = null;
    public static Integer pageCount = null;

    public static String host = "https://www.bulei.cc";

    public static void search(String keyWord, String pageNo) {

    }

    public static List<Menu> getCategory(String id, String c1ass, String pageNo, String area, String year) {
        List<Menu> Menus = new ArrayList<>();
        //电影 1 电视剧 2 综艺 3
        if (!TextUtils.isEmpty(id))
            id = "/id/" + id;
        if (!TextUtils.isEmpty(area))
            area = "/area/" + area;
        String lang = "";
        //lang="/lang/粤语";
        if (!TextUtils.isEmpty(year))
            year = "/year/" + year;
        if (!TextUtils.isEmpty(c1ass))
            c1ass = "/class/" + c1ass;
        pageNo = "/page/" + pageNo;
        String url = host + "/index.php/vod/show" + area + c1ass + id + lang + year + pageNo + ".html";
        try {
            Document document = Jsoup.connect(url).timeout(10_000).get();
            Elements es = document.getElementsByClass("module-poster-item module-item");
            for (Element e : es) {
                String title = e.getElementsByClass("module-poster-item-title").get(0).text();
                String note = e.getElementsByClass("module-item-note").get(0).text();
                String pic = e.getElementsByClass("lazy lazyload").get(0).attr("data-original");
                String href = e.attr("href");
                Menu menu = new Menu();
                List<PlayItem> items = new ArrayList<>();
                PlayItem playItem = new PlayItem();
                playItem.setUrl(href);
                items.add(playItem);
                menu.setItems(items);
                menu.setCategory(title);
                menu.setVodPic(pic);
                menu.setVodRemarks(note);
                Menus.add(menu);
            }
            Elements pages = document.getElementsByClass("page-link page-next");
            if (pages.size() > 0) {
                String href = pages.get(pages.size() - 1).attr("href");
                String count = href.substring(href.lastIndexOf("/") + 1).replace(".html", "");
                pageCount = Integer.parseInt(count);
            } else {
                pageCount = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Menus;
    }

    public List<Menu> getMenuList(String url) {
        url=url.replace("detail","play").replace(".html","/sid/1/nid/1.html");
        List<Menu> list=new ArrayList<>();
        try {
            Document document = Jsoup.connect(host +url).get();
            Elements ec = document.getElementsByClass("swiper-wrapper");
            Elements panel2 = document.getElementsByClass("module-play-list");
            if(ec.size()>1){
                Elements children = ec.get(1).children();
                for (int i = 0; i < children.size(); i++) {
                    Menu menu=new Menu();
                    Element child =children.get(i);
                    System.out.println(child.text());
                    Element a = panel2.get(i);
                    Elements as=a.getElementsByClass("module-play-list-link");
                    menu.setCategory(child.text());
                    List<PlayItem> items = new ArrayList<>();
                    for (Element element : as) {
                        String text = element.text();
                        String href = element.attr("href");
                        PlayItem playItem = new PlayItem();
                        playItem.setName(text);
                        playItem.setUrl(href);
                        items.add(playItem);
                    }
                    menu.setItems(items);
                    list.add(menu);
                    System.out.println("-------------------------------");
                }
                Elements es = document.select("script");
                for (Element a : es) {
                    String data = a.data();
                    if(data.contains("player_aaaa")){
                        data = data.replace("var player_aaaa=", "");
                        JSONObject jsonObject=new JSONObject(data);
                        String link = jsonObject.getString("url");
                        if(!TextUtils.isEmpty(link)){
                            link="https://tv.bulei.cc/aly/?url="+link;
                            System.out.println(link);
                            list.get(0).getItems().get(0).setKey(link);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getPlayUrl(String url) {
        try {
            Document document = Jsoup.connect(host + url).timeout(10_000).get();
            Elements es = document.select("script");
            for (Element a : es) {
                String data = a.data();
                if (data.contains("player_aaaa")) {
                    data = data.replace("var player_aaaa=", "");
                    JSONObject jsonObject = new JSONObject(data);
                    String link = jsonObject.getString("url");
                    link = "https://tv.bulei.cc/aly/?url=" + link;
                    System.out.println(link);
                    return link;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
