package com.rohanharrison.notes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_body, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            EditText mText = (EditText)findViewById(R.id.content);
            String content = mText.getText().toString();
            String title = mText.getText().toString();

           mPostNoteTask = (postNote) new postNote().execute(title, content);
        }

        return super.onOptionsItemSelected(item);
    }

    class postNote extends AsyncTask<String, String, Boolean> {
        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        private static final String LOGIN_URL = "http://rohanharrison.com/message/androidPostNote.php";

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

            String name = SaveSharedPreference.getUserName(Body.this);

            final AutoCompleteTextView loginInput = (AutoCompleteTextView) findViewById(R.id.email);
            String message = "";
            try {

                HashMap<String, String> params = new HashMap<>();
                //params.put(name, args[0]);
                params.put("title", args[0]);
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
