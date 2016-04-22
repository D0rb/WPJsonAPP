package com.example.dor.wpjsonapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private HashMap<Integer, String> Categories = new HashMap<>();
    private ArrayList<Title> Posts = new ArrayList<>();
    private TextView title, content, category, title1, title2;
    private SlidingMenu menu = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSlidingMenu();
        title = (TextView) findViewById(R.id.title);
        content = (TextView) findViewById(R.id.content);
        category = (TextView) findViewById(R.id.category);
        title1 = (TextView) findViewById(R.id.title1);
        title2 = (TextView) findViewById(R.id.title2);;
        new loadCategories().execute("http://ipets.co.il/json/wp-json/wp/v2/categories");

        title1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText(Posts.get(0).title);
                content.setText(Posts.get(0).content);
                category.setText(Posts.get(0).category);
                menu.showContent();
            }
        });
        title2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText(Posts.get(1).title);
                content.setText(Posts.get(1).content);
                category.setText(Posts.get(1).category);
                menu.showContent();
            }
        });
        menu.toggle();
    }

    private class getPosts extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return Stream.downloadSource(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < 2; i++) {

                    Title title = new Title();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONArray _jsonArray = null;
                    title.title = fixTitles(html2text(android.text.Html.fromHtml(jsonObject.getString("title")).toString()));
                    title.content = fixContent(html2text(android.text.Html.fromHtml(jsonObject.getString("content")).toString()));
                    _jsonArray = jsonObject.getJSONArray("categories");
                    title.category = Categories.get(_jsonArray.get(0));
                    Posts.add(title);
                }
                Log.d("Dor", "loading posts finished " + String.valueOf(Posts.size()));
            } catch (JSONException e) {
                Log.d("Dor", "Error " + e.toString());
            }
            super.onPostExecute(s);
        }
    }

    private class loadCategories extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return Stream.downloadSource(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Categories.put(jsonObject.getInt("id"), jsonObject.getString("name"));
                    Log.d("Dor", jsonObject.getString("name"));
                }
                Log.d("Dor", "loading Categories finished " + String.valueOf(Categories.size()));
            } catch (JSONException e) {
                Log.d("Dor", "Error " + e.toString());
            }
            new getPosts().execute("http://ipets.co.il/json/wp-json/wp/v2/posts");
            super.onPostExecute(s);
        }

    }
    public void initSlidingMenu(){
        setTitle("Choose Post");
        menu = new SlidingMenu(getApplicationContext());
        menu.setMode(SlidingMenu.LEFT);
        menu.setShadowDrawable(R.drawable.menu_shadow);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setBehindOffsetRes(R.dimen.slidingmenuoffset);
        menu.setShadowWidthRes(R.dimen.slidingmenuWidth);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.sliding_menu);;
    }

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }
    public String fixTitles(String text) {
        String txt = text;
        String tags[] = {"\"}", "{\"", "rendered", ":", "\""};
        for (int i = 0; i < tags.length; i++) {
            txt = txt.replace(tags[i], "");
        }
        return txt;
    }

    public String fixContent(String text) {
        String txt = text;
        String tags[] = {"\"}", "{\"", "rendered", ":", "\"", "\\", "n"};
        for (int i = 0; i < tags.length; i++) {
            txt = txt.replace(tags[i], "");
        }
        return txt;
    }
}
