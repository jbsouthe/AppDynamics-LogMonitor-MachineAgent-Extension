package com.appdynamics.machineagent.logmonitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Utility {
    private static final Logger logger = LogManager.getFormatterLogger();

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
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:"+patternWithDir);
        File baseDir = getBaseDir(patternWithDir);
        logger.debug("Base Dir: %s", baseDir.toString());
        try(Stream<Path> stream = Files.walk(baseDir.toPath(), FileVisitOption.FOLLOW_LINKS)) {
            stream.filter(pathMatcher::matches).forEach( file -> fileList.add(file.toFile()) );
        } catch (Exception exception) {
            logger.warn("IOException trying to find log files, pattern: '%s' Exception: %s",patternWithDir, exception, exception);
        }
        logger.info("Log Files for pattern '%s' returning list(%d): %s", patternWithDir, fileList.size(), fileList);
        return fileList.toArray( new File[0] );
    }

    private static File getBaseDir( String pattern ) {
        if( pattern.contains("*") ) {
            pattern = pattern.substring(0, pattern.indexOf("*") );
        }
        if( pattern.contains("?") ) {
            pattern = pattern.substring(0, pattern.indexOf("?") );
        }
        return new File(pattern.substring(0, pattern.lastIndexOf("/")));
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
