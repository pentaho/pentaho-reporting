/*
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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.fonts.afm;

import java.io.IOException;

/**
 * Creation-Date: 21.07.2007, 20:20:07
 *
 * @author Thomas Morgner
 */
public class AfmHeader {
  private static final double[] EMPTY_DOUBLES = new double[ 0 ];

  private static final String METRICS_SETS = "MetricsSets ";
  private static final String FONT_NAME = "FontName ";
  private static final String FULL_NAME = "FullName ";
  private static final String FAMILY_NAME = "FamilyName ";
  private static final String WEIGHT = "Weight ";
  private static final String FONT_BBOX = "FontBBox ";
  private static final String VERSION = "Version ";
  private static final String NOTICE = "Notice ";
  private static final String ENCODING_SCHEME = "EncodingScheme ";
  private static final String MAPPING_SCHEME = "MappingScheme ";
  private static final String ESC_CHAR = "EscChar ";
  private static final String CHARACTERSET = "CharacterSet ";
  private static final String CHARACTERS = "Characters ";
  private static final String ISBASEFONT = "IsBaseFont ";
  private static final String VVECTOR = "VVector ";
  private static final String ISFIXEDV = "IsFixedV ";
  private static final String CAPHEIGHT = "CapHeight ";
  private static final String XHEIGHT = "XHeight ";
  private static final String ASCENDER = "Ascender ";
  private static final String DESCENDER = "Descender ";

  private String fontName;
  private int metricsSets;
  private String familyName;
  private String fullName;
  private int weight;
  private double[] bbox;
  private String version;
  private String notice;
  private String encodingScheme;
  private int mappingScheme;
  private int escChar;
  private String characterSet;
  private int characters;
  private boolean baseFont;
  private double[] vvector;
  private boolean fixedV;
  private double capHeight;
  private double xHeight;
  private double ascender;
  private double descender;

  public AfmHeader() {
    bbox = new double[ 4 ];
  }

  public void addData( final String line ) throws IOException {
    if ( line.startsWith( METRICS_SETS ) ) {
      metricsSets = AfmParseUtilities.parseInt( METRICS_SETS, line );
    } else if ( line.startsWith( FONT_NAME ) ) {
      fontName = line.substring( FONT_NAME.length() );
    } else if ( line.startsWith( FULL_NAME ) ) {
      fullName = line.substring( FULL_NAME.length() );
    } else if ( line.startsWith( FAMILY_NAME ) ) {
      familyName = line.substring( FAMILY_NAME.length() );
    } else if ( line.startsWith( WEIGHT ) ) {
      final String weightText = line.substring( WEIGHT.length() );
      if ( "bold".equalsIgnoreCase( weightText ) ) {
        weight = 700;
      } else if ( "light".equalsIgnoreCase( weightText ) ) {
        weight = 200;
      } else {
        weight = 400;
      }

    } else if ( line.startsWith( FONT_BBOX ) ) {
      bbox = AfmParseUtilities.parseDoubleArray( line, 4 );
    } else if ( line.startsWith( VERSION ) ) {
      version = line.substring( VERSION.length() );
    } else if ( line.startsWith( NOTICE ) ) {
      notice = line.substring( NOTICE.length() );
    } else if ( line.startsWith( ENCODING_SCHEME ) ) {
      encodingScheme = line.substring( ENCODING_SCHEME.length() );
    } else if ( line.startsWith( MAPPING_SCHEME ) ) {
      mappingScheme = AfmParseUtilities.parseInt( MAPPING_SCHEME, line );
    } else if ( line.startsWith( ESC_CHAR ) ) {
      escChar = AfmParseUtilities.parseInt( ESC_CHAR, line );
    } else if ( line.startsWith( CHARACTERSET ) ) {
      characterSet = line.substring( CHARACTERSET.length() );
    } else if ( line.startsWith( CHARACTERS ) ) {
      characters = AfmParseUtilities.parseInt( CHARACTERS, line );
    } else if ( line.startsWith( ISBASEFONT ) ) {
      final String baseFontText = line.substring( ISBASEFONT.length() );
      baseFont = "true".equalsIgnoreCase( baseFontText );
    } else if ( line.startsWith( VVECTOR ) ) {
      vvector = AfmParseUtilities.parseDoubleArray( line, 2 );
    } else if ( line.startsWith( ISFIXEDV ) ) {
      final String boolText = line.substring( ISFIXEDV.length() );
      fixedV = "true".equalsIgnoreCase( boolText );
    } else if ( line.startsWith( CAPHEIGHT ) ) {
      capHeight = AfmParseUtilities.parseDouble( CAPHEIGHT, line );
    } else if ( line.startsWith( XHEIGHT ) ) {
      xHeight = AfmParseUtilities.parseDouble( XHEIGHT, line );
    } else if ( line.startsWith( ASCENDER ) ) {
      ascender = AfmParseUtilities.parseDouble( ASCENDER, line );
    } else if ( line.startsWith( DESCENDER ) ) {
      descender = AfmParseUtilities.parseDouble( DESCENDER, line );
    }

  }

  public double getDescender() {
    return descender;
  }

  public double getAscender() {
    return ascender;
  }

  public double getxHeight() {
    return xHeight;
  }

  public double getCapHeight() {
    return capHeight;
  }

  public boolean isFixedV() {
    return fixedV;
  }

  public double[] getVvector() {
    if ( vvector == null ) {
      return EMPTY_DOUBLES;
    }
    return (double[]) vvector.clone();
  }

  public boolean isBaseFont() {
    return baseFont;
  }

  public int getCharacters() {
    return characters;
  }

  public String getCharacterSet() {
    return characterSet;
  }

  public int getEscChar() {
    return escChar;
  }

  public int getMappingScheme() {
    return mappingScheme;
  }

  public String getEncodingScheme() {
    return encodingScheme;
  }

  public String getNotice() {
    return notice;
  }

  public String getVersion() {
    return version;
  }

  public double[] getBbox() {
    return (double[]) bbox.clone();
  }

  public int getWeight() {
    return weight;
  }

  public String getFullName() {
    return fullName;
  }

  public String getFamilyName() {
    return familyName;
  }

  public int getMetricsSets() {
    return metricsSets;
  }

  public String getFontName() {
    return fontName;
  }

}
