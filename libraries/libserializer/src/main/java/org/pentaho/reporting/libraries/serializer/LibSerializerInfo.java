/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.serializer;

import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * Creation-Date: 23.10.2005, 18:49:39
 *
 * @author Thomas Morgner
 */
public class LibSerializerInfo extends ProjectInformation {
  /**
   * The info singleton.
   */
  private static LibSerializerInfo singleton;

  /**
   * Returns the single instance of this class.
   *
   * @return The single instance of information about the JCommon library.
   */
  public static synchronized LibSerializerInfo getInstance() {
    if ( singleton == null ) {
      singleton = new LibSerializerInfo();
      singleton.initialize();
    }
    return singleton;
  }


  /**
   * Constructs an empty project info object.
   */
  private LibSerializerInfo() {
    super( "libserializer", "LibSerializer" );
  }

  /**
   * Second step of the initialization.
   */
  private void initialize() {
    setInfo( "http://reporting.pentaho.org/libserializer/" );
    setCopyright( "(C)opyright 2006-2011, by Pentaho Corporation, Object Refinery Limited and Contributors" );

    setLicenseName( "LGPL" );

    setBootClass( LibSerializerBoot.class.getName() );
  }

}
