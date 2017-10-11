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

package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.inlinedata;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.data.inlinedata.InlineDataFactoryModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.DataFactoryWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import javax.swing.table.TableModel;
import java.io.IOException;

public class TableDataFactoryWriteHandler implements DataFactoryWriteHandler {
  public TableDataFactoryWriteHandler() {
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

    try {
      final TableDataFactory tableDataFactory = (TableDataFactory) dataFactory;

      final AttributeList rootAttrs = new AttributeList();
      if ( xmlWriter.isNamespaceDefined( InlineDataFactoryModule.NAMESPACE ) == false ) {
        rootAttrs.addNamespaceDeclaration( "data", InlineDataFactoryModule.NAMESPACE );
      }
      xmlWriter.writeTag( InlineDataFactoryModule.NAMESPACE, "inline-datasource", rootAttrs, XmlWriterSupport.OPEN );

      final String[] tables = tableDataFactory.getQueryNames();
      for ( int i = 0; i < tables.length; i++ ) {
        final String tableName = tables[i];
        final TableModel tableModel = tableDataFactory.queryData( tableName, null );

        xmlWriter
            .writeTag( InlineDataFactoryModule.NAMESPACE, "inline-table", "name", tableName, XmlWriterSupport.OPEN );
        xmlWriter.writeTag( InlineDataFactoryModule.NAMESPACE, "definition", XmlWriterSupport.OPEN );

        final Class[] colTypes = new Class[tableModel.getColumnCount()];
        for ( int col = 0; col < tableModel.getColumnCount(); col += 1 ) {
          final AttributeList colAttrs = new AttributeList();
          colAttrs.setAttribute( InlineDataFactoryModule.NAMESPACE, "name", tableModel.getColumnName( col ) );

          final Class columnClass = tableModel.getColumnClass( col );
          if ( columnClass == null ) {
            colAttrs.setAttribute( InlineDataFactoryModule.NAMESPACE, "type", Object.class.getName() );
            colTypes[col] = Object.class;
          } else {
            colAttrs.setAttribute( InlineDataFactoryModule.NAMESPACE, "type", columnClass.getName() );
            colTypes[col] = columnClass;
          }

          xmlWriter.writeTag( InlineDataFactoryModule.NAMESPACE, "data", colAttrs, XmlWriterSupport.CLOSE );
        }
        xmlWriter.writeCloseTag(); // definition

        for ( int row = 0; row < tableModel.getRowCount(); row += 1 ) {
          xmlWriter.writeTag( InlineDataFactoryModule.NAMESPACE, "row", XmlWriterSupport.OPEN );
          for ( int col = 0; col < tableModel.getColumnCount(); col += 1 ) {
            final AttributeList colAttrs = new AttributeList();
            final Object value = tableModel.getValueAt( row, col );
            if ( value == null ) {
              colAttrs.setAttribute( InlineDataFactoryModule.NAMESPACE, "null", "true" );
              xmlWriter.writeTag( InlineDataFactoryModule.NAMESPACE, "data", colAttrs, XmlWriterSupport.CLOSE );
            } else {
              final Class valueClass = value.getClass();
              if ( ObjectUtilities.equal( colTypes[col], valueClass ) == false ) {
                colAttrs.setAttribute( InlineDataFactoryModule.NAMESPACE, "type", valueClass.getName() );
              }

              try {
                final String s = ConverterRegistry.toAttributeValue( value );
                xmlWriter.writeTag( InlineDataFactoryModule.NAMESPACE, "column", colAttrs, XmlWriterSupport.OPEN );
                xmlWriter.writeTextNormalized( s, true );
                xmlWriter.writeCloseTag();

              } catch ( BeanException e ) {
                throw new ReportWriterException( "Unable to convert value at (row:" + row + ";column:" + col
                    + ") into a string." + value.getClass() );
              }
            }

          }
          xmlWriter.writeCloseTag(); // row

        }

        xmlWriter.writeCloseTag(); // inline-table
      }
      xmlWriter.writeCloseTag();
    } catch ( ReportDataFactoryException rfe ) {
      // should never happen ..
      throw new ReportWriterException( "Failed to write data-factory", rfe );
    }

  }
}
