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

import java.io.Serializable;

/**
 * Created by akash on 8/28/16.
 */
public class VachanaMini implements Serializable{
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
}
