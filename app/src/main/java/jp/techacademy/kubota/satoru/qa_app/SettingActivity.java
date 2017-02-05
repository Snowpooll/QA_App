package jp.techacademy.kubota.satoru.qa_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class SettingActivity extends AppCompatActivity {

    DatabaseReference mDatabaseReference;
    private EditText nameText;
    private EditText preferenceNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //get of name is preference
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String name =sp.getString(Const.NameKey,"");
        preferenceNameText =(EditText)findViewById(R.id.nameText);
        preferenceNameText.setText(name);


        mDatabaseReference = FirebaseDatabase.getInstance().getReference();


        //UI setting
        setTitle("設定");
        nameText =(EditText)findViewById(R.id.nameText);
        Button changeButton =(Button)findViewById(R.id.changeButton);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if keybord show is close
                InputMethodManager im =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                //login user save
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user == null){
                    //not login
                    Snackbar.make(view,"ログインしていません",Snackbar.LENGTH_LONG).show();
                    return;
                }

                //change name is saved Firebase
                String name = nameText.getText().toString();
                DatabaseReference userRef =mDatabaseReference.child(Const.UserPath).child(user.getUid());
                Map<String ,String> data = new HashMap<String, String>();
                data.put("name",name);
                userRef.setValue(data);

                //change name is saved Preference
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor =sp.edit();
                editor.putString(Const.NameKey,name);
                editor.commit();

                Snackbar.make(view,"表示名を変更しました",Snackbar.LENGTH_LONG).show();
            }
        });

        Button logoutButton = (Button)findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                nameText.setText("");
                Snackbar.make(view,"ログアウトしました",Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
