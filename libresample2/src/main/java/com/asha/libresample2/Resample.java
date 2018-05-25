package com.asha.libresample2;

import java.nio.ByteBuffer;

/**
 * Created by hzqiujiadi on 2018/5/24.
 * hzqiujiadi ashqalcn@gmail.com
 */
public class Resample {

    private long ptr = -1;

    private static final String RESAMPLE_LIB = "resample";

    static {
        System.loadLibrary(RESAMPLE_LIB);
    }

    public void create(int inputRate, int outputRate, int bufferSize, int channels) {
        ptr = init(inputRate, outputRate, bufferSize, channels);
        if (ptr == -1) {
            throw new IllegalArgumentException("create failed");
        }
    }

    public void destroy() {
        close(ptr);
        ptr = -1;
    }

    public double getFactor() {
        return getFactor(ptr);
    }

    public int resample(ByteBuffer inputBuffer, ByteBuffer outputBuffer, int byteLen) {
        return resample(getFactor(), inputBuffer, outputBuffer, byteLen);
    }

    public int resampleEx(ByteBuffer inputBuffer, ByteBuffer outputBuffer, int byteLen) {
        return resampleEx(ptr, inputBuffer, outputBuffer, byteLen);
    }

    private native int resample(double factor, ByteBuffer inputBuffer, ByteBuffer outputBuffer, int dataLen);

    private native int resampleEx(long ptr, ByteBuffer inputBuffer, ByteBuffer outputBuffer, int dataLen);

    private native long init(int inputRate, int outputRate, int bufferSize, int channels);

    private native double getFactor(long ptr);

    private native void close(long ptr);
}
