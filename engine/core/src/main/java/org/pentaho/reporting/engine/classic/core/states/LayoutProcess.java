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

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;

public interface LayoutProcess extends Cloneable {
  public static final int LEVEL_STRUCTURAL_PREPROCESSING = Integer.MAX_VALUE;
  public static final int LEVEL_PAGINATE = -2;
  public static final int LEVEL_COLLECT = -1;

  public LayoutProcess getParent();

  public boolean isPageListener();

  public OutputFunction getOutputFunction();

  public StructureFunction[] getCollectionFunctions();

  public LayoutProcess deriveForStorage();

  public LayoutProcess deriveForPagebreak();

  public Object clone();

  /**
   * This function must be implemented in a re-entrant way. Report events can cause nested report events to be fired.
   *
   * @param originalEvent
   */
  public void fireReportEvent( ReportEvent originalEvent );

  public void restart( final ReportState state ) throws ReportProcessingException;
}
