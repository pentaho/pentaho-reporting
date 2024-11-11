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


package org.pentaho.reporting.designer.core.util.undo;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 * @noinspection ReturnOfCollectionOrArrayField, AssignmentToCollectionOrArrayFieldFromParameter
 */
public class MassElementStyleUndoEntry implements UndoEntry {
  private InstanceID[] visualElements;
  private Object[][] oldStyle;
  private Object[][] currentStyle;

  public MassElementStyleUndoEntry( final InstanceID[] visualElements,
                                    final Object[][] oldStyle,
                                    final Object[][] currentStyle ) {

    this.visualElements = visualElements;
    this.oldStyle = oldStyle;
    this.currentStyle = currentStyle;
  }

  protected InstanceID[] getVisualElements() {
    return visualElements;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition reportDefinition = renderContext.getReportDefinition();
    final StyleKey[] keys = StyleKey.getDefinedStyleKeys();
    for ( int i = 0; i < visualElements.length; i++ ) {
      final InstanceID visualElement = visualElements[ i ];
      final ReportElement element = ModelUtility.findElementById( reportDefinition, visualElement );
      final ElementStyleSheet styleSheet = element.getStyle();
      final Object[] properties = oldStyle[ i ];
      for ( int j = 0; j < keys.length; j++ ) {
        final StyleKey key = keys[ j ];
        styleSheet.setStyleProperty( key, properties[ key.identifier ] );
      }
    }
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition reportDefinition = renderContext.getReportDefinition();
    final StyleKey[] keys = StyleKey.getDefinedStyleKeys();
    for ( int i = 0; i < visualElements.length; i++ ) {
      final InstanceID visualElement = visualElements[ i ];
      final ReportElement element = ModelUtility.findElementById( reportDefinition, visualElement );
      final ElementStyleSheet styleSheet = element.getStyle();
      final Object[] properties = currentStyle[ i ];
      for ( int j = 0; j < keys.length; j++ ) {
        final StyleKey key = keys[ j ];
        styleSheet.setStyleProperty( key, properties[ key.identifier ] );
      }
    }
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }
}
