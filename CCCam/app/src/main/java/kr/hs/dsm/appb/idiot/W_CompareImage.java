package kr.hs.dsm.appb.idiot;

/**
 * Created by dsm_024 on 2016-05-31.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import compare.EyeCompare;
import compare.FaceCompare;
import compare.MouthCompare;
import compare.NoseCompare;


/**
 * Created by 10415 on 2016-05-30.
 */
public class W_CompareImage extends Activity {

    private String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
    protected  class Vector{
        double x;
        double y;
        public Vector(double x, double y){
            this.x = x;
            this.y = y;
        }

        public void add(Vector vector){
            this.x += vector.x;
            this.y += vector.y;
        }
        public void sub(Vector vector){
            this.x -= vector.x;
            this.y -= vector.y;
        }

        public void mult(double how){
            this.x *= how;
            this.y *= how;
        }

        public void div(int how){
            this.x /= how;
            this.y /= how;
        }

        public int mag(){
            int Vsize = (int)Math.sqrt(this.x*this.x+this.y*this.y);
            return Vsize;
        }

        public void nomalize(){
            if(this.mag() != 0){
                this.div(this.mag());
            }
        }

        public void limit(int limitNum){
            if(this.mag()>limitNum){
                this.nomalize();
                this.mult(limitNum);
            }
        }

        public Vector addStatic(Vector Vector1, Vector Vector2){
            Vector addedVector = new Vector(Vector1.x+Vector2.x,Vector1.y+Vector2.y);
            return addedVector;
        }

        public Vector subStatic(Vector Vector1, Vector Vector2){
            Vector subedVector = new Vector(Vector1.x-Vector2.x,Vector1.y-Vector2.y);
            return subedVector;
        }
    }
    public class MyView extends View implements Runnable{
        Thread animator = null;
        int width;
        int height;

        Vector location;
        Vector velocity;
        Vector acceleration;
        Vector dir;
        int touchX;
        int touchY=300;
        Vector touch = new Vector(touchX, touchY);
        Canvas mainCanvas = null;
        Bitmap CanvasBitmap;
        Bitmap gradient = BitmapFactory.decodeResource(super.getResources(),
                R.drawable.gradient);


        public  boolean onTouchEvent(MotionEvent event){
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    touchX = (int)event.getX();
                    touchY = (int)event.getY();
                    break;
                default:
            }
            return true;
        }

        public MyView(Context context) {
            super(context);
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            width = displayMetrics.widthPixels;
            height = displayMetrics.heightPixels;
            touchX = width/2;
            touchY = height/2;
            CanvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            this.location = new Vector(width/2, height/5);
            this.velocity = new Vector(-5, 0);
        }

        public void update(){
            touch = new Vector(touchX, touchY);
            dir = touch.subStatic(touch, location);
            dir.nomalize();
            dir.mult(0.5);
            acceleration = dir;
            velocity.add(acceleration);
            velocity.limit(20);
            location.add(velocity);
        }

        public void checkEdge(){
            if(location.x<0){
                location.x=0;
                velocity.x *= -1;
            } else if(location.x>width){
                location.x = width;
                velocity.x *= -1;
            };

            if(location.y<0){
                location.y=0;
                velocity.y *= -1;
            } else if(location.y>height){
                location.y = height;
                velocity.y *= -1;
            };


        }

        void start(){
            animator = new Thread(this);
            animator.start();
        }

        public void run() {
            while(true){
                //     System.out.println("in run while");
                postInvalidate();
//                invalidate();
                try {
                    Thread.sleep(15, 0);
                }
                catch (InterruptedException e)
                {
                    ;
                }
            }
        }

        Paint Pnt = new Paint();
        Paint TextPnt = new Paint();

        @Override
        public void onDraw(Canvas canvas) {
            update();
            checkEdge();

            Canvas c = new Canvas();
            c.drawBitmap(CanvasBitmap, 0, 0, new Paint());
            c = new Canvas(CanvasBitmap);
            int backColor = Color.argb(255, 255, 255, 255);

            setBackgroundColor(backColor);




            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            Pnt.setColor(color);
            Pnt.setStrokeWidth(50);
            Pnt.setStyle(Paint.Style.STROKE);

            TextPnt.setAlpha(100);
            TextPnt.setTextSize(90);

            c.drawText("캐리커처가 그려지고 있습니다",(width - TextPnt.measureText("캐리커처가 그려지고 있습니다")) / 2, height / 10, TextPnt);
            c.drawText("화면을 드래그 하세요",(width - TextPnt.measureText("화면을 드래그 하세요")) / 2, height / 7, TextPnt);

            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, gradient.getWidth(), gradient.getHeight());
            paint.setAntiAlias(true);
            c.drawARGB(0, 0, 0, 0);
            int size = (gradient.getWidth() / 2);
            //     c.drawCircle(size, size, size, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            c.drawBitmap(gradient, rect, rect, paint);

            c.drawCircle((int)location.x, (int)location.y, 50, Pnt);
            canvas.drawBitmap(CanvasBitmap, 0, 0, new Paint());
            // CanvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // canvas.setBitmap(CanvasBitmap);
            // canvas = new Canvas(CanvasBitmap);
            super.onDraw(canvas);
          /*  if(!CompareStart) {
                CompareStart = true;
                startCompare();
            }*/
        }
    }

    boolean ThFaceCheck;
    boolean ThEyeCheck;
    boolean ThLipsCheck;

    public Bitmap copyBtFace1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CompareStartTask().execute();
    }

    public class CompareStartTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            MyView view = new MyView(W_CompareImage.this);
            setContentView(view);
            view.start();
        }

        @Override
        protected Void doInBackground(Void... params) {
            startCompare();
            return null;
        }
    }

    public void startCompare(){
        final Intent intentMain = new Intent(this,
                MainActivity.class);

        final Intent intent = getIntent();


        try {

            ThFaceCheck = false;
            ThEyeCheck = false;
            ThLipsCheck = false;

            /**    객체 생성 부분   **/
            W_Face_Type_Path faceTypePath = new W_Face_Type_Path();
            W_Imgread_Type_Path imgreadTypePath = new W_Imgread_Type_Path();
            W_Compare_Type_Rect compareTypeRect = new W_Compare_Type_Rect();
            W_Compare_Type_Var compareTypeVar = new W_Compare_Type_Var();
            W_Final_Type_INT finalTypeInt = new W_Final_Type_INT();
            FaceCompare faceCompare = new FaceCompare();
            EyeCompare eyeCompare = new EyeCompare();
            MouthCompare mouthCompare = new MouthCompare();
            NoseCompare noseCompare = new NoseCompare();

            long LongFace = intent.getLongExtra("face", 0);
            long LongEye1 = intent.getLongExtra("eye1", 0);
            long LongEye2 = intent.getLongExtra("eye2", 0);
            long LongNose = intent.getLongExtra("nose", 0);
            long LongMouth = intent.getLongExtra("mouth", 0);

            //convert long to mat
            Mat face = new Mat(LongFace);
            Mat eye1 = new Mat(LongEye1);
            Mat nose = new Mat(LongNose);
            Mat mouth = new Mat(LongMouth);


            Mat compareFace = new Mat();
            Mat compareManHair = new Mat();
            Mat compareGirlHair = new Mat();
            Mat compareEye1 = new Mat();
            Mat compareEye2 = new Mat();
            Mat compareNose = new Mat();
            Mat compareMouth = new Mat();

            Mat finalEye1 = null;
            Mat finalEye2 = null;
            Mat finalNose = null;
            Mat finalMouth = null;

            for (int w = 0; w < 100; w++) {
                compareEye1 = Imgcodecs.imread(faceTypePath.eye_Path + w + ".png");

                if (compareEye1.dataAddr() != 0x0) {
                    try {
                        compareTypeRect.compareEye1Ret = eyeCompare.compareFeature(eye1, compareEye1);
                    } catch (Exception e) {
                        System.out.println("Eye Exception");
                    }
                }

                if (compareTypeRect.compareEye1Ret > 0 && compareTypeRect.compareEye1Ret < compareTypeVar.compareEye1Val) {
                    finalEye1 = compareEye1;
                    compareTypeVar.compareEye1Val = compareTypeRect.compareEye1Ret;
                    finalTypeInt.finalEyeInt = w;
                    //            System.out.println("i와 fanalEyeInt" + w + " and " + finalTypeInt.finalEyeInt);
                }

                if (compareTypeRect.compareEye2Ret != 0 && compareTypeRect.compareEye2Ret < compareTypeVar.compareEye2Val) {
                    finalEye2 = compareEye2;
                    compareTypeVar.compareEye2Val = compareTypeRect.compareEye2Ret;
                }
            }

            for (int j = 0; j < 100; j++) {
                compareNose = Imgcodecs.imread(faceTypePath.nose_Path + j + ".png");

                if (compareNose.dataAddr() != 0x0) {
                    try {
                        compareTypeRect.compareNoseRet = noseCompare.compareFeature(nose, compareMouth);
                    } catch (Exception e) {
                        System.out.println("Nose Exception");
                    }
                }

                //    System.out.println(j + "번째 코 avg = " + compareTypeRect.compareNoseRet);
                if (compareTypeRect.compareNoseRet != 0 && compareTypeRect.compareNoseRet < compareTypeVar.compareNoseVal) {
                    compareTypeVar.compareNoseVal = compareTypeRect.compareNoseRet;
                    //      System.out.println("i와 fanalNose" + j + " and " + finalTypeInt.finalNoseInt);
                    finalTypeInt.finalNoseInt = j;
                }
            }


            for (int i = 0; i < 100; i++) {


                compareMouth = Imgcodecs.imread(faceTypePath.mouth_Path + i + ".png");

                if (compareMouth.dataAddr() != 0x0) {
                    try {
                        compareTypeRect.compareMouthRet = mouthCompare.compareFeature(mouth, compareMouth);
                    } catch (Exception e) {
                        System.out.println("Mouth Exception");
                    }

                }


                //   System.out.println(i + "번째 입술 avg = " + compareTypeRect.compareMouthRet);
                if (compareTypeRect.compareMouthRet != 0 && compareTypeRect.compareMouthRet < compareTypeVar.compareMouthVal) {
                    finalMouth = compareMouth;
                    compareTypeVar.compareMouthVal = compareTypeRect.compareMouthRet;
                    System.out.println("i와 fanalLips" + i + " and " + finalTypeInt.finalLipsInt);
                    finalTypeInt.finalLipsInt = i;
                }


                if (compareTypeRect.compareNoseRet != 0 && compareTypeRect.compareNoseRet < compareTypeVar.compareNoseVal) {
                    finalNose = compareNose;
                    compareTypeVar.compareNoseVal = compareTypeRect.compareNoseRet;
                }
                if (compareTypeRect.compareMouthRet != 0 && compareTypeRect.compareMouthRet < compareTypeVar.compareMouthVal) {
                    finalMouth = compareMouth;
                    compareTypeVar.compareMouthVal = compareTypeRect.compareMouthRet;
                }
            }

            int random = (int) (Math.random() * 15) + 1;

            System.out.println("가장 유사한 FACE 번호: " + finalTypeInt.finalFaceInt);
            System.out.println("가장 유사한 EYE 번호 : " + finalTypeInt.finalEyeInt);
            System.out.println("가장 유사한 Lips 번호 : " + finalTypeInt.finalLipsInt);
            System.out.println("가장 유사한 Noes 번호 : " + finalTypeInt.finalNoseInt);
            System.out.println("랜덤 hair 번호 : " + random);


            if (finalTypeInt.finalEyeInt == 0) {
                finalTypeInt.finalEyeInt = (int) (Math.random() * 30 + 1);
            }
            if (finalTypeInt.finalFaceInt == 0) {
                finalTypeInt.finalFaceInt = (int) (Math.random() * 10 + 1);
            }

            if (finalTypeInt.finalNoseInt == 0) {
                finalTypeInt.finalNoseInt = (int) (Math.random() * 7 + 1);
            }

            if (finalTypeInt.finalLipsInt == 0) {
                finalTypeInt.finalLipsInt = (int) (Math.random() * 10 + 1);
            }


            Bitmap BtFace, BtNose, copyBtNose, BtEye, copyBtEye, BtLips, copyBtLips, BtLogo;
            Bitmap copyBtFace;

            BtFace = BitmapFactory.decodeFile(faceTypePath.girlsHair_Path +  random + ".png");
            copyBtFace = BtFace.copy(Bitmap.Config.ARGB_8888, true);
            copyBtFace = getResizedBitmap(copyBtFace, 468, 945);

            BtLogo = BitmapFactory.decodeFile(faceTypePath.logo_Path);
            Bitmap copyBtLogo = BtLogo.copy(Bitmap.Config.ARGB_8888, true);
            copyBtLogo = getResizedBitmap(copyBtLogo, copyBtFace.getWidth()/10*8, copyBtFace.getHeight()/10);

            BtEye = BitmapFactory.decodeFile(faceTypePath.eye_Path + finalTypeInt.finalEyeInt + ".png");
            //  Bitmap copyBtEye = Bitmap.createScaledBitmap(BtEye,53 , 26  , true);
            copyBtEye = BtEye.copy(Bitmap.Config.ARGB_8888, true);
            copyBtEye = getResizedBitmap(copyBtEye, copyBtFace.getWidth() / 4, copyBtFace.getHeight() / 7);

            BtLips = BitmapFactory.decodeFile(faceTypePath.mouth_Path + finalTypeInt.finalLipsInt + ".png");
            //     Bitmap sizingBmp = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
            copyBtLips = BtLips.copy(Bitmap.Config.ARGB_8888, true);
            copyBtLips = getResizedBitmap(copyBtLips, copyBtFace.getWidth()/10*3, copyBtFace.getHeight()/10*1);
            BtNose = BitmapFactory.decodeFile(faceTypePath.nose_Path + finalTypeInt.finalNoseInt + ".png");
            copyBtNose = BtNose.copy(Bitmap.Config.ARGB_8888, true);
            copyBtNose = getResizedBitmap(copyBtNose, copyBtFace.getWidth()/10*3, copyBtFace.getWidth()/10*3);
            System.out.println("size : "+ copyBtFace.getWidth()/10*3 + "x" + copyBtFace.getHeight()/10*3);

            int eyeY = copyBtFace.getHeight()/100*40;
            int eyeLX = copyBtFace.getWidth()/100*25;
            int eyeRX = copyBtFace.getWidth()/100*61;

            int noseX = copyBtFace.getWidth()/100*40;
            int noseY = copyBtFace.getHeight()/100*46;

            int lipX = copyBtFace.getWidth()/100*40;
            int lipY = copyBtFace.getHeight()/100*60;

            int logoX = copyBtFace.getWidth()/100*35;
            int logoY = copyBtFace.getHeight()/100*75;

            pasteImage(copyBtFace, change(copyBtEye, 2), eyeLX, eyeY);
            pasteImage(copyBtFace, copyBtEye, eyeRX, eyeY);
            pasteImage(copyBtFace, copyBtNose, noseX, noseY);
            pasteImage(copyBtFace, copyBtLips, lipX, lipY);
            pasteImage(copyBtFace, copyBtLogo, logoX, logoY);

            final Bitmap copyBtFace2 = copyBtFace;
            final Intent PushIntent = new Intent(this,
                    CompareResult.class);



            faceTypePath = null;
            imgreadTypePath = null;
            compareTypeRect = null;
            compareTypeVar = null;
            finalTypeInt = null;
            faceCompare = null;
            eyeCompare = null;
            mouthCompare = null;
            noseCompare = null;
            copyBtEye.isRecycled();
            copyBtFace.isRecycled();
            copyBtLips.isRecycled();
            copyBtLogo.isRecycled();
            copyBtNose.isRecycled();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            copyBtFace2.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytesFace = stream.toByteArray();
            PushIntent.putExtra("bytesFace",bytesFace);

            finish();
            startActivity(PushIntent);

            System.gc();





        }catch (Exception e){

        }
    }

    public void  EyeCompare(){
    }

    public static void pasteImage(Bitmap target, Bitmap src, int x, int y) {
        Canvas canvas = new Canvas(target);
        canvas.drawBitmap(src, x, y, null);
        //return bmOverlay;
    }

    public static final int FLIP_VERTICAL = 1;
    public static final int FLIP_HORIZONTAL = 2;

    public static Bitmap change(Bitmap src, int type) {
        Matrix matrix = new Matrix();

        if (type == FLIP_VERTICAL) {
            matrix.preScale(1.0f, -1.0f);
        }
        // if horizonal
        else if (type == FLIP_HORIZONTAL) {
            // x = x * -1
            matrix.preScale(-1.0f, 1.0f);
            // unknown type
        } else {
            return null;
        }
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();

        return resizedBitmap;
    }


    private void saveBitmap(Bitmap compliteFace, String folder, String name){
        String ex_storage =Environment.getExternalStorageDirectory().getAbsolutePath();
        String string_path = ex_storage+folder;
        String file_name = name;

        Toast.makeText(getApplicationContext(),
                "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();

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
    }
}


class W_Face_Type_Path{

    private String sd = Environment.getExternalStorageDirectory().getAbsolutePath();

    protected String girlsHair_Path = sd + "/" + ".CCCam_Picture" +"/"+"girls_face"+"/"+"hair";
    protected String eye_Path = sd + "/" + ".CCCam_Picture" +"/"+"eyes"+"/"+"eye_brow";
    protected String nose_Path = sd + "/" + ".CCCam_Picture" +"/"+"nose"+"/"+"_";
    protected String mouth_Path = sd + "/" + ".CCCam_Picture" +"/"+"mouth"+"/"+"_";
    protected String logo_Path = sd + "/" + ".CCCam_Picture" + "/" + "logo" + "/" + "watermark.png";

}

class W_Imgread_Type_Path{

    protected String facePath;
    protected String eye1Path;
    protected String eye2Path;
    protected String nosePath ;
    protected String mouthPath;

}

class W_Compare_Type_Rect{

    protected double compareFaceRet = 0;
    protected double compareManHairRet = 0;
    protected double compareGirlHairRet = 0;
    protected double compareEye1Ret = 0;
    protected double compareEye2Ret = 0;
    protected double compareNoseRet = 0;
    protected double compareMouthRet = 0;

}

class W_Compare_Type_Var {

    protected double compareFaceVal = 10000;
    protected double compareManHairVal = 10000;
    protected double compareEye1Val = 10000;
    protected double compareEye2Val = 10000;
    protected double compareNoseVal = 10000;
    protected double compareMouthVal = 10000;

}

class W_Final_Type_INT {
    public int finalFaceInt = 0;
    public int finalEyeInt = 0;
    public int finalLipsInt = 0;
    public int finalNoseInt = 0;
}