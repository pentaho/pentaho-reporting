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

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types;

import org.pentaho.metadata.model.concept.types.ColumnWidth;
import org.pentaho.metadata.model.concept.types.ColumnWidth.WidthType;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

public class ColumnWidthConceptMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new ColumnWidthConceptMapper();

  public ColumnWidthConceptMapper() {
  }

  /**
   * @param value
   * @param type
   * @return
   */
  public Object getValue( final Object value, final Class type, final DataAttributeContext context ) {
    if ( value == null ) {
      return null;
    }

    if ( value instanceof ColumnWidth == false ) {
      return null;
    }

    if ( type == null || Object.class.equals( type ) || ColumnWidth.class.equals( type ) ) {
      if ( value instanceof ColumnWidthWrapper ) {
        return value;
      }
      return new ColumnWidthWrapper( (ColumnWidth) value );
    }

    final ColumnWidth width = (ColumnWidth) value;
    final float widthValue = (float) width.getWidth();
    final WidthType widthType = width.getType();

    final float rawWidth;
    if ( widthType == WidthType.CM ) {
      rawWidth = (float) widthValue * ( 72.0f * 100 / 254.0f );
    } else if ( widthType == WidthType.INCHES ) {
      rawWidth = 72 * widthValue;
    } else if ( widthType == WidthType.PERCENT ) {
      rawWidth = -Math.max( 0, widthValue );
    } else if ( widthType == WidthType.PIXELS ) {
      final OutputProcessorMetaData data = context.getOutputProcessorMetaData();
      final double resolution = data.getNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION );
      final double deviceScaleFactor = ( 72.0 / resolution );
      rawWidth = (float) ( widthValue * deviceScaleFactor );
    } else {// if (widthType == ColumnWidth.POINTS
      rawWidth = Math.max( 0, widthValue );
    }

    final String valueAsString = String.valueOf( rawWidth );
    if ( String.class.isAssignableFrom( type ) ) {
      return valueAsString;
    }
    try {
      return ConverterRegistry.toPropertyValue( valueAsString, type );
    } catch ( BeanException e ) {
      // ignore ..
    }
    return null;
  }
}
