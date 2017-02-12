package jp.techacademy.kubota.satoru.qa_app;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AnswerSendActivity extends AppCompatActivity implements View.OnClickListener,DatabaseReference.CompletionListener {
    private EditText answerEdit;
    private Question mQuestion;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_send);

        //pass question obuject save
        Bundle extras  = getIntent().getExtras();
        mQuestion = (Question)extras.get("question");

        //ui setting
        answerEdit = (EditText)findViewById(R.id.answerEdit);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("投稿中.....");

        Button sendButton = (Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        //keybord close
        InputMethodManager im =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference answerRef = databaseReference.child(Const.ContentsPath).child(String.valueOf(mQuestion.getmGenre()))
                .child(mQuestion.getmQuestionUid()).child(Const.AnswersPath);

        Map<String,String> data = new HashMap<String, String>();

        //uid
        data.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        //name is get prefence
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sp.getString(Const.NameKey,"");
        data.put("name",name);

        //get answer
        String answer = answerEdit.getText().toString();

        if(answer.length() ==0){
            //show error
            Snackbar.make(view,"回答を入力してください",Snackbar.LENGTH_LONG).show();
            return;
        }
        data.put("body",answer);

        progressDialog.show();
        answerRef.push().setValue(data,this);

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
