package com.ioter.eastsoft.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.ioter.eastsoft.R;
import com.ioter.eastsoft.di.component.AppComponent;

/**
 *
 */
public class MainActivity extends BaseActivity
{

    @Override
    public int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
    }

    @Override
    public void init() {
        setTitle("首页");
        initView();
    }

    private void initView(){
        //初始化二维码扫描头
        if (Build.VERSION.SDK_INT > 21) {

            //扫条码 需要相机对应用开启相机和存储权限；
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            } else {
                //说明已经获取到摄像头权限了 想干嘛干嘛
            }
            //读写内存权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 请求权限
                ActivityCompat
                        .requestPermissions(
                                this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                2);
            }

        } else {
            //这个说明系统版本在6.0之下，不需要动态获取权限。
        }
    }

    public void tv_read(View view){
        startActivity(new Intent(this,ReadActivity.class));
    }

    public void tv_find(View view){
        startActivity(new Intent(this,SettingActivity.class));
    }

    public void tv_set(View view){
        Intent intent2 = new Intent(this, MessageActivity.class);
        startActivity(intent2);
    }
    public void tv_baby(View view){
        Intent intent2 = new Intent(this, BabyActivity.class);
        startActivity(intent2);
    }
}

