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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.tools.configeditor.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.boot.Module;
import org.pentaho.reporting.libraries.base.config.metadata.ConfigurationDomain;
import org.pentaho.reporting.libraries.base.config.metadata.ConfigurationMetaData;
import org.pentaho.reporting.libraries.base.config.metadata.ConfigurationMetaDataEntry;
import org.pentaho.reporting.libraries.base.config.metadata.ConfigurationMetaDataParser;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.tools.configeditor.Messages;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The module node factory is used to build the lists of modules and their assigned keys for the ConfigTreeModel.
 *
 * @author Thomas Morgner
 */
public class ModuleNodeFactory {
  private static class ConfigTreeModuleNodeComparator implements Comparator<ConfigTreeModuleNode> {
    public int compare( final ConfigTreeModuleNode n1, final ConfigTreeModuleNode n2 ) {
      return ( n1.getName().compareTo( n2.getName() ) );
    }
  }

  /**
   * Sorts the given modules by their class package names.
   *
   * @author Thomas Morgner
   */
  private static class ModuleSorter implements Comparator<Module>, Serializable {
    /**
     * DefaultConstructor.
     */
    protected ModuleSorter() {
    }

    /**
     * Compares its two arguments for order.  Returns a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the second.<p>
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
     * than the second.
     * @throws ClassCastException if the arguments' types prevent them from being compared by this Comparator.
     */
    public int compare( final Module o1, final Module o2 ) {
      final String name1;
      final String name2;

      if ( o1.getClass().getPackage() == null || o2.getClass().getPackage() == null ) {
        name1 = ModuleNodeFactory.getPackage( o1.getClass() );
        name2 = ModuleNodeFactory.getPackage( o2.getClass() );
      } else {
        name1 = o1.getClass().getPackage().getName();
        name2 = o2.getClass().getPackage().getName();
      }
      return name1.compareTo( name2 );
    }
  }

  private static final Log logger = LogFactory.getLog( ModuleNodeFactory.class );

  /**
   * Provides access to externalized strings
   */
  private final Messages messages;

  /**
   * All known modules as known at construction time.
   */
  private final Module[] activeModules;
  /**
   * A list of global module nodes.
   */
  private final ArrayList<ConfigTreeModuleNode> globalNodes;
  /**
   * A list of local module nodes.
   */
  private final ArrayList<ConfigTreeModuleNode> localNodes;
  /**
   * A hashtable of all defined config description entries.
   */
  private final HashMap<String, ConfigDescriptionEntry> configEntryLookup;
  private AbstractBoot packageManager;

  /**
   * Create a new and uninitialized module node factory.
   */
  public ModuleNodeFactory( final AbstractBoot packageManager ) {
    this.packageManager = packageManager;
    messages = Messages.getInstance();
    activeModules = packageManager.getPackageManager().getAllModules();
    Arrays.sort( activeModules, new ModuleSorter() );
    globalNodes = new ArrayList<ConfigTreeModuleNode>();
    localNodes = new ArrayList<ConfigTreeModuleNode>();
    configEntryLookup = new HashMap<String, ConfigDescriptionEntry>();

  }

  public void load( final boolean append ) throws IOException {
    final String configurationDomain = packageManager.getConfigurationDomain();
    final ConfigurationDomain domain = ConfigurationMetaData.getInstance().createDomain( configurationDomain );

    final ConfigurationMetaDataParser parser = new ConfigurationMetaDataParser();
    parser.parseConfiguration( this.packageManager );
    if ( append == false ) {
      configEntryLookup.clear();
    }

    final ConfigurationMetaDataEntry[] entries = domain.getAll();
    for ( int i = 0; i < entries.length; i++ ) {
      final ConfigurationMetaDataEntry entry = entries[ i ];
      if ( entry.getClassName() != null ) {
        final ClassConfigDescriptionEntry value = new ClassConfigDescriptionEntry( entry.getKey() );
        try {
          value.setBaseClass(
            Class.forName( entry.getClassName(), false, ObjectUtilities.getClassLoader( packageManager.getClass() ) ) );
          value.setHidden( entry.isHidden() );
          value.setGlobal( entry.isGlobal() );
          value.setDescription( entry.getDescription() );
          configEntryLookup.put( entry.getKey(), value );
          continue;
        } catch ( Exception e ) {
          logger.info(
            "Failed to load defined class '" + entry.getClassName() + "' , fall back to plain-text entry for key "
              + entry.getKey() );
        }
      }
      final String[] tags = entry.getTags();
      if ( tags.length > 0 ) {
        final EnumConfigDescriptionEntry value = new EnumConfigDescriptionEntry( entry.getKey() );
        value.setOptions( tags );
        value.setHidden( entry.isHidden() );
        value.setGlobal( entry.isGlobal() );
        value.setDescription( entry.getDescription() );
        configEntryLookup.put( entry.getKey(), value );
        continue;
      }

      final TextConfigDescriptionEntry value = new TextConfigDescriptionEntry( entry.getKey() );
      value.setHidden( entry.isHidden() );
      value.setGlobal( entry.isGlobal() );
      value.setDescription( entry.getDescription() );
      configEntryLookup.put( entry.getKey(), value );
    }
  }

  public void load( final InputStream in, final boolean append )
    throws IOException {
    if ( append == false ) {
      configEntryLookup.clear();
    }
    final ConfigDescriptionModel model = new ConfigDescriptionModel();
    try {
      model.load( in );
    } catch ( SAXException saxException ) {
      final String error = messages.getString( "ModuleNodeFactory.ERROR_0001_PARSE_FAILURE",
        saxException.getMessage() ); //$NON-NLS-1$
      ModuleNodeFactory.logger.error( error, saxException );
      throw new IOException( error );
    } catch ( ParserConfigurationException pE ) {
      final String error = messages.getString( "ModuleNodeFactory.ERROR_0002_PARSER_CONFIG_ERROR",
        pE.getMessage() ); //$NON-NLS-1$
      ModuleNodeFactory.logger.error( error, pE );
      throw new IOException( error );
    }

    final ConfigDescriptionEntry[] entries = model.toArray();
    for ( int i = 0; i < entries.length; i++ ) {
      //Log.debug ("Entry: " + entries[i].getKeyName() + " registered");
      configEntryLookup.put( entries[ i ].getKeyName(), entries[ i ] );
    }
  }


  /**
   * (Re)Initializes the factory from the given report configuration. This will assign all keys frmo the report
   * configuration to the model and assignes the definition from the configuration description if possible.
   */
  public void init() {
    globalNodes.clear();
    localNodes.clear();

    //Iterator enum = config.findPropertyKeys("");
    final Iterator keys = configEntryLookup.keySet().iterator();
    while ( keys.hasNext() ) {
      final String key = (String) keys.next();
      processKey( key );
    }
  }

  /**
   * Processes a single report configuration key and tries to find a definition for that key.
   *
   * @param key the name of the report configuration key
   */
  private void processKey( final String key ) {
    ConfigDescriptionEntry cde = configEntryLookup.get( key );

    //Log.debug ("ActiveModule: " + mod.getClass() + " for key " + key);
    if ( cde == null ) {
      // check whether the system properties define such an key.
      // if they do, then we can assume, that it is just a sys-prop
      // and we ignore the key.
      //
      // if this is no system property, then this is a new entry, we'll
      // assume that it is a local text key.
      //
      // Security restrictions are handled as if the key is not defined
      // in the system properties. It is safer to add too much than to add
      // less properties ...
      try {
        if ( System.getProperties().containsKey( key ) ) {
          ModuleNodeFactory.logger.debug( "Ignored key from the system properties: " + key ); //$NON-NLS-1$
          return;
        } else {
          ModuleNodeFactory.logger.debug( "Undefined key added on the fly: " + key ); //$NON-NLS-1$
          cde = new TextConfigDescriptionEntry( key );
        }
      } catch ( final SecurityException se ) {
        ModuleNodeFactory.logger
          .debug( "Unsafe key-definition due to security restrictions: " + key, se ); //$NON-NLS-1$
        cde = new TextConfigDescriptionEntry( key );
      }
    }

    // We ignore hidden keys.
    if ( cde.isHidden() ) {
      return;
    }

    final Module mod = lookupModule( key );
    if ( mod == null ) {
      return;
    }
    if ( cde.isGlobal() == false ) {
      ConfigTreeModuleNode node = lookupNode( mod, localNodes );
      if ( node == null ) {
        node = new ConfigTreeModuleNode( mod );
        localNodes.add( node );
      }
      node.addAssignedKey( cde );
    }

    // The global configuration provides defaults for the local
    // settings...
    ConfigTreeModuleNode node = lookupNode( mod, globalNodes );
    if ( node == null ) {
      node = new ConfigTreeModuleNode( mod );
      globalNodes.add( node );
    }
    node.addAssignedKey( cde );
  }

  /**
   * Tries to find a module node for the given module in the given list.
   *
   * @param key      the module that is searched.
   * @param nodeList the list with all known modules.
   * @return the node containing the given module, or null if not found.
   */
  private ConfigTreeModuleNode lookupNode( final Module key, final ArrayList nodeList ) {
    if ( key == null ) {
      return null;
    }
    for ( int i = 0; i < nodeList.size(); i++ ) {
      final ConfigTreeModuleNode node = (ConfigTreeModuleNode) nodeList.get( i );
      if ( key == node.getModule() ) {
        return node;
      }
    }
    return null;
  }

  /**
   * Returns the name of the package for the given class. This is a workaround for the classloader behaviour of JDK1.2.2
   * where no package objects are created.
   *
   * @param c the class for which we search the package.
   * @return the name of the package, never null.
   */
  public static String getPackage( final Class c ) {
    final String className = c.getName();
    final int idx = className.lastIndexOf( '.' );
    if ( idx <= 0 ) {
      // the default package
      return ""; //$NON-NLS-1$
    } else {
      return className.substring( 0, idx );
    }
  }

  /**
   * Looks up the module for the given key. If no module is responsible for the key, then it will be assigned to the
   * core module.
   * <p/>
   * If the core is not defined, then a ConfigTreeModelException is thrown. The core is the base for all modules, and is
   * always defined in a sane environment.
   *
   * @param key the name of the configuration key
   * @return the module that most likely defines that key
   */
  private Module lookupModule( final String key ) {
    Module retval = null;
    int confidence = -1;
    for ( int i = 0; i < activeModules.length; i++ ) {
      final String modPackage = ModuleNodeFactory.getPackage( activeModules[ i ].getClass() );
      // Log.debug ("Module package: " + modPackage + " for " + activeModules[i].getClass());
      if ( key.startsWith( modPackage ) ) {
        if ( confidence < modPackage.length() ) {
          confidence = modPackage.length();
          retval = activeModules[ i ];
        }
      }
    }
    return retval;
  }

  /**
   * Returns all global nodes. You have to initialize the factory before using this method.
   *
   * @return the list of all global nodes.
   */
  public ConfigTreeModuleNode[] getGlobalNodes() {
    final ConfigTreeModuleNode[] retval = globalNodes.toArray( new ConfigTreeModuleNode[ globalNodes.size() ] );
    Arrays.sort( retval, new ConfigTreeModuleNodeComparator() );
    return retval;
  }

  /**
   * Returns all local nodes. You have to initialize the factory before using this method.
   *
   * @return the list of all global nodes.
   */
  public ConfigTreeModuleNode[] getLocalNodes() {
    final ConfigTreeModuleNode[] retval = localNodes.toArray( new ConfigTreeModuleNode[ localNodes.size() ] );
    Arrays.sort( retval, new ConfigTreeModuleNodeComparator() );
    return retval;
  }

  /**
   * Returns the entry for the given key or null, if the key has no metadata.
   *
   * @param key the name of the key
   * @return the entry or null if not found.
   */
  public ConfigDescriptionEntry getEntryForKey( final String key ) {
    return configEntryLookup.get( key );
  }
}
