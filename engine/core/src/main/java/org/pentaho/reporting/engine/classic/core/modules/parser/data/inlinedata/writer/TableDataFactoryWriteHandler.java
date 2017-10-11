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

package org.pentaho.reporting.engine.classic.core.modules.parser.data.inlinedata.writer;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleDataFactoryWriterHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.data.inlinedata.InlineDataFactoryModule;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import javax.swing.table.TableModel;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class TableDataFactoryWriteHandler implements BundleDataFactoryWriterHandler {
  public TableDataFactoryWriteHandler() {
  }

  /**
   * Writes a data-source into a own file. The name of file inside the bundle is returned as string. The file name
   * returned is always absolute and can be made relative by using the IOUtils of LibBase. If the writer-handler did not
   * generate a file on its own, it should return null.
   *
   * @param bundle
   *          the bundle where to write to.
   * @param state
   *          the writer state to hold the current processing information.
   * @return the name of the newly generated file or null if no file was created.
   * @throws IOException
   *           if any error occured
   * @throws BundleWriterException
   *           if a bundle-management error occured.
   */
  public String writeDataFactory( final WriteableDocumentBundle bundle, final DataFactory dataFactory,
      final BundleWriterState state ) throws IOException, BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }

    try {
      final TableDataFactory tableDataFactory = (TableDataFactory) dataFactory;

      final String fileName =
          BundleUtilities.getUniqueName( bundle, state.getFileName(), "datasources/inline-ds{0}.xml" );
      if ( fileName == null ) {
        throw new IOException( "Unable to generate unique name for Inline-Data-Source" );
      }

      final OutputStream outputStream = bundle.createEntry( fileName, "text/xml" );
      final DefaultTagDescription tagDescription = new DefaultTagDescription();
      tagDescription.setNamespaceHasCData( InlineDataFactoryModule.NAMESPACE, false );
      tagDescription.setElementHasCData( InlineDataFactoryModule.NAMESPACE, "data", true );
      final XmlWriter xmlWriter =
          new XmlWriter( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ", "\n" );
      final AttributeList rootAttrs = new AttributeList();
      rootAttrs.addNamespaceDeclaration( "data", InlineDataFactoryModule.NAMESPACE );
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
          final String columnName = tableModel.getColumnName( col );
          colAttrs.setAttribute( InlineDataFactoryModule.NAMESPACE, "name", StringUtils.isEmpty( columnName )
              ? "<empty-" + col + ">" : columnName );

          final Class columnClass = tableModel.getColumnClass( col );
          if ( columnClass == null ) {
            colAttrs.setAttribute( InlineDataFactoryModule.NAMESPACE, "type", Object.class.getName() );
            colTypes[col] = Object.class;
          } else {
            colAttrs.setAttribute( InlineDataFactoryModule.NAMESPACE, "type", columnClass.getName() );
            colTypes[col] = columnClass;
          }

          xmlWriter.writeTag( InlineDataFactoryModule.NAMESPACE, "column", colAttrs, XmlWriterSupport.CLOSE );
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
                xmlWriter.writeTag( InlineDataFactoryModule.NAMESPACE, "data", colAttrs, XmlWriterSupport.OPEN );
                xmlWriter.writeTextNormalized( s, true );
                xmlWriter.writeCloseTag();

              } catch ( BeanException e ) {
                throw new BundleWriterException( "Unable to convert value at (row:" + row + ";column:" + col
                    + ") into a string." + value.getClass() );
              }
            }

          }
          xmlWriter.writeCloseTag(); // row

        }

        xmlWriter.writeCloseTag(); // inline-table
      }

      xmlWriter.writeCloseTag();
      xmlWriter.close();
      return fileName;
    } catch ( ReportDataFactoryException e ) {
      throw new BundleWriterException(
          "This should not happen in the way we use the table-factory, but hey, better safe than sorry." );
    }
  }
}
