package jp.techacademy.kubota.satoru.qa_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FavLIstQuestion extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private DatabaseReference questionRef;
    private ListView listView;
    private ArrayList<Question> questionArrayList;
    private ArrayList<Favorite> favoriteArrayList;
    private FavoriteListQuestionAdapter adapter;

    private ChildEventListener eventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap)dataSnapshot.getValue();

            //favorite only
            for(Favorite favorite: favoriteArrayList){
                if(dataSnapshot.getKey().equals(favorite.getFavoriteQuestionId())){
                    String title = (String)map.get("title");
                    String body = (String)map.get("body");
                    String name = (String)map.get("name");
                    String uid = (String)map.get("uid");
                    String imageString = (String)map.get("image");
                    Bitmap image =null;
                    byte[] bytes;
                    if(imageString != null){
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        bytes = Base64.decode(imageString,Base64.DEFAULT);
                    }else {
                        bytes = new byte[0];
                    }
                    ArrayList<Answer> answerArrayList = new ArrayList<Answer>();
                    HashMap answerMap = (HashMap)map.get("answers");
                    if(answerMap != null){
                        for (Object key :  answerMap.keySet()){
                            HashMap tmp = (HashMap)answerMap.get((String) key);
                            String answerBody =(String)tmp.get("body");
                            String answerName =(String)tmp.get("name");
                            String answerUid =(String)tmp.get("uid");
                            Answer answer = new Answer(answerBody,answerName,answerUid,(String) key);
                            answerArrayList.add(answer);
                        }
                    }
                    Question question = new Question(
                            title,
                            body,
                            name,
                            uid,
                            dataSnapshot.getKey(),
                            0,
                            bytes,
                            answerArrayList
                    );
                    questionArrayList.add(question);
                    adapter.notifyDataSetChanged();
                }

            }
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
        setContentView(R.layout.activity_fav_list_question);

        //pass question object save
        Bundle extras = getIntent().getExtras();
        favoriteArrayList = (ArrayList<Favorite>)extras.getSerializable("favoriteList");

        //firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();


        //listview
        listView = (ListView)findViewById(R.id.listview);
        adapter = new FavoriteListQuestionAdapter(this);
        questionArrayList = new ArrayList<Question>();
        adapter.notifyDataSetChanged();

        adapter.setFavoriteQuestionArrayList(questionArrayList);
        listView.setAdapter(adapter);

        //selected genre register listener
        if(questionRef != null){
            questionRef.removeEventListener(eventListener);
        }

        questionRef = databaseReference.child(Const.ContentsPath).child("1");
        questionRef.addChildEventListener(eventListener);
        questionRef = databaseReference.child(Const.ContentsPath).child("2");
        questionRef.addChildEventListener(eventListener);
        questionRef = databaseReference.child(Const.ContentsPath).child("3");
        questionRef.addChildEventListener(eventListener);
        questionRef = databaseReference.child(Const.ContentsPath).child("4");
        questionRef.addChildEventListener(eventListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //pass question instance, start detail screen
                Intent intent = new Intent(getApplicationContext(),QuestionDetailActivity.class);
                intent.putExtra("question",questionArrayList.get(position));

                //send isFavorite?
                boolean isFavorite = false;
                String favoriteKey ="";
                if(favoriteArrayList != null){
                    for(Favorite favorite: favoriteArrayList){
                        if(questionArrayList.get(position).getmUid().equals(favorite.getFavoriteQuestionId())){
                            isFavorite = true;
                            favoriteKey = favorite.getFavoriteKey();
                            break;

                        }
                    }
                }
                intent.putExtra("favoriteFlag",isFavorite);
                intent.putExtra("favoritekey",favoriteKey);
                startActivity(intent);

            }
        });
    }
}
