package com.mc.studyapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.ToastUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ThreeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        View view = findViewById(R.id.fab);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showLong("弹通知");
                NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
                hookNotificationManager(notificationManager);
                Notification notification;
                notification = new Notification.Builder(ThreeActivity.this)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentText("弹通知")
                        .setContentTitle("弹通知")
                        .build();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel("110", "default_channel", NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(notificationChannel);
                }
                notificationManager.notify(110, notification);
            }
        });
    }

    private void hookNotificationManager(NotificationManager notificationManager) {
        try {
            Method getService = NotificationManager.class.getDeclaredMethod("getService");
            getService.setAccessible(true);
            final Object invoke = getService.invoke(notificationManager);


            Class<?> iNotificationManager = Class.forName("android.app.INotificationManager");
            Object newProxyInstance = Proxy.newProxyInstance(notificationManager.getClass().getClassLoader(), new Class[]{iNotificationManager},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            Log.e("ThreeActivity", "invoke: " + method.getName());
                            return method.invoke(invoke, args);
                        }
                    });

            Field sService = NotificationManager.class.getDeclaredField("sService");
            sService.setAccessible(true);
            sService.set(notificationManager, newProxyInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
