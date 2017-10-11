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

package org.pentaho.reporting.engine.classic.core.metadata;

/**
 * The attribute registry allows to update the available attributes for an element. The update should happen during the
 * boot-process or reports may behave inconsistently.
 */
public interface AttributeRegistry {
  /**
   * Adds a new or updates the metadata for an existing attribute of a report-element.
   *
   * @param metaData
   *          the new metadata object.
   */
  public void putAttributeDescription( AttributeMetaData metaData );

  /**
   * Retrieves the metadata for an attribute by namespace and name.
   *
   * @param namespace
   *          the namespace for the attribute.
   * @param name
   *          the attribute name.
   * @return the attribute definition or null, if the attribute is not defined.
   */
  public AttributeMetaData getAttributeDescription( String namespace, String name );
}
