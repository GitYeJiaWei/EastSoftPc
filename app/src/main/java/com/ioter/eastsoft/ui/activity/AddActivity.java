package com.ioter.eastsoft.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ioter.eastsoft.R;
import com.ioter.eastsoft.bean.BaseEpc;
import com.ioter.eastsoft.common.util.ToastUtil;
import com.ioter.eastsoft.data.greendao.EpcModel;
import com.ioter.eastsoft.data.greendao.GreenDaoManager;
import com.ioter.eastsoft.data.greendao.dao.EpcModelDao;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddActivity extends NewBaseActivity {

    @BindView(R.id.btn_sure)
    Button btnSure;
    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.tv_Epc)
    EditText tvEpc;
    @BindView(R.id.tv_Name)
    EditText tvName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);

        init();
        reHelp();
    }


    public void init() {
        setTitle("添加信息");
        //initdate();
    }

    private void initdate(){
        List<EpcModel> list = new ArrayList<>();
        EpcModel insertData1 = new EpcModel(null, "8888201905270100", "UHF RFID",null,null);
        EpcModel insertData2 = new EpcModel(null, "8888201905270101", "宣传手册",null,null);
        EpcModel insertData3 = new EpcModel(null, "8888201905270102", "电源线1",null,null);
        EpcModel insertData4 = new EpcModel(null, "8888201905270103", "电源线2",null,null);
        EpcModel insertData5 = new EpcModel(null, "8888201905270104", "电源线3",null,null);
        EpcModel insertData6 = new EpcModel(null, "8888201905270105", "RFID工作站",null,null);
        EpcModel insertData7 = new EpcModel(null, "8888201905270106", "4通道读写器",null,null);
        EpcModel insertData8 = new EpcModel(null, "8888201905270107", "设备零件1",null,null);
        EpcModel insertData9 = new EpcModel(null, "8888201905270108", "设备零件",null,null);
        EpcModel insertData10 = new EpcModel(null, "8888201905270109", "M6E读写器1",null,null);
        EpcModel insertData11 = new EpcModel(null, "8888201905270110", "M6E读写器2",null,null);
        EpcModel insertData12 = new EpcModel(null, "E20000169404018117706281", "小白",null,null);
        EpcModel insertData13 = new EpcModel(null, "E2000016940401821770627A", "小黄",null,null);
        EpcModel insertData14 = new EpcModel(null, "E200001693170280160076F2", "小艾",null,null);

        list.add(insertData1);
        list.add(insertData2);
        list.add(insertData3);
        list.add(insertData4);
        list.add(insertData5);
        list.add(insertData6);
        list.add(insertData7);
        list.add(insertData8);
        list.add(insertData9);
        list.add(insertData10);
        list.add(insertData11);
        list.add(insertData12);
        list.add(insertData13);
        list.add(insertData14);
        saveNLists(list);
    }

    //获取EPC群读数据
    @Override
    public void handleUi(BaseEpc baseEpc) {
        super.handleUi(baseEpc);

        String card = baseEpc._EPC;
        tvEpc.setText(card);

        if (TextUtils.isEmpty(card)) {
            return;
        }
        Query<EpcModel> nQuery = getUserDao().queryBuilder()
                .where(EpcModelDao.Properties.Card.eq(card))//.where(UserDao.Properties.Id.notEq(999))
                .build();
        List<EpcModel> users = nQuery.list();
        Log.i("tagGreen", "当前数量：" + users.size());
        if (users.size() > 0) {
            ToastUtil.toast("该腕带编号已存在");
        }
    }

    @OnClick({R.id.btn_sure, R.id.btn_scan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_sure:
                //停止读标签和停止感应模块
                helper.stopInventroyAndGpio();
                btnScan.setText("扫描");
                String card = tvEpc.getText().toString();
                String name = tvName.getText().toString();

                if (TextUtils.isEmpty(card) || TextUtils.isEmpty(name)) {
                    ToastUtil.toast("请输入正确数据");
                    return;
                }
                //插入数据
                EpcModel insertData = new EpcModel(null, card, name,null,null);
                getUserDao().insert(insertData);
                ToastUtil.toast("添加成功");
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.btn_scan:
                //读标签
                if (btnScan.getText().toString().trim().equals("扫描")) {
                    //开始读标签
                    helper.startInventroy();
                    btnScan.setText("停止");
                } else {
                    //停止读标签和停止感应模块
                    helper.stopInventroyAndGpio();
                    btnScan.setText("扫描");
                }
                break;
        }
    }

    private EpcModelDao getUserDao() {
        return GreenDaoManager.getInstance().getSession().getEpcModelDao();
    }


}
