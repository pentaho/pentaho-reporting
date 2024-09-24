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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader;

import java.io.Serializable;

/**
 * A resource is a wrapper around the final product. It shall not hold any references to the ResourceData object used to
 * create the resource (to allow efficient 2-stage caching).
 * <p/>
 * Although this interfaces declares to be serializable, this might not be the case for some of the content contained in
 * the resource object. Cache implementors should be aware of that issue and should act accordingly (for instance by not
 * caching that object).
 *
 * @author Thomas Morgner
 */
public interface Resource extends Serializable {
  public Object getResource() throws ResourceException;

  public Class getTargetType();

  public boolean isTemporaryResult();

  public long getVersion( ResourceKey key );

  /**
   * The primary source is also included in this set. The dependencies are given as ResourceKey objects. The keys itself
   * do not hold any state information.
   * <p/>
   * The dependencies do not track deep dependencies. So if Resource A depends on Resource B which depends on Resource
   * C, then A only knows about B, not C.
   *
   * @return
   */
  public ResourceKey[] getDependencies();

  public ResourceKey getSource();
}
