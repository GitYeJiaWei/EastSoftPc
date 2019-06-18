package com.ioter.eastsoft.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ioter.eastsoft.AppApplication;
import com.ioter.eastsoft.R;
import com.ioter.eastsoft.common.util.ACache;
import com.ioter.eastsoft.common.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends NewBaseActivity implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.seekBar1)
    SeekBar seekBar1;
    @BindView(R.id.tvShow1)
    TextView tvShow1;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.seekBar2)
    SeekBar seekBar2;
    @BindView(R.id.tvShow2)
    TextView tvShow2;
    @BindView(R.id.tv3)
    TextView tv3;
    @BindView(R.id.seekBar3)
    SeekBar seekBar3;
    @BindView(R.id.tvShow3)
    TextView tvShow3;
    @BindView(R.id.tv4)
    TextView tv4;
    @BindView(R.id.seekBar4)
    SeekBar seekBar4;
    @BindView(R.id.tvShow4)
    TextView tvShow4;
    @BindView(R.id.bt_sure)
    Button btSure;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.tv5)
    TextView tv5;
    @BindView(R.id.seekBar5)
    SeekBar seekBar5;
    @BindView(R.id.tvShow5)
    TextView tvShow5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        setTitle("天线设置");
        initview();
    }

    private void initview() {
        String key1 = ACache.get(AppApplication.getApplication()).getAsString("key1");
        if (TextUtils.isEmpty(key1)) {
            key1 = "15";
        }
        tvShow1.setText(key1);
        seekBar1.setProgress(Integer.valueOf(key1) - 5);

        String key2 = ACache.get(AppApplication.getApplication()).getAsString("key2");
        if (TextUtils.isEmpty(key2)) {
            key2 = "15";
        }
        tvShow2.setText(key2);
        seekBar2.setProgress(Integer.valueOf(key2) - 5);

        String key3 = ACache.get(AppApplication.getApplication()).getAsString("key3");
        if (TextUtils.isEmpty(key3)) {
            key3 = "15";
        }
        tvShow3.setText(key3);
        seekBar3.setProgress(Integer.valueOf(key3) - 5);

        String key4 = ACache.get(AppApplication.getApplication()).getAsString("key4");
        if (TextUtils.isEmpty(key4)) {
            key4 = "15";
        }
        tvShow4.setText(key4);
        seekBar4.setProgress(Integer.valueOf(key4) - 5);

        String key5 = ACache.get(AppApplication.getApplication()).getAsString("key5");
        if (TextUtils.isEmpty(key5)) {
            key5 = "4";
        }
        tvShow5.setText(key5);
        seekBar5.setProgress(Integer.valueOf(key5));

        seekBar2.setOnSeekBarChangeListener(this);
        seekBar1.setOnSeekBarChangeListener(this);
        seekBar3.setOnSeekBarChangeListener(this);
        seekBar4.setOnSeekBarChangeListener(this);
        seekBar5.setOnSeekBarChangeListener(this);
    }

    @OnClick({R.id.bt_sure, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_sure:
                String key1 = tvShow1.getText().toString();
                ACache.get(AppApplication.getApplication()).put("key1", key1);
                String key2 = tvShow2.getText().toString();
                ACache.get(AppApplication.getApplication()).put("key2", key2);
                String key3 = tvShow3.getText().toString();
                ACache.get(AppApplication.getApplication()).put("key3", key3);
                String key4 = tvShow4.getText().toString();
                ACache.get(AppApplication.getApplication()).put("key4", key4);
                String key5 = tvShow5.getText().toString();
                ACache.get(AppApplication.getApplication()).put("key5", key5);
                ToastUtil.toast("设置成功");
                finish();
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    //拖动中
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekBar1:
                tvShow1.setText(progress + 5 + "");
                break;
            case R.id.seekBar2:
                tvShow2.setText(progress + 5 + "");
                break;
            case R.id.seekBar3:
                tvShow3.setText(progress + 5 + "");
                break;
            case R.id.seekBar4:
                tvShow4.setText(progress + 5 + "");
                break;
            case R.id.seekBar5:
                tvShow5.setText(progress+"");
                break;
        }
    }

    //开始拖动
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    //停止拖动
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
