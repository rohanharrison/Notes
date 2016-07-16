package com.rohanharrison.notes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView mListView;
    ArrayList idarraylist = new ArrayList();
    private getNotes mAuthTaskgetNotes = null;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Initialize swipe to refresh view
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String email = SaveSharedPreference.getUserName(MainActivity.this);
                String password = SaveSharedPreference.getPassword(MainActivity.this);
                mAuthTaskgetNotes = (getNotes) new getNotes().execute(email, password);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        String email = SaveSharedPreference.getUserName(MainActivity.this);
        String password = SaveSharedPreference.getPassword(MainActivity.this);
        mAuthTaskgetNotes = (getNotes) new getNotes().execute(email, password);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Body.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_signout) {
            SaveSharedPreference.clearUserName(MainActivity.this);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    class getNotes extends AsyncTask<String, String, JSONObject> {

        private static final String LOGIN_URL = "http://107.170.28.29/notes/android/androidGetNoteList.php";
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        private static final String TAG_ID = "id";
        private static final String TAG_TITLE = "title";
        private static final String TAG_COUNT = "noteCount";
        JSONParser jsonParser = new JSONParser();
        int noteCT = 0;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Fetching Notes...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("name", args[0]);

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "GET", params);


                if (json != null) {

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
            ArrayList al = new ArrayList();


            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (json != null) {

                try {
                    success = json.getInt(TAG_SUCCESS);
                    message = json.getString(TAG_MESSAGE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (json != null) {

                final ListView lv = (ListView) findViewById(R.id.notesList);
                lv.setClickable(true);

                try {
                    String noteCount = json.getString(TAG_COUNT);
                    noteCT = Integer.parseInt(noteCount);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    for (int i = 0; i < noteCT; i++) {

                        String ids = json.getString(TAG_ID);
                        String[] idsparts = ids.split(",");
                        idarraylist.add(idsparts[i].replaceAll("[^a-zA-Z0-9]", ""));


                        String titles = json.getString(TAG_TITLE);
                        titles = titles.replace("\\n", "\n");
                        String[] parts = titles.split(",");
                        al.add(parts[i].replaceAll("[^a-zA-Z0-9]", ""));
                    }

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {

                            new loadNote().execute(idarraylist.get(position).toString(), "null");

                        }

                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final List<String> notes_list = new ArrayList<String>(al);
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, notes_list);
                lv.setAdapter(arrayAdapter);


            } else {
                Log.d("Failure", message);
            }
        }
    }

    class loadNote extends AsyncTask<String, String, JSONObject> {

        private static final String LOGIN_URL = "http://107.170.28.29/notes/android/androidGetNote.php";
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        private static final String TAG_ID = "id";
        private static final String TAG_TITLE = "title";
        private static final String TAG_BODY = "body";
        JSONParser jsonParser = new JSONParser();
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Fetching Data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("id", args[0]);


                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "GET", params);


                if (json != null) {


                    String contentBody = json.getString(TAG_BODY);
                    contentBody = contentBody.substring(2, contentBody.length() - 2);

                    contentBody = contentBody.replace("\\n", "\n");


                    Intent intent = new Intent(MainActivity.this, Body.class);
                    intent.putExtra("contentID", args[0]);
                    intent.putExtra("contentBody", contentBody);
                    startActivity(intent);

                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


    }
}


