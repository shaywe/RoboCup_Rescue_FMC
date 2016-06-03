package fmcp;

import java.util.Collection;
import java.util.EnumSet;

import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.messages.Command;
import rescuecore2.log.Logger;

import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.StandardEntityURN;


import rescuecore2.worldmodel.EntityID;
import java.util.List;

import commlib.components.AbstractCSAgent;
import commlib.data.RCRSCSData;
import commlib.information.AmbulanceTeamInformation;
import commlib.information.PositionInformation;
import commlib.information.VictimInformation;
import commlib.information.WorldInformation;
import commlib.message.BaseMessageType;
import commlib.message.RCRSCSMessage;
import commlib.report.ReportMessage;
import commlib.task.TaskMessage;
import commlib.message.BaseMessageType;



import commlib.message.RCRSCSMessage;
import rescuecore2.Constants;
import rescuecore2.standard.kernel.comms.ChannelCommunicationModel;


/**
   A central agent.
 */
public class CentralAgent extends AbstractCSAgent<Building> {
	
	private DataList agents;
	private DataList victims;
	private Utility utilities [][];
	
	
	
	private boolean	channelComm = this.status = nelComm;
	
	
	@Override
    public String toString() {
        return "centre agent";
    }
	
	@Override
	public void postConnect(){
		boolean speakComm = config.getValue(Constants.COMMUNICATION_MODEL_KEY)
				.equals(ChannelCommunicationModel.class.getName());
		
		int numChannels = this.config.getIntValue("comms.channels.count");
		if((speakComm) && (numChannels > 1)){
			this.channelComm = true;
		}else{
			this.channelComm = false;
		}
	}

    
    /**
     * Implements the agent specific processing
     */
    protected void thinking (int time, ChangeSet change, Collection <Command> heard) {
    	//super.receiveMessage(heard);
        //this.thinking(time, changed, heard);
        //super.sendMessage(time);
    	if (time == config.getIntValue(kernel.KernelConstants.IGNORE_AGENT_COMMANDS_KEY)) {
        	if(channelComm){
                int channel = 1; // the channel the agent is going to use in order to send and receive messages
				// Assign the agent to channel 1
				setMessageChannel(channel); // shall be used once --> in a simulation or a time step?
			}
        }
    	
    	
    	// consolidate and update information
    	consolidateData();
    	
    	// inform agents??
    	
    	// run FMC_TA
    	
    	// assign tasks
    	
    	// rest
    	sendRest(time);
    }
    
    private void consolidateData () {
    	for (RCRSCSMessage msg : receivedMessageList) {
    		System.out.println(msg.getSendTime());
			System.out.println(msg.isSendable());
			System.out.println(msg.getMessageType());
			System.out.println(msg.getMessageType() == BaseMessageType.POSITION);
			System.out.println(msg instanceof WorldInformation);
			
			System.out.println("*****msg*******");
			if (msg instanceof TaskMessage) {
				System.out.println("CENTER RECEIVED TASK MESSAGE");
			}
			
			switch (msg.getMessageType()) {
				//
			case AMBULANCE_TEAM:
				//
				agents.updateAgentData(((AmbulanceTeamInformation)msg).getEntityID(),
										((AmbulanceTeamInformation)msg).getHP(),
										((AmbulanceTeamInformation)msg).getDamage(),
										((AmbulanceTeamInformation)msg).getPositionID(),
										((AmbulanceTeamInformation)msg).getBuriedness());
				break;
				//
			case VICTIM:
				// 
				victims.updateAgentData(((VictimInformation)msg).getVictimID(),
										((VictimInformation)msg).getHP(),
										((VictimInformation)msg).getDamage(),
										((VictimInformation)msg).getAreaID(),
										((VictimInformation)msg).getBuriedness(),
										((VictimInformation)msg).getCoodinate());
				// execute algorithm and assign tasks to agents
				
				break;
			case POSITION:
				//
				agents.updateAgentData( ((PositionInformation)msg).getAgentID(),
										((PositionInformation)msg).getCoordinate());
				break;
			case BLOCKADE:
				//
				//blockades.updateAgentData(id, position);
				break;
				
			case BUILDING:
				//
				
				break;
				
			case CLEAR_ROUTE:
				break;
			case DECIDE_LEADER:
				break;
			
			case EXTINGUISH_AREA:
				break;
			case REST_TASK:
				break;
			case SCOUT_AREA:
				break;
			case RESCUE_AREA:
				break;
			case REST_AT_REFUGE_TASK:
				break;
				
				
			case BLOCKADE_WITH_COORDINATE:
				break;
			case DONE:
				break;
			case EXCEPTION:
				break;
			
			case FIRE_BRIGADE:
				break;
			case MOVE_TASK:
				break;
			case MOVE_WITH_STAGING_POST_TASK:
				break;
			case POLICE_FORCE:
				break;
			
		
			
			case TRANSFER_PATHWAY:
				break;
			case UNPASSABLE:
				break;
			case VICTIM_WITH_COORDINATE:
				break;
				
			default:
				break;
			}
			
			
			for (RCRSCSData<?> data: msg.getData()) {
				System.out.println("*****data*******");
				System.out.println(data);
				System.out.println(data.getData());
				System.out.println(data.getType());
			}
		
    	}

    }

    @Override
    protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
        return EnumSet.of(StandardEntityURN.AMBULANCE_CENTRE);
    }
    
    public Utility[][] getUtilities () {
    	return this.utilities;
    }
    
    public void setUtilityOf (int i, int j, Utility utility) {
    	
    }
}

