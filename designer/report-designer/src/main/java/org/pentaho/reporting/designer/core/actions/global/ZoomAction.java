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


package org.pentaho.reporting.designer.core.actions.global;

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This class requires private access to the report-frame, so it cannot be implemented outside..
 *
 * @author Thomas Morgner
 */
public abstract class ZoomAction extends AbstractReportContextAction implements ToggleStateAction {
  private int percentage;

  protected ZoomAction( final int percentage ) {
    super();
    this.percentage = percentage;

    putValue( Action.NAME, ActionMessages.getString( "ZoomAction.Text", Integer.valueOf( percentage ) ) );
    putValue( Action.SHORT_DESCRIPTION,
      ActionMessages.getString( "ZoomAction.Description", Integer.valueOf( percentage ) ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "ZoomAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "ZoomAction.Accelerator" ) );

    if ( percentage == 50 ) {
      putValue( Action.SMALL_ICON, createOverlayImageIcon( IconLoader.getInstance().getZoomIcon(),
        IconLoader.getInstance().getZoomOverlay50Icon() ) );
    } else if ( percentage == 100 ) {
      putValue( Action.SMALL_ICON, createOverlayImageIcon( IconLoader.getInstance().getZoomIcon(),
        IconLoader.getInstance().getZoomOverlay100Icon() ) );
    } else if ( percentage == 200 ) {
      putValue( Action.SMALL_ICON, createOverlayImageIcon( IconLoader.getInstance().getZoomIcon(),
        IconLoader.getInstance().getZoomOverlay200Icon() ) );
    } else if ( percentage == 400 ) {
      putValue( Action.SMALL_ICON, createOverlayImageIcon( IconLoader.getInstance().getZoomIcon(),
        IconLoader.getInstance().getZoomOverlay400Icon() ) );
    } else {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getZoomIcon() );
    }
  }

  public static ImageIcon createOverlayImageIcon( final ImageIcon... imageIcons ) {
    final BufferedImage bi = new BufferedImage
      ( imageIcons[ 0 ].getIconWidth(), imageIcons[ 0 ].getIconWidth(), BufferedImage.TYPE_INT_ARGB );
    final Graphics graphics = bi.getGraphics();
    for ( final ImageIcon imageIcon : imageIcons ) {
      graphics.drawImage( imageIcon.getImage(), 0, 0, null );
    }
    return new ImageIcon( bi );
  }

  public int getPercentage() {
    return percentage;
  }
}
