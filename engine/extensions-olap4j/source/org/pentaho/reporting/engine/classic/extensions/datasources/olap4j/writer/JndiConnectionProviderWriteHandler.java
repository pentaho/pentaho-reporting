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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.writer;

import java.io.IOException;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.Olap4JDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.JndiConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

/**
 * Creation-Date: Jan 19, 2007, 5:03:22 PM
 *
 * @author Thomas Morgner
 */
public class JndiConnectionProviderWriteHandler
    implements OlapConnectionProviderWriteHandler
{
  public JndiConnectionProviderWriteHandler()
  {
  }

  public String writeReport(final XmlWriter xmlWriter,
                            final OlapConnectionProvider connectionProvider) throws IOException, BundleWriterException
  {
    if (xmlWriter == null)
    {
      throw new NullPointerException();
    }
    if (connectionProvider == null)
    {
      throw new NullPointerException();
    }

    final JndiConnectionProvider driverProvider =
        (JndiConnectionProvider) connectionProvider;
    xmlWriter.writeTag(Olap4JDataFactoryModule.NAMESPACE, "jndi", XmlWriterSupport.OPEN);

    xmlWriter.writeTag(Olap4JDataFactoryModule.NAMESPACE, "path", XmlWriterSupport.OPEN);
    xmlWriter.writeTextNormalized(driverProvider.getConnectionPath(), false);
    xmlWriter.writeCloseTag();

    xmlWriter.writeCloseTag();
    return null;
  }
}
