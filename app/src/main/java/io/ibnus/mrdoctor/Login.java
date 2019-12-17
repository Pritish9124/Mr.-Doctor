package io.ibnus.mrdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.ibnus.mrdoctor.Common.Common;
import io.paperdb.Paper;

public class Login extends AppCompatActivity {
    Button registerUser;
    EditText username, password;
    Button loginButton;
    TextView doctor_login;
    String user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merge_login_view);


        Window window = this.getWindow();

        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));


        Paper.init(this);

        registerUser = findViewById(R.id.registerButton);
        username = (EditText)findViewById(R.id.emailEditText);
        password = (EditText)findViewById(R.id.passwordEditText);
        loginButton = (Button)findViewById(R.id.loginButton);

        doctor_login = findViewById(R.id.doctor_login);
        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });



        doctor_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, DoctorLogin.class));
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();


                if (isValidPhoneNumber(user))

                {
                    if(user.equals("")){
                        username.setError("can't be blank");

                        Toast.makeText(Login.this, "can't be blank", Toast.LENGTH_SHORT).show();
                    }
                    else if(pass.equals("")){
                        password.setError("can't be blank");
                        Toast.makeText(Login.this, "can't be blank", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String url = "https://mrdoctor-cbd32.firebaseio.com/users.json";
                        final ProgressDialog pd = new ProgressDialog(Login.this);
                        pd.setMessage("Loading...");
                        pd.show();

                        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                            @Override
                            public void onResponse(String s) {
                                if(s.equals("null")){
                                    Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    try {
                                        JSONObject obj = new JSONObject(s);

                                        if(!obj.has(user)){
                                            Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                                        }
                                        else if(obj.getJSONObject(user).getString("password").equals(pass)){
                                            UserDetails.username = user;
                                            UserDetails.password = pass;

                                            Paper.book().write(Common.USER_KEY,user);
                                            Paper.book().write(Common.USER_PASS,pass);
                                            startActivity(new Intent(Login.this, DoctorsNav.class));
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(Login.this, "incorrect password", Toast.LENGTH_LONG).show();
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

                        RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                        rQueue.add(request);
                    }

                }
                else
                {
                    username.setError("Invalid Phone Number!");
                    Toast.makeText(Login.this, "Invalid Phone Number!", Toast.LENGTH_SHORT).show();
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



    public static boolean validateLetters(String txt) {

        String regx = "^[\\p{L} .'-]+$";
        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(txt);
        return matcher.find();

    }

}
