package com.ioter.eastsoft.common.rx.subscriber;

import android.content.Context;
import android.util.Log;

import com.ioter.eastsoft.common.exception.ApiException;
import com.ioter.eastsoft.common.exception.BaseException;
import com.ioter.eastsoft.common.rx.RxErrorHandler;
import com.ioter.eastsoft.common.util.ToastUtil;


public abstract class ErrorHandlerSubscriber<T> extends DefualtSubscriber<T>
{


    protected RxErrorHandler mErrorHandler = null;

    protected Context mContext;

    public ErrorHandlerSubscriber(Context context)
    {

        this.mContext = context;
        mErrorHandler = new RxErrorHandler(mContext);
    }

    @Override
    public void onError(Throwable e)
    {
        BaseException baseException =  mErrorHandler.handleError(e);
        if(e instanceof ApiException)
        {
            ToastUtil.toast(((ApiException) e).getDisplayMessage());
            return;
        }

        if(baseException==null){
            e.printStackTrace();
            Log.d("ErrorHandlerSubscriber",e.getMessage());
        }
        else {

            mErrorHandler.showErrorMessage(baseException);
        }

    }


}
