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
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Random;
import java.util.logging.LogRecord;

public class MyService extends AccessibilityService {


    public static int state=0;
   private String ToastString="";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType()==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){

            if ( getRootInActiveWindow()!=null&&state==0){
                act();

            }
           else if(state!=0){
               //showToast("state:"+state);
            }else if (getRootInActiveWindow()==null){
               //showToast("Source为空");
           }

        }

    }



    // final_nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);

    public void act(){


        AccessibilityNodeInfo node = findViewByText("发现");
        if (node!=null){
           state=1;
           Point p = getPointtByNode(node);
           showToast("任务开始");
           new Thread(new Runnable() {
                @SuppressLint("SuspiciousIndentation")
                @Override
                public void run() {

                    sleep(1000);
                    clickPoint(p);
                    Random random=new Random();
                    sleep(1000+ random.nextInt(1000));

                }
            }).start();

        }
        else
        showToast("未找到,任务结束");

    }


    public AccessibilityNodeInfo findViewByText(String str){
        List<AccessibilityNodeInfo> accessibilityNodes= getRootInActiveWindow().findAccessibilityNodeInfosByText(str);
        if (accessibilityNodes.size()>0){
            return accessibilityNodes.get(0);
        }
        else {
            return null;
        }
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

    public void clickPoint(Point point) {


        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path path = new Path();
        path.moveTo(point.x, point.y);
        builder.addStroke(new GestureDescription.StrokeDescription(path, 100L, 200L));
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


    }

    static Point getPointtByNode(AccessibilityNodeInfo node){
        if (node == null){
            return new Point(0, 0);
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
