package domain.hackathon.personal_assistant;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;




import com.google.firebase.auth.FirebaseAuth;

import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

public class Homescreen_nav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth auth;
    private boolean isRecording = false;
    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;
    private static String audioFilePath;
    private  boolean recstop = false;


    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {android.Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO},
                    200);
        }



        FloatingActionButton myFab = (FloatingActionButton)  findViewById(R.id.fabnote);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presentActivity(v);
            }
        });

        audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/records/myaudio.3gp";

        FloatingActionButton voice = (FloatingActionButton)  findViewById(R.id.fabvoice);

        voice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!recstop)
                {
                    recordAudio();
                    recstop = true;
                }
                else if (recstop)
                {
                    recstop = false;
                    playAudio();
                }
            }
        });





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        auth = FirebaseAuth.getInstance();


    }

    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, notesActivity.class);
        intent.putExtra(notesActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(notesActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
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
            startActivity(new Intent(Homescreen_nav.this, MapsActivity.class));

        } else if (id == R.id.youtube) {
            startActivity(new Intent(Homescreen_nav.this, Youtube.class));

        } else if (id == R.id.banking) {

        } else if (id == R.id.food) {

        } else if (id == R.id.googleSearch) {

        } else if (id == R.id.scheduling) {

        } else if (id == R.id.settings) {

        } else if (id == R.id.about) {

        } else if (id == R.id.logout){
            auth.signOut();
            startActivity(new Intent(Homescreen_nav.this, MainActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    void recordAudio() {
        isRecording = true;

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaRecorder.start();

        mediaRecorder.stop();
        mediaRecorder.release();

    }
    public void stopAudio (View view)
    {
        if (isRecording)
        {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        } else {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    void playAudio() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}


//package org.norbert;
//
//        import java.io.*;
//
//public class PythonCaller {
//
//    /**
//     * @param args
//     * @throws IOException
//     */
//    public static void main(String[] args) throws IOException {
//// set up the command and parameter
//        String pythonScriptPath = "/home/norbert/python/helloPython.py";
//        String[] cmd = new String[2];
//        cmd[0] = "python"; // check version of installed python: python -V
//        cmd[1] = pythonScriptPath;
//
//// create runtime to execute external command
//        Runtime rt = Runtime.getRuntime();
//        Process pr = rt.exec(cmd);
//
//// retrieve output from python script
//        BufferedReader bfr = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//        String line = "";
//        while((line = bfr.readLine()) != null) {
//// display each output line form python script
//            System.out.println(line);
//        }
//    }
//}