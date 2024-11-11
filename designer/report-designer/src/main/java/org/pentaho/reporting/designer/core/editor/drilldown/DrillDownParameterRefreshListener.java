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


package org.pentaho.reporting.designer.core.editor.drilldown;

import java.util.EventListener;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public interface DrillDownParameterRefreshListener extends EventListener {
  public void requestParameterRefresh( final DrillDownParameterRefreshEvent model );
}
