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

package org.pentaho.reporting.tools.configeditor.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.tools.configeditor.model.ClassConfigDescriptionEntry;

/**
 * The class key editor is used to edit report configuration keys which take the name of an class as value.
 *
 * @author Thomas Morgner
 */
public class ClassKeyEditor extends TextKeyEditor {
  private static final Log logger = LogFactory.getLog( ClassKeyEditor.class );
  /**
   * The base class, to which all value classes must be assignable.
   */
  private Class baseClass;

  /**
   * Creates a new class key editor for the given entry and configuration. The given display name will be used as label
   * text.
   *
   * @param config      the report configuration.
   * @param entry       the configuration description entry that describes the key
   * @param displayName the text for the label
   */
  public ClassKeyEditor( final HierarchicalConfiguration config,
                         final ClassConfigDescriptionEntry entry,
                         final String displayName ) {
    super( config, entry, displayName );
    baseClass = entry.getBaseClass();
    if ( baseClass == null ) {
      ClassKeyEditor.logger.warn( "Base class undefined, defaulting to java.lang.Object" ); //$NON-NLS-1$
      baseClass = Object.class;
    }
    validateContent();
  }

  /**
   * Checks whether the given value is a valid classname and is assignable from the base class.
   */
  public void validateContent() {
    if ( baseClass == null ) {
      // validate is called before the baseclass is set ... ugly!
      return;
    }
    try {
      final String className = getContent();
      if ( className == null ) {
        setValidInput( false );
      } else {
        final ClassLoader classLoader = ObjectUtilities.getClassLoader( getClass() );
        final Class c = Class.forName( className, false, classLoader );
        setValidInput( baseClass.isAssignableFrom( c ) );
      }
    } catch ( Exception e ) {
      // ignored ..
      setValidInput( false );
    }
    // Log.debug ("Validate ClassContent:" + getContent() + " is Valid: " + isValidInput());
  }
}
