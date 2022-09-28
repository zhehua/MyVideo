package com.hm.myvideo;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.hm.myvideo.beans.Menu;
import com.hm.myvideo.beans.PlayItem;
import com.hm.myvideo.util.Constants;
import com.hm.myvideo.util.TvUtil;
import com.hm.myvideo.util.VideoUtil;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private ScrollView scrollView, menu;
    private LinearLayout baseView;
    public static List<PlayItem> nameUrl = new ArrayList<>();
    private int height = 130;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        if (menus.size() == 0) {
            initData();
        }
    }

    public static List<Menu> menus = new ArrayList<>();

    void initData() {
        new Thread(() -> {
            menus = TvUtil.getMenus();
            uiHandler.sendEmptyMessage(0);
        }).start();
    }

    private Handler uiHandler = new Handler() {

        public void handleMessage(Message msg) {
            initView();
        }

    };
    boolean isM = true;

    void initView() {
        baseView = (LinearLayout) findViewById(R.id.baseView);
        LinearLayout menuList = (LinearLayout) findViewById(R.id.menuList);
        scrollView = findViewById(R.id.scrollView);
        menu = findViewById(R.id.menu);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height-2, 1.0f

        );
        btnParams.setMargins(0, 0, 0, 2);
        for (int i = 0; i < menus.size(); i++) {
            Button button1 = new Button(this);//绑定当前窗口
            button1.setId(View.generateViewId());
            button1.setLayoutParams(btnParams);
            button1.setTextSize(Constants.menuTextSize);//设置字体大小
            Menu menu = menus.get(i);
            button1.setText(menu.getCategory());
            button1.setTextColor(Color.WHITE);
            button1.setBackgroundColor(Color.BLACK);
            //button1.setPadding(0,0,0,50);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showUI(menu.getItems());
                }
            });


            if (i == 0) {
                button1.setFocusable(true);
                button1.setFocusableInTouchMode(true);
                button1.requestFocus();
                button1.requestFocusFromTouch();
            }
            menuList.addView(button1);
        }
        //比赛

        Button button1 = new Button(this);
        button1.setId(View.generateViewId());
        button1.setLayoutParams(btnParams);
        button1.setTextSize(Constants.menuTextSize);
        button1.setText("赛事");
        button1.setTextColor(Color.WHITE);
        button1.setBackgroundColor(Color.BLACK);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SportActivity.class);
                startActivity(intent);
            }
        });
        menuList.addView(button1);

        //电影

        Button movBtn = new Button(this);
        movBtn.setId(View.generateViewId());
        movBtn.setLayoutParams(btnParams);
        movBtn.setTextSize(Constants.menuTextSize);
        movBtn.setText("电影");
        movBtn.setTextColor(Color.WHITE);
        movBtn.setBackgroundColor(Color.BLACK);


        movBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);
                startActivity(intent);
            }
        });
        menuList.addView(movBtn);


        if (menus.size() > 0)
            showUI(menus.get(0).getItems());

    }

    int btnId = View.generateViewId();

    //右边栏
    private void showUI(List<PlayItem> items) {
        baseView.removeAllViews();
        nameUrl = items;
        double line = 5.0;
        double ceil = Math.ceil(nameUrl.size() / line);
        //System.out.println(ceil);
        for (int i = 0; i < ceil; i++) {
            LinearLayout layout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(//布局参数设置
                    ViewGroup.LayoutParams.MATCH_PARENT,//宽度内容占满整行
                    height//高度内容自适应
                    , 1.0f
            );
            layout.setLayoutParams(layoutParams);//给layout设置布局参数
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(0, 0, 0, 5);
            LinearLayout.LayoutParams btntParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1.0f

            );
            btntParams.setMargins(5, 0, 0, 0);
            int width = layout.getWidth() / 5;
            for (int j = 0; j < line; j++) {
                int index = (int) ((line * i) + j);
                if (index >= nameUrl.size())
                    break;
                //System.out.println(index);
                PlayItem playItem = nameUrl.get(index);
                Button button = new Button(this);//绑定当前窗口
                if (index == 0)
                    button.setId(btnId);
                button.setLayoutParams(btntParams);
                button.setTextSize(Constants.itemTextSize);//设置字体大小
                //button.setText(index + 1 + " " + playItem.getName());
                button.setText(playItem.getName());
                //  button.setMinHeight(weight);
                button.setMaxWidth(width);
                //button1.setTextColor(Color.WHITE);
                button.setBackgroundColor(Color.WHITE);
                button.setPadding(0, 0, 0, 0);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        play(index);
                    }
                });

                layout.addView(button);
            }
            baseView.addView(layout);
        }
    }

    private void play(int index) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, PlayActivity.class);
        intent.putExtra("index", index);
        startActivity(intent);
    }



}
