package com.example.steven.sjtu_lib_v1.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ListView;
import android.widget.TextView;

import com.example.steven.sjtu_lib_v1.R;
import com.example.steven.sjtu_lib_v1.adapter.TableAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Created by steven on 2016/2/11.
 */
public class Single_detail extends AppCompatActivity{
    @Bind(R.id.detail)TextView tv;
    @Bind(R.id.listview_table) ListView lv_table;

    TableAdapter adapter;
    List<Element> table_data=new ArrayList<Element>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_info);
        ButterKnife.bind(this);

        adapter=new TableAdapter(getApplicationContext(),0,table_data);
        lv_table.setAdapter(adapter);

        String detail_html=get_html_from_intent();
        String url=get_url_from_intent();
        tv.setText(Html.fromHtml(detail_html));
        get_table_data(url);
    }

    private String get_html_from_intent() {
        String detail_html=getIntent().getExtras().getString("detail");
        return detail_html;
    }

    public String get_url_from_intent() {
        String url=getIntent().getExtras().getString("url");
        return url;
    }

    public void get_table_data(String url) {
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
                        Elements EXLLocationTableColumn1_eles=doc.getElementsByClass("EXLLocationTableColumn1");
                        for(Element i:EXLLocationTableColumn1_eles){
                            table_data.add(i.parent());
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
