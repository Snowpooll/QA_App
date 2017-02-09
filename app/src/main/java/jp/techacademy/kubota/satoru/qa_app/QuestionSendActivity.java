package jp.techacademy.kubota.satoru.qa_app;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class QuestionSendActivity extends AppCompatActivity implements View.OnClickListener,DatabaseReference.CompletionListener{

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final int CHOOSER_REQUEST_CODE = 100;

    private ProgressDialog progressDialog;
    private EditText titleEdit;
    private EditText bodyEdit;
    private ImageView imageView;
    private Button sendButton;

    private  int mGenre;
    private Uri pictureUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_send);

        //junle number save
        Bundle extras =getIntent().getExtras();
        mGenre = extras.getInt("genre");

        //ui setting
        setTitle("質問作成");
        titleEdit =(EditText)findViewById(R.id.titleEdit);
        bodyEdit =(EditText)findViewById(R.id.editBody);

        sendButton =(Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);

        imageView =(ImageView)findViewById(R.id.imageView);
        imageView.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("投稿中...");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHOOSER_REQUEST_CODE){
            if(resultCode != RESULT_OK){
                if(pictureUri != null){
                    getContentResolver().delete(pictureUri,null,null);
                    pictureUri=null;
                }
                return;
            }

            //get image
            Uri uri = (data == null || data.getData() == null) ? pictureUri:data.getData();

            //get Bitmap of Uri
            Bitmap image;
            try {
                ContentResolver contentResolver = getContentResolver();
                InputStream inputStream = contentResolver.openInputStream(uri);
                image =BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }catch (IOException e){
                return;
            }

            //Resize  the get bitmap long side to 500px
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            float scale = Math.min((float)500 / imageWidth, (float)500 / imageHeight);

            Matrix matrix = new Matrix();
            matrix.postScale(scale,scale);

            Bitmap resizeImage = Bitmap.createBitmap(image,0,0,imageWidth,imageHeight,matrix,true);

            //bitmap is setting imageview
            imageView.setImageBitmap(resizeImage);
            pictureUri=null;

        }
    }

    @Override
    public void onClick(View view) {
        if(view == imageView){
            //check permission allow?
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    //allow
                    showChooser();
                }else {
                    //denyed request is dialog show
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSIONS_REQUEST_CODE);
                    return;
                }
            }else {
                showChooser();
            }
        }else if(view == sendButton){
            //if keybord show , close
            InputMethodManager im =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference genref = databaseReference.child(Const.ContentsPath).child(String.valueOf(mGenre));

            Map<String,String> data = new HashMap<String, String>();


            //UID
            data.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());


            //get title & body
            String title = titleEdit.getText().toString();
            String body = bodyEdit.getText().toString();

            if(title.length() ==0){
                Snackbar.make(view,"タイトルを入力してください",Snackbar.LENGTH_LONG).show();
                return;
            }
            if(body.length() ==0){
                Snackbar.make(view,"本文を入力してください",Snackbar.LENGTH_LONG).show();
                return;
            }

            //get name of Firebase
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            String name = sp.getString(Const.NameKey,"");
            data.put("title",title);
            data.put("body",body);
            data.put("name",name);

            //get image
            BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();

            //if not setting image ,get image is BASE64 encode
            if(drawable != null){
                Bitmap bitmap =drawable.getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
                String bitmapString = Base64.encodeToString(baos.toByteArray(),Base64.DEFAULT);

                data.put("image",bitmapString);
            }
            genref.push().setValue(data,this);
            progressDialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSIONS_REQUEST_CODE:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //user allow
                    showChooser();
                }
                return;
        }
    }

    private void showChooser(){
        //gallery select intent
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);

        //shot with camera intent
        String filename = System.currentTimeMillis() +".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,filename);
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");

        pictureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,pictureUri);

        //set gallery intent .call createChooser()
        Intent chooserIntent = Intent.createChooser(galleryIntent,"画像を取得");


        //EXTRA_INITIAL_INTENT add camera shot intent
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent[]{cameraIntent});
        startActivityForResult(chooserIntent,CHOOSER_REQUEST_CODE);

    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        progressDialog.dismiss();

        if(databaseError == null){
            finish();
        }else {
            Snackbar.make(findViewById(android.R.id.content),"投稿に失敗しました",Snackbar.LENGTH_LONG).show();
        }
    }
}
