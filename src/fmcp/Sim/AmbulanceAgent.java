package fmcp;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;

import rescuecore2.worldmodel.EntityID;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.messages.Command;
import rescuecore2.log.Logger;

import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.standard.entities.AmbulanceCentre;
import rescuecore2.standard.entities.AmbulanceTeam;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.entities.Civilian;
import rescuecore2.standard.entities.Refuge;
import commlib.message.BaseMessageType;
import commlib.message.RCRSCSMessage;
import commlib.report.ReportMessage;
import commlib.task.TaskMessage;
import firesimulator.world.AmbulanceCenter;
import commlib.data.RCRSCSData;
import commlib.information.AmbulanceTeamInformation;
import commlib.information.BlockadeInformation;
import commlib.information.BuildingInformation;
import commlib.information.PositionInformation;
import commlib.information.VictimInformation;
import commlib.information.WorldInformation;
import rescuecore2.Constants;
import rescuecore2.standard.entities.Blockade;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.kernel.comms.ChannelCommunicationModel;

/**
   Ambulance Team
 */
public class AmbulanceAgent extends AbstractAgent<AmbulanceTeam> {
    private Collection<EntityID> unexploredBuildings;
    private boolean channelComm;
    
  
    
    @Override
    public String toString() {
        return "ambulance agent";
    }

    @Override
    protected void postConnect() {
        super.postConnect();
        
        model.indexClass(StandardEntityURN.CIVILIAN, StandardEntityURN.FIRE_BRIGADE, StandardEntityURN.POLICE_FORCE, StandardEntityURN.AMBULANCE_TEAM, StandardEntityURN.REFUGE,StandardEntityURN.HYDRANT,StandardEntityURN.GAS_STATION, StandardEntityURN.BUILDING); // change???
        
        unexploredBuildings = new HashSet<EntityID>(buildingIDs);
        
        // remove this???
        boolean speakComm = config.getValue(Constants.COMMUNICATION_MODEL_KEY)
				.equals(ChannelCommunicationModel.class.getName());
		
		int numChannels = this.config.getIntValue("comms.channels.count");
		
		if((speakComm) && (numChannels > 1)){
			this.channelComm = true;
		}
		else{
			this.channelComm = false;
		}
    }

    
    /**
     * this what happens at extended class before 'thinking' is called:
     * 		super.receiveMessage(heard);
     * 		this.thinking(time, changed, heard);
     * 		super.sendMessage(time);
     */
    
    protected void thinking(int time, ChangeSet changed, Collection<Command> heard) {
    	// set communication channel
    	setChannel(time, channelComm);
    	
		/*
		 * inform center:
		 * 1. inform with self position
		 * 2. inform with precepted entities (Building and Victims)  in its radius
		 */
		informCenter(time, changed);
		
		
		
		// get info and tasks
		for(RCRSCSMessage msg : this.receivedMessageList){
			Logger.info(msg.toString());

			boolean a = msg.getMessageType() == BaseMessageType.AMBULANCE_TEAM;
			
			if (msg instanceof WorldInformation) { //Information message
    			
    		}
    		else {
    			if (msg instanceof ReportMessage) { //Report message
    				
    			}
    			else {
        			if (msg instanceof TaskMessage) {
        				
        			}
        		}
    		}
			for (RCRSCSData<?> data : msg.getData()) {
				//data.
				
				
				
			}
			
			
		}
    	
		// update
		updateUnexploredBuildings(changed);
		
		// Am I transporting a civilian to a refuge?
		if(someoneOnBoard()){
			// Am I at a refuge?
			if(location() instanceof Refuge){
				// Unload!
				sendUnload(time);
				return;
			}
			else{
				// Move to a refuge
				List<EntityID> path = search.breadthFirstSearch(me().getPosition(),refugeIDs);
				if(path != null){
					Logger.info("Moving to refuge");
					sendMove(time, path);
					return;
				}
				// What do I do now? Might as well carry on and see if we can dig
				// someone else out.
				Logger.debug("Failed to plan path to refuge");
			}
		}
		
				
		    		
		    		
		// Go through targets (sorted by distance) and check for things we can do
		for(Human next : getTargets()){
			if(next.getPosition().equals(location().getID())){
				// Targets in the same place might need rescuing or loading
				if((next instanceof Civilian) && next.getBuriedness() == 0
						&& !(location() instanceof Refuge)){
					// Load
					Logger.info("Loading " + next);
					sendLoad(time, next.getID());
					return;
				}
				if(next.getBuriedness() > 0){
					// Rescue
					Logger.info("Rescueing " + next);
					sendRescue(time, next.getID());
					return;
				}
			}
			else{
				// Try to move to the target
				List<EntityID> path = search.breadthFirstSearch(me().getPosition(),
						next.getPosition());
				if(path != null){
					Logger.info("Moving to target");
					sendMove(time, path);
					return;
				}
			}
		}
		    		
		    		
		// Nothing to do
		List<EntityID> path = search.breadthFirstSearch(me().getPosition(),
				unexploredBuildings);
		if(path != null){
			Logger.info("Searching buildings");
			sendMove(time, path);
			return;
		}
		
		Logger.info("Moving randomly");
		sendMove(time, randomWalk());
		// someone else out.
		Logger.debug("Failed to plan path to refuge");
		
    }
   

    @Override
    protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
        return EnumSet.of(StandardEntityURN.AMBULANCE_TEAM);
    }
    
    /*
       returns whether the agent is carried by this agent
    */
    private boolean someoneOnBoard() {
        for (StandardEntity next : model.getEntitiesOfType(StandardEntityURN.CIVILIAN)) {
            if (((Human)next).getPosition().equals(getID())) {
                Logger.debug(next + " is on board");
                return true;
            }
        }
        return false;
    }
    
    // change to get from central agent
    private List<Human> getTargets() {
        List<Human> targets = new ArrayList<Human>();
        for (StandardEntity next : model.getEntitiesOfType(StandardEntityURN.CIVILIAN, StandardEntityURN.FIRE_BRIGADE, StandardEntityURN.POLICE_FORCE, StandardEntityURN.AMBULANCE_TEAM)) {
            Human h = (Human)next;
            if (h == me()) {
                continue;
            }
            if (h.isHPDefined()
                && h.isBuriednessDefined()
                && h.isDamageDefined()
                && h.isPositionDefined()
                && h.getHP() > 0
                && (h.getBuriedness() > 0 || h.getDamage() > 0)) {
                targets.add(h);
            }
        }
        Collections.sort(targets, new DistanceSorter(location(), model));
        return targets;
    }

    
    private void updateUnexploredBuildings(ChangeSet changed) {
        for (EntityID next : changed.getChangedEntities()) {
            unexploredBuildings.remove(next);
        }
    }
    
    private void informCenter (int time, ChangeSet changed) {
    	
    	// inform Center with position
    	addMessage(new PositionInformation (time, me().getID(),
											me().getLocation(this.model)));
		

    	// Inform Center with precepted entities
    	StandardEntity entity;
		BlockadeInformation blockadeInfo;
		BuildingInformation buildingInfo;
		VictimInformation victimInfo;
		
		for(EntityID id : changed.getChangedEntities()){
			entity = this.model.getEntity(id);
			
			if(entity instanceof Blockade){ // Blockade
				Blockade blockade = (Blockade) entity;
				if(blockade.isPositionDefined() & blockade.isRepairCostDefined()){
					blockadeInfo = new BlockadeInformation(time, blockade.getID(),
							blockade.getPosition(), blockade.getRepairCost());
					addMessage(blockadeInfo);
				}
			}else if(entity instanceof Civilian){ // Civilian
				Civilian victim = (Civilian) entity;
				if(victim.isPositionDefined() & victim.isHPDefined()
					& victim.isBuriednessDefined() & victim.isDamageDefined()){
					victimInfo = new VictimInformation (time, 
														victim.getID(),
														victim.getPosition(), 
														victim.getHP(), 
														victim.getBuriedness(),
														victim.getDamage(),
														victim.getLocation(model));
					addMessage(victimInfo);
					
				}
			}else if(entity instanceof AmbulanceTeam) { // ambulance team
				AmbulanceTeam ambulance = (AmbulanceTeam) entity;
				if(ambulance.isPositionDefined() & ambulance.isHPDefined()
						& ambulance.isBuriednessDefined() & ambulance.isDamageDefined()){
					AmbulanceTeamInformation ambulanceInfo = new AmbulanceTeamInformation(time, 
																ambulance.getID(),
																ambulance.getHP(), 
																ambulance.getDamage(),
																ambulance.getBuriedness(),
																ambulance.getPosition());
					addMessage(ambulanceInfo);
			}else if (entity instanceof Refuge) { // refuge
				Refuge building = (Refuge) entity;
				buildingInfo = new BuildingInformation (time, 
														building.getID(),
														-1, 
														-1);
				addMessage(buildingInfo);
				// inform Center with position
		    	addMessage(new PositionInformation (time,
		    										building.getID(),
		    										building.getLocation(this.model)));
				
					
			}
		}
    }
    }
}
