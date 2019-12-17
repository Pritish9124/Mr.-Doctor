package io.ibnus.mrdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.ibnus.mrdoctor.Common.Common;
import io.paperdb.Paper;


public class Chat extends AppCompatActivity {
    LinearLayout layout,chat_lin;
    RelativeLayout layout_2;
    ImageView sendButton,close,imageView2;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;

     ImageButton chat_cam;

    String downloadUrl;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 10;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;


    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Window window = this.getWindow();

        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Paper.init(this);
        layout =  findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);
        chat_cam = findViewById(R.id.chat_cam);
        close = findViewById(R.id.close);
        imageView2 = findViewById(R.id.imageview2);
        chat_lin = findViewById(R.id.chat_lin);

        Firebase.setAndroidContext(this);
        if (Paper.book().read(Common.DOCTOR_KEY) != null)
        {
            reference1 = new Firebase("https://mrdoctor-cbd32.firebaseio.com/users/"+UserDetails.chatWith+"/messages/" + UserDetails.username);
            reference2 = new Firebase("https://mrdoctor-cbd32.firebaseio.com/doctors/"+UserDetails.username+"/messages/" + UserDetails.chatWith);

            getName("https://mrdoctor-cbd32.firebaseio.com/users/"+UserDetails.chatWith+".json");

        }
        else
        {
            reference1 = new Firebase("https://mrdoctor-cbd32.firebaseio.com/doctors/"+UserDetails.chatWith+"/messages/" + UserDetails.username);
            reference2 = new Firebase("https://mrdoctor-cbd32.firebaseio.com/users/"+UserDetails.username+"/messages/" + UserDetails.chatWith);


            getName("https://mrdoctor-cbd32.firebaseio.com/doctors/"+UserDetails.chatWith+".json");

        }



        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat_lin.setVisibility(View.VISIBLE);
                close.setVisibility(View.GONE);
                imageView2.setVisibility(View.GONE);
            }
        });


        chat_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");



                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);

                Log.e("kkk",map.toString());
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(userName.equals(UserDetails.username)){
                    addMessageBox( message, 1);
                }
                else{
                    addMessageBox(message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getName(String url)
    {


        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {

                try {
                    JSONObject jsonObject = new JSONObject(s);

                    toolbar.setTitle(jsonObject.getString("name"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("res",s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);

    }

    public void addMessageBox(String message, int type){


        boolean isValid = URLUtil.isValidUrl(message);
        TextView textView = new TextView(Chat.this);
        ImageView imageView = new ImageView(Chat.this);



        if (isValid)
        {
            Glide.with(getApplicationContext()).load(message).into(imageView);
            Glide.with(getApplicationContext()).load(message).into(imageView2);

            Log.e("Glide",message);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(500, 500);
            lp2.weight = 1.0f;

            if(type == 1) {
                lp2.gravity = Gravity.LEFT;
               imageView.setBackgroundResource(R.drawable.my_message);
                lp2.setMargins(0,0,0,50);
                // textView.setTextSize();
                imageView.setPadding(30,25,30,25);
            }
            else{
                lp2.gravity = Gravity.RIGHT;
                lp2.setMargins(0,0,0,50);
                imageView.setBackgroundResource(R.drawable.their_message);
                imageView.setPadding(30,25,30,25);

            }
           // imageView.setLayoutParams(lp2);

            imageView.setLayoutParams(new ViewGroup.LayoutParams(500,
                    500));
            layout.addView(imageView);
            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.fullScroll(scrollView.FOCUS_DOWN);
                }
            });


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chat_lin.setVisibility(View.GONE);
                    close.setVisibility(View.VISIBLE);
                    imageView2.setVisibility(View.VISIBLE);
                }
            });

        }
        else
        {

            if (!isValid)
            {
                textView.setText(message);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp2.weight = 1.0f;

                if(type == 1) {
                    lp2.gravity = Gravity.LEFT;
                    textView.setBackgroundResource(R.drawable.my_message);

                    lp2.setMargins(0,0,0,10);

                    textView.setTextColor(Color.WHITE);
                    // textView.setTextSize();

                    textView.setPadding(30,25,30,25);
                }
                else{
                    lp2.gravity = Gravity.RIGHT;
                    lp2.setMargins(0,0,0,10);
                    textView.setBackgroundResource(R.drawable.their_message);
                    textView.setTextColor(Color.BLACK);
                    textView.setPadding(30,25,30,25);

                }
                textView.setLayoutParams(lp2);
                layout.addView(textView);
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(scrollView.FOCUS_DOWN);
                    }
                });
            }


        }



    }




    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //imageView.setImageBitmap(bitmap);

                uploadImage();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                      downloadUrl = uri.toString();
                                    Log.e("Downloaded URL",downloadUrl);
                                    Toast.makeText(Chat.this,downloadUrl , Toast.LENGTH_SHORT).show();


                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("message", downloadUrl);
                                    map.put("user", UserDetails.username);
                                    reference1.push().setValue(map);
                                    reference2.push().setValue(map);
                                    messageArea.setText("");
                                }
                            });




                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Chat.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

}