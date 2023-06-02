package com.example.paddel;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** PaddelPlugin */
public class PaddelPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  public static final int REQUEST_LOAD_MODEL = 0;
  public static final int REQUEST_RUN_MODEL = 1;
  public static final int RESPONSE_LOAD_MODEL_SUCCESSED = 0;
  public static final int RESPONSE_LOAD_MODEL_FAILED = 1;
  public static final int RESPONSE_RUN_MODEL_SUCCESSED = 2;
  public static final int RESPONSE_RUN_MODEL_FAILED = 3;
  private Context applicationContext;

  protected Predictor predictor = new Predictor();
  // Model settings of ocr
  protected String modelPath = "models/ch_PP-OCRv2";
  protected String labelPath = "labels/ppocr_keys_v1.txt";
  protected String imagePath = "";
  protected int cpuThreadNum = 4;
  protected String cpuPowerMode = "LITE_POWER_HIGH";
  protected int detLongSize = 960;
  protected float scoreThreshold = 0.1f;
  private String currentPhotoPath;
  protected Handler receiver = null; // Receive messages from worker thread
  protected Handler sender = null; // Send command to worker thread
  protected HandlerThread worker = null; // Worker thread to load&run model

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "paddel");
    channel.setMethodCallHandler(this);
    applicationContext = flutterPluginBinding.getApplicationContext();

  }

  public boolean onLoadModel() {
    if (predictor.isLoaded()) {
      predictor.releaseModel();
    }
    boolean pre = predictor.init(applicationContext, modelPath, labelPath,  0, cpuThreadNum, cpuPowerMode, detLongSize, scoreThreshold);
   Log.d("MrSkee","OnLOAD MODEL "+pre);
    return pre;
  }


  private String kqPredict = "";
  public boolean onRunModel() {
//    String run_mode = spRunMode.getSelectedItem().toString();
//    int run_det = run_mode.contains("检测") ? 1 : 0;
//    int run_cls = run_mode.contains("分类") ? 1 : 0;
//    int run_rec = run_mode.contains("识别") ? 1 : 0;
    Log.d("RuningModel","onRunModel");
    return predictor.isLoaded() && predictor.runModel(1, 1, 1, new Predictor.CallBackRuningModel() {
      @Override
      public void callBack(String kq) {
        kqPredict = kq;
      }
    });
  }

  public void requestRunModel(){
    worker = new HandlerThread("Predictor Worker");
    worker.start();
    sender = new Handler(worker.getLooper()) {
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case REQUEST_LOAD_MODEL:
            // Load model and reload test image
            if (onLoadModel()) {
              receiver.sendEmptyMessage(RESPONSE_LOAD_MODEL_SUCCESSED);
            } else {
              receiver.sendEmptyMessage(RESPONSE_LOAD_MODEL_FAILED);
            }
            break;
          case REQUEST_RUN_MODEL:
            // Run model if model is loaded
            if (onRunModel()) {
              receiver.sendEmptyMessage(RESPONSE_RUN_MODEL_SUCCESSED);
            } else {
              receiver.sendEmptyMessage(RESPONSE_RUN_MODEL_FAILED);
            }
            break;
          default:
            break;
        }
      }
    };
  }

  public void runModel() {
    sender.sendEmptyMessage(REQUEST_RUN_MODEL);
  }
  public void loadModel() {
    sender.sendEmptyMessage(REQUEST_LOAD_MODEL);
  }
  public static Bitmap decodeByteArray(byte[] imageData) {
    // Decode the byte array to a Bitmap
    Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    return bitmap;
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      String arg1 = call.argument("arg1");
      byte[] imageData = call.argument("imageData");
      requestRunModel();
      receiver = new Handler() {
        @Override
        public void handleMessage(Message msg) {
          switch (msg.what) {
            case RESPONSE_LOAD_MODEL_SUCCESSED:
              String kq = "Model: " + modelPath.substring(modelPath.lastIndexOf("/") + 1) +"\nCPU Thread Num: " + cpuThreadNum + "\nCPU Power Mode: " + cpuPowerMode + " \nSTATUS: load model successed";
              Log.d("HOINGUYEN ",kq);
              result.success(kq);
              break;
            case RESPONSE_LOAD_MODEL_FAILED:
//              result.success("RESPONSE_LOAD_MODEL_FAILED");
              break;
            case RESPONSE_RUN_MODEL_SUCCESSED:
//              result.success("RESPONSE_RUN_MODEL_SUCCESSED");

              break;
            case RESPONSE_RUN_MODEL_FAILED:
//              result.success("RESPONSE_RUN_MODEL_FAILED");


              break;
            default:
              break;
          }
        }
      };
      if(!predictor.isLoaded()){
//        result.success("STATUS: model is not loaded");
        loadModel();
      }else {
        Bitmap bitmap = decodeByteArray(imageData);
        predictor.setInputImage(bitmap);
        runModel();
      }


    } else  if (call.method.equals("runingModel")){
      byte[] imageData = call.argument("imageData");
        Bitmap bitmap = decodeByteArray(imageData);
        predictor.setInputImage(bitmap);
        runModel();
    }else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

//  private native int nativeAdd(int x, int y);
//
//  static {
//    System.loadLibrary("native_add");
//  }
}
