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

package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.compounddata;

import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.data.compounddata.CompoundDataFactoryModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.data.sql.SQLDataFactoryModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.DataFactoryWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.DataFactoryWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

public class CompoundDataFactoryWriteHandler implements DataFactoryWriteHandler {
  public CompoundDataFactoryWriteHandler() {
  }

  /**
   * Writes a data-source into a XML-stream.
   *
   * @param reportWriter
   *          the writer context that holds all factories.
   * @param xmlWriter
   *          the XML writer that will receive the generated XML data.
   * @param dataFactory
   *          the data factory that should be written.
   * @throws IOException
   *           if any error occured
   * @throws ReportWriterException
   *           if the data factory cannot be written.
   */
  public void write( final ReportWriterContext reportWriter, final XmlWriter xmlWriter, final DataFactory dataFactory )
    throws IOException, ReportWriterException {
    if ( reportWriter == null ) {
      throw new NullPointerException();
    }
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }
    if ( xmlWriter == null ) {
      throw new NullPointerException();
    }

    final CompoundDataFactory compoundDataFactory = (CompoundDataFactory) dataFactory;

    final AttributeList rootAttrs = new AttributeList();
    if ( xmlWriter.isNamespaceDefined( CompoundDataFactoryModule.NAMESPACE ) == false ) {
      rootAttrs.addNamespaceDeclaration( "data", CompoundDataFactoryModule.NAMESPACE );
    }
    xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "sql-datasource", rootAttrs, XmlWriterSupport.OPEN );
    xmlWriter.writeTag( CompoundDataFactoryModule.NAMESPACE, "compound-datasource", rootAttrs, XmlWriterSupport.OPEN );

    for ( int i = 0; i < compoundDataFactory.size(); i++ ) {
      final DataFactory df = compoundDataFactory.get( i );
      final DataFactoryWriteHandler writerHandler = DataFactoryWriter.lookupWriteHandler( df );
      if ( writerHandler == null ) {
        throw new ReportWriterException( "Unable to find writer-handler for data-factory " + df.getClass() );
      }

      writerHandler.write( reportWriter, xmlWriter, dataFactory );
    }

    xmlWriter.writeCloseTag();
  }
}
