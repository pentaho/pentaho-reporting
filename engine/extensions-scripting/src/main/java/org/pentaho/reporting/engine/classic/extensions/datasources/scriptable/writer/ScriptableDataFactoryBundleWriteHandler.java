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

package org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleDataFactoryWriterHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactoryModule;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

/**
 * Creation-Date: Jan 19, 2007, 4:44:05 PM
 *
 * @author Thomas Morgner
 */
public class ScriptableDataFactoryBundleWriteHandler implements BundleDataFactoryWriterHandler {
  public ScriptableDataFactoryBundleWriteHandler() {
  }

  /**
   * Writes a data-source into a own file. The name of file inside the bundle is returned as string. The file name
   * returned is always absolute and can be made relative by using the IOUtils of LibBase. If the writer-handler did not
   * generate a file on its own, it should return null.
   *
   * @param bundle
   *          the bundle where to write to.
   * @param dataFactory
   *          the data factory that should be written.
   * @param state
   *          the writer state to hold the current processing information.
   * @return the name of the newly generated file or null if no file was created.
   * @throws IOException
   *           if any error occurred
   * @throws BundleWriterException
   *           if a bundle-management error occurred.
   */
  public String writeDataFactory( final WriteableDocumentBundle bundle, final DataFactory dataFactory,
      final BundleWriterState state ) throws IOException, BundleWriterException {
    final String fileName =
        BundleUtilities.getUniqueName( bundle, state.getFileName(), "datasources/scriptable-ds{0}.xml" );
    if ( fileName == null ) {
      throw new IOException( "Unable to generate unique name for Inline-Data-Source" );
    }

    final OutputStream outputStream = bundle.createEntry( fileName, "text/xml" );
    final DefaultTagDescription tagDescription = new DefaultTagDescription();
    tagDescription.setDefaultNamespace( ScriptableDataFactoryModule.NAMESPACE );
    tagDescription.setNamespaceHasCData( ScriptableDataFactoryModule.NAMESPACE, false );
    tagDescription.setElementHasCData( ScriptableDataFactoryModule.NAMESPACE, "query", true );

    final XmlWriter xmlWriter = createXmlWriter( outputStream, tagDescription );

    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", ScriptableDataFactoryModule.NAMESPACE );

    xmlWriter.writeTag( ScriptableDataFactoryModule.NAMESPACE, "scriptable-datasource", rootAttrs, XmlWriter.OPEN );

    final ScriptableDataFactory scriptableDataFactory = (ScriptableDataFactory) dataFactory;
    final AttributeList configAttrs = new AttributeList();
    configAttrs.setAttribute( ScriptableDataFactoryModule.NAMESPACE, "language", String.valueOf( scriptableDataFactory
        .getLanguage() ) );
    if ( !StringUtils.isEmpty( scriptableDataFactory.getScript() ) ) {
      configAttrs.setAttribute( ScriptableDataFactoryModule.NAMESPACE, "script", String.valueOf( scriptableDataFactory
          .getScript() ) );
    }
    if ( !StringUtils.isEmpty( scriptableDataFactory.getShutdownScript() ) ) {
      configAttrs.setAttribute( ScriptableDataFactoryModule.NAMESPACE, "shutdown-script", String
          .valueOf( scriptableDataFactory.getShutdownScript() ) );
    }
    xmlWriter.writeTag( ScriptableDataFactoryModule.NAMESPACE, "config", configAttrs, XmlWriterSupport.CLOSE );

    final String[] queryNames = scriptableDataFactory.getQueryNames();
    for ( int i = 0; i < queryNames.length; i++ ) {
      final String queryName = queryNames[i];
      final String query = scriptableDataFactory.getQuery( queryName );
      xmlWriter.writeTag( ScriptableDataFactoryModule.NAMESPACE, "query", "name", queryName, XmlWriterSupport.OPEN );
      xmlWriter.writeTextNormalized( query, false );
      xmlWriter.writeCloseTag();
    }
    xmlWriter.writeCloseTag();
    xmlWriter.close();
    return fileName;
  }

  /**
   * package-local visibility for testing purposes
   */
  XmlWriter createXmlWriter( final OutputStream outputStream, final DefaultTagDescription tagDescription )
    throws UnsupportedEncodingException {
    return new XmlWriter( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ", "\n" );
  }

}
