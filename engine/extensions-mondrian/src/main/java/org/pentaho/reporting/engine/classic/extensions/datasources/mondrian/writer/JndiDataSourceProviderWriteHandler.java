/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.writer;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.JndiDataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.MondrianDataFactoryModule;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 25.08.2009 Time: 18:56:13
 *
 * @author Thomas Morgner.
 */
public class JndiDataSourceProviderWriteHandler
  implements DataSourceProviderBundleWriteHandler, DataSourceProviderWriteHandler {
  /**
   * Writes a data-source into a XML-stream.
   *
   * @param bundle             the document bundle that is produced.
   * @param state              the current writer state.
   * @param xmlWriter          the XML writer that will receive the generated XML data.
   * @param dataSourceProvider the data factory that should be written.
   * @throws java.io.IOException if any error occured
   */
  public void write( final WriteableDocumentBundle bundle,
                     final BundleWriterState state,
                     final XmlWriter xmlWriter,
                     final DataSourceProvider dataSourceProvider ) throws IOException, BundleWriterException {
    if ( dataSourceProvider instanceof JndiDataSourceProvider == false ) {
      throw new BundleWriterException( "This is not a JNDI connection" );
    }
    write( xmlWriter, (JndiDataSourceProvider) dataSourceProvider );
  }

  /**
   * Writes a data-source into a XML-stream.
   *
   * @param reportWriter the writer context that holds all factories.
   * @param xmlWriter    the XML writer that will receive the generated XML data.
   * @param dataFactory  the data factory that should be written.
   * @throws java.io.IOException                                                                      if any error
   * occured
   * @throws org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException if the data
   * factory
   *                                                                                                  cannot be written.
   */
  public void write( final ReportWriterContext reportWriter,
                     final XmlWriter xmlWriter,
                     final DataSourceProvider dataFactory ) throws IOException, ReportWriterException {
    if ( dataFactory instanceof JndiDataSourceProvider == false ) {
      throw new ReportWriterException( "This is not a JNDI connection" );
    }
    write( xmlWriter, (JndiDataSourceProvider) dataFactory );
  }

  protected void write( XmlWriter writer, final JndiDataSourceProvider provider ) throws IOException {
    writer.writeTag( MondrianDataFactoryModule.NAMESPACE, "jndi", XmlWriter.OPEN );
    writer.writeTag( MondrianDataFactoryModule.NAMESPACE, "path", XmlWriter.OPEN );
    writer.writeTextNormalized( provider.getConnectionPath(), false );
    writer.writeCloseTag();
    writer.writeCloseTag();
  }
}
