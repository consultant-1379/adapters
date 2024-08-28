package com.ericsson.eniq.etl.asn1;

import java.util.ArrayList;

public class ASN1Rule {


    String name;

    String internalType;
    
    String type;
    
    String info;
    
    boolean isChoice = false;

    ArrayList choices = new ArrayList();
    
    String defaultValue = null;
    
    boolean optional = false;

    public ASN1Rule copy(){
      final ASN1Rule result = new ASN1Rule();
      result.name = this.name;
      result.internalType = this.internalType;
      result.info = this.info;
      result.type = this.type;      
      result.isChoice = this.isChoice;
      result.choices = (ArrayList)this.choices.clone();
      result.defaultValue = this.defaultValue;
      result.optional = this.optional;
      return result;
    }
    
    
    public String toString(){
      String dv ="";
      if (optional){
        dv = "OPTIONAL default:["+defaultValue+"]"; 
      }
      return  name+" "+type+" ("+internalType+")" +dv;
    }
    
}
