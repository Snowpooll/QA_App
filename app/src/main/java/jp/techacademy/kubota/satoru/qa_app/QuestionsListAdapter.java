package jp.techacademy.kubota.satoru.qa_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by snowpool on 17/02/06.
 */

public class QuestionsListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater=null;
    private ArrayList<  Question> questionArrayList;


    public QuestionsListAdapter(Context context){
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return questionArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return questionArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.list_questions,parent,false);
        }
        TextView titleText = (TextView)convertView.findViewById(R.id.titleText);
        titleText.setText(questionArrayList.get(position).getmTitle());

        TextView nameText = (TextView)convertView.findViewById(R.id.nameText);
        nameText.setText(questionArrayList.get(position).getmName());

        TextView resText = (TextView)convertView.findViewById(R.id.resText);
        int resNum = questionArrayList.get(position).getmAnswerArrayList().size();
        resText.setText(String.valueOf(resNum));

        byte[] bytes = questionArrayList.get(position).getmBitmapArray();
        if(bytes.length !=0){
            Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length).copy(Bitmap.Config.ARGB_8888, true);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView);
            imageView.setImageBitmap(image);
        }

        return convertView;
    }

    public void  setQuestionArrayList(ArrayList<Question> QuestionArrayList){
        questionArrayList = QuestionArrayList;
    }
}
