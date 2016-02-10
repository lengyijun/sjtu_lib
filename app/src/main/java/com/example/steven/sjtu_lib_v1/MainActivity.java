package com.example.steven.sjtu_lib_v1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.paging.listview.PagingListView;
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
import butterknife.OnItemClick;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.paging_list_view) PagingListView plistview;
    @Bind(R.id.textView)TextView tv;

    String base_url="http://ourex.lib.sjtu.edu.cn/primo_library/libweb/action/search.do?fn=search&tab=default_tab&vid=chinese&scp.scps=scope%3A%28SJT%29%2Cscope%3A%28sjtu_metadata%29%2Cscope%3A%28sjtu_sfx%29%2Cscope%3A%28sjtulibzw%29%2Cscope%3A%28sjtulibxw%29%2CDuxiuBook&vl%28freeText0%29=";
    String url;

    List<String> data=new ArrayList<String>();
    static public List<String> NextUrls=new ArrayList<String>();
    List<Element> book_elements=new ArrayList<Element>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        String bookname=getIntent().getExtras().getString("bookname");
        this.url=base_url+bookname;

        get_list_from_url(url);

        plistview.setAdapter(new BookItemAdapter(this,0,book_elements));
        plistview.setHasMoreItems(true);
        plistview.setPagingableListener(new PagingListView.Pagingable() {

            @Override
            public void onLoadMoreItems() {
                new NextAsyncTask().execute();
            }
        });
    }

    private void get_list_from_url(String url) {
        OkHttpUtils .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Toast.makeText(getApplicationContext(), "fail to connect", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        Document doc = Jsoup.parse(response);

                        new Refrsh_next_url().execute(doc);
                        Elements elements = doc.getElementsByClass("EXLSummary");
                        for (Element i : elements) {
                            if (!i.getElementsMatchingText("馆藏信息").isEmpty()) {
                                book_elements.add(i);
                            }
                        }
                    }
                });

    }

    @OnItemClick(R.id.paging_list_view) void onItemSelected(int position){
        Book_detail bookDetail=new Book_detail(book_elements.get(position));
        bookDetail.show(getFragmentManager(), "book");
    }

    private class NextAsyncTask extends AsyncTask<Void,Void,Void> {
        int saved_postion;

        @Override
        protected void onPostExecute(Void Void) {
            plistview.onFinishLoading(true,book_elements);
            plistview.setSelection(saved_postion);
            super.onPostExecute(Void);
        }

        @Override
        protected Void doInBackground(Void... params) {
            synchronized (this){
                while (NextUrls.size() == 0 || book_elements.size() == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            get_list_from_url(NextUrls.get(0));
            NextUrls.clear();

            return null;
        }

        @Override
        protected void onPreExecute() {
//            saved_postion=wholeBooks.size()-1;
            saved_postion=plistview.getFirstVisiblePosition();
            super.onPreExecute();
        }
    }

    private class Refrsh_next_url extends AsyncTask<Object,Void,Elements>{

        @Override
        protected void onPostExecute(Elements elements) {
            if(elements.size()!=0){
                MainActivity.this.NextUrls.add(elements.first().attr("href"));
            }
            super.onPostExecute(elements);
        }

        @Override
        protected Elements doInBackground(Object... params) {
            Element come_in= (Element) params[0];
            Elements elements=come_in .getElementsByAttributeValue("title", "下一页");
            return elements;
        }
    }

}
