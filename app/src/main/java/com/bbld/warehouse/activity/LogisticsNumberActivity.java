package com.bbld.warehouse.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bbld.warehouse.R;
import com.bbld.warehouse.base.BaseActivity;
import com.bbld.warehouse.bean.AddOrderLogisticsInfo;
import com.bbld.warehouse.bean.GetLogisticsList;
import com.bbld.warehouse.bean.GetLogisticsTrackInfo;
import com.bbld.warehouse.bean.GetOrderLogisticsInfo;
import com.bbld.warehouse.network.RetrofitService;
import com.bbld.warehouse.utils.MyToken;

import java.util.List;

import butterknife.BindView;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by dell on 2017/5/24.
 */

public class LogisticsNumberActivity extends BaseActivity {
    @BindView(R.id.tv_channelname)
    TextView tvChannelName;
    @BindView(R.id.tv_dealername)
    TextView tvDealerName;
    @BindView(R.id.tv_orderid)
    TextView tvOrderID;
    @BindView(R.id.lv_add_logistics)
    ListView lvAddLogistics;
    @BindView(R.id.tv_data)
    TextView tvData;
    @BindView(R.id.tv_ordercount)
    TextView tvOrderCount;
    @BindView(R.id.tv_productcount)
    TextView tvProductCount;
    @BindView(R.id.ed_logisticsid)
    EditText edLogisticsID;
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.sp_logistics)
    Spinner spLogistics;
    private  int  invoiceid;
    private int logisticsId;
    private String number;
    private  String OrderID;
    private  String ChannelName;
    private  String DealerName;
    private  String Count;
    private  String ProductCount;
    private  String Date;
    private GetLogisticsListAdapter getLogisticsListAdapter;
    private List<GetLogisticsList.GetLogisticsListList> getLogisticsList;
    private List<GetOrderLogisticsInfo.GetOrderLogisticsInfoList> logisticsInfoList;
    private GetOrderLogisticsInfoAdapter getOrderLogisticsInfoAdapter;
    @Override
    protected void initViewsAndEvents() {
        tvOrderID.setText("订单号"+OrderID);
        tvChannelName.setText(ChannelName);
        tvData.setText(Date);
        tvDealerName.setText(DealerName);
        tvProductCount.setText(ProductCount);
        tvOrderCount.setText(Count);
        loadData();
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        OrderID= extras.getString("OrderID()");
        ChannelName=  extras.getString("ChannelName()");
        DealerName=   extras.getString("DealerName()");
        Count=  extras.getString("Count()");
        ProductCount= extras.getString("ProductCount()");
        Date=  extras.getString("Date()");
        invoiceid = Integer.parseInt(extras.getString("OrderID()"));
    }
    private void loadData() {


        /*
  物流信息查询接口
 */
        Call<GetOrderLogisticsInfo> call1 = RetrofitService.getInstance().getOrderLogisticsInfo(new MyToken(LogisticsNumberActivity.this).getToken() + "",  invoiceid);
        call1.enqueue(new Callback<GetOrderLogisticsInfo>() {
            @Override
            public void onResponse(Response<GetOrderLogisticsInfo> response, Retrofit retrofit) {
                if (response.body() == null) {
                    showToast("服务器出错");
                    return;
                }
                if (response.body().getStatus() == 0) {
                    logisticsInfoList=response.body().getList();
                    setAdapter2();
                }

            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });
  /*
    获取物流公司字典接口
   */
        Call<GetLogisticsList> call2 = RetrofitService.getInstance().getLogisticsList();
        call2.enqueue(new Callback<GetLogisticsList>() {
            @Override
            public void onResponse(Response<GetLogisticsList> response, Retrofit retrofit) {
                if (response.body() == null) {
                    showToast("服务器出错");
                    return;
                }
                if (response.body().getStatus() == 0) {
                    getLogisticsList = response.body().getList();

                    setAdapter1();
                }

            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });


    }
private void setData(){
     /*
     * 增加物流信息接口
     */
    Call<AddOrderLogisticsInfo> call3 = RetrofitService.getInstance().addOrderLogisticsInfo(new MyToken(LogisticsNumberActivity.this).getToken() + "",invoiceid,logisticsId,number);
    call3.enqueue(new Callback<AddOrderLogisticsInfo>() {
        @Override
        public void onResponse(Response<AddOrderLogisticsInfo> response, Retrofit retrofit) {
            if (response.body() == null) {
                showToast("服务器出错");
                return;
            }
            if (response.body().getStatus() == 0) {
                showToast(response.body().getMes()+"");


            }

        }

        @Override
        public void onFailure(Throwable throwable) {
        }
    });
}
    private void setAdapter1() {
        getLogisticsListAdapter = new LogisticsNumberActivity.GetLogisticsListAdapter();
        spLogistics.setAdapter(getLogisticsListAdapter);

    }
    private void setAdapter2() {
        getOrderLogisticsInfoAdapter = new GetOrderLogisticsInfoAdapter();
        lvAddLogistics.setAdapter(getOrderLogisticsInfoAdapter);
    }
    class GetLogisticsListAdapter extends BaseAdapter {
       GetLogisticsListHolder holder = null;

        @Override
        public int getCount() {
            return getLogisticsList.size();
        }

        @Override
        public GetLogisticsList.GetLogisticsListList getItem(int i) {
            return getLogisticsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_sp_logistics_tracking, null);
                holder=new  GetLogisticsListHolder();
                holder.tvInfo = (TextView) view.findViewById(R.id.tv_info);
                view.setTag(holder);
            }
            holder = (GetLogisticsListHolder) view.getTag();
            final GetLogisticsList.GetLogisticsListList list = getItem(i);
            holder.tvInfo.setText(list.getLogisticsName()+ "");

            return view;
        }

        class GetLogisticsListHolder {
            TextView tvInfo;
        }
    }
    class GetOrderLogisticsInfoAdapter extends BaseAdapter {
        GetOrderLogisticsInfoHolder holder = null;

        @Override
        public int getCount() {
            return logisticsInfoList.size();
        }

        @Override
        public GetOrderLogisticsInfo.GetOrderLogisticsInfoList getItem(int i) {
            return logisticsInfoList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_logistics_number, null);
                holder = new GetOrderLogisticsInfoHolder();
                holder.tvLogisticsNmae = (TextView) view.findViewById(R.id.tv_logisticsname);
                holder.tvLogisticsNumber= (TextView) view.findViewById(R.id.tv_logisticsnumber);
                view.setTag(holder);
            }
            holder = (GetOrderLogisticsInfoHolder) view.getTag();
            final GetOrderLogisticsInfo.GetOrderLogisticsInfoList list = getItem(i);
            holder.tvLogisticsNmae.setText(list.getLogisticsName()+"");

            holder.tvLogisticsNumber.setText(list.getLogisticsNumber()+"");
            return view;
        }
        class GetOrderLogisticsInfoHolder {

            TextView tvLogisticsNmae;
            TextView tvLogisticsNumber;
        }
    }
    @Override
    public int getContentView() {
        return R.layout.activity_logistics_number;
    }
}
