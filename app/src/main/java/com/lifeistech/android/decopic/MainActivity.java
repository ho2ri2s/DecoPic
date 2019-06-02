package com.lifeistech.android.decopic;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    int picnum = 0;
    String stampName;
    float x;
    float y;
    //ドラッグしたものが写真の上にあるかどうか
    boolean flag = false;

    private ImageView picture;
    private ImageView stamp[] = new ImageView[4];
    private FrameLayout frameLayout;

    //画像を読み込むときに使用する変数
    private static final int REQUEST_ORIGIN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        picture = (ImageView)findViewById(R.id.picture);
        frameLayout = (FrameLayout)findViewById(R.id.framLayout);

        for(int i = 0; i < 4; i++){
            stamp[i] = (ImageView)findViewById(getResources().getIdentifier("imageView" + i, "id", getPackageName()));
        }
        stamp[0].setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                stampName = "Star";
                ClipData clipData = ClipData.newPlainText("Stamp0", "Drag");
                view.startDrag(clipData, new View.DragShadowBuilder(view), (Object)view, 0);
                return false;
            }
        });

        stamp[1].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                stampName = "Heart";
                ClipData clipData = ClipData.newPlainText("stamp1", "Drag");
                view.startDrag(clipData, new View.DragShadowBuilder(view), (Object)view, 0);
                return false;
            }
        });

        stamp[2].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                stampName = "Ribon";
                ClipData clipData = ClipData.newPlainText("stamp2", "Drag");
                view.startDrag(clipData, new View.DragShadowBuilder(view), (Object)view, 0);
                return false;
            }
        });

        stamp[3].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                stampName = "Note";
                ClipData clipData = ClipData.newPlainText("stamp3", "Drag");
                view.startDrag(clipData, new View.DragShadowBuilder(view), (Object)view, 0);
                return false;
            }
        });

        picture.setOnDragListener(new View.OnDragListener(){

            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()){
                    case DragEvent.ACTION_DRAG_EXITED:
                        flag = false;
                        break;
                    case DragEvent.ACTION_DROP:
                        x = dragEvent.getX();
                        y = dragEvent.getY();
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        flag = true;
                        break;
                     default:
                         break;
                }
                return true;
            }
        });

        for(int i = 0; i < 4; i++){
            stamp[i].setOnDragListener(new View.OnDragListener(){
                @Override
                public boolean onDrag(View view, DragEvent dragEvent) {
                    if(dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED){
                        if(flag){
                            switch (stampName){
                                case "Star":
                                    addView(0);
                                    break;
                                case "Heart":
                                    addView(1);
                                    break;
                                case "Ribon":
                                    addView(2);
                                    break;
                                case "Note":
                                    addView(3);
                                    break;
                            }
                        }
                        return false;
                    }
                    return true;
                }
            });
        }

    }

    public void addView(int stampNum){
        //スタンプのサイズを180*180に設定
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(180, 180);
        ImageView image = new ImageView(getApplicationContext());
        //drawableファイルに入っているスタンプを表示する
        image.setImageResource(getResources().getIdentifier("stamp" + stampNum, "drawable", getPackageName()));

        frameLayout.addView(image, params);

        image.setTranslationX(x - (stamp[stampNum].getWidth() / 2));
        image.setTranslationY(y - (stamp[stampNum].getHeight() / 2));
        //値がゼロになる
        Log.d("MYTAG", stamp[stampNum].getWidth() / 2 + "");
    }

    public void select(View buttonView){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_ORIGIN);
    }

    //requestCode = REQUEST_ORIGIN, resultCode = 起動したActivityから動作が正常に行われたかの結果, data = 起動したActivityからのIntentが送られる
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                picture.setImageBitmap(bitmap);

                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 保存するメソッド
    public void save() throws Exception {
        try {
            frameLayout.setDrawingCacheEnabled(true);
            Bitmap save_bmp = Bitmap.createBitmap(frameLayout.getDrawingCache());
            String folderpath = Environment.getExternalStorageDirectory() + "/DecoPic/";
            File folder = new File(folderpath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File file = new File(folderpath, "sample" + picnum + ".png");
            if (file.exists()) {
                for (; file.exists(); picnum++) {
                    file = new File(folderpath, "sample" + picnum + ".png");
                }
            }
            FileOutputStream outStream = new FileOutputStream(file);
            save_bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.close();
            Toast.makeText(
                    getApplicationContext(),
                    "Image saved",
                    Toast.LENGTH_SHORT).show();
            frameLayout.setDrawingCacheEnabled(false);
            showFolder(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // イメージファイルが保存されたことを通知するメソッド
    private void showFolder(File path) throws Exception {
        try {
            ContentValues values = new ContentValues();
            ContentResolver contentResolver = getApplicationContext()
                    .getContentResolver();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATE_MODIFIED,
                    System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.SIZE, path.length());
            values.put(MediaStore.Images.Media.TITLE, path.getName());
            values.put(MediaStore.Images.Media.DATA, path.getPath());
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            throw e;
        }
    }

    // メニューを作るメソッド
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    // メニューのボタンが押された時に呼ばれるメソッド
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //メニューの保存ボタンを押した際のコード
        if(id == R.id.action_save){
            try{
                //ストレージへの書き込みが許可されている場合
                if(ContextCompat.checkSelfPermission(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED){
                    save();
                 //ストレージへの書き込みが許可されていない場合
                }else{
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        Toast.makeText(this, "ストレージへのアクセスを許可してください!", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    }else{
                        //まだ許可を求める前の時、許可を求めるダイアログを表示する
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0: //ActivityCompat.requestPermissions()の第3引数で指定した値
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //許可された場合の処理
                    try {
                        save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(this, "ストレージへのアクセスを許可してください", Toast.LENGTH_LONG).show();
                }
            break;
        }
    }
}
