package com.tool.helper;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Random;
import java.util.logging.LogRecord;

public class MyService extends AccessibilityService {


//AccessibilityService被系统接管，组件之间通信用广播。
    public static int state=0;
    private String ToastString="";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event.getEventType()==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){

            if (state==0){
              act();
            }
            else if(state!=0){
            }

        }
        if (event.getPackageName().equals("com.tool.helper")){
            if (event.getEventType()==AccessibilityEvent.TYPE_VIEW_CLICKED){

                if (event.getText().get(0).equals("Button")){
                    state=0;
                    showToast("置位");
                }

            }
            if (event.getEventType()==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){



            }

        }

    }



    // final_nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);

    @SuppressLint("SuspiciousIndentation")
    public void act(){


           new Thread(new Runnable() {
                @SuppressLint("SuspiciousIndentation")
                @Override
                public void run() {

                    sleep(3000);
                    AccessibilityNodeInfo wd=getRootInActiveWindow();
                    AccessibilityNodeInfo node = findViewByText("开始学习",wd);
                    if (node==null)
                        node = findViewByText("继续学习",wd);
                    if (node!=null) {
                        state = 1;
                        showToast("任务开始");
                        Point p = getPointtByNode(node);
                        clickPoint(p);
                        sleep(100);

                    }else {showToast("未找到启动点"); return;}
                    while (state==1){
                       wd=getRootInActiveWindow();
                       delay_set("我认识",wd);
                       delay_set("下一个",wd);
                       delay_set("想起来了",wd);
                       delay_set("下一组",wd);
                       delay_set("完成",wd);
                    }
                }
            }).start();




    }

    public void delay_set(String str,AccessibilityNodeInfo MyaccessibilityNodeInfo){

        AccessibilityNodeInfo accessibilityNodeInfo=findViewByText(str,MyaccessibilityNodeInfo);
        if (accessibilityNodeInfo!=null){
            clickPoint(getPointtByNode(accessibilityNodeInfo));
            sleep(100);
            //showToast("找到了");
        }else {

        }

    }



    public AccessibilityNodeInfo findViewByText(String str,AccessibilityNodeInfo accessibilityNodeInfo) {



       if(accessibilityNodeInfo!=null){

           if (accessibilityNodeInfo.findAccessibilityNodeInfosByText(str).size()>0)
           return accessibilityNodeInfo.findAccessibilityNodeInfosByText(str).get(0);
       }

        return null;

    }

    public void sleep(int s){
        try {
            Thread.sleep(s);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onInterrupt() {

    }

    Handler handler= new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what==0){
                Toast.makeText(getApplicationContext(),ToastString,Toast.LENGTH_SHORT).show();
            }
        }
    };





    //此函数回调执行完，才能获取Window，不然获取Window的操作会打断click。
    public boolean clickPoint(Point point) {

        if (point==null)
            return false;

        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path path = new Path();
        path.moveTo(point.x, point.y);
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 50));
        GestureDescription gesture = builder.build();
        dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                //showToast("点击完成");


            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                //showToast("点击取消");

            }
        }, null);


        return true;

    }

    static Point getPointtByNode(AccessibilityNodeInfo node){

        if (node==null){
            return null;
        }

        Rect rect = new Rect();
        node.getBoundsInScreen(rect);
        Point point = new Point(rect.centerX(), rect.centerY());
        return point;
    }

    public void showToast(String str){

        ToastString=str;
        handler.sendEmptyMessage(0);
    }
}
