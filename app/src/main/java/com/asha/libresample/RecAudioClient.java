package com.asha.libresample;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by lake on 16-5-24.
 *
 */
public class RecAudioClient {
    private static final String TAG = "SOUNDTOUCH";
    private final Object syncOp = new Object();
    private AudioRecordThread audioRecordThread;
    private AudioRecord audioRecord;
    private ByteBuffer audioBuffer;
    private int audioRecordBufferSize;
    private int audioRecordSliceSize;
    private AudioCallback callback;
    private static final int sSampleRate = 48000;

    public RecAudioClient() {
    }

    public int prepare(int audioSource, AudioCallback callback) {
        synchronized (syncOp) {
            this.callback = callback;
            audioRecordSliceSize = 2048;
            audioRecordBufferSize = audioRecordSliceSize;
            return prepareAudio(audioSource);
        }
    }

    private boolean start() {
        synchronized (syncOp) {
            if (audioRecordThread != null && audioRecordThread.isRunning) {
                return true;
            }

            audioRecord.startRecording();
            audioRecordThread = new AudioRecordThread();
            audioRecordThread.start();

            if (callback != null) {
                callback.onRecStarted(audioRecord);
            }
            return true;
        }
    }

    private boolean stop() {
        synchronized (syncOp) {
            audioRecordThread.quit();
            try {
                audioRecordThread.join();
            } catch (InterruptedException ignored) {
            }
            audioRecordThread = null;
            audioRecord.stop();
            if (callback != null) {
                callback.onRecStopped();
            }
            return true;
        }
    }

    public boolean destroy() {
        synchronized (syncOp) {
            audioRecord.release();
            return true;
        }
    }

    private int prepareAudio(int audioSource) {
        int minBufferSize = AudioRecord.getMinBufferSize(sSampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(audioSource,
                sSampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize * 5);
        // audioRecordBufferSize
        audioBuffer = ByteBuffer.allocateDirect(audioRecordBufferSize * 2);
        if (AudioRecord.STATE_INITIALIZED != audioRecord.getState()) {
            Log.e("aa", "audioRecord.getState()!=AudioRecord.STATE_INITIALIZED!");
            return -1;
        }
        if (AudioRecord.SUCCESS != audioRecord.setPositionNotificationPeriod(audioRecordSliceSize)) {
            Log.e("aa", "AudioRecord.SUCCESS != audioRecord.setPositionNotificationPeriod(" + audioRecordSliceSize + ")");
            return -1;
        }
        return audioRecord.getAudioSessionId();
    }

    public void resume() {
        start();
    }

    public void pause() {
        stop();
    }

    private class AudioRecordThread extends Thread {
        private boolean isRunning = true;

        AudioRecordThread() {
            isRunning = true;
        }

        public void quit() {
            isRunning = false;
        }

        @Override
        public void run() {
            while (isRunning) {
                int size = audioRecord.read(audioBuffer.array(), audioBuffer.arrayOffset(), audioRecordBufferSize);

                if (!isRunning) {
                    return;
                }

                if (size > 0) {
                    audioBuffer.clear();
                    audioBuffer.put(audioBuffer.array(), audioBuffer.arrayOffset(), audioRecordBufferSize);
                    audioBuffer.flip();
                    audioBuffer.mark();
                    callback.onRecvAudioData(audioBuffer);
                } else {
                    callback.onRecError("size 0!");
                }
            }
        }
    }

    public interface AudioCallback {
        void onRecvAudioData(ByteBuffer audioBuffer);
        void onRecStarted(AudioRecord audioRecord);
        void onRecStopped();
        void onRecError(String s);
    }
}
