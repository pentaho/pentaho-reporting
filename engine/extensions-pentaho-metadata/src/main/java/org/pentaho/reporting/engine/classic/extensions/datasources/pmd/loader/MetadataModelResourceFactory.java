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


package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.loader;

import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.repository.IMetadataDomainRepository;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.metadata.util.XmiParser;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
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

public class MetadataModelResourceFactory implements ResourceFactory {
  public static final FactoryParameterKey DOMAIN_ID = new FactoryParameterKey( "domain-id" );

  public MetadataModelResourceFactory() {
  }

  public Resource create( final ResourceManager manager,
                          final ResourceData data,
                          final ResourceKey context ) throws ResourceCreationException, ResourceLoadingException {
    final InputStream stream = data.getResourceAsStream( manager );
    final long version = data.getVersion( manager );
    final ResourceKey key = data.getKey();
    final Object o = key.getFactoryParameters().get( DOMAIN_ID );
    if ( o == null ) {
      throw new ResourceLoadingException( "Your resource-key must have a domain-id factory key defined." );
    }
    try {
      final InMemoryMetadataDomainRepository repo = new InMemoryMetadataDomainRepository();
      final XmiParser parser = new XmiParser();
      final Domain domain = parser.parseXmi( stream );
      domain.setId( String.valueOf( o ) );
      repo.storeDomain( domain, true );
      return new SimpleResource( key, repo, IMetadataDomainRepository.class, version );
    } catch ( ResourceCreationException e ) {
      throw e;
    } catch ( ResourceLoadingException e ) {
      throw e;
    } catch ( IOException ioe ) {
      throw new ResourceLoadingException( "IOError", ioe );
    } catch ( Exception e ) {
      throw new ResourceCreationException( "Generic Error", e );
    } finally {
      try {
        stream.close();
      } catch ( IOException e ) {
        // ignore ..
      }
    }
  }

  public Class getFactoryType() {
    return IMetadataDomainRepository.class;
  }

  public void initializeDefaults() {

  }
}
