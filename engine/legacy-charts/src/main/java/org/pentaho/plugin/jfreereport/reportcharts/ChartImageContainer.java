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

package org.pentaho.plugin.jfreereport.reportcharts;

import org.pentaho.reporting.engine.classic.core.LocalImageContainer;
import org.pentaho.reporting.engine.classic.core.URLImageContainer;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

import java.awt.*;
import java.net.URL;

/**
 * Creation-Date: 01.10.2006, 15:47:03
 *
 * @author Thomas Morgner
 * @deprecated No longer used. Will be removed.
 */
public class ChartImageContainer implements URLImageContainer, LocalImageContainer {
  private static class ChartImageKey {
    private int row;

    private String expressionName;

    private ChartImageKey( final int row, final String expressionName ) {
      this.row = row;
      this.expressionName = expressionName;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final ChartImageKey that = (ChartImageKey) o;

      if ( row != that.row ) {
        return false;
      }
      if ( !expressionName.equals( that.expressionName ) ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result;
      result = row;
      result = 29 * result + expressionName.hashCode();
      return result;
    }
  }

  private String target;

  private Image image;

  private int width;

  private int height;

  private ChartImageKey key;

  public ChartImageContainer( final String target, final Image image, final int width, final int height, final int row,
                              final String keyName ) {
    if ( target == null ) {
      throw new NullPointerException();
    }
    if ( image == null ) {
      throw new NullPointerException();
    }
    if ( keyName == null ) {
      throw new NullPointerException();
    }

    this.target = target;
    this.image = image;
    this.width = width;
    this.height = height;
    this.key = new ChartImageKey( row, keyName );
  }

  public Object getIdentity() {
    return key;
  }

  public Image getImage() {
    return image;
  }

  public String getName() {
    return getSourceURLString();
  }

  public boolean isIdentifiable() {
    return true;
  }

  public int getImageHeight() {
    return height;
  }

  public int getImageWidth() {
    return width;
  }

  public float getScaleX() {
    return 1;
  }

  public float getScaleY() {
    return 1;
  }

  public URL getSourceURL() {
    return null;
  }

  public String getSourceURLString() {
    return target;
  }

  /**
   * The URL returned by this container cannot be read.
   *
   * @return
   */
  public boolean isLoadable() {
    return false;
  }

  public ResourceKey getResourceKey() {
    return null;
  }
}
