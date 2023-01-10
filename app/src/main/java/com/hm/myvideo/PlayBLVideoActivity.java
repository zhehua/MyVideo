package com.hm.myvideo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.hm.myvideo.beans.Menu;
import com.hm.myvideo.beans.PlayItem;
import com.hm.myvideo.util.BLVideoUtil;
import com.hm.myvideo.util.HeaderUtil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.video.VideoSize;

import java.util.ArrayList;
import java.util.List;


public class PlayBLVideoActivity extends Activity {
    private final String TAG = "PlayActivity";
    private StyledPlayerView playerView;
    private ExoPlayer player;
    private LinearLayout itemView, detail;
    private Button full, invert;
    private TextView showTitle, showLoading;
    private String title, showTitleTex;
    private WebView webView;
    private Spinner lineSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bl_vedio_play);
        initWebView();
        initView();
        HeaderUtil.handleSSLHandshake();
    }

    boolean isFull = false, inverted = false;
    Boolean isPlay = true;

    private void initView() {
        lineSpinner = findViewById(R.id.lineSpinner);
        showTitle = findViewById(R.id.showTitle);
        showLoading = findViewById(R.id.showLoading);
        full = findViewById(R.id.full);
        invert = findViewById(R.id.invert);
        detail = findViewById(R.id.detail);
        full.setOnClickListener(v -> {
            detail.setVisibility(View.GONE);
            isFull = true;
        });
        full.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    full.setBackgroundColor(Color.rgb(0, 191, 255));
                } else {
                    full.setBackgroundColor(Color.parseColor("#FF6200EE"));
                }

            }
        });

        invert.setOnClickListener(v -> {
            if (inverted)
                inverted = false;
            else
                inverted = true;
            itemView.removeAllViews();
            initVodList(curItems);
        });
        invert.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    invert.setBackgroundColor(Color.rgb(0, 191, 255));
                } else {
                    invert.setBackgroundColor(Color.parseColor("#FF6200EE"));
                }

            }
        });

        itemView = findViewById(R.id.itemView);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int index = bundle.getInt("url");
        Menu menu = BLVideoActivity.menus.get(index);
        PlayItem item = menu.getItems().get(0);
        System.out.println(item.getUrl());
        BLVideoUtil videoUtil = new BLVideoUtil();
        new Thread(() -> {
            List<Menu> list = videoUtil.getMenuList(item.getUrl());
            Message message = new Message();
            message.obj = list;
            message.what = 0;
            uiHandler.sendMessage(message);
        }).start();

        title = menu.getCategory();


        String url = item.getUrl();
        System.out.println("即将播放：" + url);
        playerView = findViewById(R.id.videoView);
        playerView.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_WHEN_PLAYING);
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        playerView.setFocusable(false);
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                    player.prepare();
                    player.play();
                } else {
                    Toast("播放失败：" + error.getLocalizedMessage());
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying)
                    showTitle.setVisibility(View.GONE);
                else
                    showTitle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVideoSizeChanged(VideoSize videoSize) {
                showTitle.setText(showTitleTex + "[" + videoSize.width + "x" + videoSize.height + "]");
                Player.Listener.super.onVideoSizeChanged(videoSize);
            }
        });
        player.setMediaItem(MediaItem.fromUri(url));
        //    player.prepare();
        //    player.play();

        full.setFocusable(true);
        full.setFocusableInTouchMode(true);
        full.requestFocus();
        full.requestFocusFromTouch();


    }

    boolean isInit = true;

    private void ui(List<Menu> list) {
        List<PlayItem> items = list.get(0).getItems();
        //----------------
        List<String> names = new ArrayList<>();
        for (Menu menu : list) {
            names.add(menu.getCategory());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, names);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lineSpinner.setAdapter(dataAdapter);
        lineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isInit) {
                    isInit = false;
                    return;
                }
                Menu menu = list.get(i);
                itemView.removeAllViews();
                initVodList(menu.getItems());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //-----------------
        initVodList(items);
        if (items.size() == 1) {
            isFull = true;
            detail.setVisibility(View.GONE);
        }

    }

    List<PlayItem> curItems = null;

    void initVodList(List<PlayItem> items) {
        curItems = items;
        LinearLayout.LayoutParams btntParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1.0f

        );
        btntParams.setMargins(0, 0, 0, 5);

        if (inverted) {
            for (int i = items.size() - 1; i >= 0; i--) {
                PlayItem playItem = items.get(i);
                addView(playItem, btntParams);
            }
        } else {
            for (int j = 0; j < items.size(); j++) {
                PlayItem playItem = items.get(j);
                addView(playItem, btntParams);
            }
        }
    }

    void addView(PlayItem playItem, LinearLayout.LayoutParams btntParams) {
        Button button = new Button(this);//绑定当前窗口
        button.setLayoutParams(btntParams);
        button.setText(playItem.getName());
        button.setTextColor(Color.BLACK);
        button.setBackgroundColor(Color.WHITE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTitleTex = title + "[" + playItem.getName() + "]";
                showTitle.setText(showTitleTex);
                BLVideoUtil util = new BLVideoUtil();
                new Thread(() -> {
                    String playUrl = util.getPlayUrl(playItem.getUrl());
                    Log.d(TAG, "当前播放链接: " + playUrl);
                    if (playUrl != null) {
                        Message message = new Message();
                        message.what = 1;
                        message.obj = playUrl;
                        uiHandler.sendMessage(message);
                    }
                }).start();
            }
        });
        button.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    button.setBackgroundColor(Color.rgb(0, 191, 255));
                } else {
                    button.setBackgroundColor(Color.WHITE);
                }

            }
        });
        itemView.addView(button);
    }

    Toast toast;

    private void Toast(String text) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    protected void onStop() {
        player.stop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*player.prepare();
        player.play();*/
    }

    @Override
    protected void onPause() {
        player.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        player.release();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:   //菜单键 全屏
                // case KeyEvent.KEYCODE_0:   //菜单键 全屏
                if (!isFull) {
                    isFull = true;
                    detail.setVisibility(View.GONE);
                } else {
                    isFull = false;
                    detail.setVisibility(View.VISIBLE);
                }
                break;

            case KeyEvent.KEYCODE_BACK:   //返回键
                if (isFull) {
                    isFull = false;
                    detail.setVisibility(View.VISIBLE);
                    return true;
                }

                break;
            case KeyEvent.KEYCODE_DPAD_LEFT: //向左键
                long currentPosition = player.getCurrentPosition();
                playerView.showController();
                player.seekTo(currentPosition - 30_000);

                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:  //向右键
                currentPosition = player.getCurrentPosition();
                playerView.showController();
                player.seekTo(currentPosition + 30_000);
                break;

            case KeyEvent.KEYCODE_ENTER:
                if (isFull) {
                    playOrPause();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (isFull) {
                    playOrPause();
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    void playOrPause() {
        if (isPlay) {
            player.pause();
            isPlay = false;
        } else {
            player.play();
            isPlay = true;
        }
    }

    private Handler uiHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                List<Menu> list = (List<Menu>) msg.obj;
                if (list.size() > 0) {
                    ui(list);

                    String key = list.get(0).getItems().get(0).getKey();
                    if (!TextUtils.isEmpty(key)) {
                        showLoading.setVisibility(View.VISIBLE);
                        showTitleTex = title + "[" + list.get(0).getItems().get(0).getName() + "]";
                        showTitle.setText(showTitleTex);
                        showLoading.setText("开始解析" + showTitle.getText());
                        webView.loadUrl(key);
                    }
                }
            } else if (msg.what == 1) {
                String playUrl = (String) msg.obj;
                System.out.println("--->>  " + playUrl);
                showLoading.setVisibility(View.VISIBLE);
                showLoading.setText("开始解析" + showTitle.getText());
                webView.loadUrl(playUrl);

            } else if (msg.what == 2) {
                showLoading.setVisibility(View.GONE);
                String playUrl = (String) msg.obj;
                player.setMediaItem(MediaItem.fromUri(playUrl));
                player.prepare();
                player.play();
            } else if (msg.what == 3) {
                showLoading.setVisibility(View.GONE);
                Toast("解析失败，请选择其他线路");
            }
        }

    };

    public class JavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            if (html.contains("解析失败")) {
                System.out.println("获取链接失败");
                Message message = new Message();
                message.what = 3;
                uiHandler.sendMessage(message);
            }
        }
    }

    String curUrl = null;

    void initWebView() {
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBlockNetworkImage(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new JavaScriptLocalObj(), "local_obj");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:window.local_obj.showSource('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                super.onPageFinished(view, url);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String host = request.getUrl().getHost();
                String path = request.getUrl().getPath();
                if (host != null && host.contains("cdn.jsdelivr.net")) {
                    return null;
                }
                if (path != null) {
                    if (path.contains("hls.js") || path.contains("flv.min.js") || path.endsWith(".css")
                            || path.endsWith("pro.min.js")
                            || path.endsWith(".ico") || path.contains("/collect") || path.endsWith("layer.min.js")) {
                        return null;
                    }
                    if (path.contains("m3u8") || path.contains(".mp4")) {
                        if (!path.equals(curUrl)) {
                            System.out.println("--- 55555555555555555， " + request.getUrl());
                            curUrl = path;
                            webView.post(new Runnable() {
                                @Override
                                public void run() {
                                    webView.stopLoading();
                                    Message message = new Message();
                                    message.what = 2;
                                    message.obj = request.getUrl().toString();
                                    uiHandler.sendMessage(message);
                                }
                            });
                        }
                        return null;
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }
        });
    }
}