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


package org.pentaho.reporting.engine.classic.core.modules.misc.beanshell;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.InputStream;

/**
 * The module definition for the beanshell expression support module.
 *
 * @author Thomas Morgner
 */
public class BeanShellModule extends AbstractModule {
  /**
   * DefaultConstructor. Loads the module specification.
   *
   * @throws ModuleInitializeException
   *           if an error occured.
   */
  public BeanShellModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem
   *          the subSystem.
   * @throws ModuleInitializeException
   *           if an error ocurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    final ClassLoader classLoader = ObjectUtilities.getClassLoader( getClass() );
    final InputStream in = classLoader.getResourceAsStream( BSHExpression.BSHHEADERFILE );
    if ( in == null ) {
      throw new ModuleInitializeException( "Unable to locate BSHHeaderFile. " + //$NON-NLS-1$
          "This file is required to execute the BeanShellExpressions." ); //$NON-NLS-1$
    }
    try {
      Class.forName( "bsh.Interpreter", false, classLoader ); //$NON-NLS-1$
    } catch ( Exception e ) {
      throw new ModuleInitializeException( "Unable to load the bean shell interpreter class. " + //$NON-NLS-1$
          "This class is required to execute the BeanShellExpressions." ); //$NON-NLS-1$
    }

    ElementMetaDataParser
        .initializeOptionalExpressionsMetaData( "org/pentaho/reporting/engine/classic/core/modules/misc/beanshell/meta-expressions.xml" );

  }
}
