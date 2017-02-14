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
 * Created by snowpool on 17/02/13.
 */

public class FavoriteListQuestionAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater=null;
    private ArrayList<Question> mQuestionArrayList;

    public FavoriteListQuestionAdapter(Context context){
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mQuestionArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mQuestionArrayList.get(position);
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
        titleText.setText(mQuestionArrayList.get(position).getmTitle());

        TextView nameText = (TextView)convertView.findViewById(R.id.nameText);
        nameText.setText(mQuestionArrayList.get(position).getmName());

        TextView resText = (TextView)convertView.findViewById(R.id.resText);
        int resNum = mQuestionArrayList.get(position).getmAnswerArrayList().size();
        resText.setText(String.valueOf(resNum));

        byte[] bytes = mQuestionArrayList.get(position).getmBitmapArray();
        if(bytes.length !=0){
            Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length).copy(Bitmap.Config.ARGB_8888, true);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView);
            imageView.setImageBitmap(image);
        }

        return convertView;
    }

    public void setFavoriteQuestionArrayList(ArrayList<Question> questionArrayList) {
        mQuestionArrayList = questionArrayList;
    }
}
