package jp.techacademy.kubota.satoru.qa_app;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by snowpool on 17/02/05.
 */

public class Question implements Serializable {
    private String mTitle;
    private String mBody;
    private String mName;
    private String mUid;
    private String mQuestionUid;
    private int mGenre;
    private byte[] mBitmapArray;
    private ArrayList<Answer> mAnswerArrayList;


    public String getmTitle() {
        return mTitle;
    }

    public String getmBody() {
        return mBody;
    }

    public String getmName() {
        return mName;
    }

    public String getmUid() {
        return mUid;
    }

    public String getmQuestionUid() {
        return mQuestionUid;
    }

    public int getmGenre() {
        return mGenre;
    }

    public byte[] getmBitmapArray() {
        return mBitmapArray;
    }

    public ArrayList<Answer> getmAnswerArrayList() {
        return mAnswerArrayList;
    }

    public Question(String title,String body,String name, String uid, String questionUid, int genre,byte[] bytes,ArrayList<Answer> answers){
        mTitle =title;
        mBody = body;
        mName = name;
        mUid = uid;
        mQuestionUid = questionUid;
        mGenre = genre;
        mBitmapArray = bytes.clone();
        mAnswerArrayList = answers;
    }
}
