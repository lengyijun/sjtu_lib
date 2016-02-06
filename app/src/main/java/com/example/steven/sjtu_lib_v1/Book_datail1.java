package com.example.steven.sjtu_lib_v1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * Created by steven on 2016/2/6.
 */
public class Book_datail1 extends DialogFragment {
    String url="http://ourex.lib.sjtu.edu.cn/primo_library/libweb/action/display.do?tabs=locationsTab&gathStatTab=true&ct=display&fn=search&doc=sjtulibzw000842374&indx=2&recIds=sjtulibzw000842374&recIdxs=1&elementId=1&renderMode=poppedOut&displayMode=full&frbrVersion=3&dscnt=0&scp.scps=scope%3A%28SJT%29%2Cscope%3A%28sjtu_metadata%29%2Cscope%3A%28sjtu_sfx%29%2Cscope%3A%28sjtulibzw%29%2Cscope%3A%28sjtulibxw%29&frbg=&tab=default_tab&dstmp=1454721170888&srt=rank&mode=Basic&&dum=true&tb=t&vl(1UIStartWith0)=contains&vl(10225382UI0)=any&vl(50541600UI1)=all_items&vl(freeText0)=git%20version%20control&vid=chinese";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.book_status, null);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        List<String> data = null;
        try {
            data = get_status();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, data));

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("hello");
        return builder.create();
    }

    private List<String> get_status() throws IOException {
        OkHttpClient client=new OkHttpClient();
//        Request request = new Request().Builder()
//                        .url(url)
//                        .build();
        List<String> data=new ArrayList<String>();
        Document doc= Jsoup.connect(url).get();
        Elements elements=doc.getElementsByClass("EXLResultStatusNotAvailable");
        for(Element i : elements){
           data.add(i.text());
        }
        return data;
    }
}
