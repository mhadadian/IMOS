/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio;

import java.io.Serializable;

/**
 *
 * @author MOSJAVA
 */
public class MapRecord implements Serializable{
    private String ID;
    private String Read;
    private String Score;
    private int Position;
    private boolean isMaped;

    public MapRecord(String ID, String Read, String Score, int Position, boolean isMaped) {
        this.ID = ID;
        this.Read = Read;
        this.Score = Score;
        this.Position = Position;
        this.isMaped = isMaped;
    }
    public MapRecord(ALRecord fqr, int Position, boolean isMaped) {
        this.ID = fqr.getID();
        this.Read = fqr.getRead();
        this.Score = fqr.getScore();
        this.Position = Position;
        this.isMaped = isMaped;
    }
    @Override
    public String toString() {
        return "["+ID+", "+isMaped+", "+Position+"]";
    }
    
    /**
     * @return the ID
     */
    public String getID() {
        return ID;
    }
    
    /**
     * @param ID the ID to set
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * @return the Read
     */
    public String getRead() {
        return Read;
    }

    /**
     * @param Read the Read to set
     */
    public void setRead(String Read) {
        this.Read = Read;
    }

    /**
     * @return the Score
     */
    public String getScore() {
        return Score;
    }

    /**
     * @param Score the Score to set
     */
    public void setScore(String Score) {
        this.Score = Score;
    }

    /**
     * @return the Position
     */
    public int getPosition() {
        return Position;
    }

    /**
     * @param Position the Position to set
     */
    public void setPosition(int Position) {
        this.Position = Position;
    }

    /**
     * @return the isMaped
     */
    public boolean isIsMaped() {
        return isMaped;
    }

    /**
     * @param isMaped the isMaped to set
     */
    public void setIsMaped(boolean isMaped) {
        this.isMaped = isMaped;
    }
    
}
