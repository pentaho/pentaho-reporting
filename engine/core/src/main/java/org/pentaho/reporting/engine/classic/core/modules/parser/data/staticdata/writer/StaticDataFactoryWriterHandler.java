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

package org.pentaho.reporting.engine.classic.core.modules.parser.data.staticdata.writer;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.NamedStaticDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleDataFactoryWriterHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.data.staticdata.StaticDataFactoryModule;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Creation-Date: Jan 18, 2007, 6:41:57 PM
 *
 * @author Thomas Morgner
 */
public class StaticDataFactoryWriterHandler implements BundleDataFactoryWriterHandler {

  public StaticDataFactoryWriterHandler() {
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

    final NamedStaticDataFactory staticDataFactory = (NamedStaticDataFactory) dataFactory;

    final String fileName = BundleUtilities.getUniqueName( bundle, state.getFileName(), "datasources/static-ds{0}.xml" );
    if ( fileName == null ) {
      throw new IOException( "Unable to generate unique name for the Named-Static-Data-Source" );
    }

    final OutputStream outputStream = bundle.createEntry( fileName, "text/xml" );
    final DefaultTagDescription tagDescription = new DefaultTagDescription();
    tagDescription.setNamespaceHasCData( StaticDataFactoryModule.NAMESPACE, false );
    tagDescription.setElementHasCData( StaticDataFactoryModule.NAMESPACE, "query", true );

    final XmlWriter xmlWriter =
        new XmlWriter( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ", "\n" );
    xmlWriter.writeXmlDeclaration( "UTF-8" );

    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", StaticDataFactoryModule.NAMESPACE );
    xmlWriter.writeTag( StaticDataFactoryModule.NAMESPACE, "static-datasource", rootAttrs, XmlWriterSupport.OPEN );

    final String[] queryNames = staticDataFactory.getQueryNames();
    for ( int i = 0; i < queryNames.length; i++ ) {
      final String queryName = queryNames[i];
      final String query = staticDataFactory.getQuery( queryName );
      xmlWriter.writeTag( StaticDataFactoryModule.NAMESPACE, "query", "name", queryName, XmlWriterSupport.OPEN );
      xmlWriter.writeTextNormalized( query, false );
      xmlWriter.writeCloseTag();
    }
    xmlWriter.writeCloseTag();
    xmlWriter.close();
    return fileName;
  }
}
