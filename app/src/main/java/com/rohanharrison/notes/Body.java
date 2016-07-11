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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body);
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

            mPostNoteTask = (postNote) new postNote().execute(email, content);
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

    class loadNote extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        private static final String LOGIN_URL = "http://rohanharrison.com/notes/android/androidGetNote.php";

        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        private static final String TAG_ID = "id";
        private static final String TAG_TITLE = "title";

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(Body.this);
            pDialog.setMessage("Fetching Data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();
                //params.put(SaveSharedPreference.getUserName(MainActivity.this), args[0]);
                params.put("name", args[0]);
                //params.put("title", args[1]);
                //params.put("password", args[1]);

                //  Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "GET", params);


                if (json != null) {
                    //Log.d("JSON result", json.toString());

                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject json) {

            int success = 0;
            String message = "";


            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (json != null) {
                //Log.d("Success!", message);


            } else {
                Log.d("Failure", message);
            }
        }

    }

}
