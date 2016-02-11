package com.example.steven.sjtu_lib_v1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.example.steven.sjtu_lib_v1.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by steven on 2016/2/7.
 */
public class Search_activity extends AppCompatActivity{
    @Bind(R.id.book_name)EditText et;
    @Bind(R.id.search_button)Button search_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.search_button) void jump_to_search(){
        String bookname=et.getText().toString();
        Intent intent=new Intent();
        intent.setClass(Search_activity.this,MainActivity.class);
        intent.putExtra("bookname",bookname);
        startActivity(intent);

    }
}
