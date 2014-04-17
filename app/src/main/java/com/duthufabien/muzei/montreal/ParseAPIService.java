package com.duthufabien.muzei.montreal;


import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

public interface ParseAPIService {

    public static String API_URL = "https://api.parse.com/1";

    @GET("/classes/PhotothequeArchives?limit=1")
    ParseResponse getPhotothequeArchive(@Query("skip") int skip);

    static class ParseResponse {
        List<PhototequeArchive> results;
    }

    static class PhototequeArchive {
        String Titre;
        String Description;
        String Date;
        String jpgFileUrl;
        String objectId;

    }

}
