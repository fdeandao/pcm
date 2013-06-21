/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.furulapps.pcm;

import java.io.*;
import java.util.*;

/**
 *
 * @author koferdo
 */
public class ListFilePlayer {
    private String dir;
    private List<String> filesOK;
    private List<String> filesBad;

    public ListFilePlayer(String dir) {
        this.dir = dir;
        this.filesOK = new ArrayList<>();
        this.filesBad = new ArrayList<>();
        File file = new File(dir);
        listDirectory(file);
        
    }
    private void listDirectory(File directory){
        File [] files = directory.listFiles();
        for(File fileAct : files){
            if(fileAct.isDirectory()){
                listDirectory(fileAct);
            }else if(fileAct.isFile()){
                if(fileAct.getName().endsWith(".bin")){
                    filesOK.add(fileAct.getAbsolutePath());
                }
            }
        }
    }
    
    public StringBuffer getListPlayersToString(){
        StringBuffer buffer = new StringBuffer();
        Iterator<String> i = filesOK.iterator();
        while(i.hasNext()){
            buffer.append(i.next()).append(Util.NEWLINE);
        }
        return buffer;
    }
}
