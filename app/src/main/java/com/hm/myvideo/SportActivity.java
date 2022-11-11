package com.hm.myvideo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.myvideo.beans.Match;
import com.hm.myvideo.beans.Menu;
import com.hm.myvideo.beans.PlayItem;
import com.hm.myvideo.util.Constants;
import com.hm.myvideo.util.SportUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.internal.StringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SportActivity extends Activity {
    private LinearLayout baseView, cat;
    // private Button all, nba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sport);
        initView();
    }

    private void initView() {
        cat = findViewById(R.id.cat);
        List<String> list = new ArrayList<>(11);
        list.add("全部");
        list.add("NBA");
        list.add("CBA");
        list.add("中超");
        list.add("英超");
        list.add("西甲");
        list.add("德甲");
        list.add("意甲");
        list.add("法甲");
        list.add("中甲");
        list.add("欧冠");
        LinearLayout.LayoutParams btntParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
        int btnId = View.generateViewId();
        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i);
            Button button = new Button(this);//绑定当前窗口
            if (i == 0)
                button.setId(btnId);
            button.setLayoutParams(btntParams);
            button.setTextSize(Constants.itemTextSize);//设置字体大小
            button.setText(name);
            button.setBackgroundColor(Color.WHITE);
            button.setPadding(0, 0, 0, 0);
            String text = button.getText().toString();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(() -> search(text)).start();
                }
            });

            cat.addView(button);
        }
        Button button = findViewById(btnId);
        button.callOnClick();
        baseView = findViewById(R.id.base);

    }

    private void search(String game) {
        SportUtil util = new SportUtil();
        String sports = util.sportStr(game);
        if (StringUtil.isBlank(sports))
            return;
        Message message = new Message();
        Bundle bundle = new Bundle();
        message.what = 1;
        bundle.putString("sports", sports);
        message.setData(bundle);
        uiHandler.sendMessage(message);
    }

    private Handler uiHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                baseView.removeAllViews();
                String sportStr = msg.getData().getString("sports");
                boolean isNull = true;

                JSONArray array = null;
                try {
                    array = new JSONArray(sportStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.optJSONObject(i);
                    List<Match> list = new ArrayList<>();
                    JSONArray matches = jsonObject.optJSONArray("matches");
                    int size = matches.length();
                    if (size > 0) {
                        isNull = false;
                        TextView date = new TextView(SportActivity.this);
                        String dateStr = jsonObject.optString("dateStr");
                        String playDateStr = jsonObject.optString("playDateStr");
                        date.setText(dateStr);
                        date.setTextSize(Constants.itemTextSize);
                        date.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                        date.setGravity(Gravity.CENTER);
                        date.setHeight(70);
                        date.setBackgroundColor(Color.BLACK);
                        date.setTextColor(Color.WHITE);
                        baseView.addView(date);

                        for (int x = 0; x < size; x++) {
                            JSONObject object = matches.optJSONObject(x);
                            Match match = new Match();
                            match.setGame(object.optString("game"));
                            match.setId(object.optInt("id"));
                            match.setName(object.optString("name"));
                            match.setGuestTeamName(object.optString("guestTeamName"));
                            match.setMasterTeamName(object.optString("masterTeamName"));
                            match.setPlayTime(object.optString("playTime"));
                            match.setLink(object.optString("link"));
                            JSONArray lives = object.optJSONArray("lives");
                            if (lives.length() > 0) {
                                List<PlayItem> items = new ArrayList<>();
                                for (int j = 0; j < lives.length(); j++) {
                                    JSONObject live = lives.optJSONObject(j);
                                    PlayItem playItem = new PlayItem();
                                    playItem.setUrl(live.optString("link"));
                                    playItem.setName(live.optString("name"));
                                    items.add(playItem);
                                }
                                match.setItems(items);
                                String link = lives.optJSONObject(0).optString("link");
                                match.setLink(link);
                            }
                    /*match.setGuestTeamLink(download(match.getGuestTeamLink()));
                    match.setMasterTeamLink(download(match.getMasterTeamLink()));*/
                            list.add(match);
                            show(match, playDateStr);
                        }

                    }

                }
                if (isNull) {
                    TextView date = new TextView(SportActivity.this);
                    date.setText("近期无直播");
                    date.setTextSize(Constants.itemTextSize);
                    date.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                    date.setGravity(Gravity.CENTER);
                    date.setHeight(70);
                    date.setBackgroundColor(Color.GRAY);
                    date.setTextColor(Color.WHITE);
                    baseView.addView(date);
                }
            } else if (msg.what == 2) {
                String link = msg.getData().getString("link");
                if (link != null) {
                    Intent intent = new Intent();
                   /* intent.setClass(SportActivity.this, com.hm.myvideo.PlaySportActivity.class);
                    intent.putExtra("url", link);*/
                    intent.setClass(SportActivity.this, PlayVideoActivity.class);
                    intent.putExtra("url", 0);
                    startActivity(intent);
                }
            }

        }
    };

    private void show(Match match, String playDateStr) {
        LinearLayout layout = new LinearLayout(SportActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(//布局参数设置
                ViewGroup.LayoutParams.MATCH_PARENT,//宽度内容占满整行
                ViewGroup.LayoutParams.WRAP_CONTENT//高度内容自适应
                , 1.0f
        );
        layout.setLayoutParams(layoutParams);//给layout设置布局参数
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(2, 5, 2, 10);
        layout.setFocusable(true);
        layout.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                layout.setBackgroundColor(Color.rgb(34, 139, 34));
            } else {
                layout.setBackgroundColor(Color.WHITE);
            }
        });
        TextView time = new TextView(SportActivity.this);
        time.setText(match.getPlayTime());
        time.setTextColor(Color.BLACK);
        time.setTextSize(Constants.itemTextSize);
        time.setWidth(120);
        layout.addView(time);

        TextView game = new TextView(SportActivity.this);
        game.setText(match.getGame());
        game.setTextColor(Color.BLACK);
        game.setTextSize(Constants.itemTextSize);
        game.setGravity(Gravity.CENTER);
        game.setWidth(250);
        layout.addView(game);

        int w = baseView.getWidth() - 320;
        int m = w / 2;
        int g = w - m;
        TextView masterTeamName = new TextView(SportActivity.this);
        masterTeamName.setText(match.getMasterTeamName() + " VS ");
        masterTeamName.setTextColor(Color.BLACK);
        masterTeamName.setTextSize(Constants.itemTextSize);
        masterTeamName.setGravity(Gravity.RIGHT);
        masterTeamName.setWidth(m);
        layout.addView(masterTeamName);

        TextView guestTeamName = new TextView(SportActivity.this);
        guestTeamName.setText(match.getGuestTeamName());
        guestTeamName.setTextColor(Color.BLACK);
        guestTeamName.setTextSize(Constants.itemTextSize);
        guestTeamName.setGravity(Gravity.LEFT);
        guestTeamName.setWidth(g);
        layout.addView(guestTeamName);

        layout.setOnClickListener(v -> {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            try {
                Date matchDate = df.parse(playDateStr + " " + match.getPlayTime() + ":00");
                Date now = new Date();
                if (matchDate.before(now)) {

                    VideoActivity.menus.clear();
                    List<PlayItem> itemList = match.getItems();
                    //System.out.println(link);
                    Menu menu = new Menu();
                    if (itemList != null && itemList.size() > 0) {

                        menu.setItems(itemList);
                        VideoActivity.menus.add(menu);
                        Intent intent = new Intent();
                       /* intent.setClass(SportActivity.this, com.hm.myvideo.PlaySportActivity.class);
                        intent.putExtra("url", match.getLink());*/
                        intent.setClass(SportActivity.this, PlayVideoActivity.class);
                        intent.putExtra("url", 0);
                        startActivity(intent);
                    } else {
                        SportUtil sportUtil = new SportUtil();
                        new Thread(() -> {
                            JSONArray lives = sportUtil.link(match.getId());
                            if (lives != null && lives.length() > 0) {
                                List<PlayItem> items = new ArrayList<>();
                                for (int j = 0; j < lives.length(); j++) {
                                    JSONObject live = lives.optJSONObject(j);
                                    PlayItem playItem = new PlayItem();
                                    playItem.setUrl(live.optString("link"));
                                    playItem.setName(live.optString("name"));
                                    items.add(playItem);
                                }
                                menu.setItems(items);
                                VideoActivity.menus.add(menu);
                            }
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            message.what = 2;
                            bundle.putString("link", "");
                            message.setData(bundle);
                            uiHandler.sendMessage(message);

                        }).start();
                    }
                } else {
                    Toast toast = Toast.makeText(SportActivity.this, "比赛未开始", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        baseView.addView(layout);
    }
}