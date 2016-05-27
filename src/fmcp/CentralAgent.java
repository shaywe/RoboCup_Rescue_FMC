package fmcp;

import java.util.Collection;
import java.util.EnumSet;

import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.messages.Command;
import rescuecore2.log.Logger;

//import rescuecore2.standard.components.StandardAgent;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.StandardEntityURN;


import rescuecore2.worldmodel.EntityID;
import java.util.List;

import commlib.components.AbstractCSAgent;
import commlib.information.WorldInformation;
import commlib.message.RCRSCSMessage;
import commlib.report.ReportMessage;
import commlib.task.TaskMessage;


import commlib.message.RCRSCSMessage;
import rescuecore2.Constants;
import rescuecore2.standard.kernel.comms.ChannelCommunicationModel;


/**
   A central agent.
 */
public class CentralAgent extends AbstractCSAgent<Building> {
	
	private List<AmbulanceAgent> agents;
	private List<EntityID> targets;
	
	private boolean	channelComm;
	
	public CentralAgent() {
		agents.get(0).
	}
	
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
        	if(this.channelComm){
                int channel = 1; // the channel the agent is going to use in order to send and receive messages
				// Assign the agent to channel 1
				setMessageChannel(channel); // shall be used once --> in a simulation or a time step?
			}
        }
    	
    	// consolidate and update information
    	for (RCRSCSMessage message : receivedMessageList) { //a list of RCRSCMessage
        	Logger.info(message.toString());
    		if (message instanceof WorldInformation) { //Information message
    			
    		}
    		else {
    			if (message instanceof ReportMessage) { //Report message
    				
    			}
    			else {
        			if (message instanceof TaskMessage) {
        				
        			}
        		}
    		}
    		
    	}
    	
    	// inform agents??
    	
    	// run FMC_TA
    	
    	// assign tasks
    	
    	// rest
    	sendRest(time);
    }
    


    @Override
    protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
        return EnumSet.of(StandardEntityURN.AMBULANCE_CENTRE);
    }
}

