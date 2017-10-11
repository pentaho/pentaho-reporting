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

import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

import java.util.Date;

public class DataTypeConceptMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new DataTypeConceptMapper();

  public DataTypeConceptMapper() {
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

    if ( value instanceof DataType == false ) {
      return null;
    }

    if ( type == null || Object.class.equals( type ) || DataType.class.equals( type ) ) {
      return value;
    }

    final DataType dataTypeSettings = (DataType) value;
    //    public static final int DATA_TYPE_STRING = 1;
    //    public static final int DATA_TYPE_DATE = 2;
    //    public static final int DATA_TYPE_BOOLEAN = 3;
    //    public static final int DATA_TYPE_NUMERIC = 4;
    //    public static final int DATA_TYPE_BINARY = 5;
    //    public static final int DATA_TYPE_IMAGE = 6;
    //    public static final int DATA_TYPE_URL = 7;
    final Class convertedValue;
    if ( DataType.STRING.equals( dataTypeSettings ) ) {
      convertedValue = String.class;
    } else if ( DataType.DATE.equals( dataTypeSettings ) ) {
      convertedValue = Date.class;
    } else if ( DataType.BOOLEAN.equals( dataTypeSettings ) ) {
      convertedValue = Boolean.class;
    } else if ( DataType.NUMERIC.equals( dataTypeSettings ) ) {
      convertedValue = Number.class;
    } else if ( DataType.URL.equals( dataTypeSettings ) ) {
      convertedValue = String.class;
    } else {
      // we cannot safely handle binary, image or URL types. For them, we fall back to the
      // database metadata instead ..
      return null;
    }

    if ( Class.class.equals( type ) ) {
      return convertedValue;
    }
    if ( String.class.equals( type ) ) {
      return convertedValue.getName();
    }
    return null;
  }
}
