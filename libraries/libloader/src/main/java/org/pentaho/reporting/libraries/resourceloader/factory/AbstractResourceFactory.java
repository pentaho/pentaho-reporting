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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.ContentNotRecognizedException;
import org.pentaho.reporting.libraries.resourceloader.LibLoaderBoot;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Creation-Date: 05.04.2006, 16:58:57
 *
 * @author Thomas Morgner
 */
public abstract class AbstractResourceFactory implements ResourceFactory {
  private static Log logger = LogFactory.getLog( AbstractResourceFactory.class );

  private static class ResourceFactoryEntry implements Comparable {
    private FactoryModule module;
    private int weight;

    private ResourceFactoryEntry( final FactoryModule module, final int weight ) {
      this.module = module;
      this.weight = weight;
    }

    public FactoryModule getModule() {
      return module;
    }

    public int getWeight() {
      return weight;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final ResourceFactoryEntry that = (ResourceFactoryEntry) o;

      if ( weight != that.weight ) {
        return false;
      }
      if ( module != null ? !module.equals( that.module ) : that.module != null ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result;
      result = ( module != null ? module.hashCode() : 0 );
      result = 31 * result + weight;
      return result;
    }

    /**
     * Compares this object with the specified object for order.  Returns a negative integer, zero, or a positive
     * integer as this object is less than, equal to, or greater than the specified object.<p>
     * <p/>
     * "Note: this class has a natural ordering that is inconsistent with equals."
     *
     * @param o the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     * the specified object.
     * @throws ClassCastException if the specified object's type prevents it from being compared to this Object.
     */
    public int compareTo( final Object o ) {
      final ResourceFactoryEntry entry = (ResourceFactoryEntry) o;
      // the order is intentionally reveresed, so that a sorted set has the best
      // fit as first entry.
      if ( weight == entry.weight ) {
        return 0;
      }
      if ( weight < entry.weight ) {
        return -1;
      }
      return 1;
    }
  }

  /**
   * The available factory methods.
   */
  private HashSet<FactoryModule> factoryModules;
  /**
   * Which type of objects do we create here?
   */
  private Class factoryType;

  protected AbstractResourceFactory( final Class factoryType ) {
    if ( factoryType == null ) {
      throw new NullPointerException();
    }
    this.factoryType = factoryType;
    this.factoryModules = new HashSet<FactoryModule>();
  }

  public Class getFactoryType() {
    return factoryType;
  }

  public void initializeDefaults() {
    final String type = getFactoryType().getName();
    final String prefix = CONFIG_PREFIX + type;
    final Configuration config = LibLoaderBoot.getInstance().getGlobalConfig();
    final Iterator itType = config.findPropertyKeys( prefix );
    while ( itType.hasNext() ) {
      final String key = (String) itType.next();
      final String modClass = config.getConfigProperty( key );
      final FactoryModule maybeFactory = ObjectUtilities.loadAndInstantiate
        ( modClass, AbstractResourceFactory.class, FactoryModule.class );
      if ( maybeFactory == null ) {
        continue;
      }
      registerModule( maybeFactory );
    }
  }

  public synchronized boolean registerModule( final String className ) {
    try {
      final ClassLoader loader = ObjectUtilities.getClassLoader( getClass() );
      final Class c = Class.forName( className, false, loader );
      registerModule( (FactoryModule) c.newInstance() );
      return true;
    } catch ( Exception e ) {
      return false;
    }
  }

  public synchronized void registerModule( final FactoryModule module ) {
    if ( factoryModules.contains( module ) ) {
      return;
    }
    factoryModules.add( module );
  }

  /**
   * @param data
   * @param context
   * @return
   * @throws ResourceCreationException
   * @throws ResourceLoadingException
   */
  public synchronized Resource create( final ResourceManager manager,
                                       final ResourceData data,
                                       final ResourceKey context )
    throws ResourceCreationException, ResourceLoadingException {
    final TreeSet<ResourceFactoryEntry> sortedEntries = new TreeSet<ResourceFactoryEntry>();
    final Iterator<FactoryModule> factoryModulesIt = factoryModules.iterator();
    while ( factoryModulesIt.hasNext() ) {
      final FactoryModule mod = factoryModulesIt.next();
      final int weight = mod.canHandleResource( manager, data );
      if ( weight >= 0 ) {
        sortedEntries.add( new ResourceFactoryEntry( mod, weight ) );
      }
    }

    final Iterator<ResourceFactoryEntry> it = sortedEntries.iterator();
    while ( it.hasNext() ) {
      final ResourceFactoryEntry entry = it.next();
      try {
        return entry.getModule().create( manager, data, context );
      } catch ( ResourceCreationException ex ) {
        if ( ex instanceof ContentNotRecognizedException ) {
          throw ex;
        }
      } catch ( Exception ex ) {
        // ok, that one failed, try the next one ...
        if ( logger.isDebugEnabled() ) {
          logger.debug( "Failed to load content with module " + entry.getModule() + ":", ex );
        }
      }
    }
    throw new ContentNotRecognizedException( "No valid handler for the given content." );
  }
}
