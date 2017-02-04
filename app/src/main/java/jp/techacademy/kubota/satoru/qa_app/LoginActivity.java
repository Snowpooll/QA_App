package jp.techacademy.kubota.satoru.qa_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText emailText;
    EditText passwordText;
    EditText nameText;

    ProgressDialog progressDialog;

    //firebase 関連
    FirebaseAuth mAuth;
    OnCompleteListener<AuthResult> mCreateAccountListener;
    OnCompleteListener<AuthResult> mLoginListener;
    DatabaseReference mDatabaseReference;

    //acccount flag
    boolean mIsCreateAccount = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //firebaseAuth get object
        mAuth = FirebaseAuth.getInstance();

        //create account
        mCreateAccountListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //success login
                    String email = emailText.getText().toString();
                    String password = passwordText.getText().toString();
                    login(email,password);
                }else {
                    //failed error show
                    View view = findViewById(android.R.id.content);
                    Snackbar.make(view,"アカウント作成に失敗しました",Snackbar.LENGTH_LONG).show();

                    //hide progress dialog
                    progressDialog.dismiss();
                }
            }
        };

        //login processing
        mLoginListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //success
                    FirebaseUser user = mAuth.getCurrentUser();
                    DatabaseReference userRef = mDatabaseReference.child(Const.UserPath).child(user.getUid());

                    if(mIsCreateAccount){
                        //create account name is saved firebase
                        String name = nameText.getText().toString();
                        Map<String,String> data = new HashMap<String,String>();
                        data.put("name",name);

                        //name is preference saved
                        saveName(name);
                    }else {
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map data =(Map)dataSnapshot.getValue();
                                saveName((String)data.get("name"));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    //hide progress dialog
                    progressDialog.dismiss();

                    //close activity
                    finish();
                }else{
                    //failed error show
                    View view = findViewById(android.R.id.content);
                    Snackbar.make(view,"ログインに失敗しました",Snackbar.LENGTH_LONG).show();
                    //hide progress dialog
                    progressDialog.dismiss();
                }

            }
        };

        //ui setting
        setTitle("ログイン");
        emailText=(EditText)findViewById(R.id.emailText);
        passwordText =(EditText)findViewById(R.id.passwordText);
        nameText=(EditText)findViewById(R.id.nameText);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("処理中....");

        Button createButton =(Button)findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close when the keybord diaplayed
                InputMethodManager im =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                String email =emailText.getText().toString();
                String password =passwordText.getText().toString();
                String name =nameText.getText().toString();

                if(email.length() !=0 && password.length() >= 6 && name.length() !=0){
                    //Save name when login
                    mIsCreateAccount = true;
                    createAccount(email,password);
                }else {
                    //error show
                    Snackbar.make(view,"正しく入力してください",Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    private void createAccount(String email,String password){
        //progress dialog show
        progressDialog.show();

        //create account
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(mCreateAccountListener);
    }

    private void login(String email ,String password){
        //progress dialog show
        progressDialog.show();

        //create account
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(mLoginListener);
    }

    private void saveName(String name){
        //saved Preference
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor =sp.edit();
        editor.putString(Const.NameKey,name);
        editor.commit();
    }
}
