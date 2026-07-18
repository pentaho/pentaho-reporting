/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.designer.core.model.lineal;

import java.util.EventListener;

/**
 * User: Martin Date: 26.01.2006 Time: 10:47:17
 */
public interface LinealModelListener extends EventListener {
  public void modelChanged( LinealModelEvent event );
}
