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
