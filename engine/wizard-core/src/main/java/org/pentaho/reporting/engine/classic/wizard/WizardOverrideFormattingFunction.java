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

package org.pentaho.reporting.engine.classic.wizard;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.AbstractElementFormatFunction;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.wizard.model.ElementFormatDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.FieldDefinition;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class WizardOverrideFormattingFunction extends AbstractElementFormatFunction implements StructureFunction {
  public WizardOverrideFormattingFunction() {
  }

  public int getProcessingPriority() {
    // executed after the metadata has been applied, but before the style-expressions get applied. 
    return 6000;
  }


  /**
   * Evaluates all defined style-expressions of the given element.
   *
   * @param e the element that should be updated.
   * @return true, if attributes or style were changed, false if no change was made.
   */
  protected boolean evaluateElement( final ReportElement e ) {
    if ( e == null ) {
      throw new NullPointerException();
    }

    boolean retval = false;
    final Object maybeFormatData = e.getAttribute( AttributeNames.Wizard.NAMESPACE, "CachedWizardFormatData" );
    if ( maybeFormatData instanceof ElementFormatDefinition ) {
      final ElementFormatDefinition formatDefinition = (ElementFormatDefinition) maybeFormatData;
      if ( formatDefinition.getBackgroundColor() != null ) {
        e.getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, formatDefinition.getBackgroundColor() );
        retval = true;
      }
      if ( formatDefinition.getFontColor() != null ) {
        e.getStyle().setStyleProperty( ElementStyleKeys.PAINT, formatDefinition.getFontColor() );
        retval = true;
      }
      if ( formatDefinition.getFontBold() != null ) {
        e.getStyle().setStyleProperty( TextStyleKeys.BOLD, formatDefinition.getFontBold() );
        retval = true;
      }
      if ( formatDefinition.getFontItalic() != null ) {
        e.getStyle().setStyleProperty( TextStyleKeys.ITALIC, formatDefinition.getFontItalic() );
        retval = true;
      }
      if ( formatDefinition.getFontName() != null ) {
        e.getStyle().setStyleProperty( TextStyleKeys.FONT, formatDefinition.getFontName() );
        retval = true;
      }
      if ( formatDefinition.getFontUnderline() != null ) {
        e.getStyle().setStyleProperty( TextStyleKeys.UNDERLINED, formatDefinition.getFontUnderline() );
        retval = true;
      }
      if ( formatDefinition.getFontItalic() != null ) {
        e.getStyle().setStyleProperty( TextStyleKeys.ITALIC, formatDefinition.getFontItalic() );
        retval = true;
      }
      if ( formatDefinition.getFontSize() != null ) {
        e.getStyle().setStyleProperty( TextStyleKeys.FONTSIZE, formatDefinition.getFontSize() );
        retval = true;
      }
      if ( formatDefinition.getFontStrikethrough() != null ) {
        e.getStyle().setStyleProperty( TextStyleKeys.STRIKETHROUGH, formatDefinition.getFontStrikethrough() );
        retval = true;
      }
      if ( formatDefinition.getHorizontalAlignment() != null ) {
        e.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, formatDefinition.getHorizontalAlignment() );
        retval = true;
      }
      if ( formatDefinition.getVerticalAlignment() != null ) {
        e.getStyle().setStyleProperty( ElementStyleKeys.VALIGNMENT, formatDefinition.getVerticalAlignment() );
        retval = true;
      }
    }

    final Object maybeFieldData = e.getAttribute( AttributeNames.Wizard.NAMESPACE, "CachedWizardFieldData" );
    if ( maybeFieldData instanceof FieldDefinition ) {
      final FieldDefinition fieldDefinition = (FieldDefinition) maybeFieldData;

      if ( fieldDefinition.getDataFormat() != null ) {
        e.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING,
          fieldDefinition.getDataFormat() );
        retval = true;
      }
      if ( fieldDefinition.getNullString() != null ) {
        e.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE,
          fieldDefinition.getNullString() );
        retval = true;
      }

      if ( "label".equals( e.getElementType().getMetaData().getName() ) &&
        !StringUtils.isEmpty( fieldDefinition.getDisplayName() ) ) {
        e.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, fieldDefinition.getDisplayName() );
      }

    }

    return retval;
  }
}
