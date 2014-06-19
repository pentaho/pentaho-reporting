/*
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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects;

import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.AbstractObjectDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectFactoryException;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;

/**
 * An object-description for an {@link org.pentaho.reporting.engine.classic.core.ElementAlignment} object.
 *
 * @author Thomas Morgner
 */
public class WhitespaceCollapseObjectDescription extends AbstractObjectDescription
{
  /**
   * Creates a new object description.
   */
  public WhitespaceCollapseObjectDescription()
  {
    super(WhitespaceCollapse.class);
    setParameterDefinition("value", String.class);
  }

  /**
   * Creates an {@link org.pentaho.reporting.engine.classic.core.ElementAlignment} object based on this description.
   *
   * @return The object.
   */
  public Object createObject()
  {
    final String o = (String) getParameter("value");
    if (o == null)
    {
      return null;
    }
    if ("discard".equalsIgnoreCase(o))
    {
      return WhitespaceCollapse.DISCARD;
    }
    if ("none".equalsIgnoreCase(o))
    {
      return WhitespaceCollapse.COLLAPSE;
    }
    if ("preserve".equalsIgnoreCase(o))
    {
      return WhitespaceCollapse.PRESERVE;
    }
    if ("preserve-breaks".equalsIgnoreCase(o))
    {
      return WhitespaceCollapse.PRESERVE_BREAKS;
    }

    return null;
  }

  /**
   * Sets the parameters in the object description to match the specified object.
   *
   * @param o the object (an {@link org.pentaho.reporting.engine.classic.core.ElementAlignment} instance).
   * @throws org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectFactoryException
   *          if the object is not recognised.
   */
  public void setParameterFromObject(final Object o)
      throws ObjectFactoryException
  {
    if (o.equals(WhitespaceCollapse.DISCARD))
    {
      setParameter("value", "discard");
    }
    else if (o.equals(WhitespaceCollapse.COLLAPSE))
    {
      setParameter("value", "collapse");
    }
    else if (o.equals(WhitespaceCollapse.PRESERVE))
    {
      setParameter("value", "preserve");
    }
    else if (o.equals(WhitespaceCollapse.PRESERVE_BREAKS))
    {
      setParameter("value", "preserve-breaks");
    }
    else
    {
      throw new ObjectFactoryException("Invalid value specified for WhitespaceCollapse");
    }
  }

}
