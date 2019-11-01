/*
 * vachana. An application for Android users, it contains kannada vachanas
 * Copyright (c) 2016. akash
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.akash.vachanas2.dbUtil;

import android.os.Parcel;
import android.os.Parcelable;

public class VachanaMini implements Parcelable {
    private int id;
    private int kathruId;
    private String kathruName;
    private String title;
    private int favorite;

    public VachanaMini(int id, int kathruId, String kathruName, String title, int favorite){
        this.id = id;
        this.kathruId = kathruId;
        this.title = title;
        this.kathruName = kathruName;
        this.favorite = favorite;
    }

    protected VachanaMini(Parcel in) {
        id = in.readInt();
        kathruId = in.readInt();
        kathruName = in.readString();
        title = in.readString();
        favorite = in.readInt();
    }

    public static final Creator<VachanaMini> CREATOR = new Creator<VachanaMini>() {
        @Override
        public VachanaMini createFromParcel(Parcel in) {
            return new VachanaMini(in);
        }

        @Override
        public VachanaMini[] newArray(int size) {
            return new VachanaMini[size];
        }
    };

    public String getKathruName() {
        return kathruName;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getKathruId() {
        return kathruId;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        if (favorite)
            this.favorite = 1;
        else
            this.favorite = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(kathruId);
        dest.writeString(kathruName);
        dest.writeString(title);
        dest.writeInt(favorite);
    }
}
