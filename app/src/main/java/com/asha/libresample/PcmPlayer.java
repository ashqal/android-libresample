package com.asha.libresample;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioTrack;

import java.nio.ByteBuffer;

/**
 * Created by lake on 22/06/16.
 *
 */

public class PcmPlayer {
    private AudioTrack trackplayer;

    public PcmPlayer() {

    }

    public void prepare(Context context, int streamType) {
        int bufsize = AudioTrack.getMinBufferSize(48000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        trackplayer = new AudioTrack(streamType,
                16000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufsize,
                AudioTrack.MODE_STREAM);
    }

    public void write(byte[] data, int offset, int len) {
        trackplayer.write(data, offset, len);
    }

    public void start() {
        trackplayer.play();
    }

    public void destroy() {
        trackplayer.stop();
        trackplayer.release();
    }

    public void pause() {
        trackplayer.pause();
        trackplayer.flush();
    }

    public void resume() {
        trackplayer.play();
    }

    public void write(ByteBuffer buffer) {
        int remain = buffer.remaining();
        buffer.get(buffer.array(), buffer.arrayOffset(), remain);
        trackplayer.write(buffer.array(), buffer.arrayOffset(), remain);
    }
}
