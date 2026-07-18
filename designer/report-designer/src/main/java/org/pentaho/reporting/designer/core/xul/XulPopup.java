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



package org.pentaho.reporting.designer.core.xul;

import org.pentaho.ui.xul.XulContainer;
import org.pentaho.ui.xul.util.Orient;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public interface XulPopup extends XulContainer {

  /**
   * @return the orientation for this container. Valid values are found in the Orient enum.
   * @see Orient
   */
  public Orient getOrientation();
}
