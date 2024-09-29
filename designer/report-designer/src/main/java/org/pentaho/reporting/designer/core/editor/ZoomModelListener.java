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


package org.pentaho.reporting.designer.core.editor;

import java.util.EventListener;

/**
 * User: Martin Date: 03.02.2006 Time: 19:29:54
 */
public interface ZoomModelListener extends EventListener {
  public void zoomFactorChanged();
}
