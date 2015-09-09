

package g.reaction;

import helpernovis.IOHelp;
import helpernovis.Location;
import helpernovis.Rand;
import java.io.PrintWriter;
import java.util.Scanner;
import g.object.GMolecule;
import langevinnovis01.Global;

public class GDecayReaction {
    
    private double kcreate;  // units uM/s
    private double kdecay;   // units 1/s
    
    // Makes sense to calculate these once so we don't have to perform the multiplications at each step
    private double probCreate;  // kcreate*volume*dt
    private double probDecay;   // kdecay*dt .  This still needs to be multiplied by the number of particles in the system
    
    private final GMolecule gmolecule;
    
    public GDecayReaction(GMolecule gmolecule){
        this.gmolecule = gmolecule;
    }
    
    // ************* GET METHODS *********
    // I might not need any of these
    public GMolecule getMolecule(){
        return gmolecule;
    }
    
    public double getDecayRate(){
        return kdecay;
    }
    
    public double getCreationRate(){
        return kcreate;
    }
    
    public String getName(){
        return gmolecule.getName();
    }
    
    /* *********** SET THE REACTION PROBABILITIES  *********/
    
    public void setCreationProbability(double dt, double volume){
        probCreate = 602.0*dt*volume*kcreate/(1000*1000*1000);
        // System.out.println("probCreate = " + probCreate + " for " + this.getName());
    }
    
    public void setDecayProbability(double dt){
        probDecay = dt*kdecay;
        // System.out.println("probDecay = " + probDecay + " for " + this.getName());
    }
    
    /* **************  LOAD FILE *****************************/
    
    public void loadReaction(String dataString){
        Scanner sc = new Scanner(dataString);
        // Skip kcreate
        sc.next();
        kcreate = sc.nextDouble();
        sc.next();
        kdecay = sc.nextDouble();
    }
    
    public static void loadReactions(Global g, Scanner dataScanner){
        while(dataScanner.hasNextLine()){
            String []  nextLine = dataScanner.nextLine().split(":");
            GMolecule molecule = g.getGMolecule(IOHelp.getNameInQuotes(new Scanner(nextLine[0])));
            GDecayReaction reaction = molecule.getGDecayReaction();
            reaction.loadReaction(nextLine[1].trim());
            reaction.setDecayProbability(g.getdt());
            if(molecule.getLocation() == Location.INSIDE){
                reaction.setCreationProbability(g.getdt(),g.getVin());
            } else if(molecule.getLocation() == Location.OUTSIDE){
                reaction.setCreationProbability(g.getdt(), g.getVout());
            } else {
                reaction.setCreationProbability(g.getdt(), g.getVtot());
            }
        }
    }
    
    public void writeReaction(PrintWriter p){
        p.println("'" + this.getName() + "' : kcreate " + getCreationRate()
                                                + " kdecay " + getDecayRate());
    }
    
    /******************************************************************\
     *         METHOD TO DETERMINE IF EITHER REACTION OCCURRED        *
     *  @return Returns 1 if a molecule should be created, -1 if a    *
     *  molecule should be destroyed, and 0 otherwise.                *
     *  Adjust the number in this method too.                         *
     *                                                                *
     * @param freeMolecules The current number of free molecules of a *
     *                      given type.                               *
    \******************************************************************/
    
    public int getReaction(int freeMolecules){
        double rand = Rand.randomPosDouble();
        if(rand < probCreate){
            // System.out.println("Should try to make a molecule.");
            return 1;
        } else if (rand < probCreate + freeMolecules*probDecay){
            // System.out.println("Should try to destroy a molecule.");
            return -1;
        } else {
            return 0; 
        }
    }
    
    
}
