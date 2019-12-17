package io.ibnus.mrdoctor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.ibnus.mrdoctor.Model.DoctorsData;

public class MyAdapter  extends BaseAdapter {

    private List<DoctorsData> mylist;
    private Context context;


    public MyAdapter(Context context, List<DoctorsData> mylist){
        this.context = context;
        this.mylist = mylist;

    }


    @Override
    public int getCount() {
        return mylist.size();
    }

    @Override
    public Object getItem(int position) {
        return mylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.list_layout, null);//set layout for displaying items

        TextView text = (TextView) view.findViewById(R.id.text_name);
        Button avtar = view.findViewById(R.id.avtar);
        text.setText(mylist.get(position).getDoctor_name());

        String first_letter = String.valueOf(mylist.get(position).getDoctor_name().charAt(0));


        avtar.setText(first_letter);


        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetails.chatWith = mylist.get(position).doctor_num;
                context.startActivity(new Intent(context, Chat.class));
            }
        });

        return view;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public boolean isEnabled(int arg0)
    {
        return true;
    }

}