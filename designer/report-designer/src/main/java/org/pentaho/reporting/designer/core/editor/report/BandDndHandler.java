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


package org.pentaho.reporting.designer.core.editor.report;

import org.pentaho.reporting.designer.core.util.dnd.ElementMetaDataTransferable;
import org.pentaho.reporting.designer.core.util.dnd.FieldDescriptionTransferable;
import org.pentaho.reporting.designer.core.util.dnd.GenericDNDHandler;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.wizard.AutoGeneratorUtility;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class BandDndHandler extends GenericDNDHandler {
  private ReportElementDragHandler dragHandler;
  private ReportElementEditorContext editorContext;

  public BandDndHandler( final ReportElementEditorContext editorContext ) {
    super( new DataFlavor[]
      { ElementMetaDataTransferable.ELEMENT_FLAVOR,
        FieldDescriptionTransferable.ELEMENT_FLAVOR } );
    this.editorContext = editorContext;
  }

  protected int updateDragOver( final DropTargetDragEvent event ) {
    try {
      if ( ElementMetaDataTransferable.ELEMENT_FLAVOR.equals( getFlavor() ) ) {
        final ElementMetaData data = (ElementMetaData) getTransferData();
        if ( data == null ) {
          return DnDConstants.ACTION_NONE;
        }

        if ( dragHandler == null ) {
          dragHandler = createDragHandler( data );
          if ( dragHandler == null ) {
            return DnDConstants.ACTION_NONE;
          }
          return dragHandler.dragStarted( event, editorContext, data, null );
        } else {
          return dragHandler.dragUpdated( event, editorContext, data, null );
        }
      }

      if ( FieldDescriptionTransferable.ELEMENT_FLAVOR.equals( getFlavor() ) ) {
        final String fieldName = (String) getTransferData();
        final ElementMetaData data = createMetaData( fieldName );
        if ( data == null ) {
          return DnDConstants.ACTION_NONE;
        }

        if ( dragHandler == null ) {
          dragHandler = createDragHandler( data );
          if ( dragHandler == null ) {
            return DnDConstants.ACTION_NONE;
          }
          return dragHandler.dragStarted( event, editorContext, data, fieldName );
        } else {
          return dragHandler.dragUpdated( event, editorContext, data, fieldName );
        }
      }

      return DnDConstants.ACTION_NONE;
    } finally {
      editorContext.getRepresentationContainer().revalidate();
      editorContext.getRepresentationContainer().repaint();
    }
  }

  private ReportElementDragHandler createDragHandler( final ElementMetaData metaData ) {
    if ( metaData == null ) {
      return null;
    }

    ReportElementEditor elementEditor = ReportElementEditorRegistry.getInstance().getPlugin( metaData.getName() );
    if ( elementEditor == null ) {
      elementEditor = ReportElementEditorRegistry.getInstance().getPlugin( null );
      if ( elementEditor == null ) {
        return null;
      }
    }
    return elementEditor.createDragHandler();
  }

  /**
   * Called while a drag operation is ongoing, when the mouse pointer has exited the operable part of the drop site for
   * the <code>DropTarget</code> registered with this listener.
   *
   * @param dte the <code>DropTargetEvent</code>
   */

  public void dragExit( final DropTargetEvent dte ) {
    if ( dragHandler == null ) {
      super.dragExit( dte );
      return;
    }
    dragHandler.dragAborted( dte, editorContext );
    dragHandler = null;
    super.dragExit( dte );

    editorContext.getRepresentationContainer().revalidate();
    editorContext.getRepresentationContainer().repaint();
  }

  public void drop( final DropTargetDropEvent dtde ) {
    if ( dragHandler == null ) {
      dtde.rejectDrop();
      return;
    }
    try {
      if ( ElementMetaDataTransferable.ELEMENT_FLAVOR.equals( getFlavor() ) ) {
        final ElementMetaData metaData = (ElementMetaData) getTransferData();
        dragHandler.drop( dtde, editorContext, metaData, null );
        return;
      }

      if ( FieldDescriptionTransferable.ELEMENT_FLAVOR.equals( getFlavor() ) ) {
        final String fieldName = (String) getTransferData();
        final ElementMetaData metaData = createMetaData( fieldName );
        dragHandler.drop( dtde, editorContext, metaData, fieldName );
        return;
      }
      dtde.rejectDrop();
    } finally {
      editorContext.getRepresentationContainer().revalidate();
      editorContext.getRepresentationContainer().repaint();
      dragHandler = null;
    }
  }

  private ElementMetaData createMetaData( final String fieldName ) {

    final ContextAwareDataSchemaModel model = editorContext.getRenderContext().getReportDataSchemaModel();
    final DataSchema dataSchema = model.getDataSchema();
    final DataAttributes attributes = dataSchema.getAttributes( fieldName );
    final DataAttributeContext dataAttributeContext = new DefaultDataAttributeContext();
    if ( attributes == null ) {
      return null;
    }
    final ElementType type = AutoGeneratorUtility.createFieldType( attributes, dataAttributeContext );
    if ( type == null ) {
      return null;
    }
    return type.getMetaData();
  }

}
