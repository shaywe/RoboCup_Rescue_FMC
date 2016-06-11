package fmcp.Sim;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.text.html.parser.Entity;

import commlib.components.AbstractCSAgent;

import java.util.HashSet;
import java.util.Collections;
import java.util.Map;

import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.Constants;
import rescuecore2.log.Logger;

///import rescuecore2.standard.components.StandardAgent;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Refuge;
import rescuecore2.standard.entities.Road;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.kernel.comms.ChannelCommunicationModel;
import rescuecore2.standard.kernel.comms.StandardCommunicationModel;

/**
   Abstract base class for agents.
   @param <E> The subclass of StandardEntity this agent wants to control.
   
   TODO
   	add search class
   	add comment to neighbours field (after figuring out what it does)
   	change method random walk to a smarter patrol 
 */
public abstract class AbstractAgent<E extends StandardEntity> extends AbstractCSAgent<E> {
    private static final int RANDOM_WALK_LENGTH = 50;

    private static final String SAY_COMMUNICATION_MODEL = StandardCommunicationModel.class.getName();
    private static final String SPEAK_COMMUNICATION_MODEL = ChannelCommunicationModel.class.getName();

    /**
       The search algorithm.
    */
    protected Search search;

    /**
       Whether to use AKSpeak messages or not.
    */
    protected boolean useSpeak;

    /**
       Cache of building IDs.
    */
    protected List<EntityID> buildingIDs;

    /**
       Cache of road IDs.
    */
    protected List<EntityID> roadIDs;

    /**
       Cache of refuge IDs.
    */
    protected List<EntityID> refugeIDs;
    
    /**
       Cache of entities and their neighbors
    */

    private Map<EntityID, Set<EntityID>> neighbours;

    /**
       Construct an AbstractSampleAgent.
    */
    protected AbstractAgent() {
    }

    
    /**
    post connect comment..
    */
    @Override
    protected void postConnect() {
        super.postConnect();
        // populating lists for quick access
        buildingIDs = new ArrayList<EntityID>();
        roadIDs = new ArrayList<EntityID>();
        refugeIDs = new ArrayList<EntityID>();
        for (StandardEntity next : model) {
            if (next instanceof Building) {
                buildingIDs.add(next.getID());
            }
            if (next instanceof Road) {
                roadIDs.add(next.getID());
            }
            if (next instanceof Refuge) {
                refugeIDs.add(next.getID());
            }
        }
        //
        search = new Search(model);
        neighbours = search.getGraph();
        //
        useSpeak = config.getValue(Constants.COMMUNICATION_MODEL_KEY).equals(SPEAK_COMMUNICATION_MODEL);
        //Logger.debug("Communcation model: " + config.getValue(Constants.COMMUNICATION_MODEL_KEY));
        //Logger.debug(useSpeak ? "Using speak model" : "Using say model");
    }

    /**
       Construct a random walk starting from this agent's current location to a random building.
       @return A list with all entities from the neighbors list.
    */
    protected List<EntityID> randomWalk() {
        List<EntityID> result = new ArrayList<EntityID>(RANDOM_WALK_LENGTH);
        Set<EntityID> seen = new HashSet<EntityID>();
        EntityID current = ((Human)me()).getPosition();
        for (int i = 0; i < RANDOM_WALK_LENGTH; ++i) {
            result.add(current);
            seen.add(current);
            List<EntityID> possible = new ArrayList<EntityID>(neighbours.get(current));
            Collections.shuffle(possible, random);
            boolean found = false;
            for (EntityID next : possible) {
                if (seen.contains(next)) {
                    continue;
                }
                current = next;
                found = true;
                break;
            }
            if (!found) {
                // We reached a dead-end.
                break;
            }
        }
        return result;
    }
    
    protected void setChannel (int time, boolean channelComm) {
    	if (time == config.getIntValue(kernel.KernelConstants.IGNORE_AGENT_COMMANDS_KEY)) {
        	if(channelComm){
                int channel = 1; // the channel the agent is going to use in order to send and receive messages
				// Assign the agent to channel 1
				setMessageChannel(channel);
			}
        }
    }
    
}

