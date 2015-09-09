/*
 * Model this class on the binding reaction class.  Here we can use a 
 * state id from the initial state to map to a transitionreaction, and then 
 * get the condition, rates, etc from that transition reaction.
 */

package reaction;

import object.Bond;
import object.Site;
import g.object.GSiteType;
import g.object.GMolecule;
import g.object.GState;
import g.reaction.GTransitionReaction;
import java.util.ArrayList;
import java.util.HashMap;
import helpernovis.Rand;
import langevinnovis01.Global;

public class TransitionReactions {

    // Check to see if we even have a reaction. As many states won't have a
    // this check should be faster than calling ArrayList.isEmpty. 
    
    private final HashMap<Integer, Boolean> hasReaction;
    
    private final HashMap<Integer, ArrayList<GTransitionReaction>> reactionMap;
    
    private final double dt;
    
    private final BindingReactions bindingReactions;
    
    public TransitionReactions(Global g, BindingReactions bindingReactions){
        ArrayList<GTransitionReaction> reactions = g.getTransitionReactions();
        ArrayList<GState> allStates = new ArrayList<>();
        this.dt = g.getdt();
        this.bindingReactions = bindingReactions;
        
        for(GMolecule gmolecule : g.getMolecules()){
            for(GSiteType gtype : gmolecule.getTypeArray()){
                for(GState gstate : gtype.getStates()){
                    allStates.add(gstate);
                }
            }
        }
        
        hasReaction = new HashMap<>(10*allStates.size());
        reactionMap = new HashMap<>(10*allStates.size());
        
        // First populate the reaction map with arraylists and set the reaction flags to false
        for(GState gstate : allStates){
            hasReaction.put(gstate.getID(), Boolean.FALSE);
            reactionMap.put(gstate.getID(), new ArrayList<GTransitionReaction>());
        }
        
        // Now populate the reaction arrays based on the initial states
        for(GTransitionReaction reaction : reactions){
            hasReaction.put(reaction.getInitialState().getID(), Boolean.TRUE);
            ArrayList<GTransitionReaction> tempReactions = reactionMap.get(reaction.getInitialState().getID());
            tempReactions.add(reaction);
        }
        
    }
    
    public void tryReactions(Site site){
        GState state = site.getState();
        if(hasReaction.get(state.getID())){
            ArrayList<GTransitionReaction> reactions = reactionMap.get(state.getID());
            // Now loop through the reactions
            boolean outerbreak = false;
            for(GTransitionReaction reaction : reactions){
                if(outerbreak){
                    break;
                }
                switch(reaction.getConditionID()){
                    
                    // If there is no condition, then just try the reaction
                    case GTransitionReaction.NONE:{
                        if(reactionOccurs(reaction.getRate())){
                            site.setState(reaction.getFinalState());
                            if(site.isBound()){
                                updateBondType(site);
                            }
                            outerbreak = true;
                        }
                        break;
                    }
                
                    // Now try the reactions with unbound (free) conditions
                    case GTransitionReaction.FREE:{
                        if(!site.isBound()){
                            if(reactionOccurs(reaction.getRate())){
                                site.setState(reaction.getFinalState());
                                outerbreak = true;
                            }
                        }
                        break;
                    }
                    
                    // Now try the reactions which only occur when a site is bound
                    case GTransitionReaction.BOUND:{
                        if(site.isBound()){
                            if(site.getBindingPartner().getTypeID() == reaction.getConditionalType().getID()){
                                // See if the reaction can occur regardless of the state of the binding partner
                                if(reaction.getConditionalState().getID() == GTransitionReaction.ANY_STATE_ID){
                                    if(reactionOccurs(reaction.getRate())){
                                        site.setState(reaction.getFinalState());
                                        updateBondType(site);
                                        outerbreak = true;
                                    }
                                }
                                // If the reaction needs a specific state, look to see if we have it
                                else if(site.getBindingPartner().getState().getID() == reaction.getConditionalState().getID()){
                                    if(reactionOccurs(reaction.getRate())){
                                        site.setState(reaction.getFinalState());
                                        updateBondType(site);
                                        outerbreak = true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                    
                    default:
                        // Do nothing
                }
            }
        }
    }
    
    private void updateBondType(Site site){
        String id = Integer.toString(site.getState().getID());
        String partnerID = Integer.toString(site.getBindingPartner().getState().getID());
        Bond bond = site.getBond();
        bond.setReactionName(bindingReactions.getName(id, partnerID));
        bond.setOffProbability(bindingReactions.getOffProb(id, partnerID));
        bond.setBondLength(bindingReactions.getBondLength(id, partnerID));
    }
    
    private boolean reactionOccurs(double rate){
        return Rand.randomPosDouble() < dt*rate;
    }
    
}
