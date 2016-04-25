package com.haozi.idscaner2016.client.biz.cardread;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.routon.idr.idrinterface.readcard.IReadCardService;

/**
 * Created by Haozi on 2016/4/24.
 */
public class ReadServiceConnection implements ServiceConnection {

    private final String TAG="ReadServiceConnection";

    private ReadCardServiceCallback mCallback;
    private Handler mHandler;

    public ReadServiceConnection(ReadCardServiceCallback mCallback, Handler mHandler) {
        this.mCallback = mCallback;
        this.mHandler = mHandler;
    }

    @Override
    public void onServiceConnected(ComponentName comp, IBinder binder) {
        Log.d(TAG, "ReadCardService Connected");
        mCallback.setReadCardService(IReadCardService.Stub.asInterface(binder));
        if(mCallback.getReadCardService()!=null){
            Message send_msg = new Message();
            send_msg.what = MainMsg.EVT_SERV_CONNECTED_RDCARD;
            if(mHandler!=null){
                mHandler.sendMessage(send_msg);
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName comp) {
        Log.d(TAG, "ReadCardService Disconnected");
        mCallback.setReadCardService(null);
        Message send_msg = new Message();
        send_msg.what = MainMsg.EVT_SERV_DISCONNECTED_RDCARD;
        if(mHandler!=null){
            mHandler.sendMessage(send_msg);
        }
    }
}
