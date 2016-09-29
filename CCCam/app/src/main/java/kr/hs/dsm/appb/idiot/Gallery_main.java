package kr.hs.dsm.appb.idiot;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import kr.hs.dsm.appb.idiot.util.BlurBitmapUtils;
import kr.hs.dsm.appb.idiot.util.ViewSwitchUtils;

import com.view.jameson.library.CardScaleHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Gallery_main extends Activity {

    private RecyclerView mRecyclerView;
    private ImageView mBlurView;
    private List<Bitmap> mList = new ArrayList<>();
    private CardScaleHelper mCardScaleHelper = null;
    private Runnable mBlurRunnable;
    private int mLastPos = -1;
    BitmapDrawable d1;

    String path = "/storage/emulated/0/DCIM/caricature";
    File file = new File(path);
    File[] files = file.listFiles();
    static Bitmap bmp = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        init();
    }

    private void init() {

        for (int i = 0; i < files.length; i++) {
            bmp = BitmapFactory.decodeFile(files[i].getAbsolutePath());
            System.out.println("bmp is : " + files[i].getPath());
            mList.add(returnBitmap(this));
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(new CardAdapter(mList));
        // mRecyclerView绑定scale效果
        mCardScaleHelper = new CardScaleHelper();
        mCardScaleHelper.setCurrentItemPos(2);
        mCardScaleHelper.attachToRecyclerView(mRecyclerView);

        initBlurBackground();
    }

    private void initBlurBackground() {
        mBlurView = (ImageView) findViewById(R.id.blurView);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    notifyBackgroundChange();
                }
            }
        });
        notifyBackgroundChange();
    }

    private void notifyBackgroundChange() {
        if (mLastPos == mCardScaleHelper.getCurrentItemPos()) return;
        mLastPos = mCardScaleHelper.getCurrentItemPos();
        final Bitmap resId = mList.get(mCardScaleHelper.getCurrentItemPos());
        mBlurView.removeCallbacks(mBlurRunnable);
        mBlurRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = resId;
                        ViewSwitchUtils.startSwitchBackgroundAnim(mBlurView, BlurBitmapUtils.getBlurBitmap(mBlurView.getContext(), bitmap, 15));

                    }
                };
        mBlurView.postDelayed(mBlurRunnable, 500);
    }

    public Bitmap returnBitmap (Gallery_main main){
        return main.bmp;
    }
}
