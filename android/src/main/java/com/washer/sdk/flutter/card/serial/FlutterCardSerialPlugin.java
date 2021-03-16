package com.washer.sdk.flutter.card.serial;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.licheedev.serialworker.core.Callback;
import com.washer.sdk.flutter.card.serial.command.receive.AutoReadCommand;
import com.washer.sdk.flutter.card.serial.command.send.SendAutoCommand;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FlutterCardSerialPlugin */
public class FlutterCardSerialPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private EventChannel eventChannel;

  private EventChannel.EventSink mySink;

  private Handler handler;

  private static final String TAG = "FlutterCardSerialPlugin";

  EventChannel.StreamHandler streamHandler = new EventChannel.StreamHandler() {
    // 这个onListen是Flutter端开始监听这个channel时的回调，第二个参数 EventSink是用来传数据的载体。
    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
      mySink = eventSink;
      handler = new Handler(Looper.getMainLooper());
    }

    // 对面不再接收
    @Override
    public void onCancel(Object o) {
      AsyncTask.execute(() -> {
        mySink = null;
        eventChannel.setStreamHandler(null);
        handler.removeCallbacksAndMessages(null);
        handler = null;
        Log.d(TAG, "Disconnected stream handler");
      });
    }
  };

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_card_serial");
    channel.setMethodCallHandler(this);

    eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_card_serial/read");
    eventChannel.setStreamHandler(streamHandler);

    EventBus.getDefault().register(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "getPlatformVersion":
        result.success("Android " + android.os.Build.VERSION.RELEASE);
        break;
      case "connect":
        try {
          SerialManager.get().initDevice();
          result.success(true);
        } catch (Exception e) {
          e.printStackTrace();
          result.error(e.getMessage(), "", "");
        }
        break;
      case "disconnect":
        SerialManager.get().release();
        break;
      case "openAuto":
        try {
          SerialManager.get().setAutoRead(new SendAutoCommand(new byte[]{1, 1}), new Callback<AutoReadCommand>() {
            @Override
            public void onSuccess(@Nullable AutoReadCommand autoReadCommand) {
              if(autoReadCommand.getResult() == 128) {
                result.success(true);
              } else {
                result.success(false);
              }
            }

            @Override
            public void onFailure(@NonNull Throwable tr) {
              result.success(false);
            }
          });
        } catch (Exception e) {
          e.printStackTrace();
          result.error(e.getMessage(), "", "");
        }
        break;
      case "closeAuto":
        try {
          SerialManager.get().setAutoRead(new SendAutoCommand(new byte[]{1, 0}), new Callback<AutoReadCommand>() {
            @Override
            public void onSuccess(@Nullable AutoReadCommand autoReadCommand) {
              if(autoReadCommand.getResult() == 128) {
                result.success(true);
              } else {
                result.success(false);
              }
            }

            @Override
            public void onFailure(@NonNull Throwable tr) {
              result.success(false);
            }
          });
        } catch (Exception e) {
          e.printStackTrace();
          result.error(e.getMessage(), "", "");
        }
        break;
      default:
        result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    SerialManager.get().release();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventMainThread(String cardId) {
    if (mySink != null && handler != null) {
      handler.post(() -> mySink.success(cardId));
    }
//    AsyncTask.execute(() -> {
//      if(mySink != null) {
//        mySink.success(cardId);
//      }
//    });
  }
}
