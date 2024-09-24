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

package org.pentaho.reporting.libraries.base.boot;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * The abstract module provides a default implementation of the module interface.
 * <p/>
 * The module can be specified in an external property file. The file name of this specification defaults to
 * "module.properties".
 * <p/>
 * The first and mandatory section is always the module info and contains the basic module properties like name, version
 * and a short description.
 * <p/>
 * <pre>
 * module.name: xls-export-gui
 * module.producer: The JFreeReport project - www.jfree.org/jfreereport
 * module.description: A dialog component for the Excel table export.
 * module.version.major: 0
 * module.version.minor: 84
 * module.version.patchlevel: 0
 * </pre>
 * The properties name, producer and description are simple strings. They may span multiple lines, but may not contain a
 * colon (':'). The version properties are integer values.
 * <p/>
 * This section may be followed by one or more "depends" sections. These sections describe the base modules that are
 * required to be active to make this module work. The package manager will enforce this policy and will deactivate this
 * module if one of the base modules is missing.
 * <p/>
 * <pre>
 * dependency.module-id.module: org.pentaho.reporting.engine.classic.core.modules.output.table.xls.XLSTableModule
 * dependency.module-id.version.major: 0
 * dependency.module-id.version.minor: 84
 * </pre>
 * <p/>
 * The property *.module references to the module implementation of the module package. The module-id is a
 * per-module-definition-unique identifier and it is recommended to set it to the referenced module's name for
 * documentary purposes.
 *
 * @author Thomas Morgner
 * @noinspection HardCodedStringLiteral
 */
public abstract class AbstractModule extends DefaultModuleInfo implements Module {
  /**
   * An empty array for performance reasons.
   */
  private static final ModuleInfo[] EMPTY_MODULEINFO = new ModuleInfo[ 0 ];

  /**
   * The list of required modules.
   */
  private ModuleInfo[] requiredModules;
  /**
   * The list of optional modules.
   */
  private ModuleInfo[] optionalModules;

  /**
   * The name of the module.
   */
  private String name;
  /**
   * A short description of the module.
   */
  private String description;
  /**
   * The name of the module producer.
   */
  private String producer;
  /**
   * The modules subsystem.
   */
  private String subsystem;

  /**
   * Default Constructor.
   */
  protected AbstractModule() {
    setModuleClass( this.getClass().getName() );
  }

  /**
   * Loads the default module description from the file "module.properties". This file must be in the same package as
   * the implementing class.
   *
   * @throws ModuleInitializeException if an error occurs.
   */
  protected void loadModuleInfo() throws ModuleInitializeException {
    final InputStream in = ObjectUtilities.getResourceRelativeAsStream( "module.properties", getClass() );
    if ( in == null ) {
      throw new ModuleInitializeException
        ( "File 'module.properties' not found in module package." );
    }

    loadModuleInfo( in );
  }

  /**
   * Loads the module description from the given input stream. The module description must conform to the rules define
   * in the class description. The file must be encoded with "ISO-8859-1" (like property files).
   *
   * @param in the input stream from where to read the file
   * @throws ModuleInitializeException if an error occurs.
   */
  protected void loadModuleInfo( final InputStream in ) throws ModuleInitializeException {
    if ( in == null ) {
      throw new NullPointerException
        ( "Given InputStream is null." );
    }

    try {
      final DefaultConfiguration props = new DefaultConfiguration();
      props.load( in );

      readModuleInfo( props );

      final ArrayList<ModuleInfo> optionalModules = new ArrayList<ModuleInfo>();
      final ArrayList<ModuleInfo> dependentModules = new ArrayList<ModuleInfo>();
      final Iterator<String> keys = props.findPropertyKeys( "dependency." );
      while ( keys.hasNext() ) {
        final String key = keys.next();
        if ( key.endsWith( ".dependency-type" ) ) {
          final String moduleHandle = key.substring( 0, key.length() - ".dependency-type".length() );
          final DefaultModuleInfo module = readExternalModule( props, moduleHandle );
          if ( "optional".equals( props.getConfigProperty( key ) ) ) {
            optionalModules.add( module );
          } else {
            dependentModules.add( module );
          }
        }

      }

      this.optionalModules = optionalModules.toArray( new ModuleInfo[ optionalModules.size() ] );
      this.requiredModules = dependentModules.toArray( new ModuleInfo[ dependentModules.size() ] );
    } catch ( IOException ioe ) {
      throw new ModuleInitializeException( "Failed to load properties", ioe );
    } finally {
      try {
        in.close();
      } catch ( IOException e ) {
        // ignore ..
      }
    }
  }

  /**
   * Reads the module definition header. This header contains information about the module itself.
   *
   * @param config the properties from where to read the content.
   */
  private void readModuleInfo( final Configuration config ) {
    setName( config.getConfigProperty( "module.name" ) );
    setProducer( config.getConfigProperty( "module.producer" ) );
    setDescription( config.getConfigProperty( "module.description" ) );
    setMajorVersion( config.getConfigProperty( "module.version.major" ) );
    setMinorVersion( config.getConfigProperty( "module.version.minor" ) );
    setPatchLevel( config.getConfigProperty( "module.version.patchlevel" ) );
    setSubSystem( config.getConfigProperty( "module.subsystem" ) );
  }

  /**
   * Reads an external module description. This describes either an optional or a required module.
   *
   * @param reader the reader from where to read the module
   * @param prefix the property-key prefix.
   * @return the read module, never null
   */
  private DefaultModuleInfo readExternalModule( final Configuration reader,
                                                final String prefix ) {
    final DefaultModuleInfo mi = new DefaultModuleInfo();
    mi.setModuleClass( reader.getConfigProperty( prefix + ".module" ) );
    mi.setMajorVersion( reader.getConfigProperty( prefix + ".version.major" ) );
    mi.setMinorVersion( reader.getConfigProperty( prefix + ".version.minor" ) );
    mi.setPatchLevel( reader.getConfigProperty( prefix + ".version.patchlevel" ) );
    return mi;
  }

  /**
   * Returns the name of this module.
   *
   * @return the module name
   * @see Module#getName()
   */
  public String getName() {
    return this.name;
  }

  /**
   * Defines the name of the module.
   *
   * @param name the module name.
   */
  protected void setName( final String name ) {
    this.name = name;
  }

  /**
   * Returns the module description.
   *
   * @return the description of the module.
   * @see Module#getDescription()
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Defines the description of the module.
   *
   * @param description the module's desciption.
   */
  protected void setDescription( final String description ) {
    this.description = description;
  }

  /**
   * Returns the producer of the module.
   *
   * @return the producer.
   * @see Module#getProducer()
   */
  public String getProducer() {
    return this.producer;
  }

  /**
   * Defines the producer of the module.
   *
   * @param producer the producer.
   */
  protected void setProducer( final String producer ) {
    this.producer = producer;
  }

  /**
   * Returns a copy of the required modules array. This array contains all description of the modules that need to be
   * present to make this module work.
   *
   * @return an array of all required modules.
   * @see Module#getRequiredModules()
   */
  public ModuleInfo[] getRequiredModules() {
    if ( this.requiredModules == null ) {
      return EMPTY_MODULEINFO;
    }
    final ModuleInfo[] retval = new ModuleInfo[ this.requiredModules.length ];
    System.arraycopy( this.requiredModules, 0, retval, 0, this.requiredModules.length );
    return retval;
  }

  /**
   * Returns a copy of the required modules array. This array contains all description of the optional modules that may
   * improve the modules functonality.
   *
   * @return an array of all required modules.
   * @see Module#getRequiredModules()
   */
  public ModuleInfo[] getOptionalModules() {
    if ( this.optionalModules == null ) {
      return EMPTY_MODULEINFO;
    }
    final ModuleInfo[] retval = new ModuleInfo[ this.optionalModules.length ];
    System.arraycopy( this.optionalModules, 0, retval, 0, this.optionalModules.length );
    return retval;
  }

  /**
   * Defines the required module descriptions for this module.
   *
   * @param requiredModules the required modules.
   */
  protected void setRequiredModules( final ModuleInfo[] requiredModules ) {
    this.requiredModules = new ModuleInfo[ requiredModules.length ];
    System.arraycopy( requiredModules, 0, this.requiredModules, 0, requiredModules.length );
  }

  /**
   * Defines the optional module descriptions for this module.
   *
   * @param optionalModules the optional modules.
   */
  public void setOptionalModules( final ModuleInfo[] optionalModules ) {
    this.optionalModules = new ModuleInfo[ optionalModules.length ];
    System.arraycopy( optionalModules, 0, this.optionalModules, 0, optionalModules.length );
  }

  /**
   * Returns a string representation of this module.
   *
   * @return the string representation of this module for debugging purposes.
   * @see Object#toString()
   */
  public String toString() {
    final String lineSeparator = StringUtils.getLineSeparator();
    final StringBuilder buffer = new StringBuilder( 120 );
    buffer.append( "Module : " );
    buffer.append( getName() );
    buffer.append( lineSeparator );
    buffer.append( "ModuleClass : " );
    buffer.append( getModuleClass() );
    buffer.append( lineSeparator );
    buffer.append( "Version: " );
    buffer.append( getMajorVersion() );
    buffer.append( '.' );
    buffer.append( getMinorVersion() );
    buffer.append( '.' );
    buffer.append( getPatchLevel() );
    buffer.append( lineSeparator );
    buffer.append( "Producer: " );
    buffer.append( getProducer() );
    buffer.append( lineSeparator );
    buffer.append( "Description: " );
    buffer.append( getDescription() );
    buffer.append( lineSeparator );
    return buffer.toString();
  }

  /**
   * Tries to load a class to indirectly check for the existence of a certain library.
   *
   * @param name    the name of the library class.
   * @param context the context class to get a classloader from.
   * @return true, if the class could be loaded, false otherwise.
   */
  protected static boolean isClassLoadable( final String name, final Class<?> context ) {
    try {
      final ClassLoader loader = ObjectUtilities.getClassLoader( context );
      Class.forName( name, false, loader );
      return true;
    } catch ( Exception e ) {
      return false;
    }
  }

  /**
   * Configures the module by loading the configuration properties and adding them to the package configuration.
   *
   * @param subSystem the subsystem.
   */
  public void configure( final SubSystem subSystem ) {
    final InputStream in = ObjectUtilities.getResourceRelativeAsStream( "configuration.properties", getClass() );
    if ( in == null ) {
      return;
    }
    try {
      subSystem.getPackageManager().getPackageConfiguration().load( in );
    } finally {
      try {
        in.close();
      } catch ( IOException e ) {
        // can be ignored ...
      }
    }
  }

  /**
   * Tries to load an module initializer and uses this initializer to initialize the module.
   *
   * @param classname the class name of the initializer.
   * @throws ModuleInitializeException if an error occures
   * @deprecated Use the method that provides a class-context instead.
   */
  protected void performExternalInitialize( final String classname )
    throws ModuleInitializeException {
    try {
      final ModuleInitializer mi =
        ObjectUtilities.loadAndInstantiate( classname, AbstractModule.class, ModuleInitializer.class );
      if ( mi == null ) {
        throw new ModuleInitializeException( "Failed to load specified initializer class." );
      }
      mi.performInit();
    } catch ( ModuleInitializeException mie ) {
      throw mie;
    } catch ( Exception e ) {
      throw new ModuleInitializeException( "Failed to load specified initializer class.", e );
    }
  }

  /**
   * Executes an weakly referenced external initializer. The initializer will be loaded using reflection and will be
   * executed once. If the initializing fails with any exception, the module will become unavailable.
   *
   * @param classname the classname of the <code>ModuleInitializer</code> implementation
   * @param context   the class-loader context from where to load the module's classes.
   * @throws ModuleInitializeException if an error occured or the initializer could not be found.
   */
  protected void performExternalInitialize( final String classname, final Class<?> context )
    throws ModuleInitializeException {
    try {
      final ModuleInitializer mi =
        ObjectUtilities.loadAndInstantiate( classname, context, ModuleInitializer.class );
      if ( mi == null ) {
        throw new ModuleInitializeException( "Failed to load specified initializer class." );
      }
      mi.performInit();
    } catch ( ModuleInitializeException mie ) {
      throw mie;
    } catch ( Exception e ) {
      throw new ModuleInitializeException( "Failed to load specified initializer class.", e );
    }
  }

  /**
   * Returns the modules subsystem. If this module is not part of an subsystem then return the modules name, but never
   * null.
   *
   * @return the name of the subsystem.
   */
  public String getSubSystem() {
    if ( this.subsystem == null ) {
      return getName();
    }
    return this.subsystem;
  }

  /**
   * Defines the subsystem name for this module. If no sub-system name is set, the sub-system defaults to the module's
   * name.
   *
   * @param name the new name of the subsystem.
   */
  protected void setSubSystem( final String name ) {
    this.subsystem = name;
  }
}
