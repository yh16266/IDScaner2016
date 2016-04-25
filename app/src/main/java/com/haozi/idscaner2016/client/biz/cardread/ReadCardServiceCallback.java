package com.haozi.idscaner2016.client.biz.cardread;

import com.routon.idr.idrinterface.readcard.IReadCardService;

/**
 * Created by Haozi on 2016/4/24.
 */
public interface ReadCardServiceCallback {
    public void setReadCardService(IReadCardService service);
    public IReadCardService getReadCardService();
}
