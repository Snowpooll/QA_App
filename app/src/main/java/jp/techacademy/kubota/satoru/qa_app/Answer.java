package jp.techacademy.kubota.satoru.qa_app;

import java.io.Serializable;

/**
 * Created by snowpool on 17/02/06.
 */

public class Answer implements Serializable{
    private String mBody;
    private String mName;
    private String mUid;
    private String mAnswerUid;

    public Answer(String body, String name, String uid, String answeruid){
        mBody = body;
        mName = name;
        mUid = uid;
        mAnswerUid = answeruid;
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

    public String getmAnswerUid() {
        return mAnswerUid;
    }
}
