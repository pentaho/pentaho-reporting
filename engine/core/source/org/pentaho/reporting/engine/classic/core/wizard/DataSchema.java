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

package org.pentaho.reporting.engine.classic.core.wizard;

import java.io.Serializable;

/**
 * Represents a compiled data-schema. All rules have been evaluated and collapseded into a set of data-attributes. A
 * Data-Schema is always a column-schema, it does not change on each row (as it could happen with MetaTableModels).
 *
 * @author Thomas Morgner
 */
public interface DataSchema extends Serializable, Cloneable
{
  public DataAttributes getTableAttributes();

  public DataAttributes getAttributes(final String name);

  public Object clone() throws CloneNotSupportedException;

  public String[] getNames();
}
