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


package org.pentaho.reporting.designer.core.util.docking;

import javax.swing.*;
import java.awt.*;

public class InternalWindow extends JPanel {
  public InternalWindow( final String titleCategory ) {
    setLayout( new BorderLayout() );

    final DefaultHeaderComponent defaultHeaderComponent = new DefaultHeaderComponent( titleCategory );
    defaultHeaderComponent.setBorder( new HeaderBorder() );

    add( defaultHeaderComponent, BorderLayout.NORTH );
    setBorder( new ShadowBorder() );
    setBorder( BorderFactory.createLineBorder( SystemColor.controlShadow ) );

  }

  public final void setFocusCycleRoot( final boolean focusCycleRoot ) {
  }

  public final boolean isFocusCycleRoot() {
    return true;
  }

  public final Container getFocusCycleRootAncestor() {
    return null;
  }
}
