package fmcp.Sim;

import java.util.List;
import java.util.Vector;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;

import rescuecore2.worldmodel.EntityID;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.messages.Command;
import rescuecore2.log.Logger;

import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.standard.entities.AmbulanceTeam;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.entities.Civilian;
import rescuecore2.standard.entities.Refuge;
import commlib.message.RCRSCSMessage;
import commlib.report.DoneReportMessage;
import commlib.task.at.RescueAreaTaskMessage;
import commlib.information.AmbulanceTeamInformation;
import commlib.information.BlockadeInformation;
import commlib.information.PositionInformation;
import commlib.information.VictimInformation;
import rescuecore2.Constants;
import rescuecore2.standard.entities.Blockade;
import rescuecore2.standard.kernel.comms.ChannelCommunicationModel;

/**
 * Ambulance Team
 */
public class AmbulanceAgent extends AbstractAgent<AmbulanceTeam> {
	private Collection<EntityID> unexploredBuildings;
	private boolean channelComm;
	private List<Human> tasks;

	@Override
	public String toString() {
		return "ambulance agent";
	}

	@Override
	protected void postConnect() {
		super.postConnect();

		model.indexClass(StandardEntityURN.CIVILIAN, StandardEntityURN.FIRE_BRIGADE, StandardEntityURN.POLICE_FORCE,
				StandardEntityURN.AMBULANCE_TEAM, StandardEntityURN.REFUGE, StandardEntityURN.HYDRANT,
				StandardEntityURN.GAS_STATION, StandardEntityURN.BUILDING); // change???

		unexploredBuildings = new HashSet<EntityID>(buildingIDs);
		
		tasks = new Vector<Human>();
		
		// remove this???
		boolean speakComm = config.getValue(Constants.COMMUNICATION_MODEL_KEY)
				.equals(ChannelCommunicationModel.class.getName());

		int numChannels = this.config.getIntValue("comms.channels.count");

		if ((speakComm) && (numChannels > 1)) {
			this.channelComm = true;
		} else {
			this.channelComm = false;
		}
	}

	/**
	 * this what happens at extended class before 'thinking' is called:
	 * super.receiveMessage(heard); this.thinking(time, changed, heard);
	 * super.sendMessage(time);
	 */

	protected void thinking(int time, ChangeSet changed, Collection<Command> heard) {
		// set communication channel
		setChannel(time, channelComm);

		/*
		 * inform center: 1. inform with self position 2. inform with precept
		 * entities (Building and Victims) in its radius
		 */
		informCenter(time, changed);

		// get tasks from Center
		getTasks(time);

		// update
		updateUnexploredBuildings(changed);

		// Am I transporting a civilian to a refuge?
		if (someoneOnBoard()) {
			// Am I at a refuge?
			if (location() instanceof Refuge) {
				// Unload!
				sendUnload(time);
				tasks.remove(whoIsOnBoard());
				addMessage(new DoneReportMessage(time, me().getID()));
				return;
			} else {
				// Move to a refuge
				List<EntityID> path = search.breadthFirstSearch(me().getPosition(), refugeIDs);
				if (path != null) {
					Logger.info("Moving to refuge");
					sendMove(time, path);
					return;
				}
				// What do I do now? Might as well carry on and see if we can
				// dig
				// someone else out.
				Logger.debug("Failed to plan path to refuge");
			}

		}

		for (Human next : tasks) {
			if (next.getHP() <= 0) {
				//is Dead 
				tasks.remove(next);
			}
			else {
				if (next.getPosition().equals(location().getID())) {
					// Targets in the same place might need rescuing or loading
					if (next.getBuriedness() == 0 && !(location() instanceof Refuge)) {
						// Load
						Logger.info("Loading " + next);
						// addMessage(message);
						sendLoad(time, next.getID());
						return;
					}
					if (next.getBuriedness() > 0) {
						// Rescue
						Logger.info("Rescueing " + next);
						// addMessage(message);
						sendRescue(time, next.getID());
						return;
					}
				} 
				else {
					// Try to move to the target
					List<EntityID> path = search.breadthFirstSearch(me().getPosition(), next.getPosition());
					if (path != null) {
						Logger.info("Moving to target");
						sendMove(time, path);
						return;
					}
				}
			}
		}

		// Nothing to do
		List<EntityID> path = search.breadthFirstSearch(me().getPosition(), unexploredBuildings);
		if (path != null) {
			Logger.info("Searching buildings");
			sendMove(time, path);
			return;
		}
		Logger.info("Moving randomly");
		sendMove(time, randomWalk());
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.AMBULANCE_TEAM);
	}

	/**
	 * returns whether the agent is carried by this agent
	 */
	private boolean someoneOnBoard() {
		for (StandardEntity next : model.getEntitiesOfType(StandardEntityURN.CIVILIAN)) {
			if (((Human) next).getPosition().equals(getID())) {
				Logger.debug(next + " is on board");
				return true;
			}
		}
		return false;
	}
	
	private EntityID whoIsOnBoard () {
		EntityID ans = null;
		for (StandardEntity next : model.getEntitiesOfType(StandardEntityURN.CIVILIAN)) {
			if (((Human) next).getPosition().equals(getID())) {
				return next.getID();
			}
		}
		return ans;
		
	}

	private void updateUnexploredBuildings(ChangeSet changed) {
		for (EntityID next : changed.getChangedEntities()) {
			unexploredBuildings.remove(next);
		}
	}

	private void getTasks(int time) {

		for (RCRSCSMessage msg : this.receivedMessageList) {
			switch (msg.getMessageType()) {

			case RESCUE_AREA:
				// change next to the civilian's identifier
				RescueAreaTaskMessage taskMsg = (RescueAreaTaskMessage) (msg);
				if (taskMsg.getAssignedAgentID().equals(me().getID())) { // not validating ownwer id because we are using one cneter
					// if this task is for me
					tasks.clear();
					for (EntityID task : taskMsg.getTargetAreaList()) {
						tasks.add((Human) model.getEntity(task));
					}
				}

				break;
			case SCOUT_AREA:
				//
				// Nothing to do
				List<EntityID> path = search.breadthFirstSearch(me().getPosition(), unexploredBuildings);
				if (path != null) {
					Logger.info("Searching buildings");
					sendMove(time, path);
					return;
				}

				Logger.info("Moving randomly");
				sendMove(time, randomWalk());
				// someone else out.
				Logger.debug("Failed to plan path to refuge");
				break;
			case AMBULANCE_TEAM:
				break;
			case BLOCKADE:
				break;
			case BLOCKADE_WITH_COORDINATE:
				break;
			case BUILDING:
				break;
			case CLEAR_ROUTE:
				break;
			case DECIDE_LEADER:
				break;
			case DONE:
				break;
			case EXCEPTION:
				break;
			case EXTINGUISH_AREA:
				break;
			case FIRE_BRIGADE:
				break;
			case MOVE_TASK:
				break;
			case MOVE_WITH_STAGING_POST_TASK:
				break;
			case POLICE_FORCE:
				break;
			case POSITION:
				break;
			case REST_AT_REFUGE_TASK:
				break;
			case REST_TASK:
				break;
			case TRANSFER_PATHWAY:
				break;
			case UNPASSABLE:
				break;
			case VICTIM:
				break;
			case VICTIM_WITH_COORDINATE:
				break;
			default:
				break;
			}
		}
	}

	private void informCenter(int time, ChangeSet changed) {
		// inform Center with position
		addMessage(new PositionInformation(time, me().getID(), me().getLocation(this.model)));

		// Inform Center with precepted entities
		StandardEntity entity;
		BlockadeInformation blockadeInfo;
		VictimInformation victimInfo;

		for (EntityID id : changed.getChangedEntities()) {
			entity = this.model.getEntity(id);

			if (entity instanceof Blockade) { // Blockade
				Blockade blockade = (Blockade) entity;
				if (blockade.isPositionDefined() & blockade.isRepairCostDefined()) {
					blockadeInfo = new BlockadeInformation(	time, 
															blockade.getID(), 
															blockade.getPosition(),
															blockade.getRepairCost());
					addMessage(blockadeInfo);
				}
			} else if (entity instanceof Civilian) { // Civilian
				Civilian victim = (Civilian) entity;
				if (victim.isDamageDefined()) {
					if (victim.getDamage() > 0) {
						if (victim.isPositionDefined() & victim.isHPDefined() & victim.isBuriednessDefined()
								& victim.isDamageDefined()) {
							victimInfo = new VictimInformation(	time, 
																victim.getID(), 
																victim.getPosition(),
																victim.getHP(), 
																victim.getBuriedness(), 
																victim.getDamage());//,
																//victim.getLocation(model));
							long ThreadId = Thread.currentThread().getId();
							System.out.println("# " + ThreadId + " - " +(victim.getLocation(model)));
							addMessage(victimInfo);
						}
					}
				}
			} else if (entity instanceof AmbulanceTeam) { // ambulance team
				AmbulanceTeam ambulance = (AmbulanceTeam) entity;
				if (ambulance.isDamageDefined()) {
					if (ambulance.getDamage() > 0) {
						if (ambulance.isPositionDefined() & ambulance.isHPDefined() & ambulance.isBuriednessDefined()) {
							AmbulanceTeamInformation ambulanceInfo = new AmbulanceTeamInformation(	time,
																									ambulance.getID(), 	
																									ambulance.getHP(), 
																									ambulance.getDamage(),
																									ambulance.getBuriedness(), 
																									ambulance.getPosition());
							addMessage(ambulanceInfo);
						}
					}
				}
			}

		}
	}

}
