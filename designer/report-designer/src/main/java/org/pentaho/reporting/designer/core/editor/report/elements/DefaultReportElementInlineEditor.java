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

package org.pentaho.reporting.designer.core.editor.report.elements;

import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.designer.core.editor.report.ReportElementInlineEditor;
import org.pentaho.reporting.designer.core.util.table.StringValueCellEditor;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;

import java.awt.*;

public class DefaultReportElementInlineEditor extends StringValueCellEditor implements ReportElementInlineEditor {
  private AttributeMetaData selectedMetaData;
  private ReportElement reportElement;
  /*
   * We have to support several types based on what attributes are available.
   *
   * field-value: core:field
   * message-value: core:value; role=message; class=string
   *
   * static-computed-value: core:value; computed=true; class=any -> show formula
   * 
   * static-text-value: core:value; class=string                 -> show text-editor
   * static-number-value: core:value; class=number               -> show text-editor
   * static-date-value: core:value; class=date                   -> show text-editor
   */

  public DefaultReportElementInlineEditor() {
  }

  public Component getElementCellEditorComponent( final ReportElementEditorContext rootBandRenderComponent,
                                                  final ReportElement value ) {
    if ( value == null ) {
      return null;
    }

    setReportDesignerContext( rootBandRenderComponent.getDesignerContext() );

    selectedMetaData = selectMetaData( value );
    if ( selectedMetaData == null ) {
      return null;
    }
    if ( selectedMetaData.isComputed() ) {
      selectedMetaData = null;
      return null;
    }
    if ( String.class.equals( selectedMetaData.getTargetType() ) == false ) {
      return null;
    }

    if ( AttributeMetaData.VALUEROLE_RESOURCE.equals( selectedMetaData.getValueRole() ) ) {
      return null;
    }

    reportElement = value;
    final Component editor = create
      ( selectedMetaData.getValueRole(), selectedMetaData.getExtraCalculationFields(), getAttributeValue() );
    if ( editor != null ) {
      final String fontName = (String) reportElement.getStyle().getStyleProperty( TextStyleKeys.FONT );
      final int fontSize = reportElement.getStyle().getIntStyleProperty( TextStyleKeys.FONTSIZE, 8 );
      final boolean fontBold = reportElement.getStyle().getBooleanStyleProperty( TextStyleKeys.BOLD );
      final boolean fontItalics = reportElement.getStyle().getBooleanStyleProperty( TextStyleKeys.ITALIC );
      final Color color = (Color) reportElement.getStyle().getStyleProperty( ElementStyleKeys.PAINT );
      final Color bgColor =
        (Color) reportElement.getStyle().getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, Color.WHITE );

      int fs = Font.PLAIN;
      if ( fontBold ) {
        fs |= Font.BOLD;
      }
      if ( fontItalics ) {
        fs |= Font.ITALIC;
      }
      configureEditorStyle( new Font( fontName, fs, fontSize ), color, bgColor );
    }
    return editor;
  }

  private AttributeMetaData selectMetaData( final ReportElement element ) {
    final ElementMetaData elementMetaData = element.getMetaData();
    final AttributeMetaData fieldData =
      elementMetaData.getAttributeDescription( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD );
    if ( fieldData != null ) {
      return fieldData;
    }
    return elementMetaData.getAttributeDescription( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
  }

  public Object getAttributeValue() {
    if ( selectedMetaData == null ) {
      return null;
    }
    if ( reportElement == null ) {
      return null;
    }

    return reportElement.getAttribute( selectedMetaData.getNameSpace(), selectedMetaData.getName() );
  }

  public boolean stopCellEditing() {
    if ( selectedMetaData == null ) {
      super.cancelCellEditing();
      return true;
    }
    if ( reportElement == null ) {
      super.cancelCellEditing();
      return true;
    }

    reportElement.setAttribute( selectedMetaData.getNameSpace(), selectedMetaData.getName(), getCellEditorValue() );
    return super.stopCellEditing();
  }
}
