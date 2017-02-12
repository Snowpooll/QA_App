package jp.techacademy.kubota.satoru.qa_app;

import android.content.Context;
import android.content.pm.InstrumentationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by snowpool on 17/02/11.
 */

public class QuestionDetailListAdapter extends BaseAdapter {

    private static final int TYPE_QUESTION =0;
    private static final int TYPE_ANSWER =-1;


    private LayoutInflater layoutInflater=null;
    private Question mQuestion;


    public QuestionDetailListAdapter(Context context,Question question){
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mQuestion = question;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_QUESTION;
        }else {
            return TYPE_ANSWER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return 1 + mQuestion.getmAnswerArrayList().size();
    }

    @Override
    public Object getItem(int position) {
        return mQuestion;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        if (getItemViewType(position) == TYPE_QUESTION){
            if(convertview == null){
                convertview = layoutInflater.inflate(R.layout.list_question_detail,parent,false);
            }
            String body = mQuestion.getmBody();
            String name = mQuestion.getmName();

            TextView bodyText = (TextView)convertview.findViewById(R.id.bodyText);
            bodyText.setText(body);

            TextView nameText = (TextView)convertview.findViewById(R.id.nameText);
            nameText.setText(name);

            byte[] bytes = mQuestion.getmBitmapArray();

            if(bytes.length !=0){
                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length).copy(Bitmap.Config.ARGB_8888,true);
                ImageView imageView = (ImageView)convertview.findViewById(R.id.imageView);
                imageView.setImageBitmap(image);
            }
        }else {
            if(convertview == null ){
                convertview = layoutInflater.inflate(R.layout.list_answer,parent,false);
            }

            Answer answer = mQuestion.getmAnswerArrayList().get(position -1);
            String body =answer.getmBody();
            String name = answer.getmName();

            TextView bodyText = (TextView)convertview.findViewById(R.id.bodyText);
            bodyText.setText(body);

            TextView nameText = (TextView)convertview.findViewById(R.id.nameText);
            nameText.setText(name);
        }
        return convertview;
    }
}
