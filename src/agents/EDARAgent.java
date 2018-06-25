/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

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
public class EDARAgent extends Agent{
    
    private AID rio;
    private boolean rioFound = false;
    private AID[] industrias;
    private int capacidadMax = 12000;
    private int capacidad = 1000;
    private int depuracion = 800;
    private double pricetoBuy = 10.0;
    
    protected void setup(){
        System.out.println("Hallo! EDAR agent "+getAID().getName()+" is ready.");
        
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            if(args.length > 0)
                capacidadMax = Integer.parseInt((String) args[0]);
            if(args.length > 1)
                depuracion = Integer.parseInt((String) args[1]);
            if(args.length > 2)
                pricetoBuy = Integer.parseInt((String) args[2]);
            
        }
        
        //agent descriptor
        ServiceDescription sd = new ServiceDescription();
        sd.setType( "EDAR" );
        sd.setName( "EDARDescriptor");
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName( getAID() );
        dfd.addServices( sd );      
        try {
            DFService.register( this, dfd );
        } catch (FIPAException ex) {
            Logger.getLogger(EDARAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Behaviour encargado de recibir peticiones de las industrias
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                    MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("envio-agua"),
                            MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
                    ACLMessage msg = receive(mt);
                    if(msg != null){
                        
                        ACLMessage reply = msg.createReply();
                        String[] priceperliter = msg.getContent().split("\\|");
                        
                        int litros = Integer.parseInt(priceperliter[0]);
                        int price = Integer.parseInt(priceperliter[1]);
                        if (capacidad + litros <= capacidadMax) {
                            if((double) litros / (double) price <= pricetoBuy){
                                System.out.println("EDAR acepta la propuesta de " + msg.getSender().getName());
                                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                reply.setContent(String.valueOf(litros));
                                capacidad += litros;
                            }
                            else{
                                reply.setPerformative(ACLMessage.REFUSE);
                                reply.setContent("Precio muy bajo");
                            }
                        }
                        else{
                            reply.setPerformative(ACLMessage.REFUSE);
                            reply.setContent("No hay espacio suficiente disponible");
                        }
                        send(reply);
                    }
                    else{
                        block();
                    }
                    
            }
        });
        //Behaviour encargado de reestablece el agua en caso que finalmente no reciba el agua de la industria
        addBehaviour(new CyclicBehaviour(this) {
           public void action() {
                   MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("envio-agua"),
                           MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
                   ACLMessage msg = receive(mt);
                   if(msg != null){
                       int litros = Integer.parseInt(msg.getContent());
                       capacidad -= litros;
                   }
                   else{
                       block();
                   }

           }
       });
        
        //Behaviour principal, donde el agente decide como actuar
        addBehaviour(new TickerBehaviour(this, 5000) {
            private int day = 0;
            protected void onTick() {
                    ++day;
                    System.out.println();
                    System.out.println("------------------------------------------------------------------------------");
                    if(day < 10) System.out.print("-");
                    if(day < 100) System.out.print("-");
                    System.out.println("--------------------------------- Dia " + String.valueOf(day) + " ------------------------------------");
                    System.out.println("------------------------------------------------------------------------------");
                    
                    System.out.println("Llueve un poco, el tanque de la EDAR se va llenando...");
                    System.out.println("CAPACIDAD EDAR: " +String.valueOf(capacidad));
                    capacidad = Math.min(capacidadMax, capacidad + 30);
                    
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
                            System.out.println("Buscando rio...");
                            rioFound = false;
                        }
                        else if(result.length == 1){
                            rio = result[0].getName();
                            rioFound = true;
                        }
                        else{
                            System.out.println("Se ha introducido mas de un rio! Solo se tirara el agua a uno de ellos...");
                            rio = result[0].getName();
                            rioFound = true;
                        }
                    }
                    catch (FIPAException fe) {
                            fe.printStackTrace();
                    }        
                    //Si puedes, extrae agua del rio para procesarla
                    int expulsa = Math.min(depuracion, capacidad);
                    
                    if(expulsa > 0 && rioFound){
                        ACLMessage depuraMsg = new ACLMessage(ACLMessage.INFORM);
                        depuraMsg.addReceiver(rio);
                        depuraMsg.setContent("Diposita");
                        send(depuraMsg); 
                        capacidad -= expulsa;
                    }
                    //CALL FOR PROPOSAL
                    if(((double)capacidad / (double)capacidadMax) < 0.2){
                        DFAgentDescription dfdInd = new DFAgentDescription();
                        ServiceDescription sdInd = new ServiceDescription();
                        sdInd.setType( "Industria" );
                        dfdInd.addServices(sdInd);

                        SearchConstraints ALLInd = new SearchConstraints();
                        ALLInd.setMaxResults(new Long(-1));

                        try {
                            DFAgentDescription[] res = DFService.search(myAgent, dfdInd, ALLInd); 
                            industrias = new AID[res.length];
                            for(int i = 0; i < res.length; ++i){
                                industrias[i] = res[i].getName();
                            }
                        }
                        
                        catch (FIPAException fe) {
                                fe.printStackTrace();
                        }
                        myAgent.addBehaviour(new CallForProposalPerformer());
                    }
            }
        } );
    }
    
    //clase para describir el call for proposal
    private class CallForProposalPerformer extends Behaviour{
        private AID bestSeller = null; // The agent who provides the best offer 
        private int bestPrice=100000;  // The best offered price
        private int repliesCnt = 0; // The counter of replies from seller agents
        private MessageTemplate mt; // The template to receive replies
        private int step = 0;
        @Override
        public void action() {
            switch(step){
                case 0:
                    //Peticion de proposals
                    int percentage = (int)(((double)capacidad / (double)capacidadMax)*100);
                    String capacityString = String.valueOf(percentage);
                    System.out.println("CALL FOR PROPOSAL del EDAR. Capacidad al " + capacityString + "%.");
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < industrias.length; ++i) {
                            cfp.addReceiver(industrias[i]);
                    } 
                    cfp.setConversationId("cfp");
                    cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp);
                    // Prepare the template to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("cfp"),
                                    MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    step = 1;
                    break;
                case 1:
                    //analisis de propuestas recibidas
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                            // Reply received
                            if (reply.getPerformative() == ACLMessage.PROPOSE) {
                                    // This is an offer 
                                    int liters = Integer.parseInt(reply.getContent());
                                    if (bestSeller == null || liters > bestPrice) {
                                            // This is the best offer at present
                                            bestPrice = liters;
                                            bestSeller = reply.getSender();
                                    }
                            }
                            repliesCnt++;
                            if (repliesCnt >= industrias.length) {
                                    System.out.println("CFP: EDAR ya ha recibido todas las propuestas");
                                    step = 2; 
                            }
                    }
                    else {
                            block();
                    }
                    break;
                case 2:
                    //Envio de aceptacion de propuesta al bestSeller
                    System.out.println("CFP: EDAR acepta la propuesta de " + bestSeller.getName());
                    ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    accept.addReceiver(bestSeller);
                    accept.setConversationId("cfp");
                    accept.setReplyWith("cpf"+System.currentTimeMillis());
                    myAgent.send(accept);
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("cfp"),
                                    MessageTemplate.MatchInReplyTo(accept.getReplyWith()));
                    step = 3;
                    break;
                case 3:      
                    // Finaliza
                    reply = myAgent.receive(mt);
                    if (reply != null) {
                            if (reply.getPerformative() == ACLMessage.INFORM) {
                                
                                    System.out.println("CPF: La planta depuradora recibe " + String.valueOf(bestPrice) + "de la industria "+reply.getSender().getName());
                                    capacidad += bestPrice;
                            }
                            else {
                                    System.out.println("CPF: No se ha llevado a cabo el cfp");
                            }
                            step = 4;
                    }
                    else {
                            block();
                    }
                    break;
            }  
        }

        @Override
        public boolean done() {
            if (step == 2 && bestSeller == null) {
                System.out.println("CPF: Sin industrias...");
            }
            return ((step == 2 && bestSeller == null) || step == 4);
        }
    }
    
    
    
}
