package domain.hackathon.personal_assistant;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GoogleSearch extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth auth;
    String search;
    String searchurl = "https://personalassistant-ec554.appspot.com/recognize/";
    private String TAG = GoogleSearch.class.getSimpleName();
    String finishedurlstring;
    HashMap<String, String> searchhash;
    private recAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerView reclist;
    List<String> searchlist;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.gtoolbar);
        setSupportActionBar(toolbar);
        Homescreen_nav.whichlayout = "google";
        reclist = (RecyclerView) findViewById(R.id.gReclist);
        searchlist = new ArrayList<String>();


        searchhash = new HashMap<>();

        final Intent intent = getIntent();
        if (intent.hasExtra("gsearch"))
            search = intent.getStringExtra("gsearch");
        else
            search = "search_for_people";
        finishedurlstring = searchurl + search + "/key" + "/Google";

        getGoogleJson();


        mAdapter = new recAdapter(searchlist);
        mLayoutManager = new LinearLayoutManager(this);
        reclist.setLayoutManager(mLayoutManager);
        reclist.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        reclist.setItemAnimator(new DefaultItemAnimator());
        reclist.setAdapter(mAdapter);
        reclist.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(GoogleSearch.this, new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View ChildView ;

                ChildView = rv.findChildViewUnder(e.getX(), e.getY());

                if(ChildView != null && gestureDetector.onTouchEvent(e)) {
                    int pos;

                    pos = rv.getChildAdapterPosition(ChildView);
                    pos = pos + (pos + 1) * 2;

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(searchlist.get(pos)));
                    startActivity(browserIntent);
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.gdrawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.gnav_view);
        navigationView.setNavigationItemSelectedListener(this);
        auth = FirebaseAuth.getInstance();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.gdrawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homescreen_nav, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.maps) {
            startActivity(new Intent(GoogleSearch.this, MapsActivity.class));
        } else if (id == R.id.youtube) {
            startActivity(new Intent(GoogleSearch.this, Youtube.class));
        } else if (id == R.id.banking) {

        } else if (id == R.id.food) {

        } else if (id == R.id.googleSearch) {

        } else if (id == R.id.scheduling) {

        } else if (id == R.id.settings) {

        } else if (id == R.id.about) {

        } else if (id == R.id.logout) {
            auth.signOut();
            finish();
            startActivity(new Intent(GoogleSearch.this, MainActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.gdrawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void getGoogleJson()
    {
        Jsonparserweather hand = new Jsonparserweather();

        // Making a request to url and getting response
        hand.makeServiceCall(finishedurlstring);
        while (Jsonparserweather.isdoneconn != true) ;

        try {
            Thread.sleep(1000);
        } catch (Exception C) {
            C.printStackTrace();
        }
        String jsonStr = Jsonparserweather.response;


        Log.e(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                JSONArray jsonArray = jsonObj.getJSONArray("results");
                searchhash.clear();

                for(int i=0; i< jsonArray.length(); i++)
                {
                    JSONObject inside = jsonArray.getJSONObject(i);
                    String title = inside.getString("title");
                    String snippet = inside.getString("snippet");
                    String url = inside.getString("url");
                    searchlist.add(title);
                    searchlist.add(snippet);
                    searchlist.add(url);


                }


            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Couldn't get json from server.",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });

        }
    }
}
