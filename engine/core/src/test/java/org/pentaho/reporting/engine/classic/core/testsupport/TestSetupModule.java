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
 * Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
 * All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.testsupport;

import org.apache.commons.lang3.StringEscapeUtils;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultAttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class TestSetupModule extends AbstractModule {
  public TestSetupModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    final String bundleLocation = "org.pentaho.reporting.engine.classic.core.testsupport.metadata";
    final String keyPrefix = "attribute.test-run.";
    final DefaultAttributeMetaData metaData =
        new DefaultAttributeMetaData( "test-run", "test-value", bundleLocation, keyPrefix, Object.class, false, 0 );

    final ElementMetaData[] types = ElementTypeRegistry.getInstance().getAllElementTypes();
    for ( int i = 0; i < types.length; i++ ) {
      final ElementMetaData type = types[i];
      final AttributeRegistry attributeRegistry =
          ElementTypeRegistry.getInstance().getAttributeRegistry( type.getName() );
      attributeRegistry.putAttributeDescription( metaData );
    }

    try {
      Driver driver = ObjectUtilities.loadAndInstantiate( "org.hsqldb.jdbcDriver", TestSetupModule.class, Driver.class );
      populateDatabase( driver );
    } catch ( Exception e ) {
      throw new ModuleInitializeException( "Failed to load the HSQL-DB driver", e );
    }
  }

  private void populateDatabase( Driver driver ) throws SQLException, IOException {
    Properties p = new Properties();
    p.setProperty( "user", "sa" );
    p.setProperty( "password", "" );
    final Connection connection = driver.connect( "jdbc:hsqldb:mem:SampleData", p );
    connection.setAutoCommit( false );
    if ( isValid( connection ) ) {
      // both the test-module here and the sample-data module try to initialize the database.
      // lets do it only once.
      return;
    }
    try {
      final InputStream in = new FileInputStream( "target/test-classes/sql/sampledata.script" );
      final InputStreamReader inReader = new InputStreamReader( in, "ISO-8859-1" );
      final BufferedReader bin = new BufferedReader( inReader, 4096 );
      try {
        final Statement statement = connection.createStatement();
        try {
          String line;
          while ( ( line = bin.readLine() ) != null ) {
            if ( line.startsWith( "CREATE SCHEMA " ) || line.startsWith( "CREATE USER SA " )
                || line.startsWith( "GRANT DBA TO SA" ) ) {
              // ignore the error, HSQL sucks
            } else {
              statement.addBatch( StringEscapeUtils.unescapeJava( line ) );
            }
          }
          statement.executeBatch();
        } finally {
          statement.close();
        }
      } finally {
        bin.close();
      }

      connection.commit();
    } catch ( FileNotFoundException fe ) {
      DebugLog.log( "Unable to populate test database, no script at ./sql/sampledata.script" );
    } finally {
      connection.close();
    }
  }

  private boolean isValid( final Connection connection ) {
    // cheap test:

    try {
      Statement statement = connection.createStatement();
      boolean result = false;
      try {
        result = statement.execute( "SELECT Count(*) FROM CUSTOMERS" );
      } finally {
        statement.close();
      }
      return result;
    } catch ( final SQLException e ) {
      return false;
    }
  }
}
