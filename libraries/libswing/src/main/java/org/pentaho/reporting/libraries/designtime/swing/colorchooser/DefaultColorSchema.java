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
