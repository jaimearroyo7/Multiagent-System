package ontology;

import ontology.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#EDAR
* @author OntologyBeanGenerator v4.1
* @version 2018/06/21, 11:49:00
*/
public class DefaultEDAR implements EDAR {

  private static final long serialVersionUID = -461650205053931224L;

  private String _internalInstanceName = null;

  public DefaultEDAR() {
    this._internalInstanceName = "";
  }

  public DefaultEDAR(String instance_name) {
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

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#DepuracionSpeed
   */
   private int depuracionSpeed;
   public void setDepuracionSpeed(int value) { 
    this.depuracionSpeed=value;
   }
   public int getDepuracionSpeed() {
     return this.depuracionSpeed;
   }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529436796.owl#priceBuy
   */
   private int priceBuy;
   public void setPriceBuy(int value) { 
    this.priceBuy=value;
   }
   public int getPriceBuy() {
     return this.priceBuy;
   }

}
