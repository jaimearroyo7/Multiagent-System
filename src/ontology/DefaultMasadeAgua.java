package ontology;

import ontology.*;
import jade.util.leap.*;


/**
* Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#MasadeAgua
* @author OntologyBeanGenerator v4.1
* @version 2018/06/21, 11:49:00
*/
public class DefaultMasadeAgua implements MasadeAgua {

  private static final long serialVersionUID = -461650205053931224L;

  private String _internalInstanceName = null;

  public DefaultMasadeAgua() {
    this._internalInstanceName = "";
  }

  public DefaultMasadeAgua(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#DBO
   */
   private int dbO;
   public void setDBO(int value) { 
    this.dbO=value;
   }
   public int getDBO() {
     return this.dbO;
   }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#Capacidad
   */
   private int capacidad;
   public void setCapacidad(int value) { 
    this.capacidad=value;
   }
   public int getCapacidad() {
     return this.capacidad;
   }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#Contaminantes
   */
   private List contaminantes = new ArrayList();
   public void addContaminantes(int elem) { 
     contaminantes.add(elem);
   }
   public boolean removeContaminantes(int elem) {
     boolean result = (boolean) contaminantes.remove(elem);
     return result;
   }
   public void clearAllContaminantes() {
     contaminantes.clear();
   }
   public Iterator getAllContaminantes() {return contaminantes.iterator(); }
   public List getContaminantes() {return contaminantes; }
   public void setContaminantes(List l) {contaminantes = l; }

}
