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

package org.pentaho.reporting.engine.classic.extensions.datasources.cda.writer;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PasswordEncryptionService;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleDataFactoryWriterHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.CdaDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.CdaModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.CdaQueryEntry;
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

/**
 * Creation-Date: Jan 19, 2007, 4:44:05 PM
 *
 * @author Thomas Morgner
 */
public class CdaDataFactoryBundleWriteHandler implements BundleDataFactoryWriterHandler {
  public CdaDataFactoryBundleWriteHandler() {
  }

  /**
   * Writes a data-source into a own file. The name of file inside the bundle is returned as string. The file name
   * returned is always absolute and can be made relative by using the IOUtils of LibBase. If the writer-handler did not
   * generate a file on its own, it should return null.
   *
   * @param bundle         the bundle where to write to.
   * @param rawDataFactory the data factory that should be written.
   * @param state          the writer state to hold the current processing information.
   * @return the name of the newly generated file or null if no file was created.
   * @throws IOException           if any error occured
   * @throws BundleWriterException if a bundle-management error occured.
   */
  public String writeDataFactory( final WriteableDocumentBundle bundle,
                                  final DataFactory rawDataFactory,
                                  final BundleWriterState state )
    throws IOException, BundleWriterException {
    final String fileName = BundleUtilities.getUniqueName( bundle, state.getFileName(), "datasources/cda-ds{0}.xml" );
    if ( fileName == null ) {
      throw new IOException( "Unable to generate unique name for cda-Data-Source" );
    }
    //TODO: refactor with CdaDataFactoryWriteHandler
    final OutputStream outputStream = bundle.createEntry( fileName, "text/xml" );
    final DefaultTagDescription tagDescription =
      new DefaultTagDescription( ClassicEngineBoot.getInstance().getGlobalConfig(), CdaModule.TAG_DEF_PREFIX );
    final XmlWriter xmlWriter = new XmlWriter
      ( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ", "\n" );

    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", CdaModule.NAMESPACE );

    xmlWriter.writeTag( CdaModule.NAMESPACE, "cda-datasource", rootAttrs, XmlWriter.OPEN );

    final CdaDataFactory dataFactory = (CdaDataFactory) rawDataFactory;
    final AttributeList configAttrs = new AttributeList();
    if ( StringUtils.isEmpty( dataFactory.getBaseUrl() ) == false ) {
      configAttrs.setAttribute( CdaModule.NAMESPACE, "base-url", dataFactory.getBaseUrl() );
    }
    if ( StringUtils.isEmpty( dataFactory.getBaseUrlField() ) == false ) {
      configAttrs.setAttribute( CdaModule.NAMESPACE, "base-url-field", dataFactory.getBaseUrlField() );
    }
    if ( StringUtils.isEmpty( dataFactory.getSolution() ) == false ) {
      configAttrs.setAttribute( CdaModule.NAMESPACE, "solution", dataFactory.getSolution() );
    }
    if ( StringUtils.isEmpty( dataFactory.getPath() ) == false ) {
      configAttrs.setAttribute( CdaModule.NAMESPACE, "path", dataFactory.getPath() );
    }
    if ( StringUtils.isEmpty( dataFactory.getFile() ) == false ) {
      configAttrs.setAttribute( CdaModule.NAMESPACE, "file", dataFactory.getFile() );
    }
    if ( StringUtils.isEmpty( dataFactory.getUsername() ) == false ) {
      configAttrs.setAttribute( CdaModule.NAMESPACE, "username", dataFactory.getUsername() );
    }
    if ( StringUtils.isEmpty( dataFactory.getPassword() ) == false ) {
      configAttrs.setAttribute( CdaModule.NAMESPACE,
        "password", PasswordEncryptionService.getInstance().encrypt( dataFactory.getPassword() ) );
    }

    configAttrs.setAttribute( CdaModule.NAMESPACE, "use-local-call", String.valueOf( dataFactory.isUseLocalCall() ) );
    configAttrs.setAttribute( CdaModule.NAMESPACE, "is-sugar-mode", String.valueOf( dataFactory.isSugarMode() ) );

    xmlWriter.writeTag( CdaModule.NAMESPACE, "config", configAttrs, XmlWriterSupport.CLOSE );

    final String[] queryNames = dataFactory.getQueryNames();
    for ( int i = 0; i < queryNames.length; i++ ) {
      final String queryName = queryNames[ i ];
      final CdaQueryEntry query = dataFactory.getQueryEntry( queryName );
      final AttributeList queryAttr = new AttributeList();
      queryAttr.setAttribute( CdaModule.NAMESPACE, "query", query.getId() );
      queryAttr.setAttribute( CdaModule.NAMESPACE, "name", query.getName() );
      xmlWriter.writeTag( CdaModule.NAMESPACE, "query", queryAttr, XmlWriterSupport.OPEN );

      final ParameterMapping[] parameterMappings = query.getParameters();
      for ( int j = 0; j < parameterMappings.length; j++ ) {
        final ParameterMapping parameterMapping = parameterMappings[ j ];
        final AttributeList paramAttr = new AttributeList();
        paramAttr.setAttribute( CdaModule.NAMESPACE, "datarow-name", parameterMapping.getName() );
        paramAttr.setAttribute( CdaModule.NAMESPACE, "variable-name", parameterMapping.getAlias() );
        xmlWriter.writeTag( CdaModule.NAMESPACE, "variable", paramAttr, XmlWriterSupport.CLOSE );
      }

      xmlWriter.writeCloseTag();
    }
    xmlWriter.writeCloseTag();
    xmlWriter.close();
    return fileName;
  }
}
