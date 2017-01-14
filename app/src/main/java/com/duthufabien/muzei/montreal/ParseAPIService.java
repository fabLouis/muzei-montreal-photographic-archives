package com.duthufabien.muzei.montreal;


import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

interface ParseAPIService {

    @GET("/classes/PhotothequeArchives?limit=1")
    ParseResponse getPhotothequeArchive(@Query("skip") int skip);

    class ParseResponse {
        List<PhototequeArchive> results;
    }

    class PhototequeArchive {
        String Titre;
        String Description;
        String Date;
        String jpgFileUrl;
        String objectId;

    }

}
