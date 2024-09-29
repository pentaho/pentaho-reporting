/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.libraries.fonts.text;

/**
 * Creation-Date: 11.06.2006, 17:02:27
 *
 * @author Thomas Morgner
 */
public class GraphemeClusterProducer implements ClassificationProducer {
  private int lastClassification;
  private GraphemeClassifier classifier;

  public GraphemeClusterProducer() {
    classifier = GraphemeClassifier.getClassifier();
  }

  /**
   * Returns an alternating counter for the grapheme clusters. The value returned can be tested for equality; if two
   * subsequent calls return the same value, the characters of these calls belong to the same cluster.
   *
   * @param codePoint
   * @return true, if a new cluster starts, false if the old cluster continues.
   */
  public boolean createGraphemeCluster( final int codePoint ) {
    final int classification = classifier.getGraphemeClassification( codePoint );
    if ( classification == GraphemeClassifier.EXTEND ) {
      lastClassification = classification;
      return false;
    }

    if ( lastClassification == GraphemeClassifier.CR &&
      classification == GraphemeClassifier.LF ) {
      lastClassification = classification;
      return false;
    }

    if ( lastClassification == GraphemeClassifier.L ) {
      if ( ( classification & GraphemeClassifier.ANY_HANGUL_MASK ) ==
        GraphemeClassifier.ANY_HANGUL_MASK ) {
        lastClassification = classification;
        return false;
      }
    }

    final boolean oldLVorV =
      ( lastClassification & GraphemeClassifier.V_OR_LV_MASK ) ==
        GraphemeClassifier.V_OR_LV_MASK;
    final boolean newVorT =
      ( classification & GraphemeClassifier.V_OR_T_MASK ) ==
        GraphemeClassifier.V_OR_T_MASK;
    if ( oldLVorV && newVorT ) {
      lastClassification = classification;
      return false;
    }

    final boolean oldLVTorT =
      ( lastClassification & GraphemeClassifier.LVT_OR_T_MASK ) ==
        GraphemeClassifier.LVT_OR_T_MASK;
    if ( oldLVTorT && classification == GraphemeClassifier.T ) {
      lastClassification = classification;
      return false;
    }

    lastClassification = classification;
    return true;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
