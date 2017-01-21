package com.example.lenovo.utility;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    private ShareDialog sharedialog;
    private Button Logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Facebook SDK initialization
        FacebookSdk.sdkInitialize(this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedialog = new ShareDialog(this);

        FloatingActionButton fab=(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent content = new ShareLinkContent.Builder().build();
                sharedialog.show(content);
            }
        });

        Bundle bundle=getIntent().getExtras();
        String name=bundle.getString("name");
        String surname=bundle.getString("surname");
        String imageurl=bundle.getString("imageUrl");

        TextView nameView = (TextView)findViewById(R.id.nameAndSurname);
        nameView.setText("" + name + " " + surname);

        Button logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });

        new DownloadImage((ImageView)findViewById(R.id.profileImage),MainActivity.this).execute(imageurl);

    }

    public class DownloadImage extends AsyncTask<String,Void,Bitmap>
    {
        ImageView profileImage;
        private ProgressDialog dialog;

        public DownloadImage(ImageView profileImage,MainActivity activity)
        {
            dialog = new ProgressDialog(activity);
            this.profileImage=profileImage;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Image Loading..");
            dialog.show();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap imgBitMap= null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                imgBitMap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return imgBitMap;
        }

        protected void onPostExecute(Bitmap result)
        {
            profileImage.setImageBitmap(result);
            dialog.dismiss();
        }

    }

    @Override
    public void onBackPressed() {

        Toast.makeText(getApplicationContext(),"Press Logout Button for close or Logout..",Toast.LENGTH_LONG).show();
    }
}
