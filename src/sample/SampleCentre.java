package sample;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collection;
import java.util.EnumSet;

import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.messages.Command;
import rescuecore2.log.Logger;

import rescuecore2.standard.components.StandardAgent;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Refuge;
import rescuecore2.standard.entities.Road;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;

/**
   A sample centre agent.
 */
public class SampleCentre extends StandardAgent<Building> {
    @Override
    public String toString() {
        return "Sample centre";
    }
    
    @Override
    protected void postConnect() {
    	super.postConnect();
    	BufferedWriter out = null;
        try {
        	out = new BufferedWriter(new FileWriter("TESTFILE.txt"));
        }
        catch (Exception e) {
        	
        }
        for (StandardEntity next : model) {
        	if (next instanceof Refuge) {
        		try {
                	out.write(this.getID() + " --- "+next + " -location:  " + next.getLocation(model));
                	out.newLine();
                }
                catch (Exception e) {
                	
                }
        	}
            
            
            
        }
    }
    
    @Override
    protected void think(int time, ChangeSet changed, Collection<Command> heard) {
        if (time == config.getIntValue(kernel.KernelConstants.IGNORE_AGENT_COMMANDS_KEY)) {
            // Subscribe to channels 1 and 2
            sendSubscribe(time, 1, 2);
        }
        
        
        BufferedWriter out = null;
        try {
        	out = new BufferedWriter(new FileWriter("TESTFILE.txt"));
        }
        catch (Exception e) {
        	
        }
        for (StandardEntity next : model) {
            try {
            	out.write(this.getID() + " --- "+next + " -location:  " + next.getLocation(model));
            	out.newLine();
            }
            catch (Exception e) {
            	
            }
            
            
        }
        
        for (Command next : heard) {
            Logger.debug("Heard " + next);
        }
        sendRest(time);
    }

    @Override
    protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
        return EnumSet.of(StandardEntityURN.FIRE_STATION,
                          StandardEntityURN.AMBULANCE_CENTRE,
                          StandardEntityURN.POLICE_OFFICE);
    }
}
