/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package g.reaction;

import helpernovis.OnRateSolver;
import helpernovis.IOHelp;
import java.util.Scanner;
import java.util.ArrayList;
import g.counter.GBondCounter;
import g.object.GMolecule;
import g.object.GSiteType;
import g.object.GState;
import langevinnovis01.Global;

public class GBindingReaction {
    
    // Might want to name the reactions
    private String name;
    
    private final GMolecule [] molecule = new GMolecule[2];
    private final GSiteType [] type = new GSiteType[2];
    private final GState [] state = new GState[2];
    
    private double kon = 0; // Units uM-1.s-1
    private double lambda = 0; // Units s-1. This is the rate used to actually compute the probability of reacting.
    private double koff = 0;  // Units s-1.  This is given directly to the bond.
    
    private double bondLength = 0.5; // Units nm
    
    public final static String ANY_STATE_STRING = "Any_State";
    public final static GState ANY_STATE = new GState(null, ANY_STATE_STRING);
    public final static int ANY_STATE_ID = -10;
    
    
    
    // It really helps to be able to get the BondData object directly from the 
    // binding reaction
    private final GBondCounter bondData;
    
    public GBindingReaction(){
        name = null;
        bondData = new GBondCounter(this);
        for(int i=0;i<2;i++){
            molecule[i] = null;
            type[i] = null;
            state[i] = null;
        }
    }
    
    public GBindingReaction(String name){
        this.name = name;
        bondData = new GBondCounter(this);
        for(int i=0;i<2;i++){
            molecule[i] = null;
            type[i] = null;
            state[i] = null;
        }
    }
    
    /* *************************************************************\  
     *             DATA SPECIFIC TO THE REACTION                   *
     *  Reaction name and rates.                                   *
    \***************************************************************/
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public void setkon(double kon){
        this.kon = kon;
        setLambda();
    }
    
    public double getkon(){
        return kon;
    }
    
    public void setkoff(double koff){
        this.koff = koff;
    }
    
    public double getkoff(){
        return koff;
    }
    
    public double getLambda(){
        return lambda;
    }
    
    private void setLambda(){
        // First rescale kon so that it is in nm^3/s
        double rescalekon = kon*1660000.0;
        double p = type[0].getRadius() + type[1].getRadius();
        double R = type[0].getReactionRadius() + type[1].getReactionRadius();
        // Rescale D so it's in nm^2/s
        double D = 1000000.0 * (type[0].getD() + type[1].getD());
        // When a site reacts its own type, the rate should be rescaled to 2*kon
        if(type[0] != type[1]){
            lambda = OnRateSolver.getrootReversible(p, p+bondLength, R, D, rescalekon);
        } else {
            lambda = OnRateSolver.getrootReversible(p, p+bondLength, R, D, 2*rescalekon);
        }
        // System.out.println("Called setLambda.  Lambda = " + lambda);
        
    }
    
    public double getBondLength(){
        return bondLength;
    }
    
    public void setBondLength(double length){
        bondLength = length;
    }
    
    /* ********  GET AND SET THE MOLECULES ********************/
    
    public GMolecule [] getMolecules(){
        return molecule;
    }
    
    public GMolecule getMolecule(int i){
        return molecule[i];
    }
    
    public void setMolecules(GMolecule mol1, GMolecule mol2){
        molecule[0] = mol1;
        molecule[1] = mol2;
    }
    
    public void setMolecule(int i, GMolecule mol){
        molecule[i] = mol;
    }
    
    /* ********** GET AND SET THE SITE TYPES *****************/
    
    public GSiteType [] getTypes(){
        return type;
    }
    
    public GSiteType getType(int i){
        return type[i];
    }
    
    public void setTypes(GSiteType type1, GSiteType type2){
        type[0] = type1;
        type[1] = type2;
    }
    
    public void setType(int i, GSiteType type){
        this.type[i] = type;
    }
    
    /* ********* GET AND SET THE STATES *********************/
    
    public GState [] getStates(){
        return state;
    }
    
    public GState getState(int i){
        return state[i];
    }
    
    public void setStates(GState state1, GState state2){
        state[0] = state1;
        state[1] = state2;
    }
    
    public void setState(int i, GState state){
        this.state[i] = state;
    }
    
    /* ***** GET THE BONDDATA OBJECT ASSOCIATED WITH THIS REACTION ****/
    
    public GBondCounter getBondCounter(){
        return bondData;
    }
    
    /* ***************** LOAD SINGLE REACTION *************************/
    
    public void loadReaction(Global g, Scanner dataScanner){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        name = IOHelp.getNameInQuotes(dataScanner);
        molecule[0] = g.getGMolecule(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        type[0] = molecule[0].getType(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        String state0 = IOHelp.getNameInQuotes(dataScanner);
        if(state0.equals(GBindingReaction.ANY_STATE_STRING)){
            state[0] = GBindingReaction.ANY_STATE;
        } else {
            state[0] = type[0].getState(state0);
        }
        // Skip '+'
        dataScanner.next();
        molecule[1] = g.getGMolecule(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        type[1] = molecule[1].getType(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        String state1 = IOHelp.getNameInQuotes(dataScanner);
        if(state1.equals(GBindingReaction.ANY_STATE_STRING)){
            state[1] = GBindingReaction.ANY_STATE;
        } else {
            state[1] = type[1].getState(state1);
        }
        // Skip 'kon'
        dataScanner.next();
        kon = dataScanner.nextDouble();
        // Skip 'koff'
        dataScanner.next();
        koff = dataScanner.nextDouble();
        // Skip "Bond_Length"
        dataScanner.next();
        bondLength = dataScanner.nextDouble();
        
        // MUST SET LAMBDA HERE.  I FORGOT TO DO THIS AT FIRST!
        setLambda();
        // If lambda*dt is too big give a warning.
        if(lambda*g.getdt() > 0.05){
            System.out.println("WARNING: lambda*dt = " + lambda*g.getdt() + "."
                    + " For accurate results you want lambda*dt << 0.01.");
        }
        // </editor-fold>
    }
    
    /* ***************** LOAD FULL ARRAY *******************************/
    
    public static ArrayList<GBindingReaction> loadReactions(Global g, Scanner sc){
        ArrayList<GBindingReaction> reactions = new ArrayList<>();
        GBindingReaction reaction;
        while(sc.hasNextLine()){
            reaction = new GBindingReaction();
            reaction.loadReaction(g, new Scanner(sc.nextLine()));
            reactions.add(reaction);
        }
        return reactions;
    }
    
    /* ******   WRITE REACTION    ***********************/
    // I only need this for testing, I think.
    @Override
    public String toString(){
       return name;
    }
    
    public String writeReaction(){
        StringBuilder sb = new StringBuilder();
        sb.append("'").append(name).append("'       ");
        if(molecule[0] != null && molecule[1] != null){
            sb.append("'").append(molecule[0].getName()).append("' : '")
                    .append(type[0].getName()).append("' : '")
                    .append(state[0].toString());
            sb.append("'  -->  '");
            sb.append(molecule[1].getName()).append("' : '")
                    .append(type[1].getName()).append("' : '")
                    .append(state[1].toString());
            sb.append("'  kon  ").append(Double.toString(kon));
            sb.append("  koff ").append(Double.toString(koff));
            sb.append("  Bond_Length ").append(Double.toString(bondLength));
        }
        return sb.toString();
    }
    
}
