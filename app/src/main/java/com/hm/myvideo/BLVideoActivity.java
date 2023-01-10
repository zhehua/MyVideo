package com.hm.myvideo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hm.myvideo.beans.Menu;
import com.hm.myvideo.beans.PlayItem;
import com.hm.myvideo.util.BLVideoUtil;
import com.hm.myvideo.util.SSLSocketFactoryCompat;
import com.hm.myvideo.util.VideoUtil;

import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BLVideoActivity extends Activity {

    private final String TAG = "VedioActivity";
    private ScrollView scrollView;
    private HorizontalScrollView menu;
    private LinearLayout baseView;
    private int height = 80;
    private EditText keyWord;
    private Button search;
    private TextView info;
    private int pageNo = 1;
    private Integer keys = 0;
    private String wd = null, clazz = "", area = "", type = "2", year = "";
    private Spinner spinner, classSpinner, areaSpinner, tSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bl_video);
        initView();
    }

    int currId;
    public static List<Menu> menus = new ArrayList<>();

    void initData(String keyWord, Integer pageNo) {
        new Thread(() -> {
            long currentTimeMillis = System.currentTimeMillis();
            VideoUtil.timestamp = currentTimeMillis;
            try {
                menus = BLVideoUtil.getCategory(type, clazz, String.valueOf(pageNo), area, year);
                if (currentTimeMillis >= VideoUtil.timestamp)
                    uiHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                message.what = 2;
                bundle.putString("text", e.getLocalizedMessage());
                uiHandler.sendMessage(message);
                e.printStackTrace();
            }

        }).start();
    }

    String item = "";
    private Handler uiHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                info.setText("当前第" + pageNo + "页，共" + BLVideoUtil.pageCount + "页");
                showUI();
                scrollView.scrollTo(0, 0);
            } else if (msg.what == 1) {
                String texts = msg.getData().getString("text");
                String[] strings = texts.split("&");
                if (strings.length == 2) {
                    //------------------
                    // System.out.println(strings[0]);
                    if (texts.startsWith("http") || texts.startsWith("rtmp")) {
                        menus.clear();
                        List<PlayItem> itemList = new ArrayList<>();
                        PlayItem playItem = new PlayItem();
                        playItem.setName("远程播放");
                        playItem.setUrl(strings[0]);
                        itemList.add(playItem);
                        Menu menu = new Menu();
                        menu.setItems(itemList);
                        menus.add(menu);
                        play(0);
                    } else {
                        keyWord.setText(strings[0]);
                        item = strings[1].trim();
                        search.callOnClick();
                    }
                    //--------------------
                   /* item = strings[1].trim();
                    search.callOnClick();*/
                } else
                    Toast.makeText(BLVideoActivity.this, "请输入搜索关键字", Toast.LENGTH_LONG).show();
            } else if (msg.what == 2) {
                Toast.makeText(BLVideoActivity.this, msg.getData().getString("text"), Toast.LENGTH_LONG).show();
            }
        }

    };


    void initView() {
        info = findViewById(R.id.info);
        search = findViewById(R.id.search);
        keyWord = findViewById(R.id.keyWord);
        search.setOnClickListener(v -> {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(BLVideoActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            if (keyWord.getText() != null) {
                String wd = keyWord.getText().toString();
                this.wd = wd;
                pageNo = 1;
                VideoUtil.pageCount = null;
                initData(wd, 1);
            }
        });
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    search.setBackgroundColor(Color.rgb(34, 139, 34));
                } else {
                    search.setBackgroundColor(Color.rgb(0, 0, 0));
                }

            }
        });

        baseView = findViewById(R.id.baseView);
        LinearLayout menuList = findViewById(R.id.menuList);
        scrollView = findViewById(R.id.scrollView);
        menu = findViewById(R.id.menu);
        //上部分 菜单
        //年份
        spinner = findViewById(R.id.ySpinner);
        tSpinner = findViewById(R.id.tSpinner);
        classSpinner = findViewById(R.id.classSpinner);
        areaSpinner = findViewById(R.id.areaSpinner);


        //类型
        List<String> types = new ArrayList();
        types.add("电视剧");
        types.add("电影");
        types.add("综艺");
        types.add("日韩剧");
        types.add("欧美剧");
        types.add("动漫");
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("电视剧", "2");
        typeMap.put("电影", "1");
        typeMap.put("综艺", "3");
        typeMap.put("日韩剧", "21");
        typeMap.put("欧美剧", "5");
        typeMap.put("动漫", "4");
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, types);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tSpinner.setAdapter(typesAdapter);
        tSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(types.get(i));
                if (isInit()) {
                    return;
                }
                type = typeMap.get(types.get(i));
                initData(null, 1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        List<String> names = new ArrayList<String>();
        int y = Calendar.getInstance().get(Calendar.YEAR);
        names.add("年份");
        for (int i = 0; i < 5; i++) {
            names.add((y - i) + "");
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, names);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(names.get(i));
                if (i > 0)
                    year = names.get(i);
                else {
                    if (isInit()) {
                        return;
                    }
                    year = "";
                }
                initData(null, 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //类型
        List<String> clazzs = new ArrayList();
        clazzs.add("剧情");
        clazzs.add("喜剧");
        clazzs.add("动作");
        clazzs.add("科幻");
        clazzs.add("爱情");
        clazzs.add("古装");
        clazzs.add("武侠");
        clazzs.add("犯罪");
        clazzs.add("恐怖");
        clazzs.add("战争");
        ArrayAdapter<String> clazzsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, clazzs);
        clazzsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(clazzsAdapter);
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(clazzs.get(i));
                if (i > 0)
                    clazz = clazzs.get(i);
                else {
                    if (isInit()) {
                        return;
                    }
                    clazz = "";
                }
                initData(null, 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //地区
        List<String> areas = new ArrayList();
        areas.add("地区");
        areas.add("香港");
        areas.add("美国");
        areas.add("内地");
        areas.add("英国");
        areas.add("韩国");
        areas.add("台湾");
        areas.add("印度");
        areas.add("日本");
        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, areas);
        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(areasAdapter);
        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(areas.get(i));
                if (i > 0)
                    area = areas.get(i);
                else {
                    if (isInit()) {
                        return;
                    }
                    area = "";
                }
                initData(null, 1);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        initData(wd, 1);
    }

    int ci = 0;
    int ciMax = 4;

    boolean isInit() {
        System.out.println("-->1 " + ci);
        if (ci < ciMax) {
            ci++;
            System.out.println("-->2 " + ci);
            return true;
        }
        return false;
    }

    //下部分 搜索结果
    private void showUI() {
        baseView.removeAllViews();
        //List<PlayItem> nameUrl=menus.
        double line = 5.0;
        double ceil = Math.ceil(menus.size() / line);
        //System.out.println(ceil);
        for (int i = 0; i < ceil; i++) {
            LinearLayout layout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(//布局参数设置
                    ViewGroup.LayoutParams.MATCH_PARENT,//宽度内容占满整行
                    ViewGroup.LayoutParams.WRAP_CONTENT//高度内容自适应
                    , 1.0f
            );
            layout.setLayoutParams(layoutParams);//给layout设置布局参数
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(0, 0, 0, 5);
            LinearLayout.LayoutParams btntParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f

            );
            btntParams.setMargins(5, 0, 0, 0);
            int width = baseView.getWidth() / 5;
            for (int j = 0; j < line; j++) {
                int index = (int) ((line * i) + j);
                if (index >= menus.size())
                    break;
                Menu menu = menus.get(index);
                LinearLayout item = new LinearLayout(this);
                LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(//布局参数设置
                        width,//宽度内容占满整行
                        ViewGroup.LayoutParams.WRAP_CONTENT//高度内容自适应
                        , 1.0f
                );
                item.setFocusable(true);
                item.setLayoutParams(itemParams);//给layout设置布局参数
                item.setOrientation(LinearLayout.VERTICAL);
                item.setPadding(3, 0, 3, 10);
                item.setBackgroundColor(Color.BLACK);
                item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            item.setBackgroundColor(Color.rgb(0, 191, 255));
                        } else {
                            item.setBackgroundColor(Color.BLACK);
                        }

                    }
                });
                item.setOnClickListener(v -> play(index));
                int picWidth = width - 20;
                LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                        picWidth,//宽度内容占满整行
                        picWidth
                        , 1.0f
                );
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(imgParams);

                Glide.with(this).load(menu.getVodPic()).centerCrop().into(imageView);
                item.addView(imageView);
                //名称
                TextView title = new TextView(this);
                title.setText(menu.getCategory());
                title.setWidth(picWidth);
                title.setTextColor(Color.WHITE);
                title.setTextSize(16);
                //title.setHeight(35);
                item.addView(title);
                //备注
                TextView remark = new TextView(this);
                remark.setText(menu.getVodRemarks());
                remark.setWidth(picWidth);
                remark.setTextColor(Color.WHITE);
                remark.setTextSize(12);
                //remark.setHeight(20);
                item.addView(remark);
                layout.addView(item);
            }
            baseView.addView(layout);
        }
    }

    private void play(int i) {
        //menu.getItems().get(0)playItem.getUrl()
        Intent intent = new Intent();
        intent.setClass(BLVideoActivity.this, PlayBLVideoActivity.class);
        intent.putExtra("url", i);
        startActivity(intent);
    }

    boolean isSelect = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            //case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (VideoUtil.pageCount != null && pageNo < VideoUtil.pageCount) {
                    isSelect = true;
                    Button button = findViewById(currId);
                    button.setFocusable(true);
                    button.setFocusableInTouchMode(true);
                    button.requestFocus();
                    button.requestFocusFromTouch();
                    pageNo++;
                    initData(wd, pageNo);
                    return true;
                }

                break;
            //  case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (VideoUtil.pageCount != null && pageNo > 1) {
                    isSelect = true;
                    pageNo--;
                    initData(wd, pageNo);
                    return true;
                }
                break;
            //case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_MENU:   //菜单键 搜索
                new Thread(() -> {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    try {
                        String text = Jsoup.connect("https://seventh.pythonanywhere.com/get").sslSocketFactory(new SSLSocketFactoryCompat()).timeout(5_000).get().text();
                        if (!StringUtil.isBlank(text)) {
                            message.what = 1;
                            bundle.putString("text", text);
                        } else {
                            message.what = 2;
                            bundle.putString("text", "文本为空");
                        }

                    } catch (Exception e) {
                        message.what = 2;
                        bundle.putString("text", e.getMessage());
                        e.printStackTrace();
                    }
                    message.setData(bundle);
                    uiHandler.sendMessage(message);
                }).start();
                break;


            default:
                break;
        }

        return super.onKeyDown(keyCode, event);

    }


}
