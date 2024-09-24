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

package org.pentaho.reporting.libraries.base.boot;

/**
 * The module initializer is used to separate the initialization process from the module definition. An invalid
 * classpath setup or an missing base module may throw an ClassCastException if the module class references this missing
 * resource. Separating them is the best way to make sure that the classloader does not interrupt the module loading
 * process.
 *
 * @author Thomas Morgner
 */
public interface ModuleInitializer {

  /**
   * Performs the initalization of the module.
   *
   * @throws ModuleInitializeException if an error occurs which prevents the module from being usable.
   */
  public void performInit() throws ModuleInitializeException;
}
