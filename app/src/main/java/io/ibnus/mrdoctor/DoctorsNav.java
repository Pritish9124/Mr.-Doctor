package io.ibnus.mrdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import io.ibnus.mrdoctor.Common.Common;
import io.ibnus.mrdoctor.Model.DoctorsData;
import io.paperdb.Paper;

public class DoctorsNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,BillingProcessor.IBillingHandler {


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    TabItem doctor_tab,chat_tab;


    BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Paper.init(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        bp = new BillingProcessor(this,null,this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        chat_tab = findViewById(R.id.tabItem2);
        doctor_tab = findViewById(R.id.tabItem);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));





    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            final ProgressDialog pd = new ProgressDialog(DoctorsNav.this);
            pd.setMessage("Signing Out...");
            pd.show();
            Paper.book().destroy();
            startActivity(new Intent(this,Login.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();

        //    bp.purchase(this,"android.test.purchase");

        } else if (id == R.id.nav_gallery) {
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            tab.select();

         //   startActivity(new Intent(this,ChatDoctors.class));

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {


            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out Mr. Doctor App.Health Care from home : https://play.google.com/store/apps/details?id=io.ibnus.mrdoctor");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {


        Toast.makeText(this, "Purchase Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(this, "Purchase Failed", Toast.LENGTH_LONG).show();




    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        ListView usersList;
        TextView noUsersText;
        ArrayList<DoctorsData> al = new ArrayList<>();
        ArrayList<String> al2 = new ArrayList<>();
        ArrayList<DoctorsData> chat_user_list = new ArrayList<>();
        int totalUsers = 0;
        ProgressDialog pd;
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tabs, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            usersList = rootView.findViewById(R.id.usersList);
            noUsersText = rootView.findViewById(R.id.noUsersText);

            pd = new ProgressDialog(getContext());
            pd.setMessage("Loading...");
            pd.show();



            if (getArguments().getInt(ARG_SECTION_NUMBER)==1)
            {
                String url = "https://mrdoctor-cbd32.firebaseio.com/doctors.json";

                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        doOnSuccess(s);

                        Log.e("res",s);
                    }
                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("" + volleyError);
                    }
                });

                RequestQueue rQueue = Volley.newRequestQueue(getContext());
                rQueue.add(request);

            }
            if (getArguments().getInt(ARG_SECTION_NUMBER)==2)
            {
                String url = "https://mrdoctor-cbd32.firebaseio.com/users/"+Paper.book().read(Common.USER_KEY).toString()+"/messages.json";

                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        Log.e("chat",s);
                        doOnSuccess2(s);
                    }
                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("" + volleyError);
                    }
                });

                RequestQueue rQueue = Volley.newRequestQueue(getContext());
                rQueue.add(request);

            }






            return rootView;
        }

        public void doOnSuccess(String s){
            try {
                JSONObject obj = new JSONObject(s);

                Iterator i = obj.keys();
                String key = "";

                while(i.hasNext()){
                    key = i.next().toString();

                    Log.e("key",key);

                    JSONObject value = obj.getJSONObject(key);
                        al.add(new DoctorsData(value.getString("name"),key));

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
                noUsersText.setVisibility(View.GONE);
                usersList.setVisibility(View.VISIBLE);

                MyAdapter adapter = new MyAdapter(getContext(), al);

                usersList.setAdapter(adapter);


            }

            pd.dismiss();
        }

        public void doOnSuccess2(String s){
            try {
                JSONObject obj = new JSONObject(s);

                Iterator i = obj.keys();
                String key = "";
                Log.e("key",key);
                while(i.hasNext()){
                    key = i.next().toString();


                    al2.add(key);


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

                                if (al2.contains(key))
                                {
                                    JSONObject value = obj.getJSONObject(key);
                                    chat_user_list.add(new DoctorsData(value.getString("name"),key));

                                }

                                noUsersText.setVisibility(View.GONE);
                                usersList.setVisibility(View.VISIBLE);

                                MyAdapter adapter = new MyAdapter(getContext(), chat_user_list);


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

                RequestQueue rQueue = Volley.newRequestQueue(getContext());
                rQueue.add(request);








            }


        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }



}
