package com.example.sharan.iotsmartchain.qrcodescanner.decode;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.qrcodescanner.QrCodeActivity;
import com.example.sharan.iotsmartchain.qrcodescanner.camera.CameraManager;
import com.google.zxing.Result;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 */
public final class CaptureActivityHandler extends Handler {
    private static final String TAG = CaptureActivityHandler.class.getName();

    private final QrCodeActivity mActivity;
    private final DecodeThread mDecodeThread;
    private State mState;

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

    public CaptureActivityHandler(QrCodeActivity activity) {
        this.mActivity = activity;
        mDecodeThread = new DecodeThread(activity);
        mDecodeThread.start();
        mState = State.SUCCESS;
        // Start ourselves capturing previews and decoding.
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {

        if(message.what == R.id.auto_focus)
        {
               Log.d(TAG, "Got auto-focus message");
                // When one auto focus pass finishes, start another. This is the closest thing to
                // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
            try{
                if (mState == State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }else if(message.what ==R.id.decode_succeeded)
        {
            Log.e(TAG, "Got decode succeeded message");
            try{
                mState = State.SUCCESS;
                mActivity.handleDecode((Result) message.obj);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }else if(message.what == R.id.decode_failed)
        {
            Log.e(TAG, "We're decoding as fast as possible, so when one decode fails, start another.");
            // We're decoding as fast as possible, so when one decode fails, start another.

            try{
                mState = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), R.id.decode);
               // mActivity.handleDecode((Result) message.obj);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }


    public void quitSynchronously() {
        mState = State.DONE;
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(mDecodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            mDecodeThread.join();
        } catch (InterruptedException e) {
            // continue
            e.printStackTrace();
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    public void restartPreviewAndDecode() {
        if (mState != State.PREVIEW) {
            CameraManager.get().startPreview();
            mState = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), R.id.decode);
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
        }
    }

}
