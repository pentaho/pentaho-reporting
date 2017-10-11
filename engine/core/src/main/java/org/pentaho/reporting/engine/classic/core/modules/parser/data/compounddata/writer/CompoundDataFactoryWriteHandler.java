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

package org.pentaho.reporting.engine.classic.core.modules.parser.data.compounddata.writer;

import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleDataFactoryWriterHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterUtilities;
import org.pentaho.reporting.engine.classic.core.modules.parser.data.compounddata.CompoundDataFactoryModule;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class CompoundDataFactoryWriteHandler implements BundleDataFactoryWriterHandler {
  public CompoundDataFactoryWriteHandler() {
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

    final CompoundDataFactory compoundDataFactory = (CompoundDataFactory) dataFactory;

    final String fileName =
        BundleUtilities.getUniqueName( bundle, state.getFileName(), "datasources/compound-ds{0}.xml" );
    if ( fileName == null ) {
      throw new IOException( "Unable to generate unique name for Inline-Data-Source" );
    }

    final OutputStream outputStream = bundle.createEntry( fileName, "text/xml" );
    final DefaultTagDescription tagDescription = new DefaultTagDescription();
    tagDescription.setDefaultNamespace( CompoundDataFactoryModule.NAMESPACE );
    tagDescription.setNamespaceHasCData( CompoundDataFactoryModule.NAMESPACE, false );
    final XmlWriter xmlWriter =
        new XmlWriter( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ", "\n" );
    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", CompoundDataFactoryModule.NAMESPACE );
    xmlWriter.writeTag( CompoundDataFactoryModule.NAMESPACE, "compound-datasource", rootAttrs, XmlWriterSupport.OPEN );

    for ( int i = 0; i < compoundDataFactory.size(); i++ ) {
      final DataFactory df = compoundDataFactory.get( i );
      final BundleDataFactoryWriterHandler writerHandler = BundleWriterUtilities.lookupWriteHandler( df );
      if ( writerHandler == null ) {
        throw new BundleWriterException( "Unable to find writer-handler for data-factory " + df.getClass() );
      }

      final String file = writerHandler.writeDataFactory( bundle, df, state );
      if ( file == null ) {
        throw new BundleWriterException( "Data-factory writer did not create a file for " + df.getClass() );
      }
      final String refFile = IOUtils.getInstance().createRelativePath( file, fileName );
      xmlWriter.writeTag( CompoundDataFactoryModule.NAMESPACE, "data-factory", "href", refFile, XmlWriterSupport.CLOSE );
    }

    xmlWriter.writeCloseTag();
    xmlWriter.close();
    return fileName;
  }
}
