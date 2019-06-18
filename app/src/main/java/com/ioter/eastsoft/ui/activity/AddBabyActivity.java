package com.ioter.eastsoft.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ioter.eastsoft.R;
import com.ioter.eastsoft.common.util.ToastUtil;
import com.ioter.eastsoft.data.greendao.EpcModel;
import com.ioter.eastsoft.data.greendao.GreenDaoManager;
import com.ioter.eastsoft.data.greendao.dao.EpcModelDao;
import com.ioter.eastsoft.di.component.AppComponent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.OnClick;

public class AddBabyActivity extends BaseActivity {

    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.tv_Epc)
    EditText tvEpc;
    @BindView(R.id.tv_Name)
    EditText tvName;
    @BindView(R.id.tv_Sex)
    EditText tvSex;
    @BindView(R.id.tv_Date)
    EditText tvDate;
    @BindView(R.id.btn_sure)
    Button btnSure;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private boolean isScan = false;

    static int wr_allCount = 0;
    static int rd_allCount = 0;
    static byte[] buffer = new byte[2000];

    String[] entries = null;
    String[] entryValues = null;

    @Override
    public int setLayout() {
        return R.layout.activity_add_baby;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        setTitle("添加信息");

    }

    class ScanThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (isScan){
                byte[] resp = AddBabyActivity.this.read();
                if(resp != null){
                    System.out.println(Bytes2HexString(resp, resp.length));
                    String tag = resolveData(resp);
                    if(tag != null){

                    }

                }
            }
        }
    }

    //Byte 数组转十六进制字符串
    public static String Bytes2HexString(byte[] b, int size) {
        String ret = "";
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    //解析返回的数据包
    private String resolveData(byte[] resp)
    {
        int length = resp.length;
        if(resp[3]==13)
        {
            byte[] tag = new byte[8];
            System.arraycopy(resp, 8, tag, 0, 8);
            String tagStr = Bytes2HexString(tag, 8);
            return tagStr;
        }
        else
        {
            return "";
        }
    }

    //从串口中读取完整的数据
    private byte[] read()
    {
        int count;
        int size;
        byte[] bytes = new byte[1500];
        int rec_num=0;
        int k=0;
        byte[] pout=new byte[64];

        try
        {
            while(true)
            {
                count = mInputStream.available();
                rec_num=0;
                if(count>0)
                {
                    size = mInputStream.available();
                    mInputStream.read(bytes);
                    if((wr_allCount+size)<=2000)
                    {
                        System.arraycopy(bytes, 0, buffer, wr_allCount, size);
                    }
                    else
                    {
                        System.arraycopy(bytes, 0, buffer, wr_allCount, 2000-wr_allCount);
                        System.arraycopy(bytes, 0, buffer, 0, size-(2000-wr_allCount));

                    }
                    wr_allCount=(wr_allCount+size)%2000;
                    k=0;
                    byte check=0;
                    byte pre_data=0;
                    while(wr_allCount!=((rd_allCount+k)%2000))
                    {
                        if(buffer[(rd_allCount+k)%2000]==0x55)
                        {
                            if((k>4)&& (check==0))
                            {
                                rd_allCount=(rd_allCount+k)%2000;
                                k=0;
                                return pout;
                            }
                            rec_num=0;
                            check=0;
                            pre_data=0;
                            rd_allCount=(rd_allCount+k)%2000;
                            k=0;
                        }
                        else if(buffer[(rd_allCount+k)%2000]==0x56)
                        {
                            if(pre_data==0x56)
                            {
                                pout[rec_num]=0x55;
                                check=(byte)(check+0x55);
                                rec_num=(rec_num+1)%1500;
                                pre_data=0x0;
                            }
                            else
                            {
                                pre_data=0x56;
                            }

                        }
                        else if(buffer[(rd_allCount+k)%2000]==0x57)
                        {
                            if(pre_data==0x56)
                            {
                                pout[rec_num]=0x56;
                                check=(byte)(check+0x56);
                                rec_num=(rec_num+1)%1500;
                                pre_data=0x0;
                            }
                            else
                            {
                                pout[rec_num]=0x57;
                                check=(byte)(check+0x57);
                                rec_num=(rec_num+1)%1500;
                                pre_data=0x0;
                            }

                        }
                        else
                        {
                            pout[rec_num]=buffer[(rd_allCount+k)%2000];
                            check=(byte)(check+buffer[(rd_allCount+k)%2000]);
                            rec_num=(rec_num+1)%1500;
                            pre_data=0x0;

                        }
                        k++;
                    }
                    Thread.sleep(10);
                    break;
                }
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }


    @OnClick({R.id.btn_scan, R.id.btn_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
                if (btnScan.getText().toString().equals("扫描")){
                    btnScan.setText("停止扫描");
                    isScan = true;
                    ScanThread thread = new ScanThread();
                    thread.start();
                }else {
                    btnScan.setText("扫描");
                    isScan = false;
                }
                break;
            case R.id.btn_sure:
                String card = tvEpc.getText().toString();
                String name = tvName.getText().toString();
                String sex = tvSex.getText().toString();
                String date = tvDate.getText().toString();

                if (TextUtils.isEmpty(card) || TextUtils.isEmpty(name) ||
                        TextUtils.isEmpty(sex) || TextUtils.isEmpty(date)) {
                    ToastUtil.toast("请输入正确数据");
                    return;
                }
                //插入数据
                EpcModel insertData = new EpcModel(null, card, name,sex,date);
                getUserDao().insert(insertData);
                ToastUtil.toast("添加成功");
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    private EpcModelDao getUserDao() {
        return GreenDaoManager.getInstance().getSession().getEpcModelDao();
    }

}
