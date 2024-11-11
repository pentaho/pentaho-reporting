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


package org.pentaho.reporting.designer.core.xul;

import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.util.Orient;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public interface XulSeparator extends XulComponent {
  /**
   * @return the orientation for this container. Valid values are found in the Orient enum.
   * @see org.pentaho.ui.xul.util.Orient
   */
  public Orient getOrientation();
}
