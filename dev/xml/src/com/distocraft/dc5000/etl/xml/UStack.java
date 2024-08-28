package com.distocraft.dc5000.etl.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * Unsynchronized stack implementation
 * 
 * @author lemminkainen
 * 
 */
class UStack {

  private final List stack;

  UStack() {
    stack = new ArrayList(30);
  }

  /**
   * Pushes one data item into stack
   * 
   * @param data
   *          Data item to be pushed
   */
  void push(final String data) {
    stack.add(data);
  }

  /**
   * Pops top item from the stack
   * 
   * @return top item
   */
  String pop() {
    if (stack.size() <= 0){
      return null;
    } else {
      return (String) stack.remove(stack.size() - 1);
    }
  }

  /**
   * Peeks if the top item in the stack equals specified data
   * 
   * @param data
   *          checked data
   * @return true if checked string is on top of stack false otherwise
   */
  boolean peek(final String data) {
    final String top = (String) stack.get(stack.size() - 1);
    return top.equals(data);
  }
  
  /**
   * Returns stack as String
   * 
   * @return Stack address
   */
  String getStack() {
    final StringBuffer sb = new StringBuffer();
    
    for(int i = 0 ; i < stack.size() ; i++) {
      sb.append((String)stack.get(i));
      if((i+1) < stack.size()) {
        sb.append("/");
      }
    }
    
    return sb.toString();
  }

}
