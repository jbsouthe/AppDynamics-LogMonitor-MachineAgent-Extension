package com.cisco.josouthe;

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
}
