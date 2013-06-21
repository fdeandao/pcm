/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.furulapps.pcm;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.*;
import org.furulapps.pcm.*;

/**
 *
 * @author koferdo
 */
public class Init {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        String hex = "4445504F525449564F2054C14348495241";                                  // AAA
        System.out.println(hexToString(hex));
        //String hex = "6174656ec3a7c3a36f";
        
        /*if (args.length > 0) {
            Load load = new Load(args[0]);
            MapListPlayer mlp = new MapListPlayer(load.getListPlayers());
            ListFilePlayer lfp = new ListFilePlayer(load.getDirFace());
            System.out.println(mlp.getPlayersToString().toString());
            System.out.println(lfp.getListPlayersToString().toString());
        } else {
            System.out.println("Necesita archivo de propiedades");
        }*/
    }
    
    private static String hexToString(String hex){
 
	  StringBuilder sb = new StringBuilder();
	  StringBuilder temp = new StringBuilder();
 
	  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
	  for( int i=0; i<hex.length()-1; i+=2 ){
 
	      //grab the hex in pairs
	      String output = hex.substring(i, (i + 2));
	      //convert hex to decimal
	      int decimal = Integer.parseInt(output, 16);
	      //convert the decimal to character
              System.out.print("Char: " + (char)decimal + "|");
              System.out.print("output: " + output + "|");
              System.out.println("Int: " + decimal + "|");
              System.out.println();
	      sb.append((char)decimal);
 
	      temp.append(decimal);
	  }
	  return sb.toString();
  }
}
