/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
