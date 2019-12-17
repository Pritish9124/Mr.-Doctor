package io.ibnus.mrdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import io.ibnus.mrdoctor.Common.Common;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {


    String user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Window window = this.getWindow();

        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        Paper.init(this);




        ItsSplashTime();



    }

    private void ItsSplashTime()
    {

        if (Paper.book().read(Common.USER_KEY)!=null)
        {


            user = Paper.book().read(Common.USER_KEY).toString();
            pass = Paper.book().read(Common.USER_PASS).toString();

            if(user.equals("")){
                // username.setError("can't be blank");
            }
            else if(pass.equals("")){
                // password.setError("can't be blank");
            }
            else{
                String url = "https://mrdoctor-cbd32.firebaseio.com/users.json";
                final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Loading...");
                pd.show();

                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        if(s.equals("null")){
                            Toast.makeText(MainActivity.this, "user not found", Toast.LENGTH_LONG).show();
                        }
                        else{
                            try {
                                JSONObject obj = new JSONObject(s);

                                if(!obj.has(user)){
                                    Toast.makeText(MainActivity.this, "user not found", Toast.LENGTH_LONG).show();
                                }
                                else if(obj.getJSONObject(user).getString("password").equals(pass)){
                                    UserDetails.username = user;
                                    UserDetails.password = pass;
                                    startActivity(new Intent(MainActivity.this, DoctorsNav.class));
                                    finish();
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "incorrect password", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        pd.dismiss();
                    }
                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("" + volleyError);
                        pd.dismiss();
                    }
                });

                RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
                rQueue.add(request);
            }



        }
        else if (Paper.book().read(Common.DOCTOR_KEY)!=null)
        {

            user = Paper.book().read(Common.DOCTOR_KEY).toString();
            pass = Paper.book().read(Common.DOCTOR_PASS).toString();

            if(user.equals("")){
                //   username.setError("can't be blank");
            }
            else if(pass.equals("")){
                //   password.setError("can't be blank");
            }
            else{
                String url = "https://mrdoctor-cbd32.firebaseio.com/doctors.json";
                final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Loading...");
                pd.show();

                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        if(s.equals("null")){
                            Toast.makeText(MainActivity.this, "Doctor not found", Toast.LENGTH_LONG).show();
                        }
                        else{
                            try {
                                JSONObject obj = new JSONObject(s);

                                if(!obj.has(user)){
                                    Toast.makeText(MainActivity.this, "Doctor not found", Toast.LENGTH_LONG).show();
                                }
                                else if(obj.getJSONObject(user).getString("password").equals(pass)){
                                    UserDetails.username = user;
                                    UserDetails.password = pass;

                                    Paper.book().write(Common.DOCTOR_KEY,user);
                                    Paper.book().write(Common.DOCTOR_PASS,pass);


                                    startActivity(new Intent(MainActivity.this, Users.class));
                                    finish();
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "incorrect password", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        pd.dismiss();
                    }
                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("" + volleyError);
                        pd.dismiss();
                    }
                });

                RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
                rQueue.add(request);
            }


        }
        else
        {


            Thread background = new Thread() {
                public void run() {
                    try {


                        // Thread will sleep for 2 seconds
                        sleep(2 * 500);

                        // After 2 seconds redirect to another intent

                        startActivity(new Intent(MainActivity.this,Login.class));
                        finish();


                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            };
            // start thread
            background.start();

        }

    }
}
