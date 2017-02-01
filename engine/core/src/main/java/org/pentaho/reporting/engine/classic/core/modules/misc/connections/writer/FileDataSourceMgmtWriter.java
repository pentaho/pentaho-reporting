package org.pentaho.reporting.engine.classic.core.modules.misc.connections.writer;

import org.pentaho.database.model.IDatabaseConnection;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.ConnectionModule;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Set;

public class FileDataSourceMgmtWriter implements DataSourceMgmtWriter {
  private static final String PROP_INDEX_TBS = "INDEX_TBS"; //$NON-NLS-1$

  private static final String PROP_DATA_TBS = "DATA_TBS"; //$NON-NLS-1$

  private static final String PROP_SERVERNAME = "SERVERNAME"; //$NON-NLS-1$

  private static final String PROP_PASSWORD = "PASSWORD"; //$NON-NLS-1$

  private static final String PROP_USERNAME = "USERNAME"; //$NON-NLS-1$

  private static final String PROP_PORT = "PORT"; //$NON-NLS-1$

  private static final String PROP_DATABASE_NAME = "DATABASE_NAME"; //$NON-NLS-1$

  private static final String PROP_HOST_NAME = "HOST_NAME"; //$NON-NLS-1$

  private static final String PROP_CONTYPE = "CONTYPE"; //$NON-NLS-1$

  private static final String PROP_TYPE = "TYPE"; //$NON-NLS-1$

  private static final String NODE_ROOT = "databaseMeta"; //$NON-NLS-1$

  private static final String NODE_ATTRIBUTES = "attributes"; //$NON-NLS-1$
  private static final String NODE_ATTRIBUTE = "attribute"; //$NON-NLS-1$

  public FileDataSourceMgmtWriter() {
  }

  public void write( final IDatabaseConnection[] connections, final OutputStream out ) throws IOException {
    DefaultTagDescription tagDescription = new DefaultTagDescription();
    tagDescription.setDefaultNamespace( ConnectionModule.NAMESPACE );
    tagDescription.setNamespaceHasCData( ConnectionModule.NAMESPACE, false );
    tagDescription.setElementHasCData( ConnectionModule.NAMESPACE, "attribute", true );

    final XmlWriter writer = new XmlWriter( new OutputStreamWriter( out, "UTF-8" ), tagDescription );
    writer.writeXmlDeclaration( "UTF-8" );

    final AttributeList rootList = new AttributeList();
    rootList.addNamespaceDeclaration( null, ConnectionModule.NAMESPACE );

    writer.writeTag( ConnectionModule.NAMESPACE, "connections", rootList, XmlWriter.OPEN );
    for ( int i = 0; i < connections.length; i++ ) {
      final IDatabaseConnection connection = connections[i];
      write( connection, writer );
    }
    writer.writeCloseTag();
    writer.flush();
  }

  private void write( final IDatabaseConnection databaseConnection, final XmlWriter writer ) throws IOException {
    final AttributeList rootAttrs = new AttributeList();
    if ( databaseConnection.getDatabaseType() != null ) {
      rootAttrs.setAttribute( ConnectionModule.NAMESPACE, PROP_TYPE, databaseConnection.getDatabaseType()
          .getShortName() );
    }
    final String port =
        StringUtils.isEmpty( databaseConnection.getDatabasePort() ) ? "0" : databaseConnection.getDatabasePort();

    // Then the basic db information
    rootAttrs.setAttribute( ConnectionModule.NAMESPACE, "name", databaseConnection.getName() );
    rootAttrs.setAttribute( ConnectionModule.NAMESPACE, "id", databaseConnection.getId() );
    rootAttrs.setAttribute( ConnectionModule.NAMESPACE, PROP_CONTYPE, setNull( databaseConnection.getAccessType()
        .getName() ) );
    rootAttrs.setAttribute( ConnectionModule.NAMESPACE, PROP_HOST_NAME, setNull( databaseConnection.getHostname() ) );
    rootAttrs.setAttribute( ConnectionModule.NAMESPACE, PROP_DATABASE_NAME, setNull( databaseConnection
        .getDatabaseName() ) );
    rootAttrs.setAttribute( ConnectionModule.NAMESPACE, PROP_PORT, String.valueOf( new Long( port ) ) ); // implicit
                                                                                                         // validate, as
                                                                                                         // the
                                                                                                         // interface
                                                                                                         // allows text
                                                                                                         // here.
    rootAttrs.setAttribute( ConnectionModule.NAMESPACE, PROP_USERNAME, setNull( databaseConnection.getUsername() ) );
    rootAttrs.setAttribute( ConnectionModule.NAMESPACE, PROP_PASSWORD, setNull( databaseConnection.getPassword() ) );
    rootAttrs.setAttribute( ConnectionModule.NAMESPACE, PROP_SERVERNAME, setNull( databaseConnection
        .getInformixServername() ) );
    rootAttrs
        .setAttribute( ConnectionModule.NAMESPACE, PROP_DATA_TBS, setNull( databaseConnection.getDataTablespace() ) );
    rootAttrs.setAttribute( ConnectionModule.NAMESPACE, PROP_INDEX_TBS, setNull( databaseConnection
        .getIndexTablespace() ) );
    writer.writeTag( ConnectionModule.NAMESPACE, NODE_ROOT, rootAttrs, XmlWriter.OPEN );

    // Now store all the attributes set on the database connection...
    //
    final Map<String, String> attributes = databaseConnection.getAttributes();
    final Set<Map.Entry<String, String>> entries = attributes.entrySet();
    if ( entries.isEmpty() == false ) {
      writer.writeTag( null, NODE_ATTRIBUTES, XmlWriter.OPEN );
      for ( final Map.Entry<String, String> e : entries ) {
        writer.writeTag( ConnectionModule.NAMESPACE, NODE_ATTRIBUTE, "name", e.getKey(), XmlWriter.OPEN );
        writer.writeTextNormalized( e.getValue(), false );
        writer.writeCloseTag();
      }
      writer.writeCloseTag();
    }
    writer.writeCloseTag();
  }

  private String setNull( final String value ) {
    if ( StringUtils.isEmpty( value ) ) {
      return null;
    }
    return value;
  }
}
