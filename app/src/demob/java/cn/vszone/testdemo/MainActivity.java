package cn.vszone.testdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    ImageView imageView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnClick();
            }
        });
        imageView = (ImageView) findViewById(R.id.imageView);
//        Object a = null;
//        try{
//            ApplicationInfo e = this.getPackageManager().getApplicationInfo(this.getPackageName(), 128);
//            a.toString();
//        }catch (PackageManager.NameNotFoundException e){
//            e.printStackTrace();
//        }
        // 创建一个okhttpclient对象
        OkHttpClient client = new OkHttpClient();
        // 创建一个Request
        final Request request = new Request.Builder().url("https://kyfw.12306.cn/otn/").build();
        // 创建Call
        Call call = client.newCall(request);

        //把call推入队列中
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                Log.d("TAG",e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.d("TAG",response.body().string());
            }
        });

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = format.parse("2017-02-07 12:22:30");
            Log.d("TAG", "time:"+String.valueOf(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sort();
    }

    private void sort(){
        String[] array = {"666","20018","88"};
        Arrays.sort(array);
        Log.d("TAG", Arrays.toString(array));
    }

    private void onBtnClick(){
//        imageView.setImageBitmap(null);
        imageView.setImageDrawable(null);
//        imageView.setImageResource(0);
        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(R.drawable.ic_launcher);
            }
        },2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
