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

import org.pentaho.metadata.model.concept.types.Alignment;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ElementAlignmentValueConverter;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

public class AlignmentConceptMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new AlignmentConceptMapper();

  private ElementAlignmentValueConverter alignmentValueConverter;

  public AlignmentConceptMapper() {
    alignmentValueConverter = new ElementAlignmentValueConverter();
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

    if ( value instanceof Alignment == false ) {
      return null;
    }

    if ( Alignment.class.equals( type ) ) {
      return value;
    }

    if ( type == null || Object.class.equals( type ) ) {
      return value;
    }

    final String textAlignment;
    if ( Alignment.LEFT.equals( value ) ) {
      textAlignment = "left";
    } else if ( Alignment.CENTERED.equals( value ) ) {
      textAlignment = "center";
    } else if ( Alignment.RIGHT.equals( value ) ) {
      textAlignment = "right";
    } else if ( Alignment.JUSTIFIED.equals( value ) ) {
      textAlignment = "justified";
    } else {
      return null;
    }

    if ( Alignment.class.equals( type ) ) {
      try {
        return alignmentValueConverter.toPropertyValue( textAlignment );
      } catch ( BeanException e ) {
        // ignore ...
        return null;
      }
    }
    if ( String.class.equals( type ) ) {
      return textAlignment;
    }

    return null;
  }
}
