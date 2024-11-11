/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.resourceloader.factory.property;

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
import java.util.Properties;

public class PropertiesResourceFactory
  implements ResourceFactory {
  public PropertiesResourceFactory() {
  }

  public Resource create( final ResourceManager manager,
                          final ResourceData data,
                          final ResourceKey context )
    throws ResourceCreationException, ResourceLoadingException {
    try {
      final Properties properties = new Properties();
      final InputStream stream = data.getResourceAsStream( manager );
      try {
        properties.load( stream );
      } finally {
        stream.close();
      }
      return new SimpleResource( data.getKey(), properties, Properties.class, data.getVersion( manager ) );
    } catch ( IOException e ) {
      throw new ResourceLoadingException( "Failed to load the properties file.", e );
    }
  }

  public Class getFactoryType() {
    return Properties.class;
  }

  public void initializeDefaults() {
    // none required ...
  }
}
