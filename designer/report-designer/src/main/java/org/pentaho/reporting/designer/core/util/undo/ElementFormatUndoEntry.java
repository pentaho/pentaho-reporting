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
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ElementFormatUndoEntry extends MassElementStyleUndoEntry {
  private Expression[][] oldExpressions;
  private Expression[][] newExpressions;

  public ElementFormatUndoEntry( final InstanceID[] elements,
                                 final Object[][] oldStyleData,
                                 final Object[][] newStyleData,
                                 final Expression[][] oldExpressions,
                                 final Expression[][] newExpressions ) {
    super( elements, oldStyleData, newStyleData );
    this.oldExpressions = oldExpressions;
    this.newExpressions = newExpressions;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    super.undo( renderContext );
    final AbstractReportDefinition reportDefinition = renderContext.getReportDefinition();
    final StyleKey[] keys = StyleKey.getDefinedStyleKeys();
    final InstanceID[] visualElements = getVisualElements();
    for ( int i = 0; i < visualElements.length; i++ ) {
      final InstanceID visualElement = visualElements[ i ];
      final ReportElement element = ModelUtility.findElementById( reportDefinition, visualElement );
      final Expression[] properties = oldExpressions[ i ];
      for ( int j = 0; j < keys.length; j++ ) {
        final StyleKey key = keys[ j ];
        element.setStyleExpression( key, properties[ key.identifier ] );
      }
    }
  }

  public void redo( final ReportDocumentContext renderContext ) {
    super.redo( renderContext );
    final AbstractReportDefinition reportDefinition = renderContext.getReportDefinition();
    final StyleKey[] keys = StyleKey.getDefinedStyleKeys();
    final InstanceID[] visualElements = getVisualElements();
    for ( int i = 0; i < visualElements.length; i++ ) {
      final InstanceID visualElement = visualElements[ i ];
      final ReportElement element = ModelUtility.findElementById( reportDefinition, visualElement );
      final Expression[] properties = newExpressions[ i ];
      for ( int j = 0; j < keys.length; j++ ) {
        final StyleKey key = keys[ j ];
        element.setStyleExpression( key, properties[ key.identifier ] );
      }
    }
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }


  public static Expression[] computeExpressions( final ReportElement visualElement ) {
    final Map<StyleKey, Expression> expressions = visualElement.getStyleExpressions();
    final StyleKey[] keys = expressions.keySet().toArray( new StyleKey[ expressions.size() ] );
    final Expression[] retval = new Expression[ StyleKey.getDefinedStyleKeyCount() ];
    for ( int i = 0; i < keys.length; i++ ) {
      final StyleKey styleKey = keys[ i ];
      if ( styleKey == null ) {
        continue;
      }
      final Expression styleExpression = visualElement.getStyleExpression( styleKey );
      if ( styleExpression != null ) {
        retval[ styleKey.identifier ] = styleExpression;
      }
    }
    return retval;
  }

  public static class EditResult {
    private ElementStyleSheet styleSheet;
    private Map<StyleKey, Expression> styleExpressions;

    public EditResult( final ElementStyleSheet styleSheet, final Map<StyleKey, Expression> styleExpressions ) {
      this.styleSheet = styleSheet;
      if ( styleExpressions != null ) {
        this.styleExpressions = Collections.unmodifiableMap( styleExpressions );
      }
    }

    public ElementStyleSheet getStyleSheet() {
      return styleSheet;
    }

    public Map getStyleExpressions() {
      if ( styleExpressions != null ) {
        return Collections.unmodifiableMap( styleExpressions );
      }
      return Collections.EMPTY_MAP;
    }


    public ElementFormatUndoEntry process( final List<Element> visualElements ) {
      return process( visualElements.toArray( new Element[ visualElements.size() ] ) );
    }

    public ElementFormatUndoEntry process( final Element[] visualElements ) {
      final Object[][] oldStyleData = new Object[ visualElements.length ][];
      final Expression[][] oldExpressions = new Expression[ visualElements.length ][];
      for ( int i = 0; i < visualElements.length; i++ ) {
        final Element visualElement = visualElements[ i ];
        oldStyleData[ i ] = MassElementStyleUndoEntryBuilder.computeStyleChangeSet( visualElement );
        oldExpressions[ i ] = ElementFormatUndoEntry.computeExpressions( visualElement );
      }

      final ElementStyleSheet editableStyleSheet = getStyleSheet();
      final StyleKey[] definedKeys = editableStyleSheet.getDefinedPropertyNamesArray();
      for ( int i = 0; i < definedKeys.length; i++ ) {
        final StyleKey key = definedKeys[ i ];
        if ( key == null ) {
          continue;
        }
        final Object value = editableStyleSheet.getStyleProperty( key );
        for ( int j = 0; j < visualElements.length; j++ ) {
          final Element element = visualElements[ j ];
          final ElementStyleSheet elementStyleSheet = element.getStyle();
          if ( ObjectUtilities.equal( value, elementStyleSheet.getStyleProperty( key ) ) == false ) {
            elementStyleSheet.setStyleProperty( key, value );
          }
        }
      }

      final Map resultExpressions = getStyleExpressions();
      final Iterator iterator = resultExpressions.entrySet().iterator();
      while ( iterator.hasNext() ) {
        final Map.Entry entry = (Map.Entry) iterator.next();
        final StyleKey key = (StyleKey) entry.getKey();
        final Expression value = (Expression) entry.getValue();

        for ( int j = 0; j < visualElements.length; j++ ) {
          final Element element = visualElements[ j ];
          if ( ObjectUtilities.equal( value, element.getStyleExpression( key ) ) == false ) {
            if ( value != null ) {
              element.setStyleExpression( key, value.getInstance() );
            } else {
              element.setStyleExpression( key, null );
            }
          }
        }
      }

      final Object[][] newStyleData = new Object[ visualElements.length ][];
      final Expression[][] newExpressions = new Expression[ visualElements.length ][];
      final InstanceID[] ids = new InstanceID[ visualElements.length ];
      for ( int i = 0; i < visualElements.length; i++ ) {
        final Element visualElement = visualElements[ i ];
        newStyleData[ i ] = MassElementStyleUndoEntryBuilder.computeStyleChangeSet( visualElement );
        newExpressions[ i ] = ElementFormatUndoEntry.computeExpressions( visualElement );
        ids[ i ] = visualElement.getObjectID();

        // make sure everyone gets informed of any style change we made
        visualElement.notifyNodePropertiesChanged();
      }

      return new ElementFormatUndoEntry( ids, oldStyleData, newStyleData, oldExpressions, newExpressions );
    }
  }

}
