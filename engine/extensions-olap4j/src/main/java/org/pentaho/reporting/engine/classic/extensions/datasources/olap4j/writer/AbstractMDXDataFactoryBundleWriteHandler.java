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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.writer;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.Olap4JDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

public abstract class AbstractMDXDataFactoryBundleWriteHandler {
  public AbstractMDXDataFactoryBundleWriteHandler() {
  }

  protected void writeBody( final AbstractMDXDataFactory dataFactory, final XmlWriter xmlWriter )
    throws IOException, BundleWriterException {
    writeConnectionInfo( xmlWriter, dataFactory.getConnectionProvider() );
    final String roleField = dataFactory.getRoleField();
    if ( roleField != null ) {
      xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "role-field", XmlWriter.OPEN );
      xmlWriter.writeTextNormalized( roleField, false );
      xmlWriter.writeCloseTag();
    }

    final String jdbcUserField = dataFactory.getJdbcUserField();
    if ( jdbcUserField != null ) {
      xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "jdbc-user-field", XmlWriter.OPEN );
      xmlWriter.writeTextNormalized( jdbcUserField, false );
      xmlWriter.writeCloseTag();
    }

    final String jdbcPasswordField = dataFactory.getJdbcPasswordField();
    if ( jdbcPasswordField != null ) {
      xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "jdbc-password-field", XmlWriter.OPEN );
      xmlWriter.writeTextNormalized( jdbcPasswordField, false );
      xmlWriter.writeCloseTag();
    }
  }

  private void writeConnectionInfo( final XmlWriter xmlWriter,
                                    final OlapConnectionProvider connectionProvider )
    throws IOException, BundleWriterException {
    if ( connectionProvider == null ) {
      throw new NullPointerException();
    }

    final String configKey = Olap4JDataFactoryModule.CONNECTION_WRITER_PREFIX + connectionProvider.getClass().getName();
    final Configuration globalConfig = ClassicEngineBoot.getInstance().getGlobalConfig();
    final String value = globalConfig.getConfigProperty( configKey );
    if ( value == null ) {
      throw new BundleWriterException
        ( "Unable to find writer for connection info of type " + connectionProvider.getClass() );
    }

    final OlapConnectionProviderWriteHandler handler = ObjectUtilities.loadAndInstantiate
      ( value, AbstractMDXDataFactoryBundleWriteHandler.class, OlapConnectionProviderWriteHandler.class );
    if ( handler != null ) {
      handler.writeReport( xmlWriter, connectionProvider );
    }
  }

  protected DefaultTagDescription createTagDescription() {
    final DefaultTagDescription tagDescription = new DefaultTagDescription();
    tagDescription.setNamespaceHasCData( Olap4JDataFactoryModule.NAMESPACE, false );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "driver", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "path", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "property", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "url", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "jdbc-user-field", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "jdbc-password-field", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "role-field", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "global-script", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "static-query", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "script", true );
    return tagDescription;
  }

}
