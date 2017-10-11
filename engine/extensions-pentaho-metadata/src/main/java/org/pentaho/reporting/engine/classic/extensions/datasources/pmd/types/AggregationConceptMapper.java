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

import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

public class AggregationConceptMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new AggregationConceptMapper();

  public AggregationConceptMapper() {
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

    if ( value instanceof AggregationType == false ) {
      return null;
    }

    if ( AggregationType.class.equals( type ) ) {
      return value;
    }

    if ( type == null || Object.class.equals( type ) ) {
      return value;
    }

    if ( AggregationType.NONE.equals( value ) ) {
      return "none";
    } else if ( AggregationType.SUM.equals( value ) ) {
      return "sum";
    } else if ( AggregationType.AVERAGE.equals( value ) ) {
      return "average";
    } else if ( AggregationType.COUNT.equals( value ) ) {
      return "count";
    } else if ( AggregationType.COUNT_DISTINCT.equals( value ) ) {
      return "count_distinct";
    } else if ( AggregationType.MINIMUM.equals( value ) ) {
      return "minimum";
    } else if ( AggregationType.MAXIMUM.equals( value ) ) {
      return "maximum";
    } else {
      return null;
    }

  }
}
