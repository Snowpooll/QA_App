package jp.techacademy.kubota.satoru.qa_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private int mGenre =0;

    private DatabaseReference  databaseReference;
    private DatabaseReference genreRef;
    private ListView listView;
    private ArrayList<Question> questionArrayList;
    private QuestionsListAdapter adapter;

    private ChildEventListener eventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap)dataSnapshot.getValue();
            String title = (String)map.get("title");
            String body = (String)map.get("body");
            String name = (String)map.get("name");
            String uid = (String)map.get("uid");
            String imageString =(String)map.get("image");
            Bitmap image = null;
            byte[] bytes;

            if(imageString !=null){
                BitmapFactory.Options options = new BitmapFactory.Options();
                bytes = Base64.decode(imageString,Base64.DEFAULT);
            }else {
                bytes = new byte[0];
            }

            ArrayList<Answer> answerArrayList = new ArrayList<Answer>();
            HashMap answerMap = (HashMap)map.get("answers");

            if(answerMap !=null){
                for(Object key :answerMap.keySet()){
                    HashMap tmp =(HashMap)answerMap.get((String)key);
                    String answerBody = (String)tmp.get("body");
                    String answerName = (String)tmp.get("name");
                    String answerUid = (String)tmp.get("uid");
                    Answer answer = new Answer(answerBody,answerName,answerUid,(String)key);
                    answerArrayList.add(answer);
                }
            }

            Question question = new Question(title,body,name, uid, dataSnapshot.getKey(),mGenre,bytes,answerArrayList);
            questionArrayList.add(question);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap)dataSnapshot.getValue();

            //find change question
            for (Question question : questionArrayList){
                if(dataSnapshot.getKey().equals(question.getmQuestionUid())){
                    question.getmAnswerArrayList().clear();
                    HashMap answerMap =(HashMap)map.get("answers");
                    if(answerMap !=null){
                        for (Object key : answerMap.keySet()){
                            HashMap tmp = (HashMap)answerMap.get((String)key);
                            String answerBody = (String)tmp.get("body");
                            String answerName =(String)tmp.get("name");
                            String answerUid = (String)tmp.get("uid");
                            Answer answer = new Answer(answerBody,answerName,answerUid,(String)key);
                            question.getmAnswerArrayList().add(answer);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

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
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // not selected genre
                if(mGenre ==0){
                    Snackbar.make(view,"ジャンルを選択してください",Snackbar.LENGTH_LONG).show();
                    return;
                }

                //login user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                //not login
                if(user == null){
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                }else {
                    //bring up the genre and start question screen
                    Intent intent = new Intent(getApplicationContext(),QuestionSendActivity.class);
                    intent.putExtra("genre",mGenre);
                    startActivity(intent);
                }
;
            }
        });

        //navigation drawer setting
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.app_name,R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.nav_hoby){
                    toolbar.setTitle("趣味");
                    mGenre=1;
                }else if(id == R.id.nav_life){
                    toolbar.setTitle("生活");
                    mGenre=2;
                }else if(id == R.id.nav_health){
                    toolbar.setTitle("健康");
                    mGenre=3;
                }else if(id == R.id.nav_computer){
                    toolbar.setTitle("コンピュータ");
                    mGenre=4;
                }

                DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawerlayout);
                drawer.closeDrawer(GravityCompat.START);

                //question list clear,adapter is listview set
                questionArrayList.clear();
                adapter.setQuestionArrayList(questionArrayList);
                listView.setAdapter(adapter);

                //add listener is selected genre
                if(genreRef !=null){
                    genreRef.removeEventListener(eventListener);
                }
                genreRef = databaseReference.child(Const.ContentsPath).child(String.valueOf(mGenre));
                genreRef.addChildEventListener(eventListener);

                return true;
            }
        });

        //firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();


        //listview preparation
        listView = (ListView)findViewById(R.id.listview);
        adapter = new QuestionsListAdapter(this);
        questionArrayList = new ArrayList<Question>();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            //setting screen
            Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
