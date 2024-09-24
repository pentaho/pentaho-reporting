/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
