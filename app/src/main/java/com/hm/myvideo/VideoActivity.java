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


import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
                info.setText("?????????" + pageNo + "?????????" + VideoUtil.pageCount + "???");
                showUI();
            } else if (msg.what == 1) {
                String texts = msg.getData().getString("text");
                String[] strings = texts.split("&");
                if (strings.length == 2) {
                    keyWord.setText(strings[0]);
                    item = strings[1].trim();
                    search.callOnClick();
                } else
                    Toast.makeText(VideoActivity.this, "????????????????????????", Toast.LENGTH_LONG).show();
            } else if (msg.what == 2) {
                Toast.makeText(VideoActivity.this, msg.getData().getString("text"), Toast.LENGTH_LONG).show();
            }
        }

    };

    void initView() {
        /*spinner = findViewById(R.id.spinner);
        List<String> names = new ArrayList<String>();
        names.add("U???");
        names.add("??????");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, names);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);*/

        spinner = findViewById(R.id.ySpinner);
        List<String> names = new ArrayList<String>();
        names.add("??????");
        int y= Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 5; i++) {
            names.add(y-i+"");
        }

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
        //?????????
        Map<Integer, String> list = new LinkedHashMap<>();
        list.put(14, "??????");
        list.put(6, "?????????");
        list.put(7, "?????????");
        list.put(8, "?????????");
        list.put(9, "?????????");
        list.put(10, "?????????");
        list.put(11, "?????????");
        list.put(12, "?????????");
        list.put(3, "??????");
        list.put(13, "?????????");
        list.put(15, "??????");
        list.put(16, "?????????");
        list.put(20, "????????????");
        list.put(21, "??????");
        list.put(22, "??????");
        list.put(23, "??????");
        list.put(24, "?????????");
        list.put(25, "??????");
        list.put(4, "??????");

        /*list.put(14, "?????????");
        list.put(30, "????????????");
        list.put(6, "?????????");
        list.put(7, "?????????");
        list.put(8, "?????????");
        list.put(9, "?????????");
        list.put(10, "?????????");
        list.put(11, "?????????");
        list.put(12, "?????????");
        list.put(13, "?????????");
        list.put(15, "?????????");
        list.put(16, "?????????");
        // list.put(20, "?????????");
        list.put(21, "?????????");
        list.put(22, "?????????");
        list.put(23, "?????????");
        list.put(24, "?????????");
        list.put(25, "????????????");
        list.put(26, "????????????");
        list.put(27, "????????????");
        //  list.put(28, "????????????");
        list.put(31, "????????????");
        list.put(32, "????????????");
        list.put(33, "????????????");*/

        int i = 0;
        for (Integer key : list.keySet()) {
            String name = list.get(key);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    height, 1.0f

            );
            //btnParams.setMargins(0, 0, 5, 0);
            Button button1 = new Button(this);//??????????????????
            button1.setId(View.generateViewId());

            button1.setLayoutParams(btnParams);
            button1.setTextSize(Constants.menuTextSize);//??????????????????
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

    //?????????
    private void showUI() {
        baseView.removeAllViews();
        //List<PlayItem> nameUrl=menus.
        double line = 5.0;
        double ceil = Math.ceil(menus.size() / line);
        //System.out.println(ceil);
        for (int i = 0; i < ceil; i++) {
            LinearLayout layout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(//??????????????????
                    ViewGroup.LayoutParams.MATCH_PARENT,//????????????????????????
                    ViewGroup.LayoutParams.WRAP_CONTENT//?????????????????????
                    , 1.0f
            );
            layout.setLayoutParams(layoutParams);//???layout??????????????????
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
                LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(//??????????????????
                        width,//????????????????????????
                        ViewGroup.LayoutParams.WRAP_CONTENT//?????????????????????
                        , 1.0f
                );
                item.setFocusable(true);
                item.setLayoutParams(itemParams);//???layout??????????????????
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
                        picWidth,//????????????????????????
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
                title.setTextSize(12);
               // title.setHeight(60);
                item.addView(title);
                //??????
                TextView remark = new TextView(this);
                remark.setText(menu.getVodRemarks());
                remark.setWidth(picWidth);
                remark.setTextColor(Color.WHITE);
                remark.setTextSize(8);
               // remark.setHeight(20);
                item.addView(remark);
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
