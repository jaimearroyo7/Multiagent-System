package ontology;

import ontology.*;


/**
* Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#Industria
* @author OntologyBeanGenerator v4.1
* @version 2018/06/21, 11:49:00
*/
public class DefaultIndustria implements Industria {

  private static final long serialVersionUID = -461650205053931224L;

  private String _internalInstanceName = null;

  public DefaultIndustria() {
    this._internalInstanceName = "";
  }

  public DefaultIndustria(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#priceSell
   */
   private int priceSell;
   public void setPriceSell(int value) { 
    this.priceSell=value;
   }
   public int getPriceSell() {
     return this.priceSell;
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

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#ExtraccionSpeed
   */
   private int extraccionSpeed;
   public void setExtraccionSpeed(int value) { 
    this.extraccionSpeed=value;
   }
   public int getExtraccionSpeed() {
     return this.extraccionSpeed;
   }

}
