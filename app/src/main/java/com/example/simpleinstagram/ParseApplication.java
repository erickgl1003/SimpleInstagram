package com.example.simpleinstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(Post.class);

        //ParseObject.registerSubclass(Post.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("77sb2llikDksdwA3nL7OKPJdeYs0eGUpZ0qAr1DJ")
                .clientKey("9nTJBeOfcMLMS5d93NUpLlaB4MUAshsjgxJC1D0W")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
