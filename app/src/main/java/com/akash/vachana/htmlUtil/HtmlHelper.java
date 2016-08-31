package com.akash.vachana.htmlUtil;

/**
 * Created by akash on 8/28/16.
 */
public class HtmlHelper {
//    private static final String STYLE = "text-align:center";
    /*private static final String HEADER = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" + "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"> \n </head>\n";
    private static final String FOOTER = "</html>";*/

    public static String getHtmlString(String body) {
        return "<body>"+
                "<big>" +
//                "<big><b>"+title+"</b></big><br/>" +

                "<p>"+
                body+
                "</p>"+
                "</big>"+
                "</body>";
    }
}
