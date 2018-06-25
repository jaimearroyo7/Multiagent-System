package ontology;

import ontology.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#Rio
* @author OntologyBeanGenerator v4.1
* @version 2018/06/21, 11:49:00
*/
public class DefaultRio implements Rio {

  private static final long serialVersionUID = -461650205053931224L;

  private String _internalInstanceName = null;

  public DefaultRio() {
    this._internalInstanceName = "";
  }

  public DefaultRio(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#CapacidadMax
   */
   private int capacidadMax;
   public void setCapacidadMax(int value) { 
    this.capacidadMax=value;
   }
   public int getCapacidadMax() {
     return this.capacidadMax;
   }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#hasWater
   */
   private MasadeAgua hasWater;
   public void setHasWater(MasadeAgua value) { 
    this.hasWater=value;
   }
   public MasadeAgua getHasWater() {
     return this.hasWater;
   }

}
