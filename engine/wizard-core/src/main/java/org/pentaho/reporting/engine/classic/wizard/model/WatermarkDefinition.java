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


package org.pentaho.reporting.engine.classic.wizard.model;

import java.io.Serializable;

public interface WatermarkDefinition extends Serializable, Cloneable {
  public String getSource();

  public void setSource( String src );

  public Length getX();

  public void setX( Length x );

  public Length getY();

  public void setY( Length y );

  public Length getWidth();

  public void setWidth( Length width );

  public Length getHeight();

  public void setHeight( Length height );

  public Boolean getKeepAspectRatio();

  public void setKeepAspectRatio( Boolean keepAspectRatio );

  public Boolean getScale();

  public void setScale( Boolean scale );

  public boolean isVisible();

  public void setVisible( boolean b );

  public Object clone() throws CloneNotSupportedException;
}
