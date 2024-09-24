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

package org.pentaho.reporting.engine.classic.extensions.modules.rhino;

import java.net.URL;

import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * The module definition for the bean scripting framework support module.
 *
 * @author Thomas Morgner
 */
public class RhinoModule extends AbstractModule {
  /**
   * DefaultConstructor. Loads the module specification.
   *
   * @throws ModuleInitializeException
   *           if an error occurred.
   */
  public RhinoModule() throws ModuleInitializeException {
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
   *           if an error occurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    try {
      final ClassLoader loader = ObjectUtilities.getClassLoader( getClass() );
      Class.forName( "org.mozilla.javascript.Context", false, loader );
    } catch ( Exception e ) {
      throw new ModuleInitializeException( "Unable to load the Rhino scripting framework class. "
          + "This class is required to execute the RhinoExpressions." );
    }

    final URL expressionMetaSource =
        ObjectUtilities.getResource(
            "org/pentaho/reporting/engine/classic/extensions/modules/rhino/meta-expressions.xml", RhinoModule.class );
    if ( expressionMetaSource == null ) {
      throw new ModuleInitializeException( "Error: Could not find the expression meta-data description file" );
    }
    try {
      ExpressionRegistry.getInstance().registerFromXml( expressionMetaSource );
    } catch ( Exception e ) {
      throw new ModuleInitializeException( "Error: Could not parse the element meta-data description file", e );
    }

  }
}
