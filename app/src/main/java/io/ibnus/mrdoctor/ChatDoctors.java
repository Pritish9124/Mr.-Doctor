package io.ibnus.mrdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import io.ibnus.mrdoctor.Common.Common;
import io.ibnus.mrdoctor.Model.DoctorsData;
import io.paperdb.Paper;

public class ChatDoctors extends AppCompatActivity {
    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    ArrayList<DoctorsData> chat_user_list = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_doctors);
        Window window = this.getWindow();

        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Paper.init(this);
        usersList = (ListView)findViewById(R.id.usersList);
        noUsersText = (TextView)findViewById(R.id.noUsersText);

        pd = new ProgressDialog(ChatDoctors.this);
        pd.setMessage("Loading...");
        pd.show();

        String url = "https://mrdoctor-cbd32.firebaseio.com/users/"+Paper.book().read(Common.USER_KEY).toString()+"/messages.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ChatDoctors.this);
        rQueue.add(request);


    }

    public void doOnSuccess(String s){
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";
            Log.e("key",key);
            while(i.hasNext()){
                key = i.next().toString();


                    al.add(key);


                totalUsers++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(totalUsers <1){
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        }
        else{




            String chat_name_url =  "https://mrdoctor-cbd32.firebaseio.com/doctors.json";
            StringRequest request = new StringRequest(Request.Method.GET, chat_name_url, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {

                    try {
                        JSONObject obj = new JSONObject(s);

                        Iterator i = obj.keys();
                        String key = "";

                        while(i.hasNext()){
                            key = i.next().toString();

                            Log.e("key",key);

                            if (al.contains(key))
                            {
                                JSONObject value = obj.getJSONObject(key);
                                chat_user_list.add(new DoctorsData(value.getString("name"),key));

                            }

                            noUsersText.setVisibility(View.GONE);
                            usersList.setVisibility(View.VISIBLE);

                            MyAdapter adapter = new MyAdapter(ChatDoctors.this, chat_user_list);

//and finally, set it into your listview:

                            usersList.setAdapter(adapter);
                            pd.dismiss();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError);
                }
            });

            RequestQueue rQueue = Volley.newRequestQueue(ChatDoctors.this);
            rQueue.add(request);








        }


    }
}