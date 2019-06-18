package com.ioter.eastsoft.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.ioter.eastsoft.AppApplication;
import com.ioter.eastsoft.R;
import com.ioter.eastsoft.bean.EpcBean;
import com.ioter.eastsoft.common.util.ACache;
import com.ioter.eastsoft.data.greendao.EpcModel;
import com.ioter.eastsoft.data.greendao.GreenDaoManager;
import com.ioter.eastsoft.data.greendao.dao.EpcModelDao;
import com.ioter.eastsoft.di.component.AppComponent;
import com.ioter.eastsoft.ui.adapter.BabyAdapter;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.OnClick;

public class BabyActivity extends BaseActivity {
    private static int PORT = 5600;
    @BindView(R.id.bt_add)
    Button btAdd;
    @BindView(R.id.tv_list)
    ListView tvList;
    @BindView(R.id.bt_listen)
    Button btListen;
    private String TAG = "Socket2";
    private DatagramSocket socket = null;
    private boolean isRead = false;
    private ArrayList<EpcBean> epcList = new ArrayList<>();
    volatile private ConcurrentHashMap<String, EpcBean> map = new ConcurrentHashMap<>();
    private BabyAdapter checkAdapter;
    private int CODE = 1;
    private boolean isport = false;
    private DatagramPacket packet;
    Object lock = new Object();  //定义任意对象作为锁对象
    private boolean islocked = false;   //定义一个boolean的变量
    private InetAddress serverAddress = null;
    private String key5 = "";

    @Override
    public int setLayout() {
        return R.layout.activity_baby;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        setTitle("婴儿防盗");
        key5 = ACache.get(AppApplication.getApplication()).getAsString("key5");
        if (TextUtils.isEmpty(key5)){
            key5 = "4";
        }
        querydata();
    }

    private void querydata() {
        epcList.clear();
        map.clear();
        checkAdapter = new BabyAdapter(this, "baby");
        tvList.setAdapter(checkAdapter);
        toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);
        List<EpcModel> users = getUserDao().loadAll();
        Log.i("tagGreen", "当前数量：" + users.size());
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getSex() != null) {
                EpcBean epcModel = new EpcBean();
                epcModel.setCard(users.get(i).getCard());
                epcModel.setName(users.get(i).getName());
                epcModel.setSex(users.get(i).getSex());
                epcModel.setDate(users.get(i).getDate());
                epcModel.setTime(System.currentTimeMillis());
                epcModel.setState("准备");
                epcList.add(epcModel);
                map.put(users.get(i).getCard(), epcModel);
            }
        }
        checkAdapter.updateDatas(epcList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    querydata();
                }
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.bt_add, R.id.bt_listen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_listen:
                if (btListen.getText().toString().equals("开始监听")) {
                    querydata();
                    isport = false;
                    isRead = true;
                    islocked = false;
                    btListen.setText("停止监听");
                    RunThreadTo runthread2 = new RunThreadTo();
                    AppApplication.getExecutorService().execute(runthread2);
                    RunThread runThread1 = new RunThread();
                    AppApplication.getExecutorService().execute(runThread1);

                } else {
                    isRead = false;
                    //querydata();
                    btListen.setText("开始监听");
                }
                break;
            case R.id.bt_add:
                Intent intent = new Intent(this, AddBabyActivity.class);
                startActivityForResult(intent, CODE);
                break;
        }
    }

    //配置读写器参数
    protected void Sound() {
        isport = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRead) {
                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
                }
            }
        }).start();
        // 蜂鸣器发声
    }

    class RunThreadTo implements Runnable {
        @Override
        public void run() {
            while (isRead) {
                synchronized (lock) {
                    if (islocked){
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Log.d(TAG, "run: 开始对比时间");
                        long currentTime = System.currentTimeMillis();
                        Iterator iterator = map.keySet().iterator();
                        while (iterator.hasNext()) {
                            Log.d(TAG, "");
                            String key = (String) iterator.next();
                            long lastTime = map.get(key).getTime();
                            Log.d(TAG, "lastTime:" + lastTime);
                            int time = Integer.valueOf(key5)*1000;
                            if (currentTime - lastTime > time) {
                                Log.d(TAG, "rfid:"+key+"异常状态：" + (currentTime - lastTime));
                                map.get(key).setState("离开检测区");
                                Message message = new Message();
                                message.what = 2;
                                getHandler.sendMessage(message);
                            } else {
                                Log.d(TAG, "rfid:"+key + "正常状态：" + (currentTime - lastTime));
                            }
                        }
                        islocked = true;
                        lock.notifyAll();
                    }
                }
            }
        }
    }

    class RunThread implements Runnable {
        @Override
        public void run() {
            while (isRead) {
                synchronized (lock) {
                    if (islocked){
                        try {
                            if (socket == null){
                                socket = new DatagramSocket(null);
                                socket.setReuseAddress(true);
                                socket.bind(new InetSocketAddress(PORT));
                                byte data[] = new byte[1024];
                                packet = new DatagramPacket(data, data.length);

                                try {
                                    serverAddress = InetAddress.getByName("192.168.66.25");
                                } catch (UnknownHostException e) {
                                    Log.d(TAG, "未找到服务器");
                                    e.printStackTrace();
                                }
                                //Inet4Address serverAddress = (Inet4Address) Inet4Address.getByName("192.168.1.32")
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "run: 开始获取扫描数据");
                        try {
                            //设置超时时间,2秒
                            socket.setSoTimeout(4000);
                            socket.receive(packet);
                            Log.d(TAG, "接收成功");
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                            islocked = false;
                            lock.notifyAll();
                            //socket.close();
                            continue;
                        }

                        byte[] bytes = packet.getData();
                        String data1 = getBufHexStr(bytes);
                        if (data1 != null && data1.length() > 0) {
                            Message message = new Message();
                            message.what = 1;
                            message.obj = data1;
                            handler.sendMessage(message);
                        } else {
                            Log.d(TAG, "run: 未读取到数据" + data1);
                        }

                        String str = "550000a5015a55";//设置要发送的报文
                        byte data[] = str.getBytes();//把字符串str字符串转换为字节数组
                        //创建一个DatagramPacket对象，用于发送数据。
                        //参数一：要发送的数据  参数二：数据的长度  参数三：服务端的网络地址  参数四：服务器端端口号
                        DatagramPacket packet = new DatagramPacket(data, data.length,serverAddress, PORT);
                        try {
                            socket.send(packet);//把数据发送到服务端。
                            Log.d(TAG, "发送成功");
                        } catch (IOException e) {
                            Log.d(TAG, "发送失败");
                            e.printStackTrace();
                        }

                        islocked = false;
                        lock.notifyAll();
                    }else {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    //将16进制的byte数组转换成字符串
    public static String getBufHexStr(byte[] raw) {
        String HEXES = "0123456789ABCDEF";
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    private Handler getHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isport) {
                Sound();
            }
            if (msg.what == 2) {
                epcList.clear();
                Iterator iterator1 = map.keySet().iterator();
                while (iterator1.hasNext()) {
                    String key1 = (String) iterator1.next();
                    EpcBean epcBean = map.get(key1);
                    epcList.add(epcBean);
                }
                checkAdapter.updateDatas(epcList);
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String data = msg.obj.toString().substring(0, 42);
                initdata(data);
            }
        }
    };

    private void initdata(String receivedStr) {
        String mac_id, rfid, str, rfid_state;
        str = "";
        if (receivedStr.substring(0, 2).equals("55") && receivedStr.substring((receivedStr.length() - 2), receivedStr.length()).equals("55"))//表示一帧完整数据
        {
            for (int n = 0; n < receivedStr.length() / 2; n++) {
                str = str + receivedStr.substring(n * 2, n * 2 + 2) + "-";
            }
            str = str.replace("56-56", "55").replace("56-57", "56");
            str = str.replace("-", "");
            int check = 0;
            //校验
            for (int ii = 0; ii < (str.length() / 2) - 2; ii++) {
                check = check + Integer.parseInt(str.substring(2 + ii * 2, 2 + ii * 2 + 2), 16);
            }
            if ((check % 256) == 0)//校验成功
            {
                if (str.length() >= 42 && str.substring(8, 8 + 2).equals("0D"))//表示是标签数据
                {
                    mac_id = new BigInteger(str.substring(2, 4 + 2), 16).toString();   //转成10进制

                    rfid = new BigInteger(str.substring(26, 8 + 26), 16).toString();   //转成10进制     258 从18位开始    257 从26位开始

                    rfid_state = str.substring(16, 2 + 16);

                    String a2 = "---" + str + "---" + mac_id + "---" + rfid + "---" + rfid_state;//数据报显示到接收区域
                    Log.d(TAG, a2);//3859284225
                    if (map.containsKey(rfid) && rfid_state.equals("00")) {
                        map.get(rfid).setTime(System.currentTimeMillis());
                        Log.d(TAG, "正常状态");
                        if (!map.get(rfid).getState().equals("正常")) {
                            map.get(rfid).setState("正常");
                            epcList.clear();
                            Iterator iterator = map.keySet().iterator();
                            while (iterator.hasNext()) {
                                String key = (String) iterator.next();
                                EpcBean epcBean = map.get(key);
                                epcList.add(epcBean);
                            }
                            checkAdapter.updateDatas(epcList);
                        }
                    } else if (map.containsKey(rfid) && rfid_state.equals("80")) {
                        map.get(rfid).setTime(System.currentTimeMillis());
                        Log.d(TAG, "剪断状态");
                        if (!map.get(rfid).getState().equals("剪断")) {
                            if (!isport) {
                                Sound();
                            }
                            map.get(rfid).setState("剪断");
                            epcList.clear();
                            Iterator iterator = map.keySet().iterator();
                            while (iterator.hasNext()) {
                                String key = (String) iterator.next();
                                EpcBean epcBean = map.get(key);
                                epcList.add(epcBean);
                            }
                            checkAdapter.updateDatas(epcList);
                        }
                    } else if (!map.containsKey(rfid)) {
                        Log.d(TAG, "未存在的婴儿腕带");
                    }
                }
            }
        }
    }

    private EpcModelDao getUserDao() {
        return GreenDaoManager.getInstance().getSession().getEpcModelDao();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRead) {
            isRead = false;
        }
        //关闭线程池
        //executorService.shutdown();
    }


}
