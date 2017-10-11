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

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.writer;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleDataFactoryWriterHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdDataFactoryModule;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class PmdDataFactoryBundleWriteHandler implements BundleDataFactoryWriterHandler {
  public PmdDataFactoryBundleWriteHandler() {
  }

  /**
   * Writes a data-source into a own file. The name of file inside the bundle is returned as string. The file name
   * returned is always absolute and can be made relative by using the IOUtils of LibBase. If the writer-handler did not
   * generate a file on its own, it should return null.
   *
   * @param bundle      the bundle where to write to.
   * @param dataFactory the data factory that should be written.
   * @param state       the writer state to hold the current processing information.
   * @return the name of the newly generated file or null if no file was created.
   * @throws IOException           if any error occured
   * @throws BundleWriterException if a bundle-management error occured.
   */
  public String writeDataFactory( final WriteableDocumentBundle bundle,
                                  final DataFactory dataFactory,
                                  final BundleWriterState state ) throws IOException,
    BundleWriterException {
    final String fileName = BundleUtilities.getUniqueName( bundle, state.getFileName(), "datasources/pmd-ds{0}.xml" );
    if ( fileName == null ) {
      throw new IOException( "Unable to generate unique name for Inline-Data-Source" );
    }

    final OutputStream outputStream = bundle.createEntry( fileName, "text/xml" );
    final DefaultTagDescription tagDescription = new DefaultTagDescription();
    tagDescription.setNamespaceHasCData( PmdDataFactoryModule.NAMESPACE, false );
    tagDescription.setElementHasCData( PmdDataFactoryModule.NAMESPACE, "global-script", true );
    tagDescription.setElementHasCData( PmdDataFactoryModule.NAMESPACE, "script", true );
    tagDescription.setElementHasCData( PmdDataFactoryModule.NAMESPACE, "static-query", true );

    final XmlWriter xmlWriter =
      new XmlWriter( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ", "\n" );

    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", PmdDataFactoryModule.NAMESPACE );

    xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "pmd-datasource", rootAttrs, XmlWriter.OPEN );

    final PmdDataFactory df = (PmdDataFactory) dataFactory;

    final AttributeList configAttrs = new AttributeList();
    configAttrs.setAttribute( PmdDataFactoryModule.NAMESPACE, "domain", String.valueOf( df.getDomainId() ) );
    configAttrs.setAttribute( PmdDataFactoryModule.NAMESPACE, "xmi-file", String.valueOf( df.getXmiFile() ) );
    xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "config", configAttrs, XmlWriterSupport.CLOSE );

    final String globalScript = df.getGlobalScript();
    final String globalScriptLanguage = df.getGlobalScriptLanguage();
    if ( StringUtils.isEmpty( globalScript ) == false && StringUtils.isEmpty( globalScriptLanguage ) == false ) {
      xmlWriter.writeTag
        ( PmdDataFactoryModule.NAMESPACE, "global-script", "language", globalScriptLanguage, XmlWriterSupport.OPEN );
      xmlWriter.writeTextNormalized( globalScript, false );
      xmlWriter.writeCloseTag();
    }

    xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "query-definitions", XmlWriterSupport.OPEN );
    final String[] queryNames = df.getQueryNames();
    for ( int i = 0; i < queryNames.length; i++ ) {
      final String queryName = queryNames[ i ];
      final String query = df.getQuery( queryName );
      xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "query", "name", queryName, XmlWriterSupport.OPEN );

      xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "static-query", XmlWriterSupport.OPEN );
      xmlWriter.writeTextNormalized( query, false );
      xmlWriter.writeCloseTag();

      final String queryScriptLanguage = df.getScriptingLanguage( queryName );
      final String queryScript = df.getScript( queryName );

      if ( StringUtils.isEmpty( queryScript ) == false &&
        ( StringUtils.isEmpty( queryScriptLanguage ) == false
          || StringUtils.isEmpty( globalScriptLanguage ) == false ) ) {
        if ( StringUtils.isEmpty( queryScriptLanguage ) ) {
          xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "script", XmlWriterSupport.OPEN );
        } else {
          xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "script", "language", queryScriptLanguage,
            XmlWriterSupport.OPEN );
        }
        xmlWriter.writeTextNormalized( queryScript, false );
        xmlWriter.writeCloseTag();
      }

      xmlWriter.writeCloseTag();
    }
    xmlWriter.writeCloseTag();

    xmlWriter.writeCloseTag();
    xmlWriter.close();
    return fileName;
  }

}
