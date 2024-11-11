/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public interface PaginationShiftState {
  PaginationShiftState pop( InstanceID id );

  long getShiftForNextChild();

  void updateShiftFromChild( long absoluteValue );

  void increaseShift( long increment );

  void setShift( long absoluteValue );

  boolean isManualBreakSuspended();

  /**
   * Defines whether any child will have its break suspended. Note that if you want to query whether it is ok to handle
   * breaks defined on the current context, you have to ask "isManualBreakSuspended()"
   *
   * @return
   */
  boolean isManualBreakSuspendedForChilds();

  void suspendManualBreaks();
}
