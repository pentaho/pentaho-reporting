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

package org.pentaho.reporting.designer.core.editor.palette;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.elements.InsertElementAction;
import org.pentaho.reporting.designer.core.util.dnd.ElementMetaDataTransferable;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.image.BufferedImage;
import java.beans.BeanInfo;
import java.util.Locale;

/**
 * A component holding the report. It contains the lineals, zoom controller in the upper left corner and as a viewport,
 * it contains the report-layout component.
 *
 * @author Ezequiel Cuellar
 */
public class PaletteButton extends JButton implements DragGestureListener {
  private static class AlwaysActiveInsertElementAction extends InsertElementAction {
    private AlwaysActiveInsertElementAction( final ElementMetaData metaData ) {
      super( metaData );
    }

    protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
    }

    protected void updateSelection() {
      setEnabled( getActiveContext() != null );
    }
  }

  private String elementName;
  private static final BufferedImage EMPTY_DRAG_IMAGE = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB );

  public PaletteButton( final ElementMetaData elementMetaData,
                        final ReportDesignerContext context ) {
    if ( elementMetaData == null ) {
      throw new NullPointerException();
    }
    elementName = elementMetaData.getName();
    putClientProperty( "hideActionText", Boolean.TRUE ); // NON-NLS

    final DragSource dragSource = new DragSource();

    dragSource.createDefaultDragGestureRecognizer( this, DnDConstants.ACTION_COPY, this );

    final InsertElementAction action = new AlwaysActiveInsertElementAction( elementMetaData );
    action.setReportDesignerContext( context );
    setAction( action );

    final Image icon = elementMetaData.getIcon( Locale.getDefault(), BeanInfo.ICON_COLOR_16x16 );
    if ( icon != null ) {
      setIcon( new ImageIcon( icon ) );
    } else {
      setText( elementMetaData.getDisplayName( Locale.getDefault() ) );
    }
    setToolTipText( elementMetaData.getDescription( Locale.getDefault() ) );
    setFocusable( false );
    setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
  }

  public void dragGestureRecognized( final DragGestureEvent anEvent ) {
    final ElementMetaData elementMetaData = ElementTypeRegistry.getInstance().getElementType( elementName );
    final ElementMetaDataTransferable transferable = new ElementMetaDataTransferable( elementMetaData );
    anEvent.startDrag( DragSource.DefaultCopyNoDrop, EMPTY_DRAG_IMAGE, new Point(), transferable, null );

    final ButtonModel model = getModel();
    model.setArmed( false );
    model.setPressed( false );
  }
}
