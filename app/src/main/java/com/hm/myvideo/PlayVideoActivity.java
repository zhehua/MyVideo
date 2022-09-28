package com.hm.myvideo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hm.myvideo.beans.Menu;
import com.hm.myvideo.beans.PlayItem;
import com.hm.myvideo.util.HeaderUtil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.util.List;


public class PlayVideoActivity extends Activity {
    private final String TAG = "PlayActivity";
    private StyledPlayerView playerView;
    private ExoPlayer player;
    private LinearLayout itemView, detail;
    private Button full, pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_play);
        initView();
        HeaderUtil.handleSSLHandshake();
    }

    boolean isFull = false;
    Boolean isPlay = true;

    private void initView() {
        full = findViewById(R.id.full);
        detail = findViewById(R.id.detail);
        full.setOnClickListener(v -> {
            detail.setVisibility(View.GONE);
            isFull = true;
        });

        pause = findViewById(R.id.pause);
        pause.setOnClickListener(v -> {
            playOrPause();
        });

        itemView = findViewById(R.id.itemView);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int index = bundle.getInt("url");
        Menu menu = VideoActivity.menus.get(index);
        ui(menu);
        String url = menu.getItems().get(0).getUrl();
        System.out.println("即将播放："+url);
        playerView = findViewById(R.id.videoView);
        playerView.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_WHEN_PLAYING);
        playerView.setShowFastForwardButton(false);
        playerView.setShowShuffleButton(false);
        playerView.setShowSubtitleButton(false);
        playerView.setShowNextButton(false);
        playerView.setShowPreviousButton(false);
        playerView.setShowRewindButton(false);
        playerView.setShowVrButton(false);
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                    player.prepare();
                    player.play();
                } else {
                    Toast("播放链接已失效");
                }
            }

        });
        player.setMediaItem(MediaItem.fromUri(url));
        player.prepare();
        player.play();
    }

    private void ui(Menu menu) {
        List<PlayItem> items = menu.getItems();
        LinearLayout.LayoutParams btntParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1.0f

        );
        btntParams.setMargins(0, 0, 0, 5);
        for (int j = 0; j < items.size(); j++) {
            PlayItem playItem = items.get(j);
            Button button = new Button(this);//绑定当前窗口
            button.setLayoutParams(btntParams);
            button.setText(playItem.getName());
            button.setTextColor(Color.BLACK);
            button.setBackgroundColor(Color.WHITE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "当前播放链接: " + playItem.getUrl());
                    player.setMediaItem(MediaItem.fromUri(playItem.getUrl()));
                    player.prepare();
                    player.play();
                }
            });
            itemView.addView(button);
            if (items.size() == 1) {
                isFull = true;
                detail.setVisibility(View.GONE);
            }
        }

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
        super.onStop();
        player.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.prepare();
        player.play();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
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


}