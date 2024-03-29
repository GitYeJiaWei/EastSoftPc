package com.ioter.eastsoft.common.rx.subscriber;

import android.content.Context;

import com.ioter.eastsoft.common.exception.BaseException;
import com.ioter.eastsoft.ui.BaseView;

import io.reactivex.disposables.Disposable;

public  abstract  class ProgressSubcriber<T> extends ErrorHandlerSubscriber<T>  {




    private BaseView mView;


    public ProgressSubcriber(Context context, BaseView view) {
        super(context);
        this.mView = view;

    }



    public boolean isShowProgress(){
        return true;
    }


    @Override
    public void onSubscribe(Disposable d) {
        if(isShowProgress()){
            mView.showLoading();
        }
    }

    @Override
    public void onComplete() {

            mView.dismissLoading();
    }

    @Override
    public void onError(Throwable e) {

        e.printStackTrace();

        BaseException baseException =  mErrorHandler.handleError(e);
        mView.showError(baseException.getDisplayMessage());

    }

}
