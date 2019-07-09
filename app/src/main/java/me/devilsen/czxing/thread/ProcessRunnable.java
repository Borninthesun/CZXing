package me.devilsen.czxing.thread;

import me.devilsen.czxing.BarcodeReader;
import me.devilsen.czxing.processor.BarcodeProcessor;

/**
 * desc : 二维码处理线程
 * date : 2019-07-01 18:47
 *
 * @author : dongSen
 */
public class ProcessRunnable implements Runnable {

    private Dispatcher dispatcher;
    private FrameData frameData;
    private BarcodeProcessor mBarcodeProcessor;
    private Callback mDecodeCallback;

    ProcessRunnable(Dispatcher dispatcher, FrameData frameData, Callback callback) {
        this.dispatcher = dispatcher;
        this.frameData = frameData;
        this.mDecodeCallback = callback;
        mBarcodeProcessor = new BarcodeProcessor();
    }

    @Override
    public void run() {
        try {
            BarcodeReader.Result result = mBarcodeProcessor.processBytes(frameData.data,
                    frameData.left,
                    frameData.top,
                    frameData.width,
                    frameData.height,
                    frameData.rowWidth);

            if (frameData.left == 0 && frameData.top == 0 && result != null) {
                boolean isDark = mBarcodeProcessor.analysisBrightness(frameData.data, frameData.width, frameData.height);
                if (mDecodeCallback != null) {
                    mDecodeCallback.onDarkBrightness(isDark);
                }
            }

            if (result != null && mDecodeCallback != null) {
                mDecodeCallback.onDecodeComplete(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dispatcher.finished(this);
        }
    }

    public void cancel() {
        mBarcodeProcessor.cancel();
    }

    public void enqueue() {
        dispatcher.enqueue(this);
    }
}
