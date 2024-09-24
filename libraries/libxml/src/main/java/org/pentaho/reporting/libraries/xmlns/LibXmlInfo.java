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

package org.pentaho.reporting.libraries.xmlns;

import org.pentaho.reporting.libraries.base.LibBaseInfo;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.resourceloader.LibLoaderInfo;

/**
 * The LibXmlInfo class contains all dependency information and some common information like version, license and
 * contributors about the library itself.
 *
 * @author Thomas Morgner
 */
public class LibXmlInfo extends ProjectInformation {
  /**
   * The XML-Namespace is used for the 'id' attribute.
   */
  public static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";

  /**
   * The XML-Namespace is used for the 'id' attribute.
   */
  public static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";

  private static LibXmlInfo info;

  /**
   * Constructs an empty project info object.
   */
  private LibXmlInfo() {
    super( "libxml", "LibXML" );
  }

  /**
   * Initialized the project info object.
   */
  private void initialize() {
    setInfo( "http://reporting.pentaho.org/libxml/" );
    setCopyright( "(C)opyright 2007-2011, by Object Refinery Limited, Pentaho Corporation and Contributors" );
    setLicenseName( "LGPL" );

    addLibrary( LibBaseInfo.getInstance() );
    addLibrary( LibLoaderInfo.getInstance() );

    setBootClass( LibXmlBoot.class.getName() );
  }

  /**
   * Returns the singleton instance of the info-class.
   *
   * @return the singleton info.
   */
  public static synchronized ProjectInformation getInstance() {
    if ( info == null ) {
      info = new LibXmlInfo();
      info.initialize();
    }
    return info;
  }
}
