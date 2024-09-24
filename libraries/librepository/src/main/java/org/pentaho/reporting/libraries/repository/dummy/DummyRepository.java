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

package org.pentaho.reporting.libraries.repository.dummy;

import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultMimeRegistry;
import org.pentaho.reporting.libraries.repository.MimeRegistry;
import org.pentaho.reporting.libraries.repository.Repository;

import java.io.Serializable;

/**
 * A dummy repositor is a empty repository that swallows all content fed into it.
 *
 * @author Thomas Morgner
 */
public class DummyRepository implements Repository, Serializable {
  private DummyContentLocation location;
  private MimeRegistry mimeRegistry;

  /**
   * Creates a new dummy repository.
   */
  public DummyRepository() {
    location = new DummyContentLocation( this, "" );
    mimeRegistry = new DefaultMimeRegistry();
  }

  /**
   * Returns the repositories root directory entry.
   *
   * @return the root directory.
   */
  public ContentLocation getRoot() {
    return location;
  }

  /**
   * Returns the repositories MimeRegistry, which is used return basic content-type information about the items stored
   * in this repository.
   *
   * @return the mime registry.
   * @see MimeRegistry
   */
  public MimeRegistry getMimeRegistry() {
    return mimeRegistry;
  }
}
