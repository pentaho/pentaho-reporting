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


package org.pentaho.reporting.libraries.fonts.encoding;

import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.SimpleResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Creation-Date: 29.04.2006, 14:32:15
 *
 * @author Thomas Morgner
 */
public class EncodingFactory implements ResourceFactory {
  public EncodingFactory() {
  }

  public Resource create( final ResourceManager manager,
                          final ResourceData data,
                          final ResourceKey context )
    throws ResourceCreationException, ResourceLoadingException {
    try {
      final InputStream in = data.getResourceAsStream( manager );
      final ObjectInputStream oin = new ObjectInputStream( in );
      try {
        final Object ob = oin.readObject();
        // yes, that will be more generic in the future ...
        if ( ob instanceof External8BitEncodingData == false ) {
          throw new ResourceCreationException( "This is no 8Bit Encoding data" );
        }
        final External8BitEncodingData encData = (External8BitEncodingData) ob;
        final External8BitEncodingCore encCore =
          new External8BitEncodingCore( encData );
        return new SimpleResource( data.getKey(), encCore, getFactoryType(), data.getVersion( manager ) );
      } finally {
        oin.close();
      }
    } catch ( IOException e ) {
      throw new ResourceLoadingException( "Failed to load resource", e );
    } catch ( ClassNotFoundException e ) {
      throw new ResourceCreationException
        ( "Missing class definition: Failed to create encoding." );
    }
  }

  public Class getFactoryType() {
    return Encoding.class;
  }

  public void initializeDefaults() {
    // do nothing -- yet ..
  }
}
