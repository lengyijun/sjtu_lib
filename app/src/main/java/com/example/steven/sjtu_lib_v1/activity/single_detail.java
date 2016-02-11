package com.example.steven.sjtu_lib_v1.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import com.example.steven.sjtu_lib_v1.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by steven on 2016/2/11.
 */
public class Single_detail extends AppCompatActivity{
    @Bind(R.id.detail)TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_info);
        ButterKnife.bind(this);

        String detail_html=get_html_from_intent();
        tv.setText(Html.fromHtml(detail_html));
    }

    private String get_html_from_intent() {
        String detail_html=getIntent().getExtras().getString("detail");
        return detail_html;
    }


}
