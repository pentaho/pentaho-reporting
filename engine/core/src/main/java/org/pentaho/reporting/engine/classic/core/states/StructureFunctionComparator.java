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


package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.function.StructureFunction;

import java.util.Comparator;

public class StructureFunctionComparator implements Comparator<StructureFunction> {
  public StructureFunctionComparator() {
  }

  public int compare( final StructureFunction s1, final StructureFunction s2 ) {
    final int dL1 = s1.getDependencyLevel();
    final int dL2 = s2.getDependencyLevel();
    if ( dL1 > dL2 ) {
      return -1;
    }
    if ( dL1 < dL2 ) {
      return 1;
    }

    final int priority1 = s1.getProcessingPriority();
    final int priority2 = s2.getProcessingPriority();
    if ( priority1 < priority2 ) {
      return -1;
    }
    if ( priority1 > priority2 ) {
      return 1;
    }
    return 0;
  }
}
