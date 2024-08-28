package com.distocraft.dc5000.etl.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;

public class TransformerCache {

  private static final Logger log = Logger.getLogger("etlengine.TransformerCache");

  private static TransformerCache tfc;

  private HashMap transformers = new HashMap();

  public TransformerCache() {
    tfc = this;
  }

  public Transformer getTransformer(final String transformerID) {

    if (transformerID == null || transformerID.length() <= 0) {
      return null;
    }

    Transformer t = (Transformer) transformers.get(transformerID);

    if (t!=null)
      return t;
    

    Iterator keys = transformers.keySet().iterator();
    while (keys.hasNext()) {

      String key = (String) keys.next();
      if (key.equalsIgnoreCase(transformerID)) {
        t = (Transformer) transformers.get(key);
        break;
      }
    }

    if (t == null) {
      log.warning("Cache miss or no such transformer \"" + transformerID + "\"");

      // Extensive debug if level is up
      if (log.getLevel() == Level.FINEST) {

        log.finest("Cache status");
        keys = transformers.keySet().iterator();
        while (keys.hasNext()) {
          final String key = (String) keys.next();
          final Transformer tr = (Transformer) transformers.get(key);
          log.finest("\"" + key + "\" = " + tr.toString());
        }
      }
    }

    return t;
  }

  /**
   * Initialize or revalidate cache
   */
  public void revalidate(final RockFactory reprock, final RockFactory dwhrock) {

    try {

      final HashMap newTransformers = new HashMap();

      final Tpactivation tpa_cond = new Tpactivation(reprock);
      tpa_cond.setStatus("ACTIVE");
      final TpactivationFactory tpaFact = new TpactivationFactory(reprock, tpa_cond);

      final Vector tps = tpaFact.get();

      for (int i = 0; i < tps.size(); i++) {
        final Tpactivation tpa = (Tpactivation) tps.get(i);

        final com.distocraft.dc5000.repository.dwhrep.Transformer t_cond = new com.distocraft.dc5000.repository.dwhrep.Transformer(
            reprock);
        t_cond.setVersionid(tpa.getVersionid());
        final com.distocraft.dc5000.repository.dwhrep.TransformerFactory tFact = new com.distocraft.dc5000.repository.dwhrep.TransformerFactory(
            reprock, t_cond);

        final Vector ts = tFact.get();

        for (int j = 0; j < ts.size(); j++) {
          final com.distocraft.dc5000.repository.dwhrep.Transformer t = (com.distocraft.dc5000.repository.dwhrep.Transformer) ts
              .get(j);
          final String tid = t.getTransformerid();

          newTransformers.put(tid, revalidateTransformer(tid, dwhrock, reprock));
        }

      }

      transformers = newTransformers;

    } catch (Exception e) {
      log.log(Level.WARNING, "Cache revalidation failed", e);
    }

  }

  /**
   * Indicate change in a topologytable
   */
  public void revalidateTable(final String table) {

  }

  private Transformer revalidateTransformer(final String tid, final RockFactory dwhrock, final RockFactory reprock)
      throws Exception {

    return TransformerFactory.create(tid, dwhrock, reprock);

  }

  public static TransformerCache getCache() {
    return tfc;
  }

  /**
   * This function updates one transformer of the TransformerCache. The
   * transformer of tech pack which name is given as parameter will be updated.
   * 
   * @param tpName
   *          Name of the techpack to revalidate.
   * @param dwhreprock
   *          RockFactory to dwhrep.
   * @param dwhrock
   *          RockFactory to dwh.
   */
  public void updateTransformer(String tpName, final RockFactory dwhreprock, final RockFactory dwhrock)
      throws Exception {
    
    HashMap newTransformers = (HashMap)transformers.clone();
    Iterator transformerIdsIter = transformers.keySet().iterator();
    
    log.fine("TransformerCache contains " + newTransformers.keySet().size() + " transformers.");
    
    while (transformerIdsIter.hasNext()) {
      String currTransformerId = (String) transformerIdsIter.next();

      if (currTransformerId.startsWith(tpName + ":")) {

        log.fine("Removing transformer " + currTransformerId + " from cache");

        // Remove the existing (ie. old) transformation.
        newTransformers.remove(currTransformerId);
      }
    }

    try {
      // Start adding the new transformations of this techpack.
      final Tpactivation whereTPActivation = new Tpactivation(dwhreprock);
      whereTPActivation.setStatus("ACTIVE");
      whereTPActivation.setTechpack_name(tpName);
      final TpactivationFactory tpActivationFact = new TpactivationFactory(dwhreprock, whereTPActivation);
      Vector tpActivations = tpActivationFact.get();

      for (int i = 0; i < tpActivations.size(); i++) {
        final Tpactivation tpa = (Tpactivation) tpActivations.get(i);

        final com.distocraft.dc5000.repository.dwhrep.Transformer t_cond = new com.distocraft.dc5000.repository.dwhrep.Transformer(
            dwhreprock);
        t_cond.setVersionid(tpa.getVersionid());
        final com.distocraft.dc5000.repository.dwhrep.TransformerFactory tFact = new com.distocraft.dc5000.repository.dwhrep.TransformerFactory(
            dwhreprock, t_cond);

        final Vector ts = tFact.get();

        for (int j = 0; j < ts.size(); j++) {
          final com.distocraft.dc5000.repository.dwhrep.Transformer t = (com.distocraft.dc5000.repository.dwhrep.Transformer) ts
              .get(j);
          final String tid = t.getTransformerid();

          log.fine("Adding transformer " + tid + " to cache");

          newTransformers.put(tid, revalidateTransformer(tid, dwhrock, dwhreprock));
        }

      }

      // Set the updated HashMap as the class variable.
      transformers = newTransformers;

    } catch (Exception e) {
      this.log.severe("Updating transformer for " + tpName + " failed.");
      throw e;
    }

  }

}
