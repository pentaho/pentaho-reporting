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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.PropertyFileConfiguration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.PadMessage;
import org.pentaho.reporting.libraries.base.util.StopWatch;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * The PackageManager is used to load and configure the modules of JFreeReport. Modules are used to extend the basic
 * capabilities of JFreeReport by providing a simple plugin-interface.
 * <p/>
 * Modules provide a simple capability to remove unneeded functionality from the JFreeReport system and to reduce the
 * overall code size. The modularisation provides a very strict way of removing unnecessary dependencies beween the
 * various packages.
 * <p/>
 * The package manager can be used to add new modules to the system or to check the existence and state of installed
 * modules.
 *
 * @author Thomas Morgner
 */
public final class PackageManager {
  /**
   * The PackageConfiguration handles the module level configuration.
   *
   * @author Thomas Morgner
   */
  public static class PackageConfiguration extends PropertyFileConfiguration {
    private static final long serialVersionUID = -2170306139946858878L;

    /**
     * DefaultConstructor. Creates a new package configuration.
     */
    public PackageConfiguration() {
      // nothing required
    }
  }

  public class BootTimeEntry implements Comparable<BootTimeEntry> {
    private long time;
    private String name;

    public BootTimeEntry( final String name, final long time ) {
      if ( name == null ) {
        throw new NullPointerException( "Name must not be null" );
      }
      this.name = name;
      this.time = time;
    }

    public int compareTo( final BootTimeEntry o ) {
      if ( time < o.time ) {
        return -1;
      }
      if ( time > o.time ) {
        return +1;
      }
      return name.compareTo( o.name );
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final BootTimeEntry that = (BootTimeEntry) o;

      if ( time != that.time ) {
        return false;
      }
      if ( name != null ? !name.equals( that.name ) : that.name != null ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = (int) ( time ^ ( time >>> 32 ) );
      result = 31 * result + ( name != null ? name.hashCode() : 0 );
      return result;
    }
  }

  private static final Log LOGGER = LogFactory.getLog( PackageManager.class );
  /**
   * An internal constant declaring that the specified module was already loaded.
   */
  private static final int RETURN_MODULE_LOADED = 0;
  /**
   * An internal constant declaring that the specified module is not known.
   */
  private static final int RETURN_MODULE_UNKNOWN = 1;
  /**
   * An internal constant declaring that the specified module produced an error while loading.
   */
  private static final int RETURN_MODULE_ERROR = 2;
  private static final boolean trackBootTime = false;
  /**
   * The module configuration instance that should be used to store module properties. This separates the user defined
   * properties from the implementation defined properties.
   */
  private final PackageConfiguration packageConfiguration;
  /**
   * A list of all defined modules.
   */
  private final ArrayList<PackageState> modules;
  /**
   * A list of module name definitions.
   */
  private final ArrayList<String> initSections;
  private HashMap<String, PackageState> modulesByClass;
  /**
   * The boot implementation for which the modules are managed.
   */
  private AbstractBoot booter;

  /**
   * Creates a new package manager.
   *
   * @param booter the booter (<code>null</code> not permitted).
   */
  public PackageManager( final AbstractBoot booter ) {
    if ( booter == null ) {
      throw new NullPointerException();
    }
    this.booter = booter;
    this.packageConfiguration = new PackageConfiguration();
    this.modules = new ArrayList<PackageState>();
    this.modulesByClass = new HashMap<String, PackageState>();
    this.initSections = new ArrayList<String>();
  }

  /**
   * Checks, whether a certain module is available.
   *
   * @param moduleDescription the module description of the desired module.
   * @return true, if the module is available and the version of the module is compatible, false otherwise.
   */
  public boolean isModuleAvailable( final ModuleInfo moduleDescription ) {
    if ( moduleDescription == null ) {
      throw new NullPointerException();
    }

    final PackageState[] packageStates =
      this.modules.toArray( new PackageState[ this.modules.size() ] );
    for ( int i = 0; i < packageStates.length; i++ ) {
      final PackageState state = packageStates[ i ];
      if ( state.getModule().getModuleClass().equals( moduleDescription.getModuleClass() ) ) {
        return ( state.getState() == PackageState.STATE_INITIALIZED );
      }
    }
    return false;
  }

  /**
   * Checks whether the given module is available. The method returns true if the module is defined and has been
   * properly initialized.
   *
   * @param moduleClass the module class to be checked.
   * @return true, if the module is available and initialized, false otherwise.
   */
  public boolean isModuleAvailable( final String moduleClass ) {
    if ( moduleClass == null ) {
      throw new NullPointerException();
    }

    final PackageState state = modulesByClass.get( moduleClass );
    if ( state == null ) {
      return false;
    }
    return state.getState() == PackageState.STATE_INITIALIZED;
  }

  /**
   * Loads all modules mentioned in the report configuration starting with the given prefix. This method is used during
   * the boot process of JFreeReport. You should never need to call this method directly.
   *
   * @param modulePrefix the module prefix.
   */
  public void load( final String modulePrefix ) {
    if ( modulePrefix == null ) {
      throw new NullPointerException();
    }

    if ( this.initSections.contains( modulePrefix ) ) {
      return;
    }
    this.initSections.add( modulePrefix );

    final Configuration config = this.booter.getGlobalConfig();
    final Iterator it = config.findPropertyKeys( modulePrefix );
    int count = 0;
    while ( it.hasNext() ) {
      final String key = (String) it.next();
      if ( key.endsWith( ".Module" ) ) {
        final String moduleClass = config.getConfigProperty( key );
        if ( moduleClass != null && moduleClass.length() > 0 ) {
          addModule( moduleClass );
          count++;
        }
      }
    }
    LOGGER.debug( "Loaded a total of " + count + " modules under prefix: " + modulePrefix );
  }

  /**
   * Initializes all previously uninitialized modules. Once a module is initialized, it is not re-initialized a second
   * time.
   */
  public synchronized void initializeModules() {
    final List<BootTimeEntry> times = new ArrayList<BootTimeEntry>();
    // sort by subsystems and dependency
    PackageSorter.sort( this.modules );

    for ( int i = 0; i < this.modules.size(); i++ ) {
      final PackageState mod = this.modules.get( i );
      if ( isConfigurable( mod ) == false ) {
        mod.markError();
        continue;
      }

      if ( mod.configure( this.booter ) ) {
        if ( LOGGER.isDebugEnabled() ) {
          LOGGER.debug( "Conf: " +
            new PadMessage( mod.getModule().getModuleClass(), 70 ) +
            " [" + mod.getModule().getSubSystem() + ']' );
        }
      }
    }

    for ( int i = 0; i < this.modules.size(); i++ ) {
      final PackageState mod = this.modules.get( i );
      if ( isInitializable( mod ) == false ) {
        mod.markError();
        continue;
      }

      final StopWatch stopWatch = StopWatch.startNew();
      if ( mod.initialize( this.booter ) ) {
        if ( LOGGER.isDebugEnabled() ) {
          LOGGER.debug( "Init: " +
            new PadMessage( mod.getModule().getModuleClass(), 70 ) +
            " [" + mod.getModule().getSubSystem() + ']' );
        }
      }
      times.add( new BootTimeEntry( mod.getModule().getModuleClass(), stopWatch.getElapsedTime() ) );
    }

    if ( trackBootTime ) {
      Collections.sort( times );
      LOGGER.debug( "Detailed Module boot times" );
      long totalTime = 0;
      for ( final BootTimeEntry time : times ) {
        totalTime += time.time;
        LOGGER.debug( time.name + " - " + time.time );
      }
      LOGGER.debug( "Total modules boot time: " + totalTime );
    }
  }
  // 1290661000
  // 4457704000

  /**
   * Checks whether the module is configurable. A module is considered configurable if all dependencies exist and are
   * configured.
   *
   * @param state the package state that should be checked.
   * @return true, if the module can be configured, false otherwise.
   */
  private boolean isConfigurable( final PackageState state ) {
    final ModuleInfo[] requiredModules = state.getModule().getRequiredModules();
    for ( int i = 0; i < requiredModules.length; i++ ) {
      final ModuleInfo module = requiredModules[ i ];
      final String key = module.getModuleClass();
      final PackageState dependentState = modulesByClass.get( key );
      if ( dependentState == null ) {
        LOGGER.warn(
          "Required dependency '" + key + "' for module '" + state.getModule().getModuleClass() + " not found." );
        return false;
      }
      if ( dependentState.getState() != PackageState.STATE_CONFIGURED ) {
        LOGGER.warn(
          "Required dependency '" + key + "' for module '" + state.getModule().getModuleClass() + " not configured." );
        return false;
      }
    }
    return true;
  }

  /**
   * Checks whether the module is configurable. A module is considered configurable if all dependencies exist and are
   * initialized.
   *
   * @param state the package state that should be checked.
   * @return true, if the module can be configured, false otherwise.
   */
  private boolean isInitializable( final PackageState state ) {
    final ModuleInfo[] requiredModules = state.getModule().getRequiredModules();
    for ( int i = 0; i < requiredModules.length; i++ ) {
      final ModuleInfo module = requiredModules[ i ];
      final String key = module.getModuleClass();
      final PackageState dependentState = modulesByClass.get( key );
      if ( dependentState == null ) {
        LOGGER.warn(
          "Required dependency '" + key + "' for module '" + state.getModule().getModuleClass() + " not found." );
        return false;
      }
      if ( dependentState.getState() != PackageState.STATE_INITIALIZED ) {
        LOGGER.warn( "Required dependency '" + key + "' for module '" + state.getModule().getModuleClass()
          + " not initializable." );
        return false;
      }
    }
    return true;
  }

  /**
   * Adds a module to the package manager. Once all modules are added, you have to call initializeModules() to configure
   * and initialize the new modules.
   *
   * @param modClass the module class
   */
  public synchronized void addModule( final String modClass ) {
    if ( modClass == null ) {
      throw new NullPointerException();
    }

    final ArrayList<Module> loadModules = new ArrayList<Module>();
    final ModuleInfo modInfo = new DefaultModuleInfo( modClass, null, null, null );
    if ( loadModule( modInfo, new ArrayList<Module>(), loadModules, false ) ) {
      for ( int i = 0; i < loadModules.size(); i++ ) {
        final Module mod = loadModules.get( i );
        final PackageState state = new PackageState( mod );
        this.modules.add( state );
        this.modulesByClass.put( mod.getModuleClass(), state );
      }
    }
  }

  /**
   * Checks, whether the given module is already loaded in either the given tempModules list or the global package
   * registry. If tmpModules is null, only the previously installed modules are checked.
   *
   * @param tempModules a list of previously loaded modules.
   * @param module      the module specification that is checked.
   * @return true, if the module is already loaded, false otherwise.
   */
  private int containsModule( final ArrayList<Module> tempModules, final ModuleInfo module ) {
    if ( tempModules != null ) {
      final ModuleInfo[] mods = tempModules.toArray( new ModuleInfo[ tempModules.size() ] );
      for ( int i = 0; i < mods.length; i++ ) {
        if ( mods[ i ].getModuleClass().equals( module.getModuleClass() ) ) {
          return RETURN_MODULE_LOADED;
        }
      }
    }

    final PackageState[] packageStates =
      this.modules.toArray( new PackageState[ this.modules.size() ] );
    for ( int i = 0; i < packageStates.length; i++ ) {
      if ( packageStates[ i ].getModule().getModuleClass().equals( module.getModuleClass() ) ) {
        if ( packageStates[ i ].getState() == PackageState.STATE_ERROR ) {
          return RETURN_MODULE_ERROR;
        } else {
          return RETURN_MODULE_LOADED;
        }
      }
    }
    return RETURN_MODULE_UNKNOWN;
  }

  /**
   * A utility method that collects all failed modules. Such an module caused an error while being loaded, and is now
   * cached in case it is referenced elsewhere.
   *
   * @param state the failed module.
   */
  private void dropFailedModule( final PackageState state ) {
    if ( this.modules.contains( state ) == false ) {
      this.modules.add( state );
    }
  }

  /**
   * Tries to load a given module and all dependent modules. If the dependency check fails for that module (or for one
   * of the dependent modules), the loaded modules are discarded and no action is taken.
   *
   * @param moduleInfo        the module info of the module that should be loaded.
   * @param incompleteModules a list of incompletly loaded modules. This are module specifications which depend on the
   *                          current module and wait for the module to be completly loaded.
   * @param modules           the list of previously loaded modules for this module.
   * @param fatal             a flag that states, whether the failure of loading a module should be considered an error.
   *                          Root-modules load errors are never fatal, as we try to load all known modules, regardless
   *                          whether they are active or not.
   * @return true, if the module was loaded successfully, false otherwise.
   */
  private boolean loadModule( final ModuleInfo moduleInfo,
                              final ArrayList<Module> incompleteModules,
                              final ArrayList<Module> modules,
                              final boolean fatal ) {
    try {
      final Module module = ObjectUtilities.loadAndInstantiate
        ( moduleInfo.getModuleClass(), booter.getClass(), Module.class );
      if ( module == null ) {
        if ( fatal ) {
          LOGGER.warn( "Unresolved dependency for package: " + moduleInfo.getModuleClass() );
        }
        LOGGER.debug( "Module class referenced, but not in classpath: " + moduleInfo.getModuleClass() );
        return false;
      }

      if ( acceptVersion( moduleInfo, module ) == false ) {
        // module conflict!
        LOGGER.warn( "Module " + module.getName() + ": required version: "
          + moduleInfo + ", but found Version: \n" + module );
        final PackageState state = new PackageState( module, PackageState.STATE_ERROR );
        dropFailedModule( state );
        return false;
      }

      final int moduleContained = containsModule( modules, module );
      if ( moduleContained == RETURN_MODULE_ERROR ) {
        // the module caused harm before ...
        LOGGER.debug( "Indicated failure for module: " + module.getModuleClass() );
        final PackageState state = new PackageState( module, PackageState.STATE_ERROR );
        dropFailedModule( state );
        return false;
      } else if ( moduleContained == RETURN_MODULE_UNKNOWN ) {
        if ( incompleteModules.contains( module ) ) {
          // we assume that loading will continue ...
          LOGGER.error
            ( "Circular module reference: This module definition is invalid: " +
              module.getClass() );
          final PackageState state = new PackageState( module, PackageState.STATE_ERROR );
          dropFailedModule( state );
          return false;
        }
        incompleteModules.add( module );
        final ModuleInfo[] required = module.getRequiredModules();
        for ( int i = 0; i < required.length; i++ ) {
          if ( loadModule( required[ i ], incompleteModules, modules, true ) == false ) {
            LOGGER.debug( "Indicated failure for module: " + module.getModuleClass() );
            final PackageState state = new PackageState( module, PackageState.STATE_ERROR );
            dropFailedModule( state );
            return false;
          }
        }

        final ModuleInfo[] optional = module.getOptionalModules();
        for ( int i = 0; i < optional.length; i++ ) {
          if ( loadModule( optional[ i ], incompleteModules, modules, true ) == false ) {
            LOGGER.debug( "Optional module: " + optional[ i ].getModuleClass() + " was not loaded." );
          }
        }
        // maybe a dependent module defined the same base module ...
        if ( containsModule( modules, module ) == RETURN_MODULE_UNKNOWN ) {
          modules.add( module );
        }
        incompleteModules.remove( module );
      }
      return true;
    } catch ( Exception e ) {
      LOGGER.warn( "Exception while loading module: " + moduleInfo, e );
      return false;
    }
  }

  /**
   * Checks, whether the given module meets the requirements defined in the module information.
   *
   * @param moduleRequirement the required module specification.
   * @param module            the module that should be checked against the specification.
   * @return true, if the module meets the given specifications, false otherwise.
   */
  private boolean acceptVersion( final ModuleInfo moduleRequirement, final Module module ) {
    if ( moduleRequirement.getMajorVersion() == null ) {
      return true;
    }
    if ( module.getMajorVersion() == null ) {
      LOGGER.warn( "Module " + module.getName() + " does not define a major version." );
    } else {
      final int compare = acceptVersion( moduleRequirement.getMajorVersion(),
        module.getMajorVersion() );
      if ( compare > 0 ) {
        return false;
      } else if ( compare < 0 ) {
        return true;
      }
    }

    if ( moduleRequirement.getMinorVersion() == null ) {
      return true;
    }
    if ( module.getMinorVersion() == null ) {
      LOGGER.warn( "Module " + module.getName() + " does not define a minor version." );
    } else {
      final int compare = acceptVersion( moduleRequirement.getMinorVersion(),
        module.getMinorVersion() );
      if ( compare > 0 ) {
        return false;
      } else if ( compare < 0 ) {
        return true;
      }
    }

    if ( moduleRequirement.getPatchLevel() == null ) {
      return true;
    }
    if ( module.getPatchLevel() == null ) {
      LOGGER.debug( "Module " + module.getName() + " does not define a patch level." );
    } else {
      if ( acceptVersion( moduleRequirement.getPatchLevel(),
        module.getPatchLevel() ) > 0 ) {
        LOGGER.debug( "Did not accept patchlevel: "
          + moduleRequirement.getPatchLevel() + " - "
          + module.getPatchLevel() );
        return false;
      }
    }
    return true;

  }

  /**
   * Compare the version strings. If the strings have a different length, the shorter string is padded with spaces to
   * make them compareable.
   *
   * @param modVer    the version string of the module
   * @param depModVer the version string of the dependent or optional module
   * @return 0, if the dependent module version is equal tothe module's required version, a negative number if the
   * dependent module is newer or a positive number if the dependent module is older and does not fit.
   */
  private int acceptVersion( final String modVer, final String depModVer ) {
    final int mLength = Math.max( modVer.length(), depModVer.length() );
    final char[] modVerArray;
    final char[] depVerArray;
    if ( modVer.length() > depModVer.length() ) {
      modVerArray = modVer.toCharArray();
      depVerArray = new char[ mLength ];
      final int delta = modVer.length() - depModVer.length();
      Arrays.fill( depVerArray, 0, delta, ' ' );
      System.arraycopy( depVerArray, delta, depModVer.toCharArray(), 0, depModVer.length() );
    } else if ( modVer.length() < depModVer.length() ) {
      depVerArray = depModVer.toCharArray();
      modVerArray = new char[ mLength ];
      final char[] b1 = new char[ mLength ];
      final int delta = depModVer.length() - modVer.length();
      Arrays.fill( b1, 0, delta, ' ' );
      System.arraycopy( b1, delta, modVer.toCharArray(), 0, modVer.length() );
    } else {
      depVerArray = depModVer.toCharArray();
      modVerArray = modVer.toCharArray();
    }
    return new String( modVerArray ).compareTo( new String( depVerArray ) );
  }

  /**
   * Returns the default package configuration. Private report configuration instances may be inserted here. These
   * inserted configuration can never override the settings from this package configuration.
   *
   * @return the package configuration.
   */
  public PackageConfiguration getPackageConfiguration() {
    return this.packageConfiguration;
  }

  /**
   * Returns an array of the currently active modules. The module definition returned contain all known modules,
   * including buggy and unconfigured instances.
   *
   * @return the modules.
   */
  public Module[] getAllModules() {
    final Module[] mods = new Module[ this.modules.size() ];
    for ( int i = 0; i < this.modules.size(); i++ ) {
      final PackageState state = this.modules.get( i );
      mods[ i ] = state.getModule();
    }
    return mods;
  }

  /**
   * Returns all active modules. This array does only contain modules which were successfully configured and
   * initialized.
   *
   * @return the list of all active modules.
   */
  public Module[] getActiveModules() {
    final ArrayList<Module> mods = new ArrayList<Module>();
    for ( int i = 0; i < this.modules.size(); i++ ) {
      final PackageState state = this.modules.get( i );
      if ( state.getState() == PackageState.STATE_INITIALIZED ) {
        mods.add( state.getModule() );
      }
    }
    return mods.toArray( new Module[ mods.size() ] );
  }

  /**
   * Prints the modules that are used.
   *
   * @param p the print stream.
   */
  public void printUsedModules( final PrintStream p ) {
    final Module[] allMods = getAllModules();
    final ArrayList<Module> activeModules = new ArrayList<Module>();
    //final ArrayList failedModules = new ArrayList();

    for ( int i = 0; i < allMods.length; i++ ) {
      if ( isModuleAvailable( allMods[ i ] ) ) {
        activeModules.add( allMods[ i ] );
      }
      //      else
      //      {
      //        failedModules.add(allMods[i]);
      //      }
    }

    p.print( "Active modules: " );
    p.println( activeModules.size() );
    p.println( "----------------------------------------------------------" );
    for ( int i = 0; i < activeModules.size(); i++ ) {
      final Module mod = activeModules.get( i );
      p.print( new PadMessage( mod.getModuleClass(), 70 ) );
      p.print( " [" );
      p.print( mod.getSubSystem() );
      p.println( "]" );
      p.print( "  Version: " );
      p.print( mod.getMajorVersion() );
      p.print( "-" );
      p.print( mod.getMinorVersion() );
      p.print( "-" );
      p.print( mod.getPatchLevel() );
      p.print( " Producer: " );
      p.println( mod.getProducer() );
      p.print( "  Description: " );
      p.println( mod.getDescription() );
    }
  }
}
