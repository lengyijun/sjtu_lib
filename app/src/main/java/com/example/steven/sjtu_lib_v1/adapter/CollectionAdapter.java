package com.example.steven.sjtu_lib_v1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.steven.sjtu_lib_v1.CollectionBook;
import com.example.steven.sjtu_lib_v1.R;

import java.util.ArrayList;

/**
 * Created by steven on 2016/3/10.
 */
public class CollectionAdapter extends ArrayAdapter<CollectionBook>{

    public CollectionAdapter(Context context, int resource, ArrayList<CollectionBook> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CollectionBook book=getItem(position);

        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_white_text,null);
        }
        TextView tv= (TextView) convertView.findViewById(R.id.text1);
        tv.setText(book.getShortName());
        return convertView;
    }

}