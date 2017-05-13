package com.example.android.demoforapi;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    final static String GITHUB_BASE_URL =
            "https://api.github.com/search/repositories";

    final static String PARAM_QUERY = "q";
    final static String PARAM_SORT = "sort";
    final static String sortBy = "stars";

    private EditText mSearchBoxEditText;
    private TextView mSearch;
    private TextView mSearchResultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = (EditText) findViewById(R.id.git_hub_query);
        mSearch = (TextView) findViewById(R.id.search_text);
        mSearchResultsTextView= (TextView) findViewById(R.id.search_result);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            makeGithubSearchQuery();
            }
        });

    }
    private void makeGithubSearchQuery() {
        String githubQuery = mSearchBoxEditText.getText().toString();
        URL githubSearchUrl = buildUrl(githubQuery);
        new MainAsynch().execute(githubSearchUrl);
    }

    public static URL buildUrl(String githubSearchQuery) {
        Uri builtUri = Uri.parse(GITHUB_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, githubSearchQuery)
                .appendQueryParameter(PARAM_SORT, sortBy)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
    private class MainAsynch extends AsyncTask<URL,Void,String>{

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String githubSearchResults = null;
            try {
                githubSearchResults = getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubSearchResults;
        }
        @Override
        protected void onPostExecute(String githubSearchResults) {

            if (githubSearchResults != null && !githubSearchResults.equals("")) {
                mSearchResultsTextView.setText(githubSearchResults);
            }
        }
    }
}
