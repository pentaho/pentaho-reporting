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

package org.pentaho.reporting.tools.configeditor.model;

import org.pentaho.reporting.libraries.base.boot.Module;

import java.util.ArrayList;

/**
 * The config tree module node is used to represent a module in the report configuration. Modules collect all
 * task-specific configuration keys and represent a report module from the package manager.
 * <p/>
 * It is assumed, that all modules define their keys within the namespace of their package.
 *
 * @author Thomas Morgner
 */
public class ConfigTreeModuleNode extends AbstractConfigTreeNode {
  /**
   * The configuration prefix shared for all keys of the module.
   */
  private final String configurationPrefix;
  /**
   * The module definition from the package manager.
   */
  private final Module module;
  /**
   * The report configuration.
   */
  //  private final HierarchicalConfiguration configuration;
  /**
   * A list of keys from that module.
   */
  private final ArrayList<ConfigDescriptionEntry> assignedKeys;

  /**
   * Creates a new module node for the given module object and report configuration.
   *
   * @param module the module for which to build a tree node.
   */
  public ConfigTreeModuleNode( final Module module ) {
    super( module.getName() );
    this.assignedKeys = new ArrayList<ConfigDescriptionEntry>();
    this.module = module;
    configurationPrefix = ModuleNodeFactory.getPackage( this.module.getClass() );
  }

  /**
   * Returns the module represented by this node.
   *
   * @return the module used in this node.
   */
  public Module getModule() {
    return module;
  }

  /**
   * Returns the configuration prefix of this module.
   *
   * @return the configuration prefix.
   */
  public String getConfigurationPrefix() {
    return configurationPrefix;
  }

  /**
   * Returns a string representation of this object.
   *
   * @return the string representing this object.
   * @see Object#toString()
   */
  public String toString() {
    final StringBuilder buffer = new StringBuilder();
    buffer.append( "ConfigTreeModule={" ); //$NON-NLS-1$
    buffer.append( getConfigurationPrefix() );
    buffer.append( '}' );
    return buffer.toString();
  }

  /**
   * Returns true if the receiver is a leaf.
   *
   * @return true if the receiver is a leaf.
   */
  public boolean isLeaf() {
    return true;
  }

  /**
   * Returns true if the receiver allows children.
   *
   * @return true if the receiver allows children.
   */
  public boolean getAllowsChildren() {
    return false;
  }

  /**
   * Adds the given key to the list of assigned keys, if not already added.
   *
   * @param key the new key to be added
   * @throws NullPointerException if the given key is null.
   */
  public void addAssignedKey( final ConfigDescriptionEntry key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( assignedKeys.contains( key ) == false ) {
      assignedKeys.add( key );
    }
  }

  /**
   * Removed the given key description from the list of assigned keys.
   *
   * @param key the key that should be removed.
   * @throws NullPointerException if the given key is null.
   */
  public void removeAssignedKey( final ConfigDescriptionEntry key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    assignedKeys.remove( key );
  }

  /**
   * Returns the list of assigned keys as object array.
   *
   * @return the assigned keys as array.
   */
  public ConfigDescriptionEntry[] getAssignedKeys() {
    return assignedKeys.toArray
      ( new ConfigDescriptionEntry[ assignedKeys.size() ] );
  }
}
