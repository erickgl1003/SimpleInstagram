package com.example.simpleinstagram;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.simpleinstagram.models.Post;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends FeedFragment {

    GridAdapter adapter;
    protected LinearLayout llProfile;
    protected ImageView ivPp;
    protected TextView tvUsername;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rvPosts = view.findViewById(R.id.rvPosts);
        llProfile = view.findViewById(R.id.llProfile);
        ivPp = view.findViewById(R.id.ivPp);
        tvUsername = view.findViewById(R.id.tvUsername);

        llProfile.setVisibility(View.VISIBLE);
        ParseUser currUser = ParseUser.getCurrentUser();
        Log.i(TAG, "currUser id = " + currUser.getObjectId());

        tvUsername.setText(currUser.getUsername());

        ParseFile image = (ParseFile) currUser.getParseFile("pp");
        Log.i("Profile Fragment", "image value " + image);
        if (image != null) {
            Glide.with(this).load(image.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivPp);
        }



        allPosts = new ArrayList<>();
        adapter = new GridAdapter(getActivity(), allPosts);


        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new GridLayoutManager(getActivity(),3));
        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // query posts from Parstagram
        queryPosts();

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                adapter.clear();
                queryPosts();
                adapter.addAll(allPosts);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

    @Override
    protected void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);

        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // save received posts to list and notify adapter of new data
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }


}
