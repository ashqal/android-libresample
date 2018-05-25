package com.asha.libresample;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.asha.libresample2.Resample;

import java.nio.ByteBuffer;

public class RecActivity extends AppCompatActivity {

    private static final String TAG = "RecActivity";
    
    private ByteBuffer cached;

    private ByteBuffer processed;

    private RecAudioClient client;

    private PcmPlayer pcmPlayer;

    private State state = State.Recording;

    private TextView tv;

    private Resample resample;

    private enum State {
        Recording,
        Playing,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        client = new RecAudioClient();
        pcmPlayer = new PcmPlayer();
        cached = ByteBuffer.allocateDirect(1024 * 100);
        processed = ByteBuffer.allocateDirect(4096);

        resample = new Resample();
        resample.create(48000, 16000, 2048, 1);

        final AudioManager audioManager = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
        audioManager.setSpeakerphoneOn(true);

        tv = findViewById(R.id.sample_text);
        record();
    }

    private void record() {
        pcmPlayer.prepare(this, AudioManager.STREAM_MUSIC);

        client.prepare(MediaRecorder.AudioSource.DEFAULT, new RecAudioClient.AudioCallback() {
            @Override
            public void onRecvAudioData(ByteBuffer buffer) {
                buffer.reset();
                switch (state) {
                    case Recording:
                        if (cached.remaining() >= buffer.remaining()) {
                            int num = resample.resample(buffer, processed, buffer.remaining());
                            Log.w(TAG, String.format("input size:%d output size:%d", buffer.limit(), num));
                            cached.put(processed.array(), processed.arrayOffset(), num);
                            setText(String.format("正在录音:%d/%d", cached.position(), cached.limit()));
                        } else {
                            cached.flip();
                            cached.mark();
                            state = State.Playing;
                        }
                        break;
                    case Playing:
                        int limit = Math.min(buffer.remaining(), cached.remaining());
                        if (limit > 0) {
                            pcmPlayer.write(cached.array(), cached.arrayOffset() + cached.position(), limit);
                            cached.position(cached.position() + limit);
                            setText(String.format("正在播放:%d/%d", cached.position(), cached.limit()));
                        } else {
                            cached.clear();
                            state = State.Recording;
                        }
                        break;
                }
            }

            @Override
            public void onRecStarted(AudioRecord audioRecord) {}

            @Override
            public void onRecStopped() {}

            @Override
            public void onRecError(String s) {}
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resample.destroy();
        client.destroy();
        pcmPlayer.destroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        client.pause();
        pcmPlayer.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        client.resume();
        pcmPlayer.resume();
    }

    private void setText(final String format) {
        tv.post(new Runnable() {
            @Override
            public void run() {
                tv.setText(format);
            }
        });
    }
}
