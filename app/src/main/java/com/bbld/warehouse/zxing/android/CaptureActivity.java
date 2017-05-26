package com.bbld.warehouse.zxing.android;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bbld.warehouse.R;
import com.bbld.warehouse.activity.OrderDeliveryActivity;
import com.bbld.warehouse.bean.CartSQLBean;
import com.bbld.warehouse.bean.ScanCode;
import com.bbld.warehouse.db.UserDataBaseOperate;
import com.bbld.warehouse.db.UserSQLiteOpenHelper;
import com.bbld.warehouse.network.RetrofitService;
import com.bbld.warehouse.utils.MyToken;
import com.bbld.warehouse.zxing.camera.CameraManager;
import com.bbld.warehouse.zxing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.wuxiaolong.androidutils.library.ActivityManagerUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * 这个activity打开相机，在后台线程做常规的扫描；它绘制了一个结果view来帮助正确地显示条形码，在扫描的时候显示反馈信息，
 * 然后在扫描成功的时候覆盖扫描结果
 */
public final class CaptureActivity extends Activity implements
        SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    // 相机控制
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private IntentSource source;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;
    // 电量控制
    private InactivityTimer inactivityTimer;
    // 声音、震动控制
    private BeepManager beepManager;

    private ImageButton imageButton_back;
    private TextView text;
    private String productId;
    private String productName;
    private ListView lvScan;
    private String invoiceid;
    private UserSQLiteOpenHelper mUserSQLiteOpenHelper;
    private UserDataBaseOperate mUserDataBaseOperate;
    private Button btnComplete;
    private TextView tv_needCount;
    private TextView tv_scanCount;
    private String needCount;
    private int scanCount;

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    /**
     * OnCreate中初始化一些辅助类，如InactivityTimer（休眠）、Beep（声音）以及AmbientLight（闪光灯）
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // 保持Activity处于唤醒状态
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.capture);
//        text=(TextView)findViewById(R.id.text);
        hasSurface = false;

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        imageButton_back = (ImageButton) findViewById(R.id.capture_imageview_back);
        imageButton_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mUserSQLiteOpenHelper = UserSQLiteOpenHelper.getInstance(CaptureActivity.this);
        mUserDataBaseOperate = new UserDataBaseOperate(mUserSQLiteOpenHelper.getWritableDatabase());

        Intent intent=getIntent();
        productId=intent.getExtras().getString("productId");
        productName=intent.getExtras().getString("productName");
        invoiceid=intent.getExtras().getString("orderId");
        needCount=intent.getExtras().getString("needCount");
        TextView tvProductName = (TextView) findViewById(R.id.tv_productName);
        tvProductName.setText(productName+"");
        lvScan=(ListView)findViewById(R.id.lv_scan);
        List<CartSQLBean> products=mUserDataBaseOperate.findUserById(productId+"");
        Collections.reverse(products);
        lvScan.setAdapter(new ScanAdapter(products));
        btnComplete=(Button)findViewById(R.id.btn_complete);
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_needCount=(TextView)findViewById(R.id.tv_needCount);
        tv_needCount.setText(needCount+"(盒)");
        tv_scanCount=(TextView)findViewById(R.id.tv_scanCount);
        scanCount=0;
        for (int i=0;i<products.size();i++){
            scanCount=scanCount+products.get(i).getProCount();
        }
        tv_scanCount.setText(scanCount+"(盒)");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // CameraManager必须在这里初始化，而不是在onCreate()中。
        // 这是必须的，因为当我们第一次进入时需要显示帮助页，我们并不想打开Camera,测量屏幕大小
        // 当扫描框的尺寸不正确时会出现bug
        cameraManager = new CameraManager(getApplication());

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);

        handler = null;

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // activity在paused时但不会stopped,因此surface仍旧存在；
            // surfaceCreated()不会调用，因此在这里初始化camera
            initCamera(surfaceHolder);
        } else {
            // 重置callback，等待surfaceCreated()来初始化camera
            surfaceHolder.addCallback(this);
        }

        beepManager.updatePrefs();
        inactivityTimer.onResume();

        source = IntentSource.NONE;
        decodeFormats = null;
        characterSet = null;
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    /**
     * 扫描成功，处理反馈信息
     *
     * @param rawResult
     * @param barcode
     * @param scaleFactor
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();

        boolean fromLiveScan = barcode != null;
        //这里处理解码完成后的结果，此处将参数回传到Activity处理
        if (fromLiveScan) {
            beepManager.playBeepSoundAndVibrate();

//            Toast.makeText(this, "扫描成功", Toast.LENGTH_SHORT).show();
//            text.setText(rawResult.getText()+""+barcode);
//            Intent intent = getIntent();
//            intent.putExtra("codedContent", rawResult.getText());
//            intent.putExtra("codedBitmap", barcode);
//            setResult(RESULT_OK, intent);
//            finish();
            //连续扫码
//            continuePreview();

            getScanCode(rawResult.getText());
//            Toast.makeText(CaptureActivity.this,""+rawResult.getText(),Toast.LENGTH_SHORT).show();
        }

    }

    private void getScanCode(final String code) {
//        Toast.makeText(CaptureActivity.this,""+code,Toast.LENGTH_SHORT).show();
        Call<ScanCode> call= RetrofitService.getInstance().scanCode(new MyToken(CaptureActivity.this).getToken()+"",
                Integer.parseInt(invoiceid),Integer.parseInt(productId),code);
        call.enqueue(new Callback<ScanCode>() {
            @Override
            public void onResponse(Response<ScanCode> response, Retrofit retrofit) {
                if (response.body()==null){
                    Toast.makeText(CaptureActivity.this,"服务器错误",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.body().getStatus()==0){
                    if (response.body().getInfo().getIsRight()==1){
                        //IsRight--条码是否正确，优先进行判断，如果是 0，则代表条码不存在，
                        //如果是 1，则代表条码存在
                        //成功，添加到数据库（productId,code,type,count）type=1=箱码;type=2=盒码
                        List<CartSQLBean> sqlCode =mUserDataBaseOperate.findUserByName(code+"");
                        if (sqlCode.isEmpty()){
                            CartSQLBean sqlBean=new CartSQLBean();
                            sqlBean.setProductId(productId+"");
                            sqlBean.setProductCode(code+"");
                            sqlBean.setProductType(response.body().getInfo().getType()+"");
                            sqlBean.setProCount(response.body().getInfo().getCount());
                            mUserDataBaseOperate.insertToUser(sqlBean);
                            List<CartSQLBean> products=mUserDataBaseOperate.findUserById(productId+"");
                            Collections.reverse(products);
                            lvScan.setAdapter(new ScanAdapter(products));
                            scanCount=0;
                            for (int i=0;i<products.size();i++){
                                scanCount=scanCount+products.get(i).getProCount();
                            }
                            tv_scanCount.setText(scanCount+"(盒)");
                            try {
                                Thread.sleep(1000);
                                continuePreview();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
//                            showScanDialog();
                        }else{
//                            Toast.makeText(CaptureActivity.this,"该商品码已经扫过",Toast.LENGTH_SHORT).show();
                            showFailDialog("该商品码已经扫过");
                            try {
                                Thread.sleep(1000);
                                continuePreview();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        //失败
//                        Toast.makeText(CaptureActivity.this,"不存在该商品,请重试",Toast.LENGTH_SHORT).show();
                        showFailDialog("不存在该商品,请重试");
                        try {
                            Thread.sleep(1000);
                            continuePreview();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(CaptureActivity.this,""+throwable,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFailDialog(String failMessage) {
        AlertDialog.Builder builder=new AlertDialog.Builder(CaptureActivity.this);
        builder.setMessage(failMessage+"");
//        builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                finish();
//            }
//        });
        builder.setNegativeButton("继续", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //连续扫码
                continuePreview();
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void showScanDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(CaptureActivity.this);
        builder.setMessage("扫描成功");
        builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("继续", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //连续扫码
                continuePreview();
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }


    /**
     * 扫码下部分的listview的adapter
     */
    class ScanAdapter extends BaseAdapter{
        private List<CartSQLBean> products;
        public ScanAdapter(List<CartSQLBean> products){
            super();
            this.products=products;
        }
        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public CartSQLBean getItem(int i) {
            return products.get(i);
        }

        @Override
        public long getItemId(int i) {
            return Long.parseLong(products.get(i).getProductId());
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ScanHolder holder=null;
            if (view==null){
                view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_lv_capture,null);
                holder=new ScanHolder();
                holder.tv_code=(TextView)view.findViewById(R.id.tv_code);
                holder.tv_type=(TextView)view.findViewById(R.id.tv_type);
                holder.tv_count=(TextView)view.findViewById(R.id.tv_count);
                view.setTag(holder);
            }
            holder= (ScanHolder) view.getTag();
            CartSQLBean product = getItem(i);
            holder.tv_code.setText(product.getProductCode()+"");
            holder.tv_type.setText(getType(product.getProductType())+"");
            holder.tv_count.setText(product.getProCount()+"(盒)");
            return view;
        }

        private String getType(String productType) {
            //type=1=箱码;type=2=盒码
            if (productType.equals("1")){
                return "箱码";
            }else{
                return "盒码";
            }
        }

        class ScanHolder{
            TextView tv_code;
            TextView tv_type;
            TextView tv_count;
        }
    }
    /**
     * 使Zxing能够继续扫描
     */
    public void continuePreview() {
        if (handler != null) {
            handler.restartPreviewAndDecode();
        }
    }
    /**
     * 初始化Camera
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            // 打开Camera硬件设备
            cameraManager.openDriver(surfaceHolder);
            // 创建一个handler来打开预览，并抛出一个运行时异常
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats,
                        decodeHints, characterSet, cameraManager);
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    /**
     * 显示底层错误信息并退出应用
     */
    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

}