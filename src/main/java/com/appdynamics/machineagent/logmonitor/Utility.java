package com.appdynamics.machineagent.logmonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    public static File[] listFiles( String patternWithDir ) {
        List<File> fileList = new ArrayList<>();
        listFiles( patternWithDir, new File(patternWithDir.substring(0,patternWithDir.indexOf("/"))), patternWithDir.substring(patternWithDir.indexOf("/")+1), fileList);
        return fileList.toArray( new File[0] );
    }
    public static void listFiles( String orignalPatternWithDir, File subDir, String pattern, List<File> fileList) {
        //System.out.println(String.format("Dir: %s Pattern: '%s' fileList: %d", subDir.toString(), pattern, fileList.size()));
        File[] files = subDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                File file = new File(dir, name);
                //System.out.println(String.format("Testing Pattern: '%s' File: '%s' isDir: %s",orignalPatternWithDir, file.toString(), file.isDirectory()));
                if( file.isDirectory() && dirPatternMatches(orignalPatternWithDir, file.toString())) {
                    listFiles(orignalPatternWithDir, file, pattern.substring(pattern.indexOf("/")+1), fileList);
                    return false;
                } else {
                    return filePatternMatches(pattern, name);
                }
            }
        });
        if( files != null && files.length >0 )
            for (File file : files)
                fileList.add(file);
    }

    private static boolean dirPatternMatches( String pattern, String name ) {
        if( pattern.startsWith(name) ) return true;
        return filePatternMatches( pattern.substring(0, pattern.lastIndexOf("/")), name );
    }

    private static boolean filePatternMatches( String pattern, String name) {
        return Pattern.compile(pattern.replace("?", ".?").replace("*", ".*?")).matcher(name).matches();
    }

    public static void copyFiles(File sourceLocation, File targetLocation) throws IOException {
        if( sourceLocation.isDirectory() ) {
            if( !targetLocation.exists() ) targetLocation.mkdir();
            for( String child : sourceLocation.list() )
                copyFiles( new File(sourceLocation, child), new File(targetLocation, child));
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    public static void copyFile(File srcFile, File destFile) throws IOException {
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Could not find the source srcFile " + srcFile);
        } else if (!destFile.exists() && !destFile.createNewFile()) {
            throw new IOException("Could not create the destination srcFile " + destFile.getAbsolutePath());
        } else {
            FileInputStream srcStream = new FileInputStream(srcFile);
            FileOutputStream destStream = new FileOutputStream(destFile);

            try {
                byte[] bytes = new byte[5120];

                for(int i = srcStream.read(bytes); i > 0; i = srcStream.read(bytes)) {
                    destStream.write(bytes, 0, i);
                }
            } finally {
                try {
                    srcStream.close();
                } catch (IOException var14) {
                }

                try {
                    destStream.close();
                } catch (IOException var13) {
                }

            }

        }
    }
}
