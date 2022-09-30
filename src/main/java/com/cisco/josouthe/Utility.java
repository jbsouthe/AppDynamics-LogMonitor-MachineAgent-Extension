package com.cisco.josouthe;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    public static String toString(Object[] a) {
        if (a == null)
            return "null";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(String.valueOf(a[i]));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public static String toString( Map<Object,Object> map ) {
        if( map == null || map.size() == 0 ) return "empty";
        StringBuilder output = new StringBuilder();
        for( Map.Entry<Object,Object> entry : map.entrySet() ) {
            output.append(entry.getKey() +"="+ entry.getValue()).append("\n");
        }
        return output.toString();
    }

    public static String removeFirstElements( int index, String[] array) {
        String[] newArray = new String[array.length-index];
        int j=0;
        for( int i=index; i< array.length; i++)
            newArray[j++] = array[i];
        return convertToStringWithoutDecorations(newArray);
    }

    public static String convertToStringWithoutDecorations( String[] array ) {
        if( array == null ) return "";
        if( array.length == 0 ) return "";
        StringBuilder sb = new StringBuilder();
        for( String string : array ) {
            sb.append(" ").append(string);
        }
        return sb.toString().trim();
    }

    public static File[] listFiles( File dir, String pattern) {
        return dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return Pattern.compile(pattern.replace("?", ".?").replace("*", ".*?")).matcher(name).matches();
            }
        });
    }
}
