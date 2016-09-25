package kr.hs.dsm.appb.idiot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import java.io.FileOutputStream;
import java.security.cert.Certificate;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    @SuppressWarnings("deprecation")
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    ImageButton SHOOT_button;
    ImageButton CHANGE_button;
    String str;
    int cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
    Camera.Parameters param;
    @SuppressWarnings("deprecation")
    Camera.PictureCallback jpegCallback;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SHOOT_button = (ImageButton) findViewById(R.id.shoot_button);
        CHANGE_button = (ImageButton) findViewById(R.id.change_button);


        Intent intent11 = new Intent(this,
                MainActivity.class);


        try {
            SHOOT_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        camera.takePicture(null, null, jpegCallback);
                        Toast.makeText(getApplicationContext(), "얼굴 인식 중 입니다. 조금만 기다려 주세요!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            CHANGE_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        surfaceDestroyed(surfaceHolder);

                        if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
                            camera = Camera.open(cameraFacing);
                        } else if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
                            camera = Camera.open(cameraFacing);
                        }
                        param = camera.getParameters();

                        if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            param.setRotation(270);
                        } else if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            param.setRotation(90);
                        }
                        camera.setParameters(param);
                        camera.setDisplayOrientation(90);

                        camera.setPreviewDisplay(surfaceHolder);
                        camera.startPreview();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            getWindow().setFormat(PixelFormat.UNKNOWN);
            surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            jpegCallback = new Camera.PictureCallback() {
                public void onPictureTaken(byte[] data, Camera camera) {
                    // 파일 저장
                    String sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
                    String path = sd + "/" + (int) System.currentTimeMillis() + ".jpg";
                    FileOutputStream outStream = null;
                    try {
                        str = String.format(path,
                                System.currentTimeMillis());
                        outStream = new FileOutputStream(str);
                        outStream.write(data);
                        outStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(),
                            "Picture Saved", Toast.LENGTH_LONG).show();
                    refreshCamera();


                    Intent intent = new Intent(MainActivity.this,
                            CameraResult.class);
                    intent.putExtra("strParamName", str);
                    int a = 0;

                    //startActivity(intent);
                    startActivityForResult(intent, a);
                }
            };
        }catch (Exception e){
            finish();
            startActivity(intent11);
            Toast.makeText(getApplicationContext(), "오류가 발생했습니다!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if((keyCode == KeyEvent.KEYCODE_BACK)){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("종료 여부")
                    .setMessage("앱을 종료 하시 겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        try {
            camera.stopPreview();
        } catch (Exception e) {
        }
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @SuppressWarnings("deprecation")
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open(cameraFacing);
        param = camera.getParameters();
        param.setRotation(270);
        camera.setParameters(param);
        camera.setDisplayOrientation(90);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format, int width, int height) {
        refreshCamera();
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

}