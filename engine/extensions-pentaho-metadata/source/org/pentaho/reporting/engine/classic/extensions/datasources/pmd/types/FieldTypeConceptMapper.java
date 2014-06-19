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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types;

import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.metadata.model.concept.types.FieldType;

public class FieldTypeConceptMapper implements ConceptQueryMapper
{
  public FieldTypeConceptMapper()
  {
  }

  /**
   * @param value
   * @param type
   * @return
   */
  public Object getValue(final Object value, final Class type, final DataAttributeContext context)
  {
    if (value == null)
    {
      return null;
    }

    if (value instanceof FieldType == false)
    {
      return null;
    }

    if (type == null || Object.class.equals(type) || FieldType.class.equals(type))
    {
      return value;
    }

    if (String.class.equals(type) == false)
    {
      return null;
    }

    final FieldType fieldTypeSettings = (FieldType) value;
    if (FieldType.ATTRIBUTE.equals(fieldTypeSettings))
    {
      return "attribute";
    }
    if (FieldType.FACT.equals(fieldTypeSettings))
    {
      return "fact";
    }
    if (FieldType.KEY.equals(fieldTypeSettings))
    {
      return "key";
    }
    if (FieldType.DIMENSION.equals(fieldTypeSettings))
    {
      return "dimension";
    }
    return null;
  }
}
