/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import ontology.*;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
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
public class IndustriaAgent extends Agent{
    private AID rio;
    private AID EDAR;
    private boolean rioFound = false;
    private boolean edarFound = false;
    private int capacidadMax = 2000;
    private int capacidad = 1000;
    private int extrae = 300;
    private int sellPrice = 50;
    private int literscfp = 0;
    private boolean proposing = false;
    
    protected void setup(){
        System.out.println("Hallo! Industry agent "+getAID().getName()+" is ready.");
        
        //parameters
        
        //DefaultMasadeAgua agua = new DefaultMasadeAgua();
        //agua.setCapacidad(0);
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            if(args.length > 0)
                capacidadMax = Integer.parseInt((String) args[0]);
            if(args.length > 1)
                extrae = Integer.parseInt((String) args[1]);
            if(args.length > 2)
                sellPrice = Integer.parseInt((String) args[2]);
            
        }
        
        //agent descriptor
        ServiceDescription sd = new ServiceDescription();
        sd.setType( "Industria" );
        sd.setName( "IndustriaDescriptor");
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName( getAID() );
        dfd.addServices( sd );      
        try {
            DFService.register( this, dfd );
        } catch (FIPAException ex) {
            Logger.getLogger(IndustriaAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        //behaviour para decidir si se necesita pedir permiso para tirar agua al alcantarillado
        addBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType( "EDAR" );
                dfd.addServices(sd);

                SearchConstraints ALL = new SearchConstraints();
                ALL.setMaxResults(new Long(-1));

                try {
                    DFAgentDescription[] result = DFService.search(myAgent, dfd, ALL); 
                    AID[] auxEDAR = new AID[result.length];
                    if(result.length == 0){
                        System.out.println(getAID().getName() + ": Buscando depuradora...");
                        edarFound = false;
                    }
                    else if(result.length == 1){
                        EDAR = result[0].getName();
                        edarFound = true;
                    }
                    else{
                        System.out.println("Se ha introducido mas de una depuradora! Solo se enviara agua a una de ellas");
                        EDAR = result[0].getName();
                        edarFound = true;
                    }
                }
                catch (FIPAException fe) {
                        fe.printStackTrace();
                }

                if(((double) capacidad / (double)capacidadMax) > 0.7){
                    if(proposing==false && edarFound)
                        myAgent.addBehaviour(new RequestPerformer());
                }       
            }
        } );

        //behaviour para obtener agua del rio
        addBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                    DFAgentDescription dfd = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType( "River" );
                    dfd.addServices(sd);
                    
                    SearchConstraints ALL = new SearchConstraints();
                    ALL.setMaxResults(new Long(-1));
                    
                    try {
                        DFAgentDescription[] result = DFService.search(myAgent, dfd, ALL); 
                        AID[] auxRio = new AID[result.length];
                        if(result.length == 0){
                            System.out.println(getAID().getName() + ": Buscando rio...");
                            rioFound = false;
                        }
                        else if(result.length == 1){
                            rio = result[0].getName();
                            rioFound = true;
                        }
                        else{
                            System.out.println("Se ha introducido mas de un Rio! Solo se recogera agua de uno de ellos");
                            rio = result[0].getName();
                            rioFound = true;
                        }
                    }
                    catch (FIPAException fe) {
                            fe.printStackTrace();
                    }        
                    //Si puedes, extrae agua del rio para procesarla
                    if(capacidad + extrae <= capacidadMax && rioFound){
                        ACLMessage extractMsg = new ACLMessage(ACLMessage.INFORM);
                        extractMsg.addReceiver(rio);
                        extractMsg.setContent("EXTRAE");
                        send(extractMsg);
                        
                        capacidad += extrae;
                    }
                    else if(rioFound)
                        System.out.println("Industria " + getAID().getName() + " está casi a capacidad maxima y no puede coger mas agua del rio. CAPACIDAD: " + String.valueOf(capacidad));             
            }
        } );
        
        addBehaviour(new CFPProposePerformer());
        addBehaviour(new CFPSendPerformer());
    }
    //Behaviour que lleva a cabo la peticion de envio de agua a la planta
    private class RequestPerformer extends Behaviour {
        private int step = 0;
        private boolean sent = false;
        private int literstosend = capacidad;
        private int pricetosend = sellPrice;
        private MessageTemplate mt;
        private int intento = 0;
        @Override
        public void action() {
            switch(step){
                case 0:
                    //Envia propuesta
                    ++intento;
                    proposing = true;
                    System.out.println("Industia " +getAID().getName()+ " propone enviar " + String.valueOf(literstosend) + " litros a la planta depuradora por " + String.valueOf(pricetosend) + " euros. (Num de intentos " + String.valueOf(intento) +").");
                    ACLMessage propose = new ACLMessage(ACLMessage.PROPOSE);
                    propose.addReceiver(EDAR);
                    propose.setConversationId("envio-agua");
                    propose.setContent(String.valueOf(literstosend)+ "|" + String.valueOf(pricetosend));
                    propose.setReplyWith("envio"+System.currentTimeMillis());
                    
                    send(propose);				
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("envio-agua"),
						MessageTemplate.MatchInReplyTo(propose.getReplyWith()));
                   ++step;
                   break;
                case 1:
                    ACLMessage reply = myAgent.receive(mt);
                    if(reply != null){
                        //Propuesta aceptada
                       if (reply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                           int litros = Integer.parseInt(reply.getContent());
                            if(capacidad >= litros){
                                capacidad -= litros;
                                System.out.println("Industia " +getAID().getName()+ " envia " + String.valueOf(litros) + " litros a la planta depuradora. CAPACIDAD: " + String.valueOf(capacidad));
                            }
                            else{
                                ACLMessage refuse = reply.createReply();
                                refuse.addReceiver(EDAR);
                                refuse.setConversationId("envio-agua");
                                refuse.setPerformative(ACLMessage.REFUSE);
                                refuse.setContent(String.valueOf(literstosend));
                                refuse.setReplyWith("envio"+System.currentTimeMillis());
                                send(refuse);
                                System.out.println("Industia " +getAID().getName()+ " ya ha mandado el agua mientras en un CFP");
                            }

                           proposing = false;
                           sent = true;
                       } //Si rechazado, toca mejorar la oferta
                       else{
                           String respuesta = reply.getContent();
                           System.out.println("Industia " +getAID().getName()+ " no ha podido enviar agua a la depuradora: " + respuesta);
                           step = 0;
                           if(respuesta.equals("Precio muy bajo"))
                                pricetosend *= 1.5;
                           else
                               literstosend /= 2;
                       }
                    }
                    else{
                        block();
                    }
                    break;
            }
        }

        @Override
        public boolean done() {
            return sent;
        }
    }
    
    //Behaviour encargado de recibir cfp
    private class CFPProposePerformer extends CyclicBehaviour {
            @Override
            public void action() {
                    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                    ACLMessage msg = myAgent.receive(mt);
                    if (msg != null) {
                            // CFP Message received. Process it
                            ACLMessage reply = msg.createReply();
                            if(((double) capacidad / (double)capacidadMax) > 0.1){
                                    System.out.println("CFP: Industria " + getAID().getName() +" envia su propuesta del CFP");
                                    reply.setConversationId("cfp");
                                    reply.setPerformative(ACLMessage.PROPOSE);
                                    reply.setContent(String.valueOf(capacidad));
                                    literscfp = capacidad;
                            }
                            else {
                                    reply.setPerformative(ACLMessage.REFUSE);
                                    reply.setContent("CFP: Agua insuficiente para enviar");
                            }
                            myAgent.send(reply);
                    }
                    else {
                            block();
                    }
            }
    }
    
    //behaviour encargado de recibir mensajes de aceptacion de cfp y enviar el resultado
    private class CFPSendPerformer extends CyclicBehaviour {
            public void action() {
                    MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("cfp"),
                                    MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
                    ACLMessage msg = myAgent.receive(mt);
                    if (msg != null) {
                            // ACCEPT_PROPOSAL Message received. Process it
                            ACLMessage reply = msg.createReply();
                            if (literscfp <= capacidad) {
                                    reply.setPerformative(ACLMessage.INFORM);
                                    //reply.setContent()
                                    reply.setConversationId("cfp");
                                    System.out.println("CFP: Industria " + getAID().getName() + " envia " + String.valueOf(literscfp) + "a la planta de tratamiento");
                                    capacidad -= literscfp;
                            }
                            else {
                                    reply.setPerformative(ACLMessage.FAILURE);
                                    reply.setConversationId("cfp");
                                    reply.setContent("CFP: El agua ya se habia enviado... :(");
                            }
                            myAgent.send(reply);
                    }
                    else {
                            block();
                    }
            }
    } 
}
