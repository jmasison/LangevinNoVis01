/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uchc.cam.langevin.g.object;

import edu.uchc.cam.langevin.g.counter.GStateCounter;

public class GState {
    
    private final String name;
    private final GSiteType type;
    
    private int stateID; // In range 1,000,000,000 to 3,999,999,999
    
    private final GStateCounter stateCounter;
    
    public GState(GSiteType type, String name){
        this.name = name;
        this.type = type;
        stateCounter = new GStateCounter(this);
    }
    
    // GET METHODS
    public String getName(){
        return name;
    }
    
    public String getAbsoluteName(){
        StringBuilder sb = new StringBuilder();
        sb.append(type.getMoleculeName()).append(" : ");
        sb.append(type.getName()).append(" : ");
        sb.append(name);
        return sb.toString();
    }
    
    public int getID(){
        return stateID;
    }
    
    public String getIdAsString(){
        return Integer.toString(stateID);
    }
    
    public GSiteType getType(){
        return type;
    }
    
    public String getTypeName(){
        return type.getName();
    }
    
    public GMolecule getMolecule(){
        return type.getMolecule();
    }
    
    public String getMoleculeName(){
        return type.getMoleculeName();
    }
    
    // SET METHODS
    
    public void setID(int id){
        this.stateID = id;
    }
    
    // Override toString()
    @Override
    public String toString(){
        return name;
    }
    
    public GStateCounter getGStateCounter(){
        return stateCounter;
    }
    
}
