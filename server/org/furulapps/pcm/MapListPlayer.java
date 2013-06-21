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
public final class MapListPlayer {
    private List<Player> players;
    private String map;

    public MapListPlayer(String map) throws Exception{
        this.map = map;
        this.players = new ArrayList<>();
        
        /* Lee map.txt */
        File mapFile = new File(this.map);
        FileReader freader = new FileReader(mapFile);
        Scanner scan = new Scanner(freader);
        int count = 0;
        while(scan.hasNext()){
            count++;
            String fila = scan.nextLine();
            fila = fila.replace(", ", ",");
            String [] posiciones = fila.split(",");
            if(posiciones.length>2){
                try{
                    int id = Integer.parseInt(posiciones[0]);
                    Player player = new Player(id, posiciones[1].replace("\"", ""), posiciones[2].replace("\"", ""));
                    if(this.players.contains(player)){
                        System.out.println("ERROR: ya existe " + player.toString());
                    }else{
                        this.players.add(player);
                    }
                }catch(NumberFormatException e){
                    System.out.println("ERROR: ID no valido " + posiciones[0] + ", linea: " + count);
                }
                
            }
        }
    }
    
    public StringBuffer getPlayersToString(){
        StringBuffer buffer = new StringBuffer();
        Iterator<Player> i = players.iterator();
        while(i.hasNext()){
            Player player = i.next();
            buffer.append(player.toString()).append(Util.NEWLINE);
        }
        return buffer;
    }
    
    private boolean checkLine(String line){
        int firstComma = line.indexOf(",");
        if(firstComma > 0){
            /* Tiene 2 comas */
            if(line.indexOf(",", firstComma) > 0){
                try{
                    Integer.parseInt(line.substring(0, firstComma));
                }catch(Exception e){
                        return false;
                }
            }
        }
        return false;
    }
}
