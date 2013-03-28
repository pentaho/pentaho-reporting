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
 * Copyright (c) 2007 - 2009 Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.base.boot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Compares two modules for order. A module is considered less than an other
 * module if the module is a required module of the compared module. Modules
 * are considered equal if they have no relation.
 * <p/>
 * When sorting, we match this modules position against all dependent
 * modules until all positions are stable. Circular references are evil
 * and are filtered during the module loading process in the package manager.
 *
 * @author Thomas Morgner
 * @noinspection ComparableImplementedButEqualsNotOverridden
 */
public final class PackageSorter
{
  /**
   * An Internal wrapper class which collects additional information
   * on the given module. Every module has a position, which is heigher
   * than the position of all dependent modules.
   *
   * @author Thomas Morgner
   */
  private static class SortModule implements Comparable
  {
    /**
     * stores the relative position of the module in the global list.
     */
    private int position;
    /**
     * The package state of the to be matched module.
     */
    private final PackageState state;
    /**
     * A list of all directly dependent subsystems.
     */
    private ArrayList dependSubsystems;
    // direct dependencies, indirect ones are handled by the
    // dependent classes ...

    /**
     * Creates a new SortModule for the given package state.
     *
     * @param state the package state object, that should be wrapped up
     *              by this class.
     */
    private SortModule(final PackageState state)
    {
      this.position = -1;
      this.state = state;
    }

    /**
     * Returns the list of all dependent subsystems. The list gets defined
     * when the sorting is started.
     *
     * @return the list of all dependent subsystems.
     */
    public ArrayList getDependSubsystems()
    {
      return this.dependSubsystems;
    }

    /**
     * Defines a list of dependent subsystems for this module. The list contains
     * the names of the dependent subsystems as strings.
     *
     * @param dependSubsystems a list of all dependent subsystems, never null.
     * @noinspection AssignmentToCollectionOrArrayFieldFromParameter as this is a 100% private class and it is
     * guaranteed that no one is going to change the array list in question.
     */
    public void setDependSubsystems(final ArrayList<String> dependSubsystems)
    {
      this.dependSubsystems = dependSubsystems;
    }

    /**
     * Returns the current position of this module in the global list.
     * The position is computed by comparing all positions of all dependent
     * subsystem modules.
     *
     * @return the current module position.
     */
    public int getPosition()
    {
      return this.position;
    }

    /**
     * Defines the position of this module in the global list of all
     * known modules.
     *
     * @param position the position.
     */
    public void setPosition(final int position)
    {
      this.position = position;
    }

    /**
     * Returns the package state contained in this SortModule.
     *
     * @return the package state of this module.
     */
    public PackageState getState()
    {
      return this.state;
    }

    /**
     * Returns a basic string representation of this SortModule. This
     * should be used for debugging purposes only.
     *
     * @return a string representation of this module.
     * @see Object#toString()
     */
    public String toString()
    {
      final StringBuilder buffer = new StringBuilder(100);
      buffer.append("SortModule: ");
      buffer.append(this.position);
      buffer.append(' ');
      buffer.append(this.state.getModule().getName());
      buffer.append(' ');
      buffer.append(this.state.getModule().getModuleClass());
      return buffer.toString();
    }

    /**
     * Compares this module against an other sort module.
     *
     * @param o the other sort module instance.
     * @return -1 if the other's module position is less than
     *         this modules position, +1 if this module is less than the
     *         other module or 0 if both modules have an equal position in
     *         the list.
     * @see Comparable#compareTo(Object)
     */
    public int compareTo(final Object o)
    {
      final SortModule otherModule = (SortModule) o;
      if (this.position > otherModule.position)
      {
        return +1;
      }
      if (this.position < otherModule.position)
      {
        return -1;
      }
      return 0;
    }
  }

  /** A logger for debug-messages. */
  private static final Log LOGGER = LogFactory.getLog(PackageSorter.class);

  /**
   * DefaultConstructor.
   */
  private PackageSorter()
  {
    // nothing required.
  }

  /**
   * Sorts the given list of package states. The packages
   * are sorted by their dependencies in a way so that all
   * dependent packages are placed on lower positions than
   * the packages which declared the dependency.
   *
   * @param modules the list of modules.
   */
  public static void sort(final List<PackageState> modules)
  {
    if (modules == null)
    {
      throw new NullPointerException();
    }

    final HashMap<String,SortModule> moduleMap = new HashMap<String,SortModule>();
    final ArrayList<PackageState> errorModules = new ArrayList<PackageState>();
    final ArrayList<SortModule> weightModules = new ArrayList<SortModule>();

    final int modulesCount = modules.size();
    for (int i = 0; i < modulesCount; i++)
    {
      final PackageState state = modules.get(i);
      if (state.getState() == PackageState.STATE_ERROR)
      {
        errorModules.add(state);
      }
      else
      {
        final SortModule mod = new SortModule(state);
        weightModules.add(mod);
        moduleMap.put(state.getModule().getModuleClass(), mod);
      }
    }

    final SortModule[] weigths = weightModules.toArray(new SortModule[weightModules.size()]);

    for (int i = 0; i < weigths.length; i++)
    {
      final SortModule sortMod = weigths[i];
      sortMod.setDependSubsystems
          (collectSubsystemModules(sortMod.getState().getModule(),
              moduleMap));
    }

    // repeat the computation until all modules have a matching
    // position. This is not the best algorithm, but it works
    // and is relativly simple. It will need some optimizations
    // in the future, but as this is only executed once, we don't
    // have to care much about it.
    boolean doneWork = true;
    while (doneWork)
    {
      doneWork = false;
      for (int i = 0; i < weigths.length; i++)
      {
        final SortModule mod = weigths[i];
        final int position = searchModulePosition(mod, moduleMap);
        if (position != mod.getPosition())
        {
          mod.setPosition(position);
          doneWork = true;
        }
      }
    }

    Arrays.sort(weigths);
    modules.clear();
    for (int i = 0; i < weigths.length; i++)
    {
      modules.add(weigths[i].getState());
    }
    for (int i = 0; i < errorModules.size(); i++)
    {
      modules.add(errorModules.get(i));
    }
  }

  /**
   * Computes the new module position. This position is computed
   * according to the dependent modules and subsystems. The returned
   * position will be higher than the highest dependent module position.
   *
   * @param smodule   the sort module for that we compute the new positon.
   * @param moduleMap the map with all modules.
   * @return the new positon.
   */
  private static int searchModulePosition
      (final SortModule smodule, final HashMap moduleMap)
  {
    final Module module = smodule.getState().getModule();
    int position = 0;

    // check the required modules. Increase our level to at least
    // one point over the highest dependent module
    // ignore missing modules.
    final ModuleInfo[] optionalModules = module.getOptionalModules();
    for (int modPos = 0; modPos < optionalModules.length; modPos++)
    {
      final String moduleName = optionalModules[modPos].getModuleClass();
      final SortModule reqMod = (SortModule) moduleMap.get(moduleName);
      if (reqMod == null)
      {
        continue;
      }
      if (reqMod.getPosition() >= position)
      {
        position = reqMod.getPosition() + 1;
      }
    }

    // check the required modules. Increase our level to at least
    // one point over the highest dependent module
    // there are no missing modules here (or the package manager
    // is invalid)
    final ModuleInfo[] requiredModules = module.getRequiredModules();
    for (int modPos = 0; modPos < requiredModules.length; modPos++)
    {
      final String moduleName = requiredModules[modPos].getModuleClass();
      final SortModule reqMod = (SortModule) moduleMap.get(moduleName);
      if (reqMod == null)
      {
        LOGGER.warn("Invalid state: Required dependency of '" + moduleName + "' had an error.");
        continue;
      }
      if (reqMod.getPosition() >= position)
      {
        position = reqMod.getPosition() + 1;
      }
    }

    // check the subsystem dependencies. This way we make sure
    // that subsystems are fully initialized before we try to use
    // them.
    final String subSystem = module.getSubSystem();
    final Iterator it = moduleMap.values().iterator();
    while (it.hasNext())
    {
      final SortModule mod = (SortModule) it.next();
      // it is evil to compute values on ourself...
      if (mod.getState().getModule() == module)
      {
        // same module ...
        continue;
      }
      final Module subSysMod = mod.getState().getModule();
      // if the module we check is part of the same subsystem as
      // we are, then we dont do anything. Within the same subsystem
      // the dependencies are computed solely by the direct references.
      if (ObjectUtilities.equal(subSystem, subSysMod.getSubSystem()))
      {
        // same subsystem ... ignore
        continue;
      }

      // does the module from the global list <mod> depend on the
      // subsystem we are part of?
      //
      // if yes, we have a relation and may need to adjust the level...
      if (smodule.getDependSubsystems().contains(subSysMod.getSubSystem()))
      {
        // check whether the module is a base module of the given
        // subsystem. We will not adjust our position in that case,
        // as this would lead to an infinite loop
        if (isBaseModule(subSysMod, module) == false)
        {
          if (mod.getPosition() >= position)
          {
            position = mod.getPosition() + 1;
          }
        }
      }
    }
    return position;
  }

  /**
   * Checks, whether a module is a base module of an given module.
   *
   * @param mod the module which to check
   * @param mi  the module info of the suspected base module.
   * @return true, if the given module info describes a base module of the
   *         given module, false otherwise.
   */
  private static boolean isBaseModule(final Module mod, final ModuleInfo mi)
  {
    final ModuleInfo[] requiredModules = mod.getRequiredModules();
    for (int i = 0; i < requiredModules.length; i++)
    {
      if (requiredModules[i].getModuleClass().equals(mi.getModuleClass()))
      {
        return true;
      }
    }
    final ModuleInfo[] optionalModules = mod.getOptionalModules();
    for (int i = 0; i < optionalModules.length; i++)
    {
      if (optionalModules[i].getModuleClass().equals(mi.getModuleClass()))
      {
        return true;
      }
    }
    return false;
  }


  /**
   * Collects all directly dependent subsystems.
   *
   * @param childMod  the module which to check
   * @param moduleMap the map of all other modules, keyed by module class.
   * @return the list of all dependent subsystems.
   */
  private static ArrayList<String> collectSubsystemModules
      (final Module childMod, final HashMap<String,SortModule> moduleMap)
  {
    final ArrayList<String> collector = new ArrayList<String>();
    final ModuleInfo[] requiredModules = childMod.getRequiredModules();
    for (int i = 0; i < requiredModules.length; i++)
    {
      final SortModule dependentModule = moduleMap.get(requiredModules[i].getModuleClass());
      if (dependentModule == null)
      {
        LOGGER.warn
            ("A dependent module was not found in the list of known modules." +
                requiredModules[i].getModuleClass());
        continue;
      }

      collector.add(dependentModule.getState().getModule().getSubSystem());
    }

    final ModuleInfo[] optionalModules = childMod.getOptionalModules();
    for (int i = 0; i < optionalModules.length; i++)
    {
      final Object o = moduleMap.get(optionalModules[i].getModuleClass());
      final SortModule dependentModule = (SortModule) o;
      if (dependentModule == null)
      {
        LOGGER.warn("A dependent module was not found in the list of known modules.");
        continue;
      }
      collector.add(dependentModule.getState().getModule().getSubSystem());
    }
    return collector;
  }
}