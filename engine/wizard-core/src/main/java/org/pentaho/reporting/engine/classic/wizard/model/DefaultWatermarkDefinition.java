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

public class DefaultWatermarkDefinition implements WatermarkDefinition {
  private String source;
  private Length x;
  private Length y;
  private Length width;
  private Length height;
  private Boolean keepAspectRatio;
  private Boolean scale;
  private boolean visible;

  public DefaultWatermarkDefinition() {
    visible = true;
  }

  public String getSource() {
    return source;
  }

  public void setSource( final String source ) {
    this.source = source;
  }

  public Length getX() {
    return x;
  }

  public void setX( final Length x ) {
    this.x = x;
  }

  public Length getY() {
    return y;
  }

  public void setY( final Length y ) {
    this.y = y;
  }

  public Length getWidth() {
    return width;
  }

  public void setWidth( final Length width ) {
    this.width = width;
  }

  public Length getHeight() {
    return height;
  }

  public void setHeight( final Length height ) {
    this.height = height;
  }

  public Boolean getKeepAspectRatio() {
    return keepAspectRatio;
  }

  public void setKeepAspectRatio( final Boolean keepAspectRatio ) {
    this.keepAspectRatio = keepAspectRatio;
  }

  public Boolean getScale() {
    return scale;
  }

  public void setScale( final Boolean scale ) {
    this.scale = scale;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible( final boolean visible ) {
    this.visible = visible;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
