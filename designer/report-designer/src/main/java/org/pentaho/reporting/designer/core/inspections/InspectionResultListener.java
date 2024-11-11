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


package org.pentaho.reporting.designer.core.inspections;

import java.util.EventListener;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public interface InspectionResultListener extends EventListener {
  public void notifyInspectionStarted();

  public void notifyInspectionResult( InspectionResult result );
}
