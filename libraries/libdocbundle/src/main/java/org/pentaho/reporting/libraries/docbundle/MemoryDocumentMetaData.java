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

import org.pentaho.reporting.libraries.docbundle.metadata.DefaultBundleManifest;
import org.pentaho.reporting.libraries.docbundle.metadata.DefaultBundleMetaData;

public class MemoryDocumentMetaData implements WriteableDocumentMetaData {
  private DefaultBundleManifest bundleManifest;
  private DefaultBundleMetaData bundleMetaData;

  public MemoryDocumentMetaData() {
    bundleManifest = new DefaultBundleManifest();
    bundleMetaData = new DefaultBundleMetaData();
  }

  public void setBundleType( final String type ) {
    if ( type == null ) {
      throw new NullPointerException();
    }

    bundleManifest.addEntry( "/", type );
  }

  public void setBundleAttribute( final String namespace, final String name, final Object value ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( namespace == null ) {
      throw new NullPointerException();
    }

    bundleMetaData.putBundleAttribute( namespace, name, value );
  }

  /**
   * Returns the bundle's defined mime-type. This value is read from the "/mimetype" entry (if existent) else from the
   * manifest's "/" entry. The bundle type acts as a hint for the content processor on what content the main document
   * contains. This entry is declarative - if the actual main document does not match the declared bundle type, parsing
   * is allowed to fail.
   *
   * @return the bundle type.
   */
  public String getBundleType() {
    return bundleManifest.getMimeType( "/" );
  }

  /**
   * Returns the declared mime-type for the given entry. The mime-type is declarative - if it does not match the actual
   * content of the entry, the content processor may raise an error.
   *
   * @param entry the entry path.
   * @return the mime-type.
   */
  public String getEntryMimeType( final String entry ) {
    return bundleManifest.getMimeType( entry );
  }

  /**
   * Returns a single document-meta-data attribute. Each attribute is specified by a namespace and attribute name and
   * contains a single string value.
   *
   * @param namespace     the namespace uri
   * @param attributeName the attribute name
   * @return the attribute value.
   */
  public Object getBundleAttribute( final String namespace, final String attributeName ) {
    if ( namespace == null ) {
      throw new NullPointerException();
    }

    if ( attributeName == null ) {
      throw new NullPointerException();
    }

    return bundleMetaData.getBundleAttribute( namespace, attributeName );
  }

  public String[] getManifestEntryNames() {
    return bundleManifest.getEntries();
  }

  public void setEntryMimeType( final String entry, final String type ) {
    if ( entry == null ) {
      throw new NullPointerException();
    }
    if ( type == null ) {
      throw new NullPointerException();
    }


    bundleManifest.addEntry( entry, type );
  }

  public void setEntryAttribute( final String entryName, final String attributeName, final String value ) {
    if ( entryName == null ) {
      throw new NullPointerException();
    }
    if ( attributeName == null ) {
      throw new NullPointerException();
    }
    bundleManifest.setAttribute( entryName, attributeName, value );
  }

  public boolean removeEntry( final String entry ) {
    if ( entry == null ) {
      throw new NullPointerException();
    }

    return bundleManifest.removeEntry( entry );
  }

  public String[] getMetaDataNamespaces() {
    return bundleMetaData.getNamespaces();
  }

  public String[] getMetaDataNames( final String namespace ) {
    return bundleMetaData.getNames( namespace );
  }

  public String getEntryAttribute( final String entryName, final String attributeName ) {
    return bundleManifest.getAttribute( entryName, attributeName );
  }

  public String[] getEntryAttributeNames( final String entryName ) {
    return bundleManifest.getAttributeNames( entryName );
  }

  public Object clone() throws CloneNotSupportedException {
    final MemoryDocumentMetaData o = (MemoryDocumentMetaData) super.clone();
    o.bundleManifest = (DefaultBundleManifest) bundleManifest.clone();
    o.bundleMetaData = (DefaultBundleMetaData) bundleMetaData.clone();
    return o;
  }
}
