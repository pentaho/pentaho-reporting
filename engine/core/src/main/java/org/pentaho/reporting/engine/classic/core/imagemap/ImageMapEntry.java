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


package org.pentaho.reporting.engine.classic.core.imagemap;

import java.awt.Shape;
import java.io.Serializable;

public interface ImageMapEntry extends Serializable {
  public String getAreaType();

  public float[] getAreaCoordinates();

  public String getAttribute( String namespace, String name );

  public String[] getNameSpaces();

  public String[] getNames( final String namespace );

  public boolean contains( float x, float y );

  public Shape getShape();

}
