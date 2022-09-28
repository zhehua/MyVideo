package com.hm.myvideo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.hm.myvideo.beans.PlayItem;
import com.hm.myvideo.util.Constants;
import com.hm.myvideo.util.TvUtil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;


public class PlayActivity extends Activity {
    private final String TAG = "PlayActivity";
    private StyledPlayerView playerView;
    private ExoPlayer player;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        index = bundle.getInt("index");
        playerView = findViewById(R.id.videoView);
        playerView.setUseController(false);
        playerView.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_WHEN_PLAYING);
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                //.setUserAgent(PubVar.usrAgent)
                .setConnectTimeoutMs(10_000)
                .setReadTimeoutMs(10_000)
                .setAllowCrossProtocolRedirects(true);


        player = new ExoPlayer.Builder(this).setMediaSourceFactory(new HlsMediaSource.Factory(dataSourceFactory)).build();
        playerView.setPlayer(player);
        player.addListener(new Player.Listener() {
           /* @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                System.out.println("调试 onIsPlayingChanged ---" + isPlaying);
                if (isPlaying)
                    progressBar.setVisibility(View.GONE);
                else
                    progressBar.setVisibility(View.VISIBLE);
            }*/

            /*@Override
            public void onIsLoadingChanged(boolean isLoading) {
                System.out.println("调试 isLoading ---" + isLoading);
            }

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                System.out.println("调试 onMediaItemTransition ---" + reason);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                System.out.println("调试 onPlaybackStateChanged ---" + playbackState);
            }

            @Override
            public void onPlayerErrorChanged(@Nullable PlaybackException error) {
                System.out.println("调试 onPlayerErrorChanged ---" + error);
            }*/

            @Override
            public void onPlayerError(PlaybackException error) {
                System.out.println("调试 PlaybackException ---" + error.errorCode + " -> " + error.getErrorCodeName());
                if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                    player.prepare();
                    player.play();
                } else {
                    Toast("播放链接已失效");
                }
            }

           /* @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                System.out.println("调试 onPlayWhenReadyChanged "+playWhenReady +" -->> "+reason);
            }*/
        });

        play(index);

    }

    private void play(int index) {
        int total = MainActivity.nameUrl.size();
        if (index >= total) {
            System.err.println("超出 " + total + " -> " + index);
            return;
        } else if (index < 0) {
            System.err.println("小于 " + total + " -> " + index);
            return;
        }
        PlayItem playItem = MainActivity.nameUrl.get(index);
        Toast("即将播放：" + playItem.getName());
        Long expirationTime = playItem.getExpirationTime();
        if (null != expirationTime && System.currentTimeMillis() > expirationTime) {
            playItem.setUrl(null);
        }
        if (playItem.getUrl() != null) {
            player.setMediaItem(MediaItem.fromUri(playItem.getUrl()));
            player.prepare();
            player.play();
        } else {
            new Thread(() -> {
                String key = playItem.getKey();
                String m3u8Url = null;
                if (key.startsWith("gz_")) {
                    m3u8Url = TvUtil.getGz(key.replace("gz_", ""));
                } else if (key.startsWith("gd_")) {
                    m3u8Url = TvUtil.getGd(index);
                } else {
                    m3u8Url = TvUtil.getM3u8Url(Constants.tvDomain + key);
                    if (null != m3u8Url)
                        m3u8Url = m3u8Url.replace("2300000", "4000000");
                }
                Log.i(TAG, "重新获取[" + playItem.getName() + "]播放地址\n" + m3u8Url);
                if (null != m3u8Url) {
                    playItem.setUrl(m3u8Url);
                    playItem.setExpirationTime(System.currentTimeMillis() + Constants.videoValidTime);
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("m3u8Url", m3u8Url);
                    message.setData(bundle);
                    uiHandler.sendMessage(message);

                } else {
                    //MainActivity.nameUrl.remove(index);
                    this.runOnUiThread(() -> {
                        Toast(playItem.getName() + "的播放链接已失效");
                    });

                }

            }).start();
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

    private Handler uiHandler = new Handler() {
        public void handleMessage(Message msg) {
            MediaItem mediaItem = MediaItem.fromUri(msg.getData().getString("m3u8Url"));
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }

    };

}