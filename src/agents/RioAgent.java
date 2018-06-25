/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jaime
 */
public class RioAgent extends Agent{
    protected void setup(){
        System.out.println("Hallo! River agent "+getAID().getName()+" is ready.");
        //agent descriptor
        ServiceDescription sd = new ServiceDescription();
        sd.setType( "River" );
        sd.setName( "RiverDescriptor");
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName( getAID() );
        dfd.addServices( sd );      
        try {
            DFService.register( this, dfd );
        } catch (FIPAException ex) {
            Logger.getLogger(RioAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                    // listen if a greetings message arrives
                    ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                    if(msg != null){
                        if ("EXTRAE".equals( msg.getContent() )) {
                            System.out.println("Industria: " + msg.getSender().getName() + " extrae agua del rio");
                        }
                        else if ("Diposita".equals( msg.getContent() )) {
                            System.out.println("EDAR: " + msg.getSender().getName() + " diposita agua limpia en el rio");
                        }
                    }
                    else{
                        block();
                    }
            }
        });
    }
}
