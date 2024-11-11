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


package org.pentaho.reporting.libraries.base.boot;

/**
 * The Module info class encapsulates metadata about a given module. It holds the list of dependencies and the module
 * version and description.
 *
 * @author Thomas Morgner
 */
public interface ModuleInfo {

  /**
   * Returns the module class of the desired base module.
   *
   * @return The module class.
   */
  public String getModuleClass();

  /**
   * Returns the major version of the base module. The string should contain a compareable character sequence so that
   * higher versions of the module are considered greater than lower versions.
   *
   * @return The major version of the module.
   */
  public String getMajorVersion();

  /**
   * Returns the minor version of the base module. The string should contain a compareable character sequence so that
   * higher versions of the module are considered greater than lower versions.
   *
   * @return The minor version of the module.
   */
  public String getMinorVersion();

  /**
   * Returns the patchlevel version of the base module. The patch level should be used to mark bugfixes. The string
   * should contain a compareable character sequence so that higher versions of the module are considered greater than
   * lower versions.
   *
   * @return The patch level version of the module.
   */
  public String getPatchLevel();

}
