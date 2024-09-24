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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.writer;

import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractNamedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.MondrianDataFactoryModule;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

public abstract class AbstractNamedMDXDataFactoryWriteHandler extends AbstractMDXDataFactoryWriteHandler {
  protected AbstractNamedMDXDataFactoryWriteHandler() {
  }

  protected void writeBody( final ReportWriterContext reportWriterContext,
                            final AbstractNamedMDXDataFactory df,
                            final XmlWriter xmlWriter ) throws IOException, ReportWriterException {
    super.writeBody( reportWriterContext, df, xmlWriter );

    final String globalScript = df.getGlobalScript();
    final String globalScriptLanguage = df.getGlobalScriptLanguage();
    if ( StringUtils.isEmpty( globalScript ) == false && StringUtils.isEmpty( globalScriptLanguage ) == false ) {
      xmlWriter.writeTag
        ( MondrianDataFactoryModule.NAMESPACE, "global-script", "language", globalScriptLanguage,
          XmlWriterSupport.OPEN );
      xmlWriter.writeTextNormalized( globalScript, false );
      xmlWriter.writeCloseTag();
    }

    xmlWriter.writeTag( MondrianDataFactoryModule.NAMESPACE, "query-definitions", XmlWriterSupport.OPEN );
    final String[] queryNames = df.getQueryNames();
    for ( int i = 0; i < queryNames.length; i++ ) {
      final String queryName = queryNames[ i ];
      final String query = df.getQuery( queryName );
      xmlWriter.writeTag( MondrianDataFactoryModule.NAMESPACE, "query", "name", queryName, XmlWriterSupport.OPEN );

      xmlWriter.writeTag( MondrianDataFactoryModule.NAMESPACE, "static-query", XmlWriterSupport.OPEN );
      xmlWriter.writeTextNormalized( query, false );
      xmlWriter.writeCloseTag();

      final String queryScriptLanguage = df.getScriptingLanguage( queryName );
      final String queryScript = df.getScript( queryName );

      if ( StringUtils.isEmpty( queryScript ) == false &&
        ( StringUtils.isEmpty( queryScriptLanguage ) == false
          || StringUtils.isEmpty( globalScriptLanguage ) == false ) ) {
        if ( StringUtils.isEmpty( queryScriptLanguage ) ) {
          xmlWriter.writeTag( MondrianDataFactoryModule.NAMESPACE, "script", XmlWriterSupport.OPEN );
        } else {
          xmlWriter.writeTag( MondrianDataFactoryModule.NAMESPACE, "script", "language", queryScriptLanguage,
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
