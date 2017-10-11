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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.writer;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PasswordEncryptionService;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.Olap4JDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

/**
 * Creation-Date: Jan 19, 2007, 5:03:22 PM
 *
 * @author Thomas Morgner
 */
public class DriverConnectionProviderWriteHandler
  implements OlapConnectionProviderWriteHandler {
  public DriverConnectionProviderWriteHandler() {
  }

  public String writeReport( final XmlWriter xmlWriter,
                             final OlapConnectionProvider connectionProvider )
    throws IOException, BundleWriterException {
    if ( xmlWriter == null ) {
      throw new NullPointerException();
    }
    if ( connectionProvider == null ) {
      throw new NullPointerException();
    }

    final DriverConnectionProvider driverProvider =
      (DriverConnectionProvider) connectionProvider;
    xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "connection", XmlWriterSupport.OPEN );

    xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "driver", XmlWriterSupport.OPEN );
    xmlWriter.writeTextNormalized( driverProvider.getDriver(), false );
    xmlWriter.writeCloseTag();

    xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "url", XmlWriterSupport.OPEN );
    xmlWriter.writeTextNormalized( driverProvider.getUrl(), false );
    xmlWriter.writeCloseTag();

    xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "properties", XmlWriterSupport.OPEN );
    final String[] propertyNames = driverProvider.getPropertyNames();
    for ( int i = 0; i < propertyNames.length; i++ ) {
      final String name = propertyNames[ i ];
      final String value = driverProvider.getProperty( name );
      xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "property", "name", name, XmlWriterSupport.OPEN );
      if ( name.toLowerCase().contains( "password" ) ) {
        xmlWriter.writeTextNormalized( PasswordEncryptionService.getInstance().encrypt( value ), false );
      } else {
        xmlWriter.writeTextNormalized( value, false );
      }
      xmlWriter.writeCloseTag();
    }
    xmlWriter.writeCloseTag();

    xmlWriter.writeCloseTag();
    return null;
  }
}
