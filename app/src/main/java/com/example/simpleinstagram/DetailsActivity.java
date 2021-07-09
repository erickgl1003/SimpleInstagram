package com.example.simpleinstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.simpleinstagram.models.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Date;


public class DetailsActivity extends AppCompatActivity {

    private TextView tvUsername;
    private ImageView ivImage;
    private TextView tvDescription;
    private TextView tvTime;
    private ImageView ivProfile;

    Post post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tvUsername = findViewById(R.id.tvUsername);
        ivImage = findViewById(R.id.ivImage);
        tvDescription = findViewById(R.id.tvDescription);
        tvTime = findViewById(R.id.tvTime);
        ivProfile = findViewById(R.id.ivProfile);

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post"));

        tvDescription.setText(post.getDescription());
        tvUsername.setText(post.getUser().getUsername());
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(ivImage);
        }
        Date createdAt = post.getCreatedAt();
        String timeAgo = Post.calculateTimeAgo(createdAt);
        tvTime.setText(timeAgo);

        ParseUser currUser = ParseUser.getCurrentUser();

        ParseFile image2 = (ParseFile) currUser.getParseFile("pp");
        Log.i("Profile Fragment", "image value " + image2);
        if (image2 != null) {
            Glide.with(this).load(image2.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivProfile);
        }
    }
}