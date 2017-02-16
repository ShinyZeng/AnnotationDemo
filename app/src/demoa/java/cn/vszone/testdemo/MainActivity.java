package cn.vszone.testdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.baidu.android.gporter.hostapi.HostUtil;

import java.io.File;

public class MainActivity extends Activity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("cn.vszone.testdemo.b");
                startActivity(intent);
            }
        });
        View button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setPackage("cn.vszone.testdemo.b");
                intent.setClassName("cn.vszone.testdemo.b","cn.vszone.testdemo.MainActivity");
                startActivity(intent);
            }
        });

        View button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installPluginApkGame(MainActivity.this);
            }
        });
    }

    /**
     * 安装插件游戏
     */
    private void installPluginApkGame(Context pContext) {
        File file = Environment.getExternalStorageDirectory();
        String path = file.getAbsolutePath().concat(File.separator).concat("KOGameBox/testdemo-demob-debug.apk");
        Log.d("MainActivity","patch="+path);
        File apkFile = new File(path);
        if(apkFile.exists()){
            Log.d("MainActivity","isExists");
            boolean isSuccess = HostUtil.installApkFile(pContext, path);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
