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


package org.pentaho.reporting.designer.core.actions.elements.layout;

import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;

public final class LayoutAutoAction extends LayoutAction {
  public LayoutAutoAction() {
    super( "LayoutAutoAction", BandStyleKeys.LAYOUT_AUTO );
  }
}
