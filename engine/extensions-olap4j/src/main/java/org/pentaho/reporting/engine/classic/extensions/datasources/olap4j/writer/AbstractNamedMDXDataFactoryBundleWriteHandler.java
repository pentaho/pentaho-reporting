/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.writer;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractNamedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.Olap4JDataFactoryModule;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

public abstract class AbstractNamedMDXDataFactoryBundleWriteHandler
  extends AbstractMDXDataFactoryBundleWriteHandler {
  public AbstractNamedMDXDataFactoryBundleWriteHandler() {
  }

  protected void writeBody( final AbstractNamedMDXDataFactory df, final XmlWriter xmlWriter )
    throws IOException, BundleWriterException {
    super.writeBody( df, xmlWriter );

    final String globalScript = df.getGlobalScript();
    final String globalScriptLanguage = df.getGlobalScriptLanguage();
    if ( StringUtils.isEmpty( globalScript ) == false && StringUtils.isEmpty( globalScriptLanguage ) == false ) {
      xmlWriter.writeTag
        ( Olap4JDataFactoryModule.NAMESPACE, "global-script", "language", globalScriptLanguage, XmlWriterSupport.OPEN );
      xmlWriter.writeTextNormalized( globalScript, false );
      xmlWriter.writeCloseTag();
    }

    xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "query-definitions", XmlWriterSupport.OPEN );
    final String[] queryNames = df.getQueryNames();
    for ( int i = 0; i < queryNames.length; i++ ) {
      final String queryName = queryNames[ i ];
      final String query = df.getQuery( queryName );
      xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "query", "name", queryName, XmlWriterSupport.OPEN );

      xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "static-query", XmlWriterSupport.OPEN );
      xmlWriter.writeTextNormalized( query, false );
      xmlWriter.writeCloseTag();

      final String queryScriptLanguage = df.getScriptingLanguage( queryName );
      final String queryScript = df.getScript( queryName );

      if ( StringUtils.isEmpty( queryScript ) == false &&
        ( StringUtils.isEmpty( queryScriptLanguage ) == false
          || StringUtils.isEmpty( globalScriptLanguage ) == false ) ) {
        if ( StringUtils.isEmpty( queryScriptLanguage ) ) {
          xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "script", XmlWriterSupport.OPEN );
        } else {
          xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "script", "language", queryScriptLanguage,
            XmlWriterSupport.OPEN );
        }
        xmlWriter.writeTextNormalized( queryScript, false );
        xmlWriter.writeCloseTag();
      }

      xmlWriter.writeCloseTag();
    }
    xmlWriter.writeCloseTag();
  }
}
