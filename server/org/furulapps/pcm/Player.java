/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.furulapps.pcm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author koferdo
 */
public class Player {
    private int id;
    private String face;
    private String hair;
    private boolean existsFace;
    private boolean existsHair;
    private String team;
    private String nationality;
    private List<Player> duplicates;

    public Player(int id, String face, String hair) {
        this.id = id;
        this.face = face;
        this.hair = hair;
        duplicates = new ArrayList<>();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.id;
        hash = 17 * hash + Objects.hashCode(this.face);
        hash = 17 * hash + Objects.hashCode(this.hair);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Player other = (Player) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.face, other.face)) {
            return false;
        }
        if (!Objects.equals(this.hair, other.hair)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Player{" + "id=" + id + ", face=" + face + ", hair=" + hair + ", existsFace=" + existsFace + ", existsHair=" + existsHair + ", team=" + team + ", nationality=" + nationality + ", duplicates=" + duplicates + '}';
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getHair() {
        return hair;
    }

    public void setHair(String hair) {
        this.hair = hair;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
    
    public boolean addDuplicate(Player duplicated){
        return duplicates.add(duplicated);
    }
    
    public boolean isExistsFace() {
        return existsFace;
    }

    public void setExistsFace(boolean existsFace) {
        this.existsFace = existsFace;
    }

    public boolean isExistsHair() {
        return existsHair;
    }

    public void setExistsHair(boolean existsHair) {
        this.existsHair = existsHair;
    }
}
