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
