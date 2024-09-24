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

package org.pentaho.reporting.libraries.base.config;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * A hierarchical configuration. Such a configuration can have one or more parent configurations providing usefull
 * default values.
 *
 * @author Thomas Morgner
 */
public class HierarchicalConfiguration implements ModifiableConfiguration {

  /**
   * A constant for serialization support.
   */
  private static final long serialVersionUID = -6962432361197107831L;

  /**
   * The instance configuration properties.
   */
  private DefaultConfiguration configuration;
  /**
   * The booter class from where to get the global configuration after deserialization.
   */
  private Class booterClass;


  /**
   * The parent configuration (null if this is the root configuration).
   */
  private transient Configuration parentConfiguration;

  /**
   * Creates a new configuration.
   */
  public HierarchicalConfiguration() {
    this.configuration = new DefaultConfiguration();
  }

  /**
   * Creates a new configuration.
   *
   * @param parentConfiguration the parent configuration.
   */
  public HierarchicalConfiguration( final Configuration parentConfiguration ) {
    this();
    this.parentConfiguration = parentConfiguration;
  }

  /**
   * Creates a new configuration, that is able to reconnect itself to the global configuration after deserialization.
   *
   * @param booterClass the booter class that holds the global configuration.
   */
  public HierarchicalConfiguration( final Class booterClass ) {
    this();
    this.booterClass = booterClass;
  }

  /**
   * Returns the configuration property with the specified key.
   *
   * @param key the property key.
   * @return the property value.
   */
  public String getConfigProperty( final String key ) {
    return getConfigProperty( key, null );
  }

  /**
   * Returns the configuration property with the specified key (or the specified default value if there is no such
   * property).
   * <p/>
   * If the property is not defined in this configuration, the code will lookup the property in the parent
   * configuration.
   *
   * @param key          the property key.
   * @param defaultValue the default value.
   * @return the property value.
   */
  public String getConfigProperty( final String key, final String defaultValue ) {
    String value = this.configuration.getProperty( key );
    if ( value == null ) {
      if ( isRootConfig() ) {
        value = defaultValue;
      } else {
        value = this.parentConfiguration.getConfigProperty( key, defaultValue );
      }
    }
    return value;
  }

  /**
   * Sets a configuration property.
   *
   * @param key   the property key.
   * @param value the property value.
   */
  public void setConfigProperty( final String key, final String value ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    if ( value == null ) {
      this.configuration.remove( key );
    } else {
      this.configuration.setProperty( key, value );
    }
  }

  /**
   * Returns true if this object has no parent.
   *
   * @return true, if this report is the root configuration, false otherwise.
   */
  private boolean isRootConfig() {
    return this.parentConfiguration == null;
  }

  /**
   * Checks, whether the given key is localy defined in this instance or whether the key's value is inherited.
   *
   * @param key the key that should be checked.
   * @return true, if the key is defined locally, false otherwise.
   */
  public boolean isLocallyDefined( final String key ) {
    return this.configuration.containsKey( key );
  }

  /**
   * Returns the collection of properties for the configuration.
   *
   * @return the properties.
   */
  protected Properties getConfiguration() {
    return this.configuration;
  }

  /**
   * The new configuartion will be inserted into the list of report configuration, so that this configuration has the
   * given report configuration instance as parent.
   *
   * @param config the new report configuration.
   */
  public void insertConfiguration( final HierarchicalConfiguration config ) {
    if ( config == null ) {
      throw new NullPointerException( "Configuration that should be inserted is null" );
    }
    config.setParentConfig( getParentConfig() );
    setParentConfig( config );
  }

  public void reconnectConfiguration( final Configuration config ) {
    if ( this.parentConfiguration != null ) {
      throw new IllegalStateException( "Cannot use reconnect to redefine the parent configuration." );
    }
    setParentConfig( config );
  }

  /**
   * Set the parent configuration. The parent configuration is queried, if the requested configuration values was not
   * found in this report configuration.
   *
   * @param config the parent configuration.
   */
  protected void setParentConfig( final Configuration config ) {
    if ( this.parentConfiguration == this ) {
      throw new IllegalArgumentException( "Cannot add myself as parent configuration." );
    }
    this.parentConfiguration = config;
  }

  /**
   * Returns the parent configuration. The parent configuration is queried, if the requested configuration values was
   * not found in this report configuration.
   *
   * @return the parent configuration.
   */
  protected Configuration getParentConfig() {
    return this.parentConfiguration;
  }

  /**
   * Returns all defined configuration properties for the report. The enumeration contains all keys of the changed
   * properties, properties set from files or the system properties are not included.
   *
   * @return all defined configuration properties for the report.
   */
  public Enumeration<String> getConfigProperties() {
    return this.configuration.getConfigProperties();
  }

  /**
   * Searches all property keys that start with a given prefix.
   *
   * @param prefix the prefix that all selected property keys should share
   * @return the properties as iterator.
   */
  public Iterator<String> findPropertyKeys( final String prefix ) {
    if ( prefix == null ) {
      throw new NullPointerException( "Prefix must not be null" );
    }
    final HashSet<String> keys = new HashSet<String>();
    collectPropertyKeys( prefix, this, keys );
    final String[] objects = keys.toArray( new String[ keys.size() ] );
    Arrays.sort( objects );
    return Arrays.asList( objects ).iterator();
  }

  /**
   * Collects property keys from this and all parent report configurations, which start with the given prefix.
   *
   * @param prefix    the prefix, that selects the property keys.
   * @param config    the currently processed report configuration.
   * @param collector the target list, that should receive all valid keys.
   */
  private void collectPropertyKeys( final String prefix,
                                    final Configuration config,
                                    final Set<String> collector ) {
    final Enumeration<String> enum1 = config.getConfigProperties();
    while ( enum1.hasMoreElements() ) {
      final String key = enum1.nextElement();
      if ( key.startsWith( prefix ) ) {
        collector.add( key );
      }
    }

    if ( config instanceof HierarchicalConfiguration ) {
      final HierarchicalConfiguration hconfig = (HierarchicalConfiguration) config;
      if ( hconfig.parentConfiguration != null ) {
        collectPropertyKeys( prefix, hconfig.parentConfiguration, collector );
      }
    }
  }

  /**
   * Helper method for serialization.
   *
   * @param out the output stream where to write the object.
   * @throws java.io.IOException if errors occur while writing the stream.
   */
  private void writeObject( final ObjectOutputStream out )
    throws IOException {
    out.defaultWriteObject();

    if ( parentConfiguration instanceof HierarchicalConfiguration ) {
      final HierarchicalConfiguration parent = (HierarchicalConfiguration) parentConfiguration;
      if ( parent.booterClass != null ) {
        out.writeBoolean( false );
      } else {
        out.writeBoolean( true );
        out.writeObject( parentConfiguration );
      }
    } else if ( parentConfiguration != null ) {
      out.writeBoolean( true );
      out.writeObject( parentConfiguration );
    } else {
      out.writeBoolean( false );
    }
  }

  /**
   * Helper method for serialization.
   *
   * @param in the input stream from where to read the serialized object.
   * @throws java.io.IOException    when reading the stream fails.
   * @throws ClassNotFoundException if a class definition for a serialized object could not be found.
   */
  private void readObject( final ObjectInputStream in )
    throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    final boolean readParent = in.readBoolean();
    if ( readParent ) {
      parentConfiguration = (Configuration) in.readObject();
    } else {
      if ( booterClass != null ) {
        final AbstractBoot boot = AbstractBoot.loadBooter( booterClass.getName(), booterClass );
        parentConfiguration = boot.getGlobalConfig();
      } else {
        parentConfiguration = null;
      }
    }
  }

  /**
   * Clones this configuration.
   *
   * @return a clone of this configuration.
   */
  public Object clone() {
    try {
      final HierarchicalConfiguration config = (HierarchicalConfiguration) super.clone();
      config.configuration = (DefaultConfiguration) configuration.clone();
      return config;
    } catch ( CloneNotSupportedException cne ) {
      throw new IllegalStateException( cne );
    }
  }

  public void addAll( final Configuration config ) {
    final Enumeration<String> configProperties = config.getConfigProperties();
    while ( configProperties.hasMoreElements() ) {
      final String key = configProperties.nextElement();
      configuration.setConfigProperty( key, config.getConfigProperty( key ) );
    }
  }
}
