package com.duthufabien.muzei.montreal;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;

import java.util.Random;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;


public class MontrealArtSource extends RemoteMuzeiArtSource {

    private static final String TAG = "MontrealArtSource";
    private static final String SOURCE_NAME = "MontrealArtSource";
    private static final int ROTATE_SUCCESS_TIME_MILLIS = 7 * 24 * 60 * 60 * 1000; // rotate every week
    private static final int PHOTOS_NUMBER = 109;
    private static final Random sRandom = new Random();

    public MontrealArtSource() {
        super(SOURCE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUserCommands(BUILTIN_COMMAND_ID_NEXT_ARTWORK);
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        Log.d(TAG, String.format("onTryUpdate(%s)", reason));

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("X-Parse-Application-Id", getString(R.string.parse_applicationId));
                request.addHeader("X-Parse-REST-API-Key", getString(R.string.parse_clientKey));
            }
        };
        RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(ParseAPIService.API_URL)
            .setRequestInterceptor(requestInterceptor)
            .build();

        ParseAPIService service = restAdapter.create(ParseAPIService.class);
        ParseAPIService.ParseResponse response = null;

        try {
            response = service.getPhotothequeArchive(sRandom.nextInt(PHOTOS_NUMBER));
        } catch (RetrofitError e) {
            Log.d(TAG, e.getResponse().getStatus()+" "+ e.getResponse().getReason()+" "+e.getResponse().getUrl());
        }

        if (response == null || response.results == null || response.results.size() == 0) {
            scheduleUpdate(System.currentTimeMillis() + ROTATE_SUCCESS_TIME_MILLIS);
        } else {
            Log.d(TAG, "response: " + response.results);
            publishArtwork(parseObjectToArtwork(response.results.get(0)));
            scheduleUpdate(System.currentTimeMillis() + ROTATE_SUCCESS_TIME_MILLIS);
        }
    }

    private Artwork parseObjectToArtwork(ParseAPIService.PhototequeArchive photo) {
        return new Artwork.Builder()
                .imageUri(Uri.parse(photo.jpgFileUrl))
                .title(photo.Titre)
                .token(photo.objectId)
                .byline(getByline(photo.Description,
                        photo.Date))
                .viewIntent(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(photo.jpgFileUrl)))
                .build();
    }

    private String getByline(String description, String date) {
        if ("S/O".equals(description)) {
            return date;
        }
        return description + "\n" + date;
    }

}
