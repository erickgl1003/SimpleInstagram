package com.example.simpleinstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Visibility;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";
    public static final int UPLOAD_REQUEST = 50;

    private EditText etUsername;
    private EditText etPassword;
    private EditText etPasswordConfirm;
    private Button btnSignup;
    private Button btnPhoto;
    private TextView tvLog;

    Bitmap bitmap = null;
    File imagT = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        btnSignup = findViewById(R.id.btnSignup);
        btnPhoto = findViewById(R.id.btnPhoto);
        tvLog = findViewById(R.id.tvLog);


        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadPhoto();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i(TAG,"onClick Sign in Button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if(!password.equals(etPasswordConfirm.getText().toString())){
                    Toast.makeText(SignupActivity.this,"Passwords don't match!",Toast.LENGTH_SHORT);
                    return;
                }
                Log.i(TAG,username + ", " + password);
                signUserTest(username, password, imagT);
            }
        });

        tvLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void onUploadPhoto(){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, UPLOAD_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == UPLOAD_REQUEST && resultCode == RESULT_OK && data != null){
            Uri photoUri = data.getData();
            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            }catch (FileNotFoundException e){
                e.printStackTrace();
                Log.e(TAG, "File not found");
            } catch (IOException e){
                Log.d(TAG, e.getLocalizedMessage());
            }
            File testDir = getApplicationContext().getFilesDir();
            imagT = new File(testDir, "photo.jpg");
            OutputStream os;
            try {
                os = new FileOutputStream(imagT);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
                btnSignup.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }
        }
    }

    private void signUserTest(String username, String password, File file){
        Log.i(TAG, "Attempting to signup user " + username);
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.d(TAG, "Signed up successfully");
                    ParseUser curr = ParseUser.getCurrentUser();
                    savePhoto(curr, new ParseFile(file));

                }
            }
        });
    }

    private void savePhoto(ParseUser curr, ParseFile parseFile) {
        curr.put("pp", parseFile);
        curr.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.d(TAG, "Saved photo");
                    goMainAcitivty();
                }
                else{
                    Log.d(TAG, "Error saving photo:" + e.getMessage() + "\n" + e.getCause());
                }
            }
        });
    }


    private void goMainAcitivty() {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }
}