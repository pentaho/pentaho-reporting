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

package org.pentaho.reporting.libraries.css.resolver.values;

import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.css.model.StyleKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Compares two resolve handlers for order. A handler declares its dependencies and therefore requires that all
 * dependent styles have been resolved before trying to compute these properties.
 * <p/>
 * When sorting, we match this modules position against all dependent modules until all positions are stable. Circular
 * references are evil and must be filtered before passing the classes to this sorter.
 *
 * @author Thomas Morgner
 */
public final class ResolveHandlerSorter {

  /**
   * An Internal wrapper class which collects additional information on the given module. Every module has a position,
   * which is heigher than the position of all dependent modules.
   *
   * @author Thomas Morgner
   */
  private static class SortModule implements Comparable {
    /**
     * stores the relative position of the module in the global list.
     */
    private int position;
    /**
     * The package state of the to be matched module.
     */
    private final StyleKey key;
    /**
     * A list of all directly dependent subsystems.
     */
    private StyleKey[] dependentKeys;
    private HashSet dependentKeySet;
    private ResolveHandlerModule module;

    /**
     * Creates a new SortModule for the given package state.
     *
     * @param module the package state object, that should be wrapped up by this class.
     */
    private SortModule( final ResolveHandlerModule module ) {
      this.position = -1;
      this.key = module.getKey();
      this.module = module;
      this.dependentKeySet = new HashSet();
      addKeys( module.getDependentKeys() );
    }

    public boolean isDependency( final StyleKey key ) {
      return dependentKeySet.contains( key );
    }

    public ResolveHandlerModule getModule() {
      return module;
    }

    /**
     * Returns the list of all dependent subsystems. The list gets defined when the sorting is started.
     *
     * @return the list of all dependent subsystems.
     */
    public StyleKey[] getDependentKeys() {
      if ( dependentKeys == null ) {
        dependentKeys = (StyleKey[]) dependentKeySet.toArray
          ( new StyleKey[ dependentKeySet.size() ] );
      }
      return dependentKeys;
    }

    public void addKeys( final StyleKey[] keys ) {
      boolean haveAdded = false;
      for ( int i = 0; i < keys.length; i++ ) {
        final StyleKey styleKey = keys[ i ];
        if ( dependentKeySet.add( styleKey ) ) {
          haveAdded = true;
        }
      }
      if ( haveAdded ) {
        dependentKeys = null;
      }
    }

    /**
     * Returns the current position of this module in the global list. The position is computed by comparing all
     * positions of all dependent subsystem modules.
     *
     * @return the current module position.
     */
    public int getPosition() {
      return this.position;
    }

    /**
     * Defines the position of this module in the global list of all known modules.
     *
     * @param position the position.
     */
    public void setPosition( final int position ) {
      this.position = position;
      this.module.setWeight( position );
    }

    /**
     * Returns the key contained in this SortModule.
     *
     * @return the key of this module.
     */
    public StyleKey getKey() {
      return this.key;
    }

    /**
     * Returns a basic string representation of this SortModule. This should be used for debugging purposes only.
     *
     * @return a string representation of this module.
     * @see Object#toString()
     */
    public String toString() {
      final StringBuffer buffer = new StringBuffer();
      buffer.append( "SortModule: " );
      buffer.append( this.position );
      buffer.append( ' ' );
      buffer.append( this.getKey().getName() );
      return buffer.toString();
    }

    /**
     * Compares this module against an other sort module.
     *
     * @param o the other sort module instance.
     * @return -1 if the other's module position is less than this modules position, +1 if this module is less than the
     * other module or 0 if both modules have an equal position in the list.
     * @see Comparable#compareTo(Object)
     */
    public int compareTo( final Object o ) {
      final SortModule otherModule = (SortModule) o;
      if ( this.position > otherModule.position ) {
        return +1;
      }
      if ( this.position < otherModule.position ) {
        return -1;
      }
      return 0;
    }


  }

  /**
   * DefaultConstructor.
   */
  private ResolveHandlerSorter() {
    // nothing required.
  }

  /**
   * Sorts the given list of package states. The packages are sorted by their dependencies in a way so that all
   * dependent packages are placed on lower positions than the packages which declared the dependency.
   *
   * @param modules the list of modules.
   */
  public static ResolveHandlerModule[] sort( final ResolveHandlerModule[] modules ) {
    // maps keys to sortmodules
    final HashMap moduleMap = new HashMap();

    for ( int i = 0; i < modules.length; i++ ) {
      moduleMap.put( modules[ i ].getKey(), new SortModule( modules[ i ] ) );
    }

    final SortModule[] sortModules = (SortModule[])
      moduleMap.values().toArray( new SortModule[ moduleMap.size() ] );
    final ArrayList parents = new ArrayList();
    for ( int i = 0; i < sortModules.length; i++ ) {
      final SortModule sortMod = sortModules[ i ];
      computeSubsystemModules( sortMod, parents, moduleMap );
      parents.clear();
    }

    // repeat the computation until all modules have a matching
    // position. This is not the best algorithm, but it works
    // and is relativly simple. It will need some optimizations
    // in the future, but as this is only executed once, we don't
    // have to care much about it.
    boolean doneWork = true;
    while ( doneWork ) {
      doneWork = false;
      for ( int i = 0; i < sortModules.length; i++ ) {
        final SortModule mod = sortModules[ i ];
        final int position = searchModulePosition( mod, moduleMap );
        if ( position != mod.getPosition() ) {
          mod.setPosition( position );
          doneWork = true;
        }
      }
    }

    Arrays.sort( sortModules );
    final ResolveHandlerModule[] retvals = new ResolveHandlerModule[ sortModules.length ];
    for ( int i = 0; i < sortModules.length; i++ ) {
      retvals[ i ] = sortModules[ i ].getModule();
    }
    return retvals;
  }

  /**
   * Computes the new module position. This position is computed according to the dependent modules and subsystems. The
   * returned position will be higher than the highest dependent module position.
   *
   * @param smodule   the sort module for that we compute the new positon.
   * @param moduleMap the map with all modules.
   * @return the new positon.
   */
  private static int searchModulePosition
  ( final SortModule smodule, final HashMap moduleMap ) {
    int position = 0;

    // check the required modules. Increase our level to at least
    // one point over the highest dependent module
    // ignore missing modules.
    final StyleKey[] dependentKeys = smodule.getDependentKeys();
    for ( int modPos = 0; modPos < dependentKeys.length; modPos++ ) {
      final SortModule reqMod = (SortModule) moduleMap.get( dependentKeys[ modPos ] );
      if ( reqMod == null ) {
        continue;
      }
      if ( reqMod.getPosition() >= position ) {
        position = reqMod.getPosition() + 1;
      }
    }
    return position;
  }

  /**
   * Collects all directly dependent subsystems.
   */
  private static void computeSubsystemModules
  ( final SortModule module,
    final ArrayList parents,
    final HashMap moduleMap ) {
    if ( parents.contains( module ) ) {
      throw new IllegalStateException( "Loop detected:" + module + " (" + parents + ')' );
    }

    final StyleKey[] keys = module.getDependentKeys();
    parents.add( module );
    for ( int i = 0; i < keys.length; i++ ) {
      final StyleKey key = keys[ i ];
      if ( key.equals( module.getKey() ) ) {
        throw new IllegalStateException( "module referencing itself as dependency" );
      }

      final SortModule child = (SortModule) moduleMap.get( key );
      if ( child == null ) {
        DebugLog.log( "Documented dependency but have no module for that one: " + key );
      } else {
        computeSubsystemModules( child, parents, moduleMap );
        module.addKeys( child.getDependentKeys() );
      }
    }
    parents.remove( module );
  }

}
