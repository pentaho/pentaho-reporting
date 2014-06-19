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

package org.pentaho.reporting.engine.classic.core.metadata;

import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

/**
 * Represents a static resource reference to a file contained in the bundle or in a location that is accessible via the
 * report's resource-manager.
 *
 * @author Thomas Morgner
 */
public class ResourceReference
{
  private ResourceKey path;
  private boolean linked;

  public ResourceReference(final ResourceKey path, final boolean linked)
  {
    if (path == null)
    {
      throw new NullPointerException();
    }
    this.path = path;
    this.linked = linked;
  }

  public ResourceKey getPath()
  {
    return path;
  }

  public boolean isLinked()
  {
    return linked;
  }


  public String toString()
  {
    return "org.pentaho.reporting.engine.classic.core.metadata.ResourceReference{" +
        "path=" + path +
        ", linked=" + linked +
        '}';
  }
}
