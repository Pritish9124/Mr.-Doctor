package io.ibnus.mrdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import io.ibnus.mrdoctor.Common.Common;
import io.paperdb.Paper;

public class DoctorLogin extends AppCompatActivity {

    EditText username, password;
    Button loginButton;
    String user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_login);
        username = (EditText)findViewById(R.id.emailEditText);
        password = (EditText)findViewById(R.id.passwordEditText);
        loginButton = (Button)findViewById(R.id.loginButton);

        Window window = this.getWindow();

        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        Paper.init(this);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();


                if (isValidPhoneNumber(user))
                {
                    if(user.equals("")){
                        username.setError("can't be blank");
                        Toast.makeText(DoctorLogin.this, "can't be blank", Toast.LENGTH_LONG).show();
                    }
                    else if(pass.equals("")){
                        password.setError("can't be blank");
                        Toast.makeText(DoctorLogin.this, "can't be blank", Toast.LENGTH_LONG).show();
                    }
                    else{
                        String url = "https://mrdoctor-cbd32.firebaseio.com/doctors.json";
                        final ProgressDialog pd = new ProgressDialog(DoctorLogin.this);
                        pd.setMessage("Loading...");
                        pd.show();

                        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                            @Override
                            public void onResponse(String s) {
                                if(s.equals("null")){
                                    Toast.makeText(DoctorLogin.this, "Doctor not found", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    try {
                                        JSONObject obj = new JSONObject(s);

                                        if(!obj.has(user)){
                                            Toast.makeText(DoctorLogin.this, "Doctor not found", Toast.LENGTH_LONG).show();
                                        }
                                        else if(obj.getJSONObject(user).getString("password").equals(pass)){
                                            UserDetails.username = user;
                                            UserDetails.password = pass;

                                            Paper.book().write(Common.DOCTOR_KEY,user);
                                            Paper.book().write(Common.DOCTOR_PASS,pass);


                                            startActivity(new Intent(DoctorLogin.this, Users.class));
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(DoctorLogin.this, "incorrect password", Toast.LENGTH_LONG).show();
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

                        RequestQueue rQueue = Volley.newRequestQueue(DoctorLogin.this);
                        rQueue.add(request);
                    }
                }
                else
                {
                    username.setError("Invalid Phone Number!");
                    Toast.makeText(DoctorLogin.this, "Invalid Phone Number!", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private boolean isValidPhoneNumber(String phone) {
        boolean check=false;
        if(Pattern.matches("^[0-9]*$", phone)) {
            if(phone.length() < 10 || phone.length() > 11) {
                // if(phone.length() != 10) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check=false;
        }
        return check;
    }

}
