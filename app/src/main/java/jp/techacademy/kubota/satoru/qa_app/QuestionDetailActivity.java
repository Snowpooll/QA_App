package jp.techacademy.kubota.satoru.qa_app;

import android.content.Intent;
import android.icu.lang.UCharacter;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class QuestionDetailActivity extends AppCompatActivity {

    private ListView listView;
    private Question mQuestion;
    private QuestionDetailListAdapter adapter;

    private DatabaseReference AnswerRef;

    private ChildEventListener eventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map =(HashMap)dataSnapshot.getValue();

            String answerUid = dataSnapshot.getKey();

            for(Answer answer : mQuestion.getmAnswerArrayList()){
                //if same uid is does nothing
                if(answerUid.equals(answer.getmAnswerUid())){
                    return;
                }
            }

            String body = (String)map.get("body");
            String name = (String)map.get("name");
            String uid = (String)map.get("uid");

            Answer answer = new Answer(body,name,uid,answerUid);
            mQuestion.getmAnswerArrayList().add(answer);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        //question object save
        Bundle extrals = getIntent().getExtras();
        mQuestion = (Question)extrals.get("question");

        setTitle(mQuestion.getmTitle());


        //listview preparation
        listView = (ListView)findViewById(R.id.listview);
        adapter = new QuestionDetailListAdapter(this,mQuestion);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        FloatingActionButton fab  =( FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //login username save
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user == null){
                    //if not login ,go to login screen
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                }else {
                    //pass question and start answer screeen
                    Intent intent = new Intent(getApplicationContext(),AnswerSendActivity.class);
                    intent.putExtra("question",mQuestion);
                    startActivity(intent);

                }
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        AnswerRef = databaseReference.child(Const.ContentsPath).child(String.valueOf(mQuestion.getmGenre())).child(mQuestion.getmQuestionUid())
                .child(Const.AnswersPath);
        AnswerRef.addChildEventListener(eventListener);
    }
}
