package com.rohanharrison.notes;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import org.json.JSONObject;

import java.util.HashMap;

public class Body extends AppCompatActivity {

    private postNote mPostNoteTask = null;
    private updateNote mUpdateNoteTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("contentBody");
            String id = extras.getString("contentID");
            //The key argument here must match that used in the other activity
            if (id != null) {
                EditText mText = (EditText) findViewById(R.id.content);
                mText.setText(value);
            }

        }

    }

    @Override
    public void onBackPressed() {
        EditText mText = (EditText) findViewById(R.id.content);
        String content = mText.getText().toString();

        if (content.length() > 0) {
            new AlertDialog.Builder(this).setIcon(R.drawable.ic_warning_black_48dp).setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    }).setNegativeButton("No", null).show();
        } else {
            //onBackPressed();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_body, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            EditText mText = (EditText) findViewById(R.id.content);
            String content = mText.getText().toString();
            String title = mText.getText().toString();

            String email = SaveSharedPreference.getUserName(Body.this);

            Bundle extras = getIntent().getExtras();
            String postNum = null;
            if (extras != null) {
                postNum = extras.getString("contentID");
            }

            if (postNum != null) {
                mUpdateNoteTask = (updateNote) new updateNote().execute(postNum, content);
            } else {
                mPostNoteTask = (postNote) new postNote().execute(email, content);

        }

            //mPostNoteTask = (postNote) new postNote().execute(email, content);
        }

        return super.onOptionsItemSelected(item);
    }

    class postNote extends AsyncTask<String, String, Boolean> {
        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        private static final String LOGIN_URL = "http://rohanharrison.com/notes/android/androidPostNote.php";

        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";


        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(Body.this);
            pDialog.setMessage("Saving Note...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args) {

            //String name = SaveSharedPreference.getUserName(Body.this);

            final AutoCompleteTextView loginInput = (AutoCompleteTextView) findViewById(R.id.email);
            String message = "";
            try {

                HashMap<String, String> params = new HashMap<>();
                //params.put(name, args[0]);
                if (args[0].length() > 0) {
                    params.put("name", args[0]);
                } else {
                    params.put("name", "null");
                }
                params.put("body", args[1]);

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

                if (json.getInt(TAG_SUCCESS) == 1) {
                    Log.d("JSON result", json.toString());
                    Intent intent = new Intent(Body.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    System.exit(0);
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    class updateNote extends AsyncTask<String, String, Boolean> {
        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        private static final String LOGIN_URL = "http://rohanharrison.com/notes/android/androidUpdateNote.php";

        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";


        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(Body.this);
            pDialog.setMessage("Updating Note...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args) {

            final AutoCompleteTextView loginInput = (AutoCompleteTextView) findViewById(R.id.email);
            String message = "";
            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("postNum", args[0]);
                params.put("body", args[1]);

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

                if (json.getInt(TAG_SUCCESS) == 1) {
                    Log.d("JSON result", json.toString());
                    Intent intent = new Intent(Body.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    System.exit(0);
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }



}
