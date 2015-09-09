/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package g.object;

import java.io.PrintWriter;
import java.util.Scanner;
import java.util.ArrayList;

import helpernovis.IOHelp;

public class GSiteType {

    private String name;
    
    private final GMolecule molecule;

    private int typeID; // Number between 1000000 and 3999999
    // Both radii in nm
    private double radius;
    // Either 1.5x radius or r+2 nm, whichever is smaller, but not smaller than r+0.5 nm.
    private double reactionRadius;
    // D here is still in um^2/s! Gets converted to nm^2/s in the Site class and the BindingReaction class.
    private double D;
    private String color;
    
    private ArrayList<GState> states = new ArrayList<>();
    
    
    // Static name for anchors
    public static String ANCHOR = "Anchor";

    
    public GSiteType(GMolecule molecule, String name){
        this.name = name;
        this.molecule = molecule;
    }
    
    // SET METHODS
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setID(int id){
        typeID = id;
    }
    
    public void setRadius(double r){
        this.radius = r;
        double minRadius = Math.max(0.5 + r, 1.5*r);
        this.reactionRadius = Math.min(minRadius, r+2);
    }
    
    public void setD(double D){
        this.D = D;
    }
    
    public void setColor(String color){
        this.color = color;
    }
    
    public void setStateArray(ArrayList<GState> states){
        this.states = states;
    }
    
    // GET METHODS
    
    public String getName(){
        return name;
    }
    
    public GMolecule getMolecule(){
        return molecule;
    }
    
    public String getMoleculeName(){
        return molecule.getName();
    }
    
    public int getID(){
        return typeID;
    }
    
    public double getRadius(){
        return radius;
    }
    
    public double getReactionRadius(){
        return reactionRadius;
    }
    
    public double getD(){
        return D;
    }
    
    public String getColorName(){
        return color;
    }
    
    public ArrayList<GState> getStates(){
        return states;
    }
    
    public GState getState(String name){
        GState s = null;
        for (GState ts : states) {
            if(ts.getName().equals(name)){
                s = ts;
                break;
            }
        }
        return s;
    }
    
    
    /********************************************************************\
     *                        ASSIGN STATE IDS                          *
    \********************************************************************/
    
    // State ids can go up to 999, so use a padding of 3.
    public void assignStateIDs(){
        for(int i=0;i<states.size();i++){
            GState state = states.get(i);
            state.setID(IOHelp.appendNumber(typeID, i, 3));
        }
        
    }
    
    /* ******************************************************************\
     *                           FILE IO                                *
    \********************************************************************/
    
    public void writeType(PrintWriter p){
        p.print("TYPE: Name \"" + getName() + "\"");
        p.print(" Radius " + IOHelp.DF[3].format(getRadius()) + " D " + IOHelp.DF[3].format(getD()) + " Color " + getColorName());
        p.print(" STATES ");
        for (GState state : states) {
            p.print("\"" + state + "\"" + " ");
        }
        p.println();
    }
    
    public static GSiteType readType(GMolecule mol, String s){
        GSiteType tempType = new GSiteType(mol, "TempName");
        Scanner sc = new Scanner(s);
        if(!sc.next().equals("TYPE:")){
            System.out.println("ERROR: Type line did not begin with \"TYPE:\"");
        }
        while(sc.hasNext()){
            String scnext = sc.next();
            switch(scnext){
                case "Name":{
                    tempType.setName(IOHelp.getNameInQuotes(sc));
                    break;
                }
                case "Radius":{
                    tempType.setRadius(Double.parseDouble(sc.next()));
                    break;
                }
                case "D":{
                    tempType.setD(Double.parseDouble(sc.next()));
                    break;
                }
                case "Color":{
                    tempType.setColor(sc.next());
                    break;
                }
                case "STATES":{
                    ArrayList<String> tempStrings = new ArrayList<>();
                    while(sc.hasNext()){
                        tempStrings.add(IOHelp.getNameInQuotes(sc));
                    }
                    ArrayList<GState> tempStates = new ArrayList<>();
                for (String tempString : tempStrings) {
                    tempStates.add(new GState(tempType, tempString));
                }
                    tempType.setStateArray(tempStates);
                    break;
                }
                default:{
                    System.out.println("Type reader received unexpected input: " + scnext);
                }
            }
        }
        return tempType;
    }
    
}
