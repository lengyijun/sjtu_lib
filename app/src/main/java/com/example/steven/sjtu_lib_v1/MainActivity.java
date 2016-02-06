package com.example.steven.sjtu_lib_v1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import com.paging.listview.PagingListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.paging_list_view) PagingListView plistview;
    String url="http://ourex.lib.sjtu.edu.cn/primo_library/libweb/action/search.do?fn=search&tab=default_tab&vid=chinese&scp.scps=scope%3A%28SJT%29%2Cscope%3A%28sjtu_metadata%29%2Cscope%3A%28sjtu_sfx%29%2Cscope%3A%28sjtulibzw%29%2Cscope%3A%28sjtulibxw%29%2CDuxiuBook&vl%28freeText0%29=%E4%B8%AD%E5%9B%BD";
    String url1="http://ourex.lib.sjtu.edu.cn/primo_library/libweb/action/search.do?fn=search&tab=default_tab&vid=chinese&scp.scps=scope%3A%28SJT%29%2Cscope%3A%28sjtu_metadata%29%2Cscope%3A%28sjtu_sfx%29%2Cscope%3A%28sjtulibzw%29%2Cscope%3A%28sjtulibxw%29%2CDuxiuBook&vl%28freeText0%29=git";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        try {
            get_html(url1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void get_html(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request=new Request.Builder()
                .url(url)
                .build();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = response.body().string();
                Document document = Jsoup.parse(html);
                Elements elements = document.getElementsByClass("EXLSummary");
                final List<String> data = new ArrayList<String>();
                for (int i = 0; i < elements.size(); i++) {
//                    先清理空的element
                    if (elements.get(i).getElementsMatchingText("馆藏信息").isEmpty()) {
                        elements.remove(i);
                    }
                }

                for (Element i : elements) {
                    final String book_name = i.getElementsByClass("EXLResultTitle").text().toString();
                    data.add(book_name);
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        plistview.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.list_white_text, data));
//                        plistview.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_single_choice, data));
                    }
                });
            }
        });
    }

    @OnItemClick(R.id.paging_list_view) void onItemSelected(int position){
        Book_detail bookDetail=new Book_detail();
        bookDetail.show(getFragmentManager(),"book");
//        Toast.makeText(this,position+"",Toast.LENGTH_SHORT).show();
    }
}
