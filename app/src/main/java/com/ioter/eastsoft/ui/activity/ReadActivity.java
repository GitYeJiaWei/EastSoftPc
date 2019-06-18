package com.ioter.eastsoft.ui.activity;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.ioter.eastsoft.AppApplication;
import com.ioter.eastsoft.R;
import com.ioter.eastsoft.bean.BaseEpc;
import com.ioter.eastsoft.bean.EpcBean;
import com.ioter.eastsoft.data.greendao.EpcModel;
import com.ioter.eastsoft.data.greendao.GreenDaoManager;
import com.ioter.eastsoft.data.greendao.dao.EpcModelDao;
import com.ioter.eastsoft.ui.adapter.CheckAdapter;

import org.greenrobot.greendao.query.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 读取腕带信息
 */
public class ReadActivity extends NewBaseActivity {

    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.tv_list)
    ListView tvList;
    private ArrayList<EpcBean> epcList = new ArrayList<>();
    private ConcurrentHashMap<String,String> map = new ConcurrentHashMap<>();
    private CheckAdapter checkAdapter;
    private ToneGenerator toneGenerator;
    private boolean isport =false;
    private boolean been =false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        ButterKnife.bind(this);

        init();
        reHelp();
        checkAdapter = new CheckAdapter(this,"warn");
        tvList.setAdapter(checkAdapter);
        toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);
    }


    //获取EPC群读数据
    @Override
    public void handleUi(BaseEpc baseEpc) {
        super.handleUi(baseEpc);
        String card = baseEpc._EPC;

        if (TextUtils.isEmpty(card)){
            return;
        }
        if (map.containsKey(card)){
            if (!isport){
                initSound();
            }
        }else {
            Query<EpcModel> nQuery = getUserDao().queryBuilder()
                    .where(EpcModelDao.Properties.Card.eq(card))//.where(UserDao.Properties.Id.notEq(999))
                    .build();
            List<EpcModel> users = nQuery.list();
            Log.i("tagGreen", "当前数量：" + users.size());
            if (users.size()>0){
                EpcBean epcModel = new EpcBean();
                epcModel.setCard(users.get(0).getCard());
                epcModel.setName(users.get(0).getName());
                epcModel.setTime(System.currentTimeMillis());

                epcList.add(epcModel);
            }else {
                return;
            }
            if (!isport){
                initSound();
            }

            map.put(card,card);
            checkAdapter.updateDatas(epcList);
        }
    }

    //配置读写器参数
    protected  void initSound()
    {
        isport = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (been){
                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
                }
            }
        }).start();
        // 蜂鸣器发声
    }

    public void init() {
        setTitle("通道门管理");
    }

    @OnClick(R.id.btn_scan)
    public void onViewClicked() {
        //读标签
        if (btnScan.getText().toString().trim().equals("开始监听")) {
            isport = false;
            //开始读标签
            helper.startInventroy();
            btnScan.setText("停止");
            been =true;

        } else {
            //停止读标签和停止感应模块
            helper.stopInventroyAndGpio();
            btnScan.setText("开始监听");
            been =false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        been = false;
    }

    private EpcModelDao getUserDao() {
        return GreenDaoManager.getInstance().getSession().getEpcModelDao();
    }
}
