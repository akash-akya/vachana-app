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

package com.akash.vachana.dbUtil;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Vachana implements Serializable {
    private boolean favorite;
    private int id;
    private String text;
    private String kathru;
    private int kathruId;

    public Vachana(int id, String text, String kathru, boolean favorite, int kathruId)
    {
        this.id = id;
        this.text = text;
        this.kathru= kathru;
        this.favorite = favorite;
        this.kathruId = kathruId;
    }

    public String getKathru() {
        return kathru;
    }

    public int getId() {
        return id;
    }

    public boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(boolean v) {
        favorite = v;
    }

    public String getText() {
        return text;
    }

    public int getKathruId() {
        return kathruId;
    }
}
