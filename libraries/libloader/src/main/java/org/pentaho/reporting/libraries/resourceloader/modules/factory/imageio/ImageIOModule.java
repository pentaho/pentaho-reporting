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

package org.pentaho.reporting.libraries.resourceloader.modules.factory.imageio;

import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * Creation-Date: 11.12.2007, 15:53:49
 *
 * @author Thomas Morgner
 */
public class ImageIOModule extends AbstractModule {

  public ImageIOModule() throws ModuleInitializeException {
    loadModuleInfo();
  }


  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
  }
}
