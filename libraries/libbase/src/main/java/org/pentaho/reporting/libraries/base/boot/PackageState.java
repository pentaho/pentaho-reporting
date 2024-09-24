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

/**
 * The package state class is used by the package manager to keep track of the activation level of the installed or
 * errornous packages.
 * <p/>
 * This is an internal class used by the PackageManager.
 *
 * @author Thomas Morgner
 */
public class PackageState {
  /**
   * A logger.
   */
  private static final Log LOGGER = LogFactory.getLog( PackageState.class );

  /**
   * A constant defining that the package is new.
   */
  public static final int STATE_NEW = 0;

  /**
   * A constant defining that the package has been loaded and configured.
   */
  public static final int STATE_CONFIGURED = 1;

  /**
   * A constant defining that the package was initialized and is ready to use.
   */
  public static final int STATE_INITIALIZED = 2;

  /**
   * A constant defining that the package produced an error and is not available.
   */
  public static final int STATE_ERROR = -2;

  /**
   * The module class that contains the package information.
   */
  private final Module module;
  /**
   * The state of the module.
   */
  private int state;

  /**
   * Creates a new package state for the given module. The module state will be initialized to STATE_NEW.
   *
   * @param module the module.
   */
  public PackageState( final Module module ) {
    this( module, STATE_NEW );
  }

  /**
   * Creates a new package state for the given module. The module state will be initialized to the given initial state.
   *
   * @param module the module.
   * @param state  the initial state
   */
  public PackageState( final Module module, final int state ) {
    if ( module == null ) {
      throw new NullPointerException( "Module must not be null." );
    }
    if ( state != STATE_CONFIGURED && state != STATE_ERROR
      && state != STATE_INITIALIZED && state != STATE_NEW ) {
      throw new IllegalArgumentException( "State is not valid" );
    }
    this.module = module;
    this.state = state;
  }

  /**
   * Configures the module and raises the state to STATE_CONFIGURED if the module is not yet configured.
   *
   * @param subSystem the sub-system.
   * @return true, if the module was configured, false otherwise.
   */
  public boolean configure( final SubSystem subSystem ) {
    if ( subSystem == null ) {
      throw new NullPointerException();
    }

    if ( this.state == STATE_NEW ) {
      try {
        this.module.configure( subSystem );
        this.state = STATE_CONFIGURED;
        return true;
      } catch ( NoClassDefFoundError noClassDef ) {
        LOGGER.warn( "Unable to load module classes for " +
          this.module.getName() + ':' + noClassDef.getMessage() );
        this.state = STATE_ERROR;
      } catch ( Exception e ) {
        if ( LOGGER.isDebugEnabled() ) {
          // its still worth a warning, but now we are more verbose ...
          LOGGER.warn( "Unable to configure the module " + this.module.getName(), e );
        } else if ( LOGGER.isWarnEnabled() ) {
          LOGGER.warn( "Unable to configure the module " + this.module.getName() );
        }
        this.state = STATE_ERROR;
      }
    }
    return false;
  }

  /**
   * Returns the module managed by this state implementation.
   *
   * @return the module.
   */
  public Module getModule() {
    return this.module;
  }

  /**
   * Returns the current state of the module. This method returns either STATE_NEW, STATE_CONFIGURED, STATE_INITIALIZED
   * or STATE_ERROR.
   *
   * @return the module state.
   */
  public int getState() {
    return this.state;
  }

  /**
   * Initializes the contained module and raises the set of the module to STATE_INITIALIZED, if the module was not yet
   * initialized. In case of an error, the module state will be set to STATE_ERROR and the module will not be
   * available.
   *
   * @param subSystem the sub-system.
   * @return true, if the module was successfully initialized, false otherwise.
   */
  public boolean initialize( final SubSystem subSystem ) {
    if ( subSystem == null ) {
      throw new NullPointerException();
    }

    if ( this.state == STATE_CONFIGURED ) {
      try {
        this.module.initialize( subSystem );
        this.state = STATE_INITIALIZED;
        return true;
      } catch ( NoClassDefFoundError noClassDef ) {
        LOGGER.warn( "Unable to load module classes for " + this.module.getName() + ':' + noClassDef.getMessage() );
        this.state = STATE_ERROR;
      } catch ( ModuleInitializeException me ) {
        if ( LOGGER.isDebugEnabled() ) {
          // its still worth a warning, but now we are more verbose ...
          LOGGER.warn( "Unable to initialize the module " + this.module.getName(), me );
        } else if ( LOGGER.isWarnEnabled() ) {
          LOGGER.warn( "Unable to initialize the module " + this.module.getName() );
        }
        this.state = STATE_ERROR;
      } catch ( Exception e ) {
        if ( LOGGER.isDebugEnabled() ) {
          // its still worth a warning, but now we are more verbose ...
          LOGGER.warn( "Unable to initialize the module " + this.module.getName(), e );
        } else if ( LOGGER.isWarnEnabled() ) {
          LOGGER.warn( "Unable to initialize the module " + this.module.getName() );
        }
        this.state = STATE_ERROR;
      }
    }
    return false;
  }

  /**
   * Compares this object with the given other object for equality.
   *
   * @param o the other object to be compared
   * @return true, if the other object is also a PackageState containing the same module, false otherwise.
   * @see Object#equals(Object)
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof PackageState ) ) {
      return false;
    }

    final PackageState packageState = (PackageState) o;

    if ( !this.module.getModuleClass().equals( packageState.module.getModuleClass() ) ) {
      return false;
    }

    return true;
  }

  /**
   * Computes a hashcode for this package state.
   *
   * @return the hashcode.
   * @see Object#hashCode()
   */
  public int hashCode() {
    return this.module.hashCode();
  }

  /**
   * Marks this package state as invalid.
   */
  public void markError() {
    this.state = STATE_ERROR;
  }
}
