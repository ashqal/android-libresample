package com.asha.libresample2;

import java.nio.ByteBuffer;

/**
 * Created by hzqiujiadi on 2018/5/24.
 * hzqiujiadi ashqalcn@gmail.com
 */
public class Resample {

    private long ptr;

    private static final String RESAMPLE_LIB = "resample";

    static {
        System.loadLibrary(RESAMPLE_LIB);
    }

    public void create(int inputRate, int outputRate, int bufferSize, int channels) {
        ptr = init(inputRate, outputRate, bufferSize, channels);
    }

    public void destroy() {
        close(ptr);
        ptr = 0;
    }

    public double getFactor() {
        return getFactor(ptr);
    }

    public int resample(ByteBuffer inputBuffer, ByteBuffer outputBuffer, int numSamples) {
        return resample(getFactor(), inputBuffer, outputBuffer, numSamples);
    }

    private native int resample(double factor, ByteBuffer inputBuffer, ByteBuffer outputBuffer, int dataLen);

    private native long init(int inputRate, int outputRate, int bufferSize, int channels);

    private native double getFactor(long ptr);

    private native void close(long ptr);
}
