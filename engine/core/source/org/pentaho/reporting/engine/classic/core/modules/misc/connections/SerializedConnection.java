package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

import org.pentaho.database.model.IDatabaseConnection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializedConnection {
  private byte[] connection;

  public SerializedConnection( final IDatabaseConnection connection ) throws DatasourceMgmtServiceException {
    try {
      final ByteArrayOutputStream bout = new ByteArrayOutputStream();
      final ObjectOutputStream out = new ObjectOutputStream( bout );
      out.writeObject( connection );
      out.close();

      this.connection = bout.toByteArray();
    } catch ( IOException ioe ) {
      throw new DatasourceMgmtServiceException( ioe );
    }
  }

  public IDatabaseConnection getConnection() {
    try {
      // return a copy, by deserializing the result.
      final ByteArrayInputStream bin = new ByteArrayInputStream( connection );
      final ObjectInputStream in = new ObjectInputStream( bin );
      return (IDatabaseConnection) in.readObject();
    } catch ( IOException e ) {
      throw new DatasourceMgmtServiceException( "Unable to deserialize database connections.", e );
    } catch ( ClassNotFoundException e ) {
      throw new DatasourceMgmtServiceException( "Unable to deserialize database connections.", e );
    }
  }
}
