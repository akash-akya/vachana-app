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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.akash.vachana.Util;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class KannadaTransliteration  {
    private static final char CHNBIN = '್';
    private static Map<String, String> KANNADA_MAP = null;

    /**
     * ASCII to Unicode mapping.
     * Order of the mapping Matters
     */
    static {
        KANNADA_MAP = new LinkedHashMap<>();

        // Ignore Capitals for some letters
        KANNADA_MAP.put("B", "b");
        KANNADA_MAP.put("C", "c");
        KANNADA_MAP.put("F", "ph");
        KANNADA_MAP.put("f", "ph");
        KANNADA_MAP.put("G", "g");
        KANNADA_MAP.put("J", "j");
        KANNADA_MAP.put("K", "k");
        KANNADA_MAP.put("M", "m");
        KANNADA_MAP.put("P", "p");
        KANNADA_MAP.put("Q", "q");
        KANNADA_MAP.put("V", "v");
        KANNADA_MAP.put("W", "v");
        KANNADA_MAP.put("w", "v");
        KANNADA_MAP.put("X", "x");
        KANNADA_MAP.put("Y", "y");
        KANNADA_MAP.put("Z", "j");
        KANNADA_MAP.put("z", "j");

        // Vyanjana
        KANNADA_MAP.put("k", "\u0C95\u0CCD");
        KANNADA_MAP.put("c", "\u0C9A\u0CCD");
        KANNADA_MAP.put("T", "\u0C9F\u0CCD");
        KANNADA_MAP.put("D", "\u0CA1\u0CCD");
        KANNADA_MAP.put("N", "\u0CA3\u0CCD");
        KANNADA_MAP.put("t", "\u0CA4\u0CCD");
        KANNADA_MAP.put("d", "\u0CA6\u0CCD");
        KANNADA_MAP.put("n", "\u0CA8\u0CCD");
        KANNADA_MAP.put("p", "\u0CAA\u0CCD");
        KANNADA_MAP.put("b", "\u0CAC\u0CCD");


        KANNADA_MAP.put("y", "\u0CAF\u0CCD");
        KANNADA_MAP.put("R", "\u0CB1\u0CCD");
        KANNADA_MAP.put("L", "\u0CB3\u0CCD");
        KANNADA_MAP.put("v", "\u0CB5\u0CCD");
        KANNADA_MAP.put("s", "\u0CB8\u0CCD");
        KANNADA_MAP.put("S", "\u0CB6\u0CCD");
        KANNADA_MAP.put("H", "\u0CB9\u0CCD");
        KANNADA_MAP.put("x", "\u0C95\u0CCD\u0CB6\u0CCD");

        KANNADA_MAP.put("\u200Dm", "\u0C82");
        KANNADA_MAP.put(":h", "\u0C83");
        KANNADA_MAP.put("m", "\u0CAE\u0CCD");

        KANNADA_MAP.put("\u0C95\u0CCDh", "\u0C96\u0CCD");
        KANNADA_MAP.put("\u0C97\u0CCDh", "\u0C98\u0CCD");
        KANNADA_MAP.put("\u0CA8\u0CCDg", "\u0C99\u0CCD");
        KANNADA_MAP.put("\u0C9A\u0CCDh", "\u0C9B\u0CCD");
        KANNADA_MAP.put("\u0C9C\u0CCDh", "\u0C9D\u0CCD");
        KANNADA_MAP.put("\u0CA8\u0CCDj", "\u0C9E\u0CCD");
        KANNADA_MAP.put("\u0C9F\u0CCDh", "\u0CA0\u0CCD");
        KANNADA_MAP.put("\u0CA1\u0CCDh", "\u0CA2\u0CCD");
        KANNADA_MAP.put("\u0CA4\u0CCDh", "\u0CA5\u0CCD");
        KANNADA_MAP.put("\u0CA6\u0CCDh", "\u0CA7\u0CCD");
        KANNADA_MAP.put("\u0CAA\u0CCDh", "\u0CAB\u0CCD");
        KANNADA_MAP.put("\u0CAC\u0CCDh", "\u0CAD\u0CCD");
        KANNADA_MAP.put("\u0CB8\u0CCDh", "\u0CB7\u0CCD");
        KANNADA_MAP.put("\u0CB1\u0CCDr", "\u0C8B");
        KANNADA_MAP.put("\u0CB3\u0CCDl", "\u0C8C");

        KANNADA_MAP.put("\u0CCD\u0C8B", "\u0CC3");
        KANNADA_MAP.put("h", "\u0CB9\u0CCD");
        KANNADA_MAP.put("j", "\u0C9C\u0CCD");
        KANNADA_MAP.put("g", "\u0C97\u0CCD");
        KANNADA_MAP.put("r", "\u0CB0\u0CCD");
        KANNADA_MAP.put("l", "\u0CB2\u0CCD");

        // Hraswa-swara modifier
        KANNADA_MAP.put("\u0CCDa", "\u200C");
        KANNADA_MAP.put("\u0CCDi", "\u0CBF");
        KANNADA_MAP.put("\u0CCDu", "\u0CC1");
        KANNADA_MAP.put("\u0C8Bu", "\u0CC3");
        KANNADA_MAP.put("\u0CCDe", "\u0CC6");
        KANNADA_MAP.put("\u0CCDo", "\u0CCA");
        KANNADA_MAP.put("\u200Ci", "\u0CC8");
        KANNADA_MAP.put("\u200Cu", "\u0CCC");
        KANNADA_MAP.put("\u200C-", "\u200D");
        KANNADA_MAP.put("\u200C:", ":");
        KANNADA_MAP.put("-", "\u200D");

        // Dheerga-swara modifier
        KANNADA_MAP.put("\u200Ca", "\u0CBE");
        KANNADA_MAP.put("\u0CBFi", "\u0CC0");
        KANNADA_MAP.put("\u0CC1u", "\u0CC2");
        KANNADA_MAP.put("\u0CC3u", "\u0CC4");
        KANNADA_MAP.put("\u0CC6e", "\u0CC7");
        KANNADA_MAP.put("\u0CCAo", "\u0CCB");
        KANNADA_MAP.put("\u0CCDA", "\u0CBE");
        KANNADA_MAP.put("\u0CCDI", "\u0CC0");
        KANNADA_MAP.put("\u0CCDU", "\u0CC2");
        KANNADA_MAP.put("\u0C8BU", "\u0CC4");
        KANNADA_MAP.put("\u0CCDE", "\u0CC7");
        KANNADA_MAP.put("\u0CCDO", "\u0CCB");

        // Dheerga-swara
        KANNADA_MAP.put("\u0C85i", "\u0C90");
        KANNADA_MAP.put("\u0C85u", "\u0C94");
        KANNADA_MAP.put("\u0C85a", "\u0C86");
        KANNADA_MAP.put("\u0C87i", "\u0C88");
        KANNADA_MAP.put("\u0C89u", "\u0C8A");
        KANNADA_MAP.put("\u0C8Ee", "\u0C8F");
        KANNADA_MAP.put("\u0C92o", "\u0C93");

        // Hraswa-swara
        KANNADA_MAP.put("a", "\u0C85");
        KANNADA_MAP.put("A", "\u0C86");
        KANNADA_MAP.put("i", "\u0C87");
        KANNADA_MAP.put("I", "\u0C88");
        KANNADA_MAP.put("u", "\u0C89");
        KANNADA_MAP.put("U", "\u0C8A");
        KANNADA_MAP.put("e", "\u0C8E");
        KANNADA_MAP.put("E", "\u0C8F");
        KANNADA_MAP.put("o", "\u0C92");
        KANNADA_MAP.put("O", "\u0C93");
        KANNADA_MAP.put("q", "\u0C95\u0CCD");

        // Numbers
/*
        KANNADA_MAP.put("\u200D1", "\u0CE7");
        KANNADA_MAP.put("\u200D2", "\u0CE8");
        KANNADA_MAP.put("\u200D3", "\u0CE9");
        KANNADA_MAP.put("\u200D4", "\u0CEA");
        KANNADA_MAP.put("\u200D5", "\u0CEB");
        KANNADA_MAP.put("\u200D6", "\u0CEC");
        KANNADA_MAP.put("\u200D7", "\u0CED");
        KANNADA_MAP.put("\u200D8", "\u0CEE");
        KANNADA_MAP.put("\u200D9", "\u0CEF");
        KANNADA_MAP.put("\u200D0", "\u0CE6");
*/
//        KANNADA_MAP.put("(.+)\u200C(.+)", "$1$2");
    }

    public static String getUnicodeString(final String src) {
        StringBuilder uniStr = new StringBuilder();

        if (src.length() > 0){
            uniStr.append(mapLang(src.substring(0,1)));

            for (int i=1; i< src.length(); i++) {
                char curChar  = src.charAt(i);
                int uniLen = uniStr.length();
                int prevCharLen = 4;

                if(i < 4) {
                    prevCharLen = (uniStr.charAt(uniLen-1) == CHNBIN)? 2 : 1;
                }

                String ch = mapLang(uniStr.substring(uniLen-prevCharLen, uniLen)+curChar);
                uniStr.replace(uniLen-prevCharLen, uniLen+ch.length(), ch);
            }

            if (uniStr.charAt(uniStr.length()-1) == '\u200C') {
                return uniStr.substring(0, uniStr.length()-1);
            }
        }
        return uniStr.toString();
    }

    private static String mapLang(final String txt) {
        StringBuilder str = new StringBuilder(txt);

        for (Map.Entry<String, String> entry : KANNADA_MAP.entrySet()) {
            replaceAll(str, entry.getKey(), entry.getValue());
//            str = str.replaceAll(entry.getKey(), entry.getValue());
        }

        return str.toString().replaceAll("(.+)\u200C(.+)", "$1$2"); //.replaceAll("(.+)\u200C$", "$1");;
    }

    public static void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length(); // Move to the end of the replacement
            index = builder.indexOf(from, index);
        }
    }
}