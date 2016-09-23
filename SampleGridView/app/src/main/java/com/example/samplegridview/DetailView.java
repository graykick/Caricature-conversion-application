package com.example.samplegridview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by 윤여환 on 2016-09-22.
 */
public class DetailView extends Activity{
    public static int ImageId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        try {
            ImageView imageView = (ImageView)findViewById(R.id.DetailImageView);
            imageView.setBackgroundResource(ImageId);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
