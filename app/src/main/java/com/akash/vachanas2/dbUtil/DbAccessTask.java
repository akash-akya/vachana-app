/*
 * vachana. An application for Android users, it contains kannada vachanas
 * Copyright (c) 2017. akash
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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.akash.vachanas2.dbUtil;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Created by akash on 5/11/17.
 */

public abstract class DbAccessTask<U,T> extends AsyncTask<U,Void,T> {
    private final WeakReference<OnCompletion<T>> onCompletion;

    public DbAccessTask(OnCompletion<T> onCompletion) {
        this.onCompletion = new WeakReference<OnCompletion<T>>(onCompletion);
    }

    @Override
    protected void onPostExecute(T result) {
        super.onPostExecute(result);
        if (onCompletion.get() != null) {
            onCompletion.get().updateUI(result);
        }
    }

    public interface OnCompletion<T> {
        void updateUI(T result);
    }
}