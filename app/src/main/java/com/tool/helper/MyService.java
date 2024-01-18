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
import java.util.logging.LogRecord;

public class MyService extends AccessibilityService {

   private String ToastString="";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType()==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){

            if ( getRootInActiveWindow()!=null){
                act(getRootInActiveWindow());

            }

           else {
                Toast.makeText(this,"Source为空", Toast.LENGTH_SHORT).show();
            }

        }

    }




    public void act(AccessibilityNodeInfo nodeInfo){
        List<AccessibilityNodeInfo> accessibilityNodes=nodeInfo.findAccessibilityNodeInfosByText("开始学习");



        if (accessibilityNodes.size()>0){
            AccessibilityNodeInfo final_nodeInfo=accessibilityNodes.get(0);
           Point p = getPointtByNode(final_nodeInfo);
           showToast("找到:"+p.x+","+p.y);

            new Thread(new Runnable() {
                @SuppressLint("SuspiciousIndentation")
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    clickPoint(p);
                       // final_nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                }
            }).start();

        }

        else

        showToast("未找到");
    }



    public void order(){

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
