/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package object;

import java.util.ArrayList;
import java.io.*;
import helpernovis.Location;

public class Molecule {
    
    private final int id; 
    private final int location;
    // The GMolecule id for this molecule.
    private final int gId;
    
    private final ArrayList<Site> sites = new ArrayList<>();
    private final ArrayList<Link> links = new ArrayList<>();
    
    private boolean isBound = false;
    private int bondNumber = 0;
    
    private final boolean is2d;
    
    // The cluster index
    private long clusterIndex = -1;
    private final ArrayList<Molecule> bindingPartners = new ArrayList<>();
    
    // The "sites" array gets shuffled after creation, and this was causing
    // problems in the allosteric reactions because they look for a specific
    // site based on the id in the gsites array of the gmolecule.  So we need
    // to have a duplicate array pointing at the same sites, but this one is
    // not shuffled.  What a pain.
    private final ArrayList<Site> originalSiteArray = new ArrayList<>();
    
    public Molecule(int id, int gId, int location, boolean is2d){
        this.id = id;
        this.gId = gId;
        this.location = location;
        this.is2d = is2d;
    }
    
    // GET METHODS
    
    public int getID(){
        return id;
    }
    
    public int getGID(){
        return gId;
    }
    
    public int getLocation(){
        return location;
    }
    
    public String getLocationName(){
        return Location.locationName[location];
    }
    
    public ArrayList<Site> getSites(){
        return sites;
    }
    
    // Uses the current site index, which could be shuffled compared to 
    // the index in the corresponding gsite array in the gmolecule.
    public Site getSite(int i){
        return sites.get(i);
    }
    
    public Site getSiteFromGSiteIndex(int i){
        return originalSiteArray.get(i);
    }
    
    public ArrayList<Link> getLinks(){
        return links;
    }
    
    public boolean isBound(){
        return isBound;
    }
    
    public boolean is2D(){
        return is2d;
    }
    
    public int getBondNumber(){
        return bondNumber;
    }
    
    public long getClusterIndex(){
        return clusterIndex;
    }
    
    public ArrayList<Molecule> getBindingPartners(){
        return bindingPartners;
    }
    
    // SET METHODS
    
    public void addSite(Site site){
        this.sites.add(site);
        // We can add the site to the original array too, because we only use
        // this method when making the molecule and before shuffling.  I 
        // suppose this could lead to trouble at some point, but it works
        // for now.
        this.originalSiteArray.add(site);
    }
    
    public void addLink(Link link){
        this.links.add(link);
    }
    
    public void addBindingPartner(Molecule molecule){
        if(!bindingPartners.contains(molecule)){
            bindingPartners.add(molecule);
        }
    }
    
    public void removeBindingPartner(Molecule molecule){
        bindingPartners.remove(molecule);
    }
    
    public void plusBond(){
        bondNumber++;
        isBound = true;
    }
    
    public void minusBond(){
        bondNumber--;
        if(bondNumber == 0){
            isBound = false;
        }
    }
    
    public void setClusterIndex(long index){
        clusterIndex = index;
    }
    
}
