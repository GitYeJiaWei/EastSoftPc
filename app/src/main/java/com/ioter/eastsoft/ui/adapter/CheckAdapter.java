package com.ioter.eastsoft.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ioter.eastsoft.R;
import com.ioter.eastsoft.bean.EpcBean;
import com.ioter.eastsoft.common.util.ToastUtil;
import com.ioter.eastsoft.data.greendao.EpcModel;
import com.ioter.eastsoft.data.greendao.GreenDaoManager;
import com.ioter.eastsoft.data.greendao.dao.EpcModelDao;

import org.greenrobot.greendao.query.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by YJW on 2018/4/11.
 */

public class CheckAdapter extends BaseAdapter {
    //定义需要包装的JSONArray对象
    public List<EpcBean> mymodelList = new ArrayList<>();
    private Context context = null;
    private String warn;
    //视图容器
    private LayoutInflater layoutInflater;

    public CheckAdapter(Context _context,String _warn) {
        this.context = _context;
        //创建视图容器并设置上下文
        this.layoutInflater = LayoutInflater.from(_context);
        warn = _warn;
    }

    public void updateDatas(List<EpcBean> datalist) {
        if (datalist == null) {
            return;
        } else {
            mymodelList.clear();
            mymodelList.addAll(datalist);
            notifyDataSetChanged();
        }

    }

    /**
     * 清空列表的所有数据
     */
    public void clearData() {
        mymodelList.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return this.mymodelList.size();
    }

    @Override
    public Object getItem(int position) {
        if (getCount() > 0) {
            return this.mymodelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ListItemView listItemView = null;
        if (convertView == null) {
            //获取list_item布局文件的视图
            convertView = layoutInflater.inflate(R.layout.check_item, null);
            //获取控件对象
            listItemView = new ListItemView();
            listItemView.position = convertView.findViewById(R.id.tv_posit);
            listItemView.card = convertView.findViewById(R.id.tv_Epc);
            listItemView.name = convertView.findViewById(R.id.tv_Name);
            listItemView.time = convertView.findViewById(R.id.tv_time);
            listItemView.del = convertView.findViewById(R.id.tv_del);
            if (warn.equals("warn")){
                listItemView.del.setVisibility(View.GONE);
            }
            if (warn.equals("save")){
                listItemView.time.setVisibility(View.GONE);
            }

            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (CheckAdapter.ListItemView) convertView.getTag();
        }

        final EpcBean m1 = (EpcBean) this.getItem(position);
        if (warn.equals("warn")){
            listItemView.position.setTextColor(Color.RED);
            listItemView.name.setTextColor(Color.RED);
            listItemView.card.setTextColor(Color.RED);
            listItemView.time.setTextColor(Color.RED );
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date(m1.getTime());
            listItemView.time.setText(simpleDateFormat.format(date));
        }
        listItemView.position.setText(position + 1 + "");
        listItemView.name.setText(m1.getName());
        listItemView.card.setText(m1.getCard());
        listItemView.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    AlertDialog.Builder dialog =new AlertDialog.Builder(context);
                    dialog.setTitle("提示：");
                    dialog.setMessage("是否删除 "+m1.getName());
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Query<EpcModel> nQuery = getUserDao().queryBuilder()
                                    .where(EpcModelDao.Properties.Card.eq(m1.getCard()))//.where(UserDao.Properties.Id.notEq(999))
                                    .build();
                            List<EpcModel> users = nQuery.list();
                            if (users.size() > 0) {
                                getUserDao().delete(users.get(0));
                                ToastUtil.toast("删除成功");
                                mymodelList.remove(position);
                                //如果运用remove(Object c)需要重写EpcModel对象的equals()
                                //方法和hashCode()方法:
                                notifyDataSetChanged();
                            } else {
                                ToastUtil.toast("删除失败");
                            }
                        }
                    });

                    dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
            }
        });
        return convertView;
    }

    private EpcModelDao getUserDao() {
        return GreenDaoManager.getInstance().getSession().getEpcModelDao();
    }

    /**
     * 使用一个类来保存Item中的元素
     * 自定义控件集合
     */
    public final class ListItemView {
        TextView position, card, name, del,time;
    }
}
