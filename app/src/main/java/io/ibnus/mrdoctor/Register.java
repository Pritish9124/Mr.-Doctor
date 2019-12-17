package io.ibnus.mrdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.ibnus.mrdoctor.Model.User;

public class Register extends AppCompatActivity {
    EditText username, password,name;
    Button registerButton;
    String user, pass,userName;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merge_registration_view);

        username = (EditText)findViewById(R.id.emailEditText);
        password = (EditText)findViewById(R.id.passwordEditText);
        name = findViewById(R.id.namneEditText);
        registerButton = (Button)findViewById(R.id.registerButton);
        login = findViewById(R.id.loginButton);


        Window window = this.getWindow();

        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        Firebase.setAndroidContext(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();
                userName = name.getText().toString();

                if(user.equals("")){
                    username.setError("can't be blank");
                    Toast.makeText(Register.this, "can't be blank", Toast.LENGTH_SHORT).show();
                }
                else if(pass.equals("")){
                        password.setError("can't be blank");
                    Toast.makeText(Register.this, "can't be blank", Toast.LENGTH_SHORT).show();
                    }
                else if(!validateLetters(userName)){
                    name.setError("Please Enter a Valid Name!");
                    Toast.makeText(Register.this, "Please Enter a Valid Name!", Toast.LENGTH_SHORT).show();
                           }
                    else if(!isValidPhoneNumber(user)){
                            username.setError("Invalid Phone Number!");
                    Toast.makeText(Register.this, "Invalid Phone Number!", Toast.LENGTH_SHORT).show();
                        }
                        else if(user.length()<5){
                               // username.setError("at least 5 characters long");
                              username.setError("Invalid Phone Number!");

                            }
                            else if(pass.length()<5){
                                password.setError("at least 5 characters long");
                    Toast.makeText(Register.this, "at least 5 characters long", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                final ProgressDialog pd = new ProgressDialog(Register.this);
                                pd.setMessage("Loading...");
                                pd.show();

                                String url = "https://mrdoctor-cbd32.firebaseio.com/users.json";

                                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                                    @Override
                                    public void onResponse(String s) {
                                        Firebase reference = new Firebase("https://mrdoctor-cbd32.firebaseio.com/users");

                                        if(s.equals("null")) {

                                          //  User model = new User(userName, pass);
                                            reference.child(user).child("name").setValue(userName);
                                            reference.child(user).child("password").setValue(pass);

                                            Toast.makeText(Register.this, "registration successful", Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            try {
                                                JSONObject obj = new JSONObject(s);

                                                if (!obj.has(user)) {
                                                    reference.child(user).child("name").setValue(userName);
                                                    reference.child(user).child("password").setValue(pass);

                                                    Toast.makeText(Register.this, "registration successful", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(Register.this,Login.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(Register.this, "username already exists", Toast.LENGTH_LONG).show();
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
                                        System.out.println("" + volleyError );
                                        pd.dismiss();
                                    }
                                });

                                RequestQueue rQueue = Volley.newRequestQueue(Register.this);
                                rQueue.add(request);
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