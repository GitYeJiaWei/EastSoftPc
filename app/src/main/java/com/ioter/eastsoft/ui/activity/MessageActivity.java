package com.ioter.eastsoft.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import com.ioter.eastsoft.R;
import com.ioter.eastsoft.bean.EpcBean;
import com.ioter.eastsoft.data.greendao.EpcModel;
import com.ioter.eastsoft.data.greendao.GreenDaoManager;
import com.ioter.eastsoft.data.greendao.dao.EpcModelDao;
import com.ioter.eastsoft.di.component.AppComponent;
import com.ioter.eastsoft.ui.adapter.CheckAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MessageActivity extends BaseActivity {

    @BindView(R.id.tv_list)
    ListView tvList;
    @BindView(R.id.bt_add)
    Button btAdd;
    private ArrayList<EpcBean> epcList = new ArrayList<>();
    private CheckAdapter checkAdapter;
    private int CODE = 1;

    @Override
    public int setLayout() {
        return R.layout.activity_message;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        setTitle("信息修改");
        checkAdapter = new CheckAdapter(this, "save");
        tvList.setAdapter(checkAdapter);
        querydata();
    }

    private void querydata() {
        epcList.clear();
        //查询数据详细
        List<EpcModel> users = getUserDao().loadAll();
        Log.i("tagGreen", "当前数量：" + users.size());
        for (int i = 0; i < users.size(); i++) {
            EpcBean epcModel = new EpcBean();
            epcModel.setCard(users.get(i).getCard());
            epcModel.setName(users.get(i).getName());

            epcList.add(epcModel);
        }

        checkAdapter.updateDatas(epcList);
    }

    private EpcModelDao getUserDao() {
        return GreenDaoManager.getInstance().getSession().getEpcModelDao();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK){
                    querydata();
                }
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.bt_add)
    public void onViewClicked() {
        Intent intent = new Intent(this, AddActivity.class);
        startActivityForResult(intent, CODE);
    }
}

