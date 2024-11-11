/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.tools.configeditor.editor;

import org.pentaho.reporting.libraries.base.boot.Module;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.tools.configeditor.Messages;
import org.pentaho.reporting.tools.configeditor.model.ConfigDescriptionEntry;

import java.util.HashMap;
import java.util.Iterator;

/**
 * The editor factory is used to create a module editor for an given module. Implementors may add their own, more
 * specialized editor components to the factory.
 *
 * @author Thomas Morgner
 */
public final class EditorFactory {
  /**
   * The singleton instance of the factory.
   */
  private static EditorFactory factory;
  /**
   * A collection containing all defined modules and their priorities.
   */
  private final HashMap<ModuleEditor, Integer> priorities;

  /**
   * Externalized String Access
   */
  private final Messages messages;

  /**
   * Creates a new editor factory, which has the default module editor registered at lowest priority.
   */
  private EditorFactory() {
    messages = Messages.getInstance();
    priorities = new HashMap<ModuleEditor, Integer>();
    registerModuleEditor( new DefaultModuleEditor(), -1 );
  }

  /**
   * Returns the singleton instance of this factory or creates a new one if no already done.
   *
   * @return the editor factory instance.
   */
  public static synchronized EditorFactory getInstance() {
    if ( factory == null ) {
      factory = new EditorFactory();
    }
    return factory;
  }

  /**
   * Registers a module editor with this factory. The editor will be registered at the given priority.
   *
   * @param editor   the editor that should be registered.
   * @param priority the priority.
   */
  public void registerModuleEditor( final ModuleEditor editor, final int priority ) {
    if ( editor == null ) {
      throw new NullPointerException( messages.getString(
        "EditorFactory.ERROR_0001_EDITOR_IS_NULL" ) ); //$NON-NLS-1$
    }
    priorities.put( editor, new Integer( priority ) );
  }

  /**
   * Returns the module editor that will be most suitable for editing the given module.
   *
   * @param module   the module that should be edited.
   * @param config   the configuration which will supply the values for the edited keys.
   * @param keyNames the configuration entries which should be edited within the module.
   * @return the module editor for the given module or null, if no editor is suitable for the given module.
   */
  public ModuleEditor getModule
  ( final Module module, final HierarchicalConfiguration config,
    final ConfigDescriptionEntry[] keyNames ) {
    if ( module == null ) {
      throw new NullPointerException( messages.getString(
        "EditorFactory.ERROR_0002_MODULE_IS_NULL" ) ); //$NON-NLS-1$
    }
    if ( config == null ) {
      throw new NullPointerException( messages.getString(
        "EditorFactory.ERROR_0003_CONFIG_IS_NULL" ) ); //$NON-NLS-1$
    }
    if ( keyNames == null ) {
      throw new NullPointerException( messages.getString(
        "EditorFactory.ERROR_0004_KEYNAMES_IS_NULL" ) ); //$NON-NLS-1$
    }
    final Iterator keys = priorities.keySet().iterator();
    ModuleEditor currentEditor = null;
    int currentEditorPriority = Integer.MIN_VALUE;

    while ( keys.hasNext() ) {
      final ModuleEditor ed = (ModuleEditor) keys.next();
      if ( ed.canHandle( module ) ) {
        final Integer prio = priorities.get( ed );
        if ( prio.intValue() > currentEditorPriority ) {
          currentEditorPriority = prio.intValue();
          currentEditor = ed;
        }
      }
    }
    if ( currentEditor != null ) {
      return currentEditor.createInstance( module, config, keyNames );
    }
    return null;
  }

}
