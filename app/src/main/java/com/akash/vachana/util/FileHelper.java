package com.akash.vachana.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by akash on 8/28/16.
 */
public class FileHelper {
    public static String getFileContent(InputStream fileStream) {
        BufferedReader reader = null;
        StringBuilder buffer = new StringBuilder();

        try {
            reader = new BufferedReader( new InputStreamReader(fileStream, "UTF-8"));

            String mLine=null;
            while ((mLine = reader.readLine()) != null) {
                buffer.append(mLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        assert buffer != null;
        return buffer.toString();
    }
}
