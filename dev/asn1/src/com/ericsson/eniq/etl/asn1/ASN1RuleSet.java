package com.ericsson.eniq.etl.asn1;

import java.util.ArrayList;
import java.util.List;

public class ASN1RuleSet {

    String name;
    
    String type;

    String typeName;
    
    String callerRuleType = null;
    
    int count = 0;

    List rules = new ArrayList();

    private int rulePointer = -1;
    private final int baseRulePointer = -1;
      
    ASN1RuleSet(){
    }
    
    public void reset(){
      this.rulePointer = this.baseRulePointer;
    }
    
    public void setCallerRuleType(final String crt){     
      callerRuleType = crt;
    }
    
    public String getCallerRuleType(){
      return callerRuleType;
    }
    
    public int status(){
      
      if (rulePointer == -1){
        return 0;
      }
      if (rulePointer > -1){
        return 1;
      }
      return -1;
    }
 
    public ASN1Rule rule(){
      
      if (rulePointer < 0 || rulePointer >= rules.size()){
        return null;
      }
      return (ASN1Rule)rules.get(rulePointer);
    }
      
    
    public boolean hasNextRule(){
      return rulePointer < (rules.size()-1);
    }
    
    public void nextRule(){     
      rulePointer++;
      if (rulePointer > 0 && rulePointer > (rules.size()-1)){
        rulePointer = rules.size()-1;
      }
    }

    public String toString(){
      final StringBuffer buf = new StringBuffer();
      buf.append(name+" "+typeName+" ("+type+")");
      for (int i = 0 ; i < rules.size() ; i++){
        buf.append(" \n   " +  ((ASN1Rule)rules.get(i)).toString());        
      }
      
      return buf.toString();
    }
    
}
