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

import java.io.Serializable;

/**
 * Provides access to the document's bundle meta-data information.
 * <p/>
 * This class unifies the information from '/mimetype', '/META-INF/manifest.xml' and '/metadata.xml'. If the manifest
 * contains a mime-type declaration for an entry, that mime-type is reported by the repository methods.
 * <p/>
 * The manifest file must follow the specification as outlined in the OpenDocument File format section 17.7. Encryption
 * is not yet supported but may be added later.
 *
 * @author Thomas Morgner
 */
public interface DocumentMetaData extends Serializable, Cloneable {
  /**
   * Returns the bundle's defined mime-type. This value is read from the "/mimetype" entry (if existent) else from the
   * manifest's "/" entry. The bundle type acts as a hint for the content processor on what content the main document
   * contains. This entry is declarative - if the actual main document does not match the declared bundle type, parsing
   * is allowed to fail.
   *
   * @return the bundle type.
   */
  public String getBundleType();

  /**
   * Returns the declared mime-type for the given entry. The mime-type is declarative - if it does not match the actual
   * content of the entry, the content processor may raise an error.
   *
   * @param entry the entry path.
   * @return the mime-type.
   */
  public String getEntryMimeType( String entry );

  /**
   * Returns a single document-meta-data attribute. Each attribute is specified by a namespace and attribute name and
   * contains a single string value.
   *
   * @param namespace     the namespace uri
   * @param attributeName the attribute name
   * @return the attribute value.
   */
  public Object getBundleAttribute( String namespace, String attributeName );

  public String[] getManifestEntryNames();

  public String[] getMetaDataNamespaces();

  public String[] getMetaDataNames( String namespace );

  public String getEntryAttribute( final String entryName, final String attributeName );

  public String[] getEntryAttributeNames( final String entryName );

  public Object clone() throws CloneNotSupportedException;
}
