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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.docbundle;

import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public interface WriteableDocumentBundle extends DocumentBundle {
  public void createDirectoryEntry( final String name, final String mimeType ) throws IOException;

  public OutputStream createEntry( final String name, final String mimetype ) throws IOException;

  public boolean removeEntry( final String name ) throws IOException;

  public WriteableDocumentMetaData getWriteableDocumentMetaData();

  public ResourceKey createResourceKey( final String entryName,
                                        final Map factoryParameters ) throws ResourceKeyCreationException;

  public boolean isEmbeddedKey( final ResourceKey resourceKey );
}
