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


package org.pentaho.reporting.designer.core.util.undo;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.util.List;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class MassElementStyleUndoEntryBuilder {
  private ReportElement[] visualElements;
  private Object[][] styleProperties;

  public MassElementStyleUndoEntryBuilder( final List<? extends Element> visualElements ) {
    this.visualElements = visualElements.toArray( new ReportElement[ visualElements.size() ] );
    this.styleProperties = new Object[ visualElements.size() ][];

    for ( int i = 0; i < visualElements.size(); i++ ) {
      final ReportElement visualElement = visualElements.get( i );
      styleProperties[ i ] = computeStyleChangeSet( visualElement );
    }
  }

  public MassElementStyleUndoEntry finish() {
    final Object[][] currentStyle = new Object[ visualElements.length ][];
    final InstanceID[] targets = new InstanceID[ visualElements.length ];
    for ( int i = 0; i < visualElements.length; i++ ) {
      final ReportElement visualElement = visualElements[ i ];
      currentStyle[ i ] = computeStyleChangeSet( visualElement );
      targets[ i ] = visualElement.getObjectID();
    }
    return new MassElementStyleUndoEntry( targets, styleProperties, currentStyle );
  }

  public static Object[] computeStyleChangeSet( final ReportElement visualElement ) {
    final ElementStyleSheet styleSheet = visualElement.getStyle();
    final StyleKey[] definedPropertyNamesArray = styleSheet.getDefinedPropertyNamesArray();
    final Object[] retval = new Object[ StyleKey.getDefinedStyleKeyCount() ];
    for ( int i = 0; i < definedPropertyNamesArray.length; i++ ) {
      final StyleKey styleKey = definedPropertyNamesArray[ i ];
      if ( styleKey == null ) {
        continue;
      }
      if ( styleSheet.isLocalKey( styleKey ) ) {
        retval[ styleKey.identifier ] = styleSheet.getStyleProperty( styleKey );
      }
    }
    return retval;
  }


}
