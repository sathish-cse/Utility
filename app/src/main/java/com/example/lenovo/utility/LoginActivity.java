package com.example.lenovo.utility;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {

    // Use to route calls back to the Facebook SDK and your registered callbacks.
    // You should call it from the initiating activity or fragments onActivityResult call.
    private CallbackManager callbackManager;

    // Use this class Graph API requests. It shows the user id, and the accepted and denied permissions.
    private AccessTokenTracker accessTokenTracker;

    // This class has basic information about person logged in.
    private ProfileTracker profileTracker;

    private TwitterLoginButton twitterButton;

    // Twitter Api key and secret key
    private static final String TWITTER_KEY = " DC5rfDC1gbycD1vRxMNcpa6Tq";
    private static final String TWITTER_SECRET = "pQQ0gQpCLwxbmnTLldv0znhuHyB27OVbxM0c1DBG12RkINvvR3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        // Twitter authentication
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        // Callback manager creation
        callbackManager = CallbackManager.Factory.create();

        // Start Tracking for AccessTokenTracker
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };
        accessTokenTracker.startTracking();

        // Start Tracking for ProfileTracking
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

            }
        };
        profileTracker.startTracking();


        // Login Button Initialiation and facebook callback
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        FacebookCallback<LoginResult> callback=new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Profile profile=Profile.getCurrentProfile();

                nextActivity(profile);

                Toast.makeText(getApplicationContext(),"Logging in..",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        };

        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager,callback);

        // for get the keyhash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.lenovo.utility", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        // For Twitter
        setUpTwitterButton();

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Facebook Login

        Profile profile = Profile.getCurrentProfile();
        nextActivity(profile);
    }

    @Override
    protected  void onPause()
    {
       super.onPause();
    }

    @Override
    protected  void onStop()
    {
        super.onStop();

        //Facebook login Close

        accessTokenTracker.stopTracking();
        profileTracker.startTracking();
    }

    @Override
    protected void onActivityResult(int requestCode,int responseCode,Intent intent)
    {

        //Facebook login Result

        callbackManager.onActivityResult(requestCode,responseCode,intent);

        twitterButton.onActivityResult(requestCode, responseCode, intent);
    }


    private void nextActivity(Profile profile)
    {
        // Pass Profile Parameter to MainActivity

        if(profile != null)
        {
            Intent main = new Intent(LoginActivity.this,MainActivity.class);
            main.putExtra("name",profile.getFirstName());
            main.putExtra("surname",profile.getLastName());
            main.putExtra("imageUrl",profile.getProfilePictureUri(200,200).toString());

            startActivity(main);
        }
    }


    private void setUpTwitterButton()
    {
        twitterButton = (TwitterLoginButton) findViewById(R.id.twitter_button);

        twitterButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.app_name), Toast.LENGTH_SHORT).show();

                setUpViewsForTweetComposer();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.app_name), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpViewsForTweetComposer() {
        TweetComposer.Builder builder = new TweetComposer.Builder(this).text("Just setting up Twitter!");
        builder.show();
    }

}
