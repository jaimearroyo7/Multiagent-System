package ontology;

import ontology.*;

import jade.util.leap.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#MasadeAgua
* @author OntologyBeanGenerator v4.1
* @version 2018/06/21, 11:49:00
*/
public interface MasadeAgua extends jade.content.Concept {

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#DBO
   */
   public void setDBO(int value);
   public int getDBO();

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#Capacidad
   */
   public void setCapacidad(int value);
   public int getCapacidad();

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#Contaminantes
   */
   public void addContaminantes(int elem);
   public boolean removeContaminantes(int elem);
   public void clearAllContaminantes();
   public Iterator getAllContaminantes();
   public List getContaminantes();
   public void setContaminantes(List l);

}
