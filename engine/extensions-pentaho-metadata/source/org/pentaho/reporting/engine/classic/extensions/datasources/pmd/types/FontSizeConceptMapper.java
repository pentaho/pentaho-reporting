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

import org.pentaho.metadata.model.concept.types.Font;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

public class FontSizeConceptMapper implements ConceptQueryMapper
{
  public FontSizeConceptMapper()
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

    if (value instanceof Font == false)
    {
      return null;
    }

    final Font fontSettings = (Font) value;
    final String valueAsString = String.valueOf(fontSettings.getHeight());
    if (type == null || Object.class.equals(type) || Number.class.isAssignableFrom(type))
    {
      try
      {
        final Object returnValue = ConverterRegistry.toPropertyValue(valueAsString, type);
        return returnValue;
      }
      catch (BeanException e)
      {
        // ignore ..
      }
    }
    if (String.class.isAssignableFrom(type))
    {
      return valueAsString;
    }
    return null;
  }
}
