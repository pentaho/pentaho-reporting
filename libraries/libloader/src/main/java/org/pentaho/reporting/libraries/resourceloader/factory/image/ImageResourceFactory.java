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


package org.pentaho.reporting.libraries.resourceloader.factory.image;

import org.pentaho.reporting.libraries.resourceloader.factory.AbstractResourceFactory;

import java.awt.*;

/**
 * Creation-Date: 05.04.2006, 17:59:50
 *
 * @author Thomas Morgner
 */
public class ImageResourceFactory extends AbstractResourceFactory {
  private static ImageResourceFactory instance;

  public ImageResourceFactory() {
    super( Image.class );
  }

  public static synchronized ImageResourceFactory getInstance() {
    if ( instance == null ) {
      instance = new ImageResourceFactory();
    }
    return instance;
  }
}
