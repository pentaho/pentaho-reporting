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

public class DefaultAttributeRegistry implements AttributeRegistry {
  private DefaultElementMetaData elementMetaData;

  public DefaultAttributeRegistry( final DefaultElementMetaData elementMetaData ) {
    if ( elementMetaData == null ) {
      throw new NullPointerException();
    }
    this.elementMetaData = elementMetaData;
  }

  public void putAttributeDescription( final AttributeMetaData metaData ) {
    this.elementMetaData.setAttributeDescription( metaData.getNameSpace(), metaData.getName(), metaData );
  }

  public AttributeMetaData getAttributeDescription( final String namespace, final String name ) {
    return elementMetaData.getAttributeDescription( namespace, name );
  }
}
