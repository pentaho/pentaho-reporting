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

package org.pentaho.reporting.libraries.designtime.swing.colorchooser;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.ColorUtility;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class DefaultColorSchema implements ColorSchema {
  private String name;
  private ArrayList<Color> colors;

  public DefaultColorSchema() {
    colors = new ArrayList<Color>( colors );
    name = "<unnamed>";
  }

  public void load( final Configuration p, final String prefix ) {
    name = p.getConfigProperty( prefix + ".name" );
    if ( name == null ) {
      name = "<unnamed>";
    }
    colors.clear();
    for ( int i = 0; ; i++ ) {
      final String colorText = p.getConfigProperty( prefix + ".color." + i );
      if ( StringUtils.isEmpty( colorText ) ) {
        break;
      }
      try {
        colors.add( ColorUtility.toPropertyValue( colorText ) );
      } catch ( Exception e ) {
        break;
      }
    }
  }

  public void load( final Properties p ) {
    name = p.getProperty( "name" );
    if ( name == null ) {
      name = "<unnamed>";
    }
    colors.clear();
    for ( int i = 0; ; i++ ) {
      final String colorText = p.getProperty( "color." + i );
      if ( StringUtils.isEmpty( colorText ) ) {
        break;
      }
      try {
        colors.add( ColorUtility.toPropertyValue( colorText ) );
      } catch ( Exception e ) {
        break;
      }
    }
  }

  public void save( final Properties p ) {
    p.setProperty( "name", name );
    final Color[] colorsArray = getColors();
    for ( int i = 0; i < colorsArray.length; i++ ) {
      final Color color = colorsArray[ i ];
      p.setProperty( "color." + i, ColorUtility.toAttributeValue( color ) );
    }
  }

  public void add( final Color c ) {
    colors.add( c );
  }

  public void clear() {
    colors.clear();
  }

  public void setName( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.name = name;
  }

  public void setColors( final Color[] colors ) {
    this.colors.clear();
    this.colors.addAll( Arrays.asList( colors ) );
  }

  public Color[] getColors() {
    return colors.toArray( new Color[ colors.size() ] );
  }

  public String getName() {
    return name;
  }
}
