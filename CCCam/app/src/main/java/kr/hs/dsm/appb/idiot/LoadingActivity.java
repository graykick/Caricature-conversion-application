package kr.hs.dsm.appb.idiot;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Toast;

import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipFile;

public class LoadingActivity extends Activity {

    private String sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

    String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        final Intent intent_main = new Intent(this, MainActivity.class);

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

                final Handler mHandler = new Handler() {
                    public void handleMessage(Message msg) {
                        startActivity(intent_main);
                    }
                };

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        mHandler.sendEmptyMessage(0);
                    }
                }, 2000);
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
                        Manifest.permission.INTERNET,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .check();

        setContentView(R.layout.activity_loading);

        PackageInfo info = null;
        PackageManager manager = this.getPackageManager();

        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        int version = info.versionCode;


        change();

        String path = (Environment.getExternalStorageDirectory() + "/CCCam_Picture");

        /*File filePre = new File(path);
        File fileNow = new File(Environment.getExternalStorageDirectory() +"/.CCCam_Picture");

        filePre.renameTo(fileNow);
        System.out.println("바뀐 CCCam 폴더 패스 : "+fileNow.getPath());*/
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    private void change() {
        try {
            File path = new File(Environment.getExternalStorageDirectory() + "/Android/obb/");
            System.out.println("path android    :" + path);

            PackageInfo info = null;
            PackageManager manager = this.getPackageManager();

            info = manager.getPackageInfo(this.getPackageName(), 0);

            int version = info.versionCode;

            if (path.exists()) {
                File filePath = new File(path + File.separator + "main." + 232 + "." + getPackageName() + ".obb");
                System.out.println("파일 패스   " + filePath);

                ZipResourceFile zipfile = new ZipResourceFile(filePath.toString());
                InputStream inputStream = zipfile.getInputStream("CCCam_Picture.zip");

                createZIPFile(inputStream);

                File ppa = new File(Environment.getExternalStorageDirectory() + "/.CCCam_Picture");

                ppa.mkdirs();

                String source = Environment.getExternalStorageDirectory() + "/Download/CCCam_Picture.zip";
                String destination = Environment.getExternalStorageDirectory() + "/.CCCam_Picture";

                net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(source);
                zipFile.extractAll(destination);

                if (inputStream == null) {
                    System.out.println("d is null!!!!!!!!!!");
                } else {
                    System.out.println("d is good");
                    File file = new File(source);
                    file.delete();
                }
            } else
                System.out.println("path가 없음!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void createZIPFile(InputStream inputStream) throws PackageManager.NameNotFoundException {

        PackageInfo info = null;
        PackageManager manager = this.getPackageManager();

        info = manager.getPackageInfo(this.getPackageName(), 0);

        int version = info.versionCode;


        File file = new File(Environment.getExternalStorageDirectory() + "/Download/CCCam_Picture.zip");
        try {

            OutputStream outputStream = new FileOutputStream(file);

            if (outputStream == null) {
                System.out.println("outputStream is null");
            } else
                System.out.println("outputStream is good");

            byte[] buf = new byte[1024];
            int len = 0;

            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
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