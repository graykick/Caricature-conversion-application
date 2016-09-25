package kr.hs.dsm.appb.idiot;

import android.Manifest;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.core.ZipFile;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;
import java.util.zip.ZipInputStream;

public class LoadingActivity extends Activity {

    private String sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

    String ex_storage =Environment.getExternalStorageDirectory().getAbsolutePath();
    String cccam_path = ex_storage + "CCCam_Picture";
    String aa = ex_storage+"/Download";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

                final Intent intent = new Intent(LoadingActivity.this,
                        MainActivity.class);

                Handler handler = new Handler(){
                    public void handleMessage(Message msg){
                        finish();
                    }
                };
                handler.sendEmptyMessageDelayed(0, 2500);
                startActivity(intent);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermission) {
                Toast.makeText(LoadingActivity.this, "권한거부\n" +
                        deniedPermission.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("사진을 찍기 위해 카메라 접근 권한이 필요합니다." +
                        "\n캐리커처 사진들을 다운로드 하기위해 Storage를 읽고 써야합니다.")
                .setDeniedMessage("CCCam을 사용할 수 없습니다..\n" +
                        "[설정] > [권한] 에서 권한을 허용할 수 있습니다")
                .setPermissions(
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .check();

        setContentView(R.layout.activity_loading);

        File fileOK = new File(Environment.getExternalStorageDirectory()+"/CCCam_Picture");
        if(!fileOK.exists()) {
            change();
       }
    }

    private void change()
    {
        try
        {
            File path = new File(Environment.getExternalStorageDirectory() + "/Android/obb/");

            PackageInfo info = null;
            PackageManager manager = this.getPackageManager();

            info = manager.getPackageInfo( this.getPackageName(), 0);

            int version = info.versionCode;

            if (path.exists())
            {
                File filePath = new File(path + File.separator + "main." + version + "." + getPackageName() + ".obb");
                System.out.println(filePath);

                ZipResourceFile zipfile = new ZipResourceFile(filePath.toString());
                InputStream inputStream = zipfile.getInputStream("CCCam_Picture.zip");

                createZIPFile(inputStream);

                File ppa = new File(Environment.getExternalStorageDirectory()+"/CCCam_Picture");
                ppa.mkdirs();

                String source = Environment.getExternalStorageDirectory()+"/Download/CCCam_Picture.zip";
                String destination = Environment.getExternalStorageDirectory()+"/CCCam_Picture";

                net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(source);
                zipFile.extractAll(destination);

                if(inputStream == null){
                    System.out.println("d is null!!!!!!!!!!");
                }else{
                    System.out.println("d is good");
                }
            }else
                System.out.println("path가 없ㅇ므!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    public void createZIPFile(InputStream inputStream){
        File file = new File(Environment.getExternalStorageDirectory()+"/Download/CCCam_Picture.zip");
        try {

            OutputStream outputStream = new FileOutputStream(file);

            if(outputStream == null){
                System.out.println("outputStream is null");
            }else
                System.out.println("outputStream is good");

            byte[] buf = new byte[1024];
            int len = 0;

            while((len = inputStream.read(buf)) > 0){
                outputStream.write(buf, 0, len);
                System.out.println(len);
            }
            outputStream.close();
            inputStream.close();
            System.out.println("finish");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}