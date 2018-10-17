package mobileprogramming.unimelb.com.instagramapplication.utils;

import java.io.File;
import java.util.ArrayList;

public class FileSearch {

    /**
     *
     * Search a directory and return a list of all directories contain in it
     * */

    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<String>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for(int i=0;i<listfiles.length;i++){
            if(listfiles[i].isDirectory()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }

    /**
     *
     * Search a directory and return a list of all files contain in it
     * */
    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<String>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for(int i=0;i<listfiles.length;i++){
            if(listfiles[i].isFile()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }

}
