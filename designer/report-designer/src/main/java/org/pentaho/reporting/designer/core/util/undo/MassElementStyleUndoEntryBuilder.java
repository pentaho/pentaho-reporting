/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
