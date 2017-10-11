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

package org.pentaho.reporting.libraries.resourceloader.cache;

import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Creation-Date: 06.04.2006, 09:53:37
 *
 * @author Thomas Morgner
 */
public class DefaultResourceDataCacheEntry implements ResourceDataCacheEntry {
  private ResourceData data;
  private long version;
  private static final long serialVersionUID = -8308023147327221724L;

  public DefaultResourceDataCacheEntry( final ResourceData data,
                                        final ResourceManager manager )
    throws ResourceLoadingException {
    if ( data == null ) {
      throw new NullPointerException();
    }
    this.version = data.getVersion( manager );
    this.data = data;
  }

  public ResourceData getData() {
    return data;
  }

  public long getStoredVersion() {
    return version;
  }
}
