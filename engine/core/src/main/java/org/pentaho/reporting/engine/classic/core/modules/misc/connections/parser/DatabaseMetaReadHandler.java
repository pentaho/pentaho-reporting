package org.pentaho.reporting.engine.classic.core.modules.misc.connections.parser;

import org.pentaho.database.model.DatabaseAccessType;
import org.pentaho.database.model.DatabaseConnection;
import org.pentaho.database.model.IDatabaseConnection;
import org.pentaho.database.service.IDatabaseDialectService;
import org.pentaho.database.util.DatabaseTypeHelper;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.ConnectionModule;
import org.pentaho.reporting.libraries.base.boot.ObjectFactory;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.PropertiesReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Map;
import java.util.Properties;

public class DatabaseMetaReadHandler extends AbstractXmlReadHandler {
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

  private static final String NODE_ATTRIBUTES = "attributes"; //$NON-NLS-1$
  private static final String NODE_ATTRIBUTE = "attribute"; //$NON-NLS-1$

  private final DatabaseTypeHelper databaseTypeHelper;
  private DatabaseConnection databaseConnection;
  private PropertiesReadHandler propertiesReadHandler;

  public DatabaseMetaReadHandler() {
    final ObjectFactory objectFactory = ClassicEngineBoot.getInstance().getObjectFactory();
    final IDatabaseDialectService dialectService = objectFactory.get( IDatabaseDialectService.class );
    this.databaseTypeHelper = new DatabaseTypeHelper( dialectService.getDatabaseTypes() );
  }

  public IDatabaseConnection getObject() throws SAXException {
    return databaseConnection;
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    databaseConnection = new DatabaseConnection();

    final String databaseType = attrs.getValue( ConnectionModule.NAMESPACE, PROP_TYPE );
    if ( databaseType != null ) {
      databaseConnection.setDatabaseType( databaseTypeHelper.getDatabaseTypeByShortName( databaseType ) );
    } else {
      databaseConnection.setDatabaseType( null );
    }

    databaseConnection.setName( attrs.getValue( ConnectionModule.NAMESPACE, "name" ) );
    databaseConnection.setId( attrs.getValue( ConnectionModule.NAMESPACE, "id" ) );

    final String accessType = attrs.getValue( ConnectionModule.NAMESPACE, PROP_CONTYPE );
    databaseConnection.setAccessType( accessType != null ? DatabaseAccessType.getAccessTypeByName( accessType ) : null );
    databaseConnection.setHostname( attrs.getValue( ConnectionModule.NAMESPACE, PROP_HOST_NAME ) );
    databaseConnection.setDatabaseName( attrs.getValue( ConnectionModule.NAMESPACE, PROP_DATABASE_NAME ) );
    databaseConnection.setDatabasePort( attrs.getValue( ConnectionModule.NAMESPACE, PROP_PORT ) );
    databaseConnection.setUsername( attrs.getValue( ConnectionModule.NAMESPACE, PROP_USERNAME ) );
    databaseConnection.setPassword( attrs.getValue( ConnectionModule.NAMESPACE, PROP_PASSWORD ) );
    databaseConnection.setInformixServername( attrs.getValue( ConnectionModule.NAMESPACE, PROP_SERVERNAME ) );
    databaseConnection.setDataTablespace( attrs.getValue( ConnectionModule.NAMESPACE, PROP_DATA_TBS ) );
    databaseConnection.setIndexTablespace( attrs.getValue( ConnectionModule.NAMESPACE, PROP_INDEX_TBS ) );
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) ) {
      if ( NODE_ATTRIBUTES.equals( tagName ) ) {
        propertiesReadHandler = new PropertiesReadHandler( NODE_ATTRIBUTE );
        return propertiesReadHandler;
      }
    }
    return null;
  }

  protected void doneParsing() throws SAXException {
    if ( propertiesReadHandler == null ) {
      return;
    }

    final Properties result = propertiesReadHandler.getResult();
    for ( final Map.Entry<Object, Object> entry : result.entrySet() ) {
      final String code = (String) entry.getKey();
      final String attribute = (String) entry.getValue();
      databaseConnection.getAttributes().put( code, ( attribute == null || attribute.length() == 0 ) ? "" : attribute ); //$NON-NLS-1$
    }
  }

}
