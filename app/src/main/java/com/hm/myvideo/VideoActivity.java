package com.hm.myvideo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.hm.myvideo.util.Constants;
import com.hm.myvideo.util.VideoUtil;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class VideoActivity extends Activity {

    private final String TAG = "VedioActivity";
    private ScrollView scrollView;
    private HorizontalScrollView menu;
    private LinearLayout baseView;
    private int height = 80;
    private EditText keyWord;
    private Button search,pre,next;
    private TextView info;
    private int pageNo = 1;
    private Integer keys = 0;
    private String wd = null;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);
        initView();
    }

    int currId;
    public static List<Menu> menus = new ArrayList<>();

    void initData(Integer key, String keyWord, Integer pageNo) {
        new Thread(() -> {
            long currentTimeMillis = System.currentTimeMillis();
            VideoUtil.timestamp = currentTimeMillis;
            try {
                //String item = spinner.getSelectedItem().toString();
                // System.out.println(selectedItem);
                menus = VideoUtil.getList(key, keyWord, pageNo, item);
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
                info.setText("当前第" + pageNo + "页，共" + VideoUtil.pageCount + "页");
                showUI();
            } else if (msg.what == 1) {
                String texts = msg.getData().getString("text");
                String[] strings = texts.split("&");
                if (strings.length == 2) {
                    keyWord.setText(strings[0]);
                    item = strings[1].trim();
                    search.callOnClick();
                } else
                    Toast.makeText(VideoActivity.this, "请输入搜索关键字", Toast.LENGTH_LONG).show();
            } else if (msg.what == 2) {
                Toast.makeText(VideoActivity.this, msg.getData().getString("text"), Toast.LENGTH_LONG).show();
            }
        }

    };

    void initView() {
        spinner = findViewById(R.id.spinner);
        List<String> names = new ArrayList<String>();
        names.add("U酷");
        names.add("飞速");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, names);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        info = findViewById(R.id.info);
        search = findViewById(R.id.search);
        pre = findViewById(R.id.pre);
        next = findViewById(R.id.next);
        pre.setOnClickListener(v -> {
            if (VideoUtil.pageCount != null && pageNo > 1) {
                pageNo--;
                initData(keys, wd, pageNo);
            }
        });
        next.setOnClickListener(v -> {
            if (VideoUtil.pageCount != null && pageNo < VideoUtil.pageCount) {
                pageNo++;
                initData(keys, wd, pageNo);
            }
        });




        keyWord = findViewById(R.id.keyWord);
        search.setOnClickListener(v -> {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(VideoActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            if (!TextUtils.isEmpty(keyWord.getText())) {
                String wd = keyWord.getText().toString();
                this.wd = wd;
                pageNo = 1;
                VideoUtil.pageCount = null;
                initData(null, wd, 1);
            }
        });

        baseView = findViewById(R.id.baseView);
        LinearLayout menuList = findViewById(R.id.menuList);
        scrollView = findViewById(R.id.scrollView);
        menu = findViewById(R.id.menu);
        //左边栏
        Map<Integer, String> list = new LinkedHashMap<>();
        list.put(14, "港剧");
        list.put(6, "动作片");
        list.put(7, "喜剧片");
        list.put(8, "爱情片");
        list.put(9, "科幻片");
        list.put(10, "恐怖片");
        list.put(11, "剧情片");
        list.put(12, "战争片");
        list.put(3, "综艺");
        list.put(13, "国产剧");
        list.put(15, "日剧");
        list.put(16, "欧美剧");
        list.put(20, "动漫电影");
        list.put(21, "台剧");
        list.put(22, "韩剧");
        list.put(23, "泰剧");
        list.put(24, "记录片");
        list.put(25, "其他");
        list.put(4, "动漫");

        /*list.put(14, "香港剧");
        list.put(30, "大陆综艺");
        list.put(6, "动作片");
        list.put(7, "喜剧片");
        list.put(8, "爱情片");
        list.put(9, "科幻片");
        list.put(10, "恐怖片");
        list.put(11, "剧情片");
        list.put(12, "战争片");
        list.put(13, "国产剧");
        list.put(15, "台湾剧");
        list.put(16, "韩国剧");
        // list.put(20, "纪录片");
        list.put(21, "动画片");
        list.put(22, "日本剧");
        list.put(23, "泰国剧");
        list.put(24, "欧美剧");
        list.put(25, "国产动漫");
        list.put(26, "日本动漫");
        list.put(27, "欧美动漫");
        //  list.put(28, "海外动漫");
        list.put(31, "港台综艺");
        list.put(32, "韩国综艺");
        list.put(33, "欧美综艺");*/

        int i = 0;
        for (Integer key : list.keySet()) {
            String name = list.get(key);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    height, 1.0f

            );
            //btnParams.setMargins(0, 0, 5, 0);
            Button button1 = new Button(this);//绑定当前窗口
            button1.setId(View.generateViewId());

            button1.setLayoutParams(btnParams);
            button1.setTextSize(Constants.menuTextSize);//设置字体大小
            button1.setText(name);
            button1.setTextColor(Color.WHITE);
            button1.setPadding(5, 0, 5, 0);
            button1.setBackgroundColor(Color.rgb(0, 0, 0));
            //button1.setPadding(0,0,0,50);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    keys = key;
                    wd = null;
                    pageNo = 1;
                    VideoUtil.pageCount = null;
                    initData(key, null, 1);
                }
            });


            if (i == 0) {
                button1.callOnClick();
            }
            i++;
            menuList.addView(button1);
        }
    }

    //右边栏
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

                TextView title = new TextView(this);
                title.setText(menu.getCategory());
                title.setWidth(picWidth);
                title.setTextColor(Color.WHITE);
                title.setTextSize(Constants.itemTextSize);
                title.setHeight(60);
                item.addView(title);
                layout.addView(item);
            }
            baseView.addView(layout);
        }
    }

    private void play(int i) {
        //menu.getItems().get(0)playItem.getUrl()
        Intent intent = new Intent();
        intent.setClass(VideoActivity.this, PlayVideoActivity.class);
        intent.putExtra("url", i);
        startActivity(intent);
    }


}
