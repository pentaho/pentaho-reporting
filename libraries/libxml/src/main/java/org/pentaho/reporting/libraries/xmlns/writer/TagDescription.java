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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.xmlns.writer;

/**
 * A tag-description provides information about xml tags. At the moment, we simply care whether an element can contain
 * CDATA. In such cases, we do not indent the inner elements.
 *
 * @author Thomas Morgner
 */
public interface TagDescription {
  /**
   * Checks, whether the element specified by the tagname and namespace can contain CDATA.
   *
   * @param namespace the namespace (as URI)
   * @param tagname   the tagname
   * @return true, if the element can contain CDATA, false otherwise
   */
  boolean hasCData( String namespace, String tagname );
}
