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

package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter;

import org.pentaho.reporting.engine.classic.core.filter.DataSource;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DataSourceCollector;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

/**
 * A data-source writer. Writes datasources and templates.
 *
 * @author Thomas Morgner.
 */
public class DataSourceWriter extends ObjectWriter {
  /**
   * The data-source.
   */
  private DataSourceCollector dataSourceCollector;

  /**
   * Creates a new writer.
   *
   * @param reportWriter
   *          the report writer.
   * @param baseObject
   *          the base object.
   * @param objectDescription
   *          the object description.
   * @param indent
   *          the current indention level.
   * @throws ReportWriterException
   *           if an error occured.
   * @throws IllegalArgumentException
   *           if the object description does not describe a datasource.
   */
  public DataSourceWriter( final ReportWriterContext reportWriter, final DataSource baseObject,
      final ObjectDescription objectDescription, final XmlWriter indent ) throws ReportWriterException {
    super( reportWriter, baseObject, objectDescription, indent );
    if ( DataSource.class.isAssignableFrom( objectDescription.getObjectClass() ) == false ) {
      throw new IllegalArgumentException( "Expect a datasource description, but got "
          + objectDescription.getObjectClass() );
    }
    dataSourceCollector = getReportWriter().getDataSourceCollector();
  }

  /**
   * Writes a parameter.
   *
   * @param name
   *          the name.
   * @throws IOException
   *           if there is an I/O problem.
   * @throws ReportWriterException
   *           if the report definition could not be written.
   */
  protected void writeParameter( final String name ) throws IOException, ReportWriterException {
    if ( "dataSource".equals( name ) == false ) {
      super.writeParameter( name );
      return;
    }

    final DataSource ds = (DataSource) getObjectDescription().getParameter( name );
    final ObjectDescription dsDesc = getParameterDescription( name );
    final String dsname = dataSourceCollector.getDataSourceName( dsDesc );

    if ( dsname == null ) {
      throw new ReportWriterException( "The datasource type is not registered: " + ds.getClass() );
    }

    final XmlWriter writer = getXmlWriter();
    writer.writeTag( ExtParserModule.NAMESPACE, AbstractXMLDefinitionWriter.DATASOURCE_TAG, "type", dsname,
        XmlWriterSupport.OPEN );

    final DataSourceWriter dsWriter = new DataSourceWriter( getReportWriter(), ds, dsDesc, writer );
    dsWriter.write();
    writer.writeCloseTag();
  }
}
