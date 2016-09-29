package kr.hs.dsm.appb.idiot;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dsm_024 on 2016-09-26.
 */

public class CompareResult extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compare);

        final ImageButton goHome = (ImageButton) findViewById(R.id.go_home);
        final Intent intentMain = new Intent(this,
                MainActivity.class);

        goHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                startActivity(intentMain);
            }
        });

        final ImageView img = (ImageView) findViewById(R.id.imageView);
        final Intent intent = getIntent();

        byte[] bytes = intent.getByteArrayExtra("bytesFace");
        final Bitmap copyBtFace2 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        img.setImageBitmap(copyBtFace2);


        // ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);
        final ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveBitmap(copyBtFace2, "/DCIM/caricature/", (int) System.currentTimeMillis() + ".jpg");
                saveBitmap(copyBtFace2, "/DCIM/", (int) System.currentTimeMillis() + ".jpg");
            }
        });



    }
    private void saveBitmap(Bitmap compliteFace, String folder, String name){
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String string_path = ex_storage+folder;
        String file_name = name;

        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path+file_name);

            compliteFace.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
        Toast.makeText(getApplicationContext(),
                "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        final Intent intentMain = new Intent(this,
                MainActivity.class);

        finish();
        startActivity(intentMain);
    }

}
