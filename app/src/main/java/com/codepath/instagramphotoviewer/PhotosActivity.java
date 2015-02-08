package com.codepath.instagramphotoviewer;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotosActivity extends ActionBarActivity {
    private static final String CLIENT_ID="001464b81b8b4d4c8c7b886ea9bbedc1";
    private static final String TAG = PhotosActivity.class.getSimpleName();
    private ArrayList<InstagramPhoto> photos;
    private ListView lvPhotos;
    private PhotosAdapter photosAdapter;
    private static final int COMMENTS_COUNT = 2;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        photos = new ArrayList<>();
        photosAdapter = new PhotosAdapter(this, photos);
        lvPhotos = (ListView) findViewById(R.id.lvphotos);
        lvPhotos.setAdapter(photosAdapter);
        fetchPopularPhotos();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPopularPhotos();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void fetchPopularPhotos() {
        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if(swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                        photos = new ArrayList<>();
                    }
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        InstagramPhoto photo = new InstagramPhoto();
                        if (jsonObject.optJSONObject("caption") != null){
                            photo.caption = jsonObject.getJSONObject("caption").getString("text");
                            photo.createdTime = Long.parseLong(jsonObject.getJSONObject("caption").getString("created_time"));
                        }else{
                            photo.caption = "";
                            photo.createdTime = 0L;
                        }
                        if (jsonObject.optJSONObject("user") != null){
                            photo.userName = jsonObject.getJSONObject("user").getString("username");
                            photo.avatarUrl = jsonObject.getJSONObject("user").getString("profile_picture");
                        } else{
                            photo.userName = "";
                            photo.avatarUrl = "";
                        }
                        if (jsonObject.optJSONObject("likes") != null){
                            photo.likeCount = Integer.parseInt(jsonObject.getJSONObject("likes").getString("count"));

                        }else{
                            photo.likeCount = 0;
                        }
                        if (jsonObject.optJSONObject("images") != null){
                            photo.imageUrl = jsonObject.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                            photo.imageHeight = Integer.parseInt(jsonObject.getJSONObject("images").getJSONObject("standard_resolution").getString("height"));

                        }else{
                            photo.imageUrl = "";
                            photo.imageHeight = 0;
                        }
                        if (jsonObject.optJSONObject("comments") != null){
                            photo.comments = new ArrayList<>();
                            JSONArray comments = jsonObject.getJSONObject("comments").getJSONArray("data");
                            for(int j = 0; j < (comments.length() < COMMENTS_COUNT ? comments.length()  : COMMENTS_COUNT); j++) {
                                JSONObject commentObject = comments.getJSONObject(j);
                                PhotoComment comment = new PhotoComment();
                                comment.senderName = commentObject.getJSONObject("from").getString("username");
                                comment.comment = commentObject.getString("text");
                                photo.comments.add(comment);
                            }
                        }else{
                            photo.comments = null;
                        }
                         photos.add(photo);
                    }

                    photosAdapter.UpdateDataList(photos);
                    photosAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Parse data failed: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //log the error
                Log.e(TAG, "Get images failed: " + responseString);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
