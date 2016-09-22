package kr.hs.dsm.appb.idiot;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;

public class LoadingActivity extends Activity {

    private String sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

    String ex_storage =Environment.getExternalStorageDirectory().getAbsolutePath();

    String cccam_path = ex_storage + "CCCam_Picture";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(LoadingActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();


                final Intent intent = new Intent(LoadingActivity.this,
                        MainActivity.class);

                finish();
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
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
        String ex_storage =Environment.getExternalStorageDirectory().getAbsolutePath();

        File imgfile1 = new File(ex_storage+"/boom/caricuture.jpg");

        setContentView(R.layout.activity_loading);

        if (imgfile1.canRead()) {
            System.out.println("imgfile1  " + imgfile1);
            Bitmap imgfile = BitmapFactory.decodeFile(imgfile1.getAbsolutePath());

            ImageView image = (ImageView) findViewById(R.id.loading_image);

            image.setImageBitmap(getCircleBitmap(imgfile));
        }
    }

    public Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int size = (bitmap.getWidth());
        canvas.drawCircle(size, size, size, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}