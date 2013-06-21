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
public class Load {
    private Properties prop;
    public Load(String fileName) throws Exception{
        prop = new Properties();
        prop.load(new FileInputStream(fileName));
        if(prop.containsKey("directoryfaces")){
            if(prop.containsKey("listplayersfile")){
                //System.out.println(prop.toString());
            }else{
                throw new Exception("No existe lista");
            }
        }else{
            throw new Exception("No existe faces");
        }
    }
    
    public String getDirFace(){
        return prop.getProperty("directoryfaces");
    }
    
    public String getListPlayers(){
        return prop.getProperty("listplayersfile");
    }
}
