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


package org.pentaho.reporting.engine.classic.core.modules.gui.base.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingCommonModule;
import org.pentaho.reporting.engine.classic.core.util.ImageUtils;

/**
 * Creation-Date: 16.11.2006, 18:51:18
 *
 * @author Thomas Morgner
 */
public class ZoomAction extends AbstractAction {
  private class PaginatedListener implements PropertyChangeListener {
    protected PaginatedListener() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      setEnabled( previewPane.isPaginated() );
    }
  }

  private double zoom;
  private PreviewPane previewPane;
  private PaginatedListener paginatedListener;

  /**
   * Defines an <code>Action</code> object with a default description string and default icon.
   */
  public ZoomAction( final double zoom, final PreviewPane previewPane ) {
    this.zoom = zoom;
    this.previewPane = previewPane;

    this.putValue( Action.NAME, NumberFormat.getPercentInstance( previewPane.getLocale() ).format( zoom ) );
    this.putValue( Action.SMALL_ICON, ImageUtils.createTransparentIcon( 16, 16 ) );
    this.putValue( SwingCommonModule.LARGE_ICON_PROPERTY, ImageUtils.createTransparentIcon( 24, 24 ) );

    paginatedListener = new PaginatedListener();
    this.previewPane.addPropertyChangeListener( PreviewPane.PAGINATED_PROPERTY, paginatedListener );
    setEnabled( previewPane.getReportJob() != null );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    previewPane.setZoom( zoom );
  }

  public void deinitialize() {
    this.previewPane.removePropertyChangeListener( PreviewPane.PAGINATED_PROPERTY, paginatedListener );
    this.previewPane = null;

  }
}
