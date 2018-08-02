package com.remainder.events.unotifier.Helpers;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    public static String BASE_URL="https://term-api.udacity.com/api";

    public static Uri getCohort(String nanoKey)
    {
        Uri uri=Uri.parse(BASE_URL).buildUpon()
                .appendPath("cohorts")
                .appendPath("open")
                .appendQueryParameter("nanodegree_key",nanoKey)
                .build();
        return uri;
    }
    public static String getTheResponse(URL url) throws IOException {
        HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
        try {
            InputStream in = httpURLConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            httpURLConnection.disconnect();
        }
    }
}
