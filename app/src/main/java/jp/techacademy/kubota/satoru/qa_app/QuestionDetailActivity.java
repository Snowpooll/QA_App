package jp.techacademy.kubota.satoru.qa_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.lang.UCharacter;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class QuestionDetailActivity extends AppCompatActivity implements View.OnClickListener,DatabaseReference.CompletionListener{

    private ListView listView;
    private Question mQuestion;
    private QuestionDetailListAdapter adapter;

    private DatabaseReference AnswerRef;

    //favorite add
    private boolean isFavorite;
    private String favoritekey;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

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

        //favorite save
        isFavorite = extrals.getBoolean("favoriteflag");
        favoritekey = extrals.getString("favoritekey");

        setTitle(mQuestion.getmTitle());


        //listview preparation
        listView = (ListView)findViewById(R.id.listview);
        adapter = new QuestionDetailListAdapter(this,mQuestion);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //favorite
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("");

        Button favoriteButton = (Button)findViewById(R.id.favorite);

        //firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser == null){
            favoriteButton.setVisibility(View.INVISIBLE);
        }else {
            if(isFavorite){
                favoriteButton.setBackgroundResource(R.drawable.btn_pressed);
                favoriteButton.setText("お気に入り解除");
            }else {
                favoriteButton.setBackgroundResource(R.drawable.btn);
                favoriteButton.setText("お気に入り登録");
            }
        }
        favoriteButton.setOnClickListener(this);
        favoriteButton.setVisibility(View.VISIBLE);


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

    @Override
    public void onClick(View view) {
        if(firebaseUser != null){
            if(isFavorite){
                isFavorite = false;
                Button favoriteButton = (Button)view.findViewById(R.id.favorite);
                favoriteButton.setBackgroundResource(R.drawable.btn);
                favoriteButton.setText("お気に入り登録");

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                if(firebaseUser !=null){
                    DatabaseReference favoriteRef  = databaseReference.child(Const.FavoritePath)
                            .child(firebaseUser.getUid())
                            .child(favoritekey);
                    favoriteRef.removeValue(this);
                    progressDialog.setMessage("お気に入りから解除中");
                    progressDialog.show();
                }

            }else {
                isFavorite=true;
                Button favoriteButton = (Button)view.findViewById(R.id.favorite);
                favoriteButton.setBackgroundResource(R.drawable.btn_pressed);
                favoriteButton.setText("お気に入りから解除");

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                if(firebaseUser !=null){
                    DatabaseReference favoriteRef = databaseReference.child(Const.FavoritePath).child(firebaseUser.getUid());
                    Map<String,String> data = new HashMap<String, String>();

                    data.put("favoritequestionid",mQuestion.getmQuestionUid());
                    favoriteRef.push().setValue(data,this);

                    progressDialog.setMessage("お気に入り登録中");
                    progressDialog.show();
                }
            }
        }

    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        progressDialog.dismiss();

        if(databaseError == null){

        }else {
            if(isFavorite){
                Snackbar.make(findViewById(android.R.id.content),"お気に入り解除失敗",Snackbar.LENGTH_LONG).show();
            }else {
                Snackbar.make(findViewById(android.R.id.content),"お気に入り登録失敗",Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
