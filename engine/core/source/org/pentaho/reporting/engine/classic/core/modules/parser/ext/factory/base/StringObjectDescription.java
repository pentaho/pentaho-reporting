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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

/**
 * An object-description for a <code>String</code> object.
 *
 * @author Thomas Morgner
 */
public class StringObjectDescription extends AbstractObjectDescription
{

  /**
   * Creates a new object description.
   */
  public StringObjectDescription()
  {
    super(String.class);
    setParameterDefinition("value", String.class);
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject()
  {
    final String o = (String) getParameter("value");
    return String.valueOf(o);
  }

  /**
   * Sets the parameters of this description object to match the supplied object.
   *
   * @param o the object (should be an instance of <code>String</code>).
   * @throws ObjectFactoryException if the object is not an instance of <code>String</code>.
   */
  public void setParameterFromObject(final Object o) throws ObjectFactoryException
  {
    if (!(o instanceof String))
    {
      throw new ObjectFactoryException("The given object is no java.lang.String.");
    }

    setParameter("value", String.valueOf(o));
  }
}
