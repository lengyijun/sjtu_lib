package com.example.steven.sjtu_lib_v1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.steven.sjtu_lib_v1.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by steven on 2016/2/7.
 */
public class Search_activity extends AppCompatActivity{
    @Bind(R.id.book_name)EditText et;
    @Bind(R.id.search_button)Button search_button;
    @Bind(R.id.radio_button)RadioGroup radioGroup;

    String base_url="http://ourex.lib.sjtu.edu.cn/primo_library/libweb/action/search." +
            "do?fn=search&tab=default_tab&vid=chinese&scp.scps=scope%3A%28SJT%29%2Csc" +
            "ope%3A%28sjtu_metadata%29%2Cscope%3A%28sjtu_sfx%29%2Cscope%3A%28sjtulib" +
            "zw%29%2Cscope%3A%28sjtulibxw%29%2CDuxiuBook&vl%28freeText0%29=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.search_button) void jump_to_search(){
        String bookname=et.getText().toString();
        final String url=base_url+bookname;

        final int choosed_id=radioGroup.getCheckedRadioButtonId();
        if (choosed_id==-1 || choosed_id ==R.id.all_lib){
            Intent intent=new Intent();
            intent.setClass(Search_activity.this,MainActivity.class);
            intent.putExtra("url",url);
            startActivity(intent);
        }else {
            OkHttpUtils.get()
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e) {

                        }

                        @Override
                        public void onResponse(String response) {
                            Document doc= Jsoup.parse(response);
                            String url_to_intent = null;
                            switch (choosed_id){
                                case R.id.new_lib:
                                     url_to_intent= doc.getElementsMatchingText("主馆图书").attr("href");
                                     break;
                                case R.id.baotu:
                                    url_to_intent= doc.getElementsMatchingText("包玉刚图书馆图书").attr("href");
                                    break;
                                case R.id.subscribing:
                                    url_to_intent= doc.getElementsMatchingText("正在订购").attr("href");
                                    break;
                                case R.id.xuhui:
                                    url_to_intent= doc.getElementsMatchingText("徐汇社科馆").attr("href");
                                    break;
                                case R.id.literature:
                                    url_to_intent= doc.getElementsMatchingText("人文学院分馆").attr("href");
                                    break;

                            }
                            Intent intent=new Intent();
                            intent.setClass(Search_activity.this,MainActivity.class);
                            intent.putExtra("url",url_to_intent);
                            startActivity(intent);

                        }
                    });

        }


    }
}
