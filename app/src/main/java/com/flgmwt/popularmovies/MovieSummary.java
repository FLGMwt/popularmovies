/**
 * Copyright (C) 2016 Ryan Stelly- All Rights Reserved
 */
package com.flgmwt.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/** Parcelable container for movie data */
public class MovieSummary implements Parcelable {
    public String title;
    public String posterUrl;
    public String plotSynopsis;
    public String rating;
    public String releaseDate;

    MovieSummary() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterUrl);
        dest.writeString(plotSynopsis);
        dest.writeString(rating);
        dest.writeString(releaseDate);
    }

    public static final Creator<MovieSummary> CREATOR = new Creator<MovieSummary>() {
        @Override
        public MovieSummary createFromParcel(Parcel in) {
            return new MovieSummary(in);
        }

        @Override
        public MovieSummary[] newArray(int size) {
            return new MovieSummary[size];
        }
    };

    protected MovieSummary(Parcel in) {
        title = in.readString();
        posterUrl = in.readString();
        plotSynopsis = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
    }
}
