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

package org.pentaho.reporting.designer.core.model.lineal;

import java.util.EventListener;

/**
 * User: Martin Date: 26.01.2006 Time: 10:47:17
 */
public interface LinealModelListener extends EventListener {
  public void modelChanged( LinealModelEvent event );
}
