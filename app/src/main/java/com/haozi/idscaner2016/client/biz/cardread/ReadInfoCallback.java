package com.haozi.idscaner2016.client.biz.cardread;

import com.routon.idr.idrinterface.readcard.ReadType;

/**
 * Created by Haozi on 2016/4/24.
 */
public interface ReadInfoCallback {
    public void updateTextCardInfo(boolean flag,ReadType rdType);
    public void updateTextStatus(String statusStr);
}
