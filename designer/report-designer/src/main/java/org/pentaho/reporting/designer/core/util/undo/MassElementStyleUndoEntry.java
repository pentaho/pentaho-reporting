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
