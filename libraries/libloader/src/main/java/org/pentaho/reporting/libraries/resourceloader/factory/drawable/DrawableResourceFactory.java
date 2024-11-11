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


package org.pentaho.reporting.libraries.resourceloader.factory.drawable;

import org.pentaho.reporting.libraries.resourceloader.factory.AbstractResourceFactory;

/**
 * Creation-Date: 05.04.2006, 17:59:50
 *
 * @author Thomas Morgner
 */
public class DrawableResourceFactory extends AbstractResourceFactory {
  private static DrawableResourceFactory instance;

  public DrawableResourceFactory() {
    super( DrawableWrapper.class );
  }

  public static synchronized DrawableResourceFactory getInstance() {
    if ( instance == null ) {
      instance = new DrawableResourceFactory();
    }
    return instance;
  }
}
