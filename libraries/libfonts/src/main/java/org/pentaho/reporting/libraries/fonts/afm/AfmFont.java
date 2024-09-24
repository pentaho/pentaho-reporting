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

import org.pentaho.reporting.libraries.fonts.io.FileFontDataInputSource;
import org.pentaho.reporting.libraries.fonts.io.FontDataInputSource;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * An AFM font is a simple text file.
 *
 * @author Thomas Morgner
 */
public class AfmFont implements Serializable {
  private static final int PRE_HEADER = 0;
  private static final int IN_HEADER = 1;
  private static final int IN_METRICS = 2;
  private static final int IN_KERNDATA = 3;
  private static final int IN_COMPOSITES = 4;
  private static final int END_OF_FILE = 5;
  private static final int IN_DIRECTION = 6;

  private AfmHeader header;
  private AfmDirectionSection[] directionSections;
  private AfmCharMetricsSection charMetricsSection;
  private AfmKernDataSection kernDataSection;
  private AfmCompositeCharDataSection compositeCharDataSection;
  private boolean embeddable;
  private String fontName;
  private String familyName;
  private String filename;
  private FontDataInputSource input;

  public AfmFont( final File font,
                  final boolean embeddable ) throws IOException {
    final FontDataInputSource fis = new FileFontDataInputSource( font );
    initialize( fis, embeddable );
    fis.dispose();
  }

  public AfmFont( final FontDataInputSource inputSource,
                  final boolean embeddable ) throws IOException {
    initialize( inputSource, embeddable );
  }

  private void initialize( final FontDataInputSource inputSource, final boolean embeddable )
    throws IOException {
    if ( inputSource == null ) {
      throw new NullPointerException();
    }

    this.filename = inputSource.getFileName();
    this.input = inputSource;
    this.embeddable = embeddable;
    header = new AfmHeader();
    directionSections = new AfmDirectionSection[ 2 ];
    directionSections[ 0 ] = new AfmDirectionSection();
    directionSections[ 1 ] = new AfmDirectionSection();
    charMetricsSection = new AfmCharMetricsSection();
    kernDataSection = new AfmKernDataSection();

    parseFontFile( inputSource );

    fontName = header.getFontName();
    if ( fontName == null ) {
      throw new IOException( "This font does not define a font-name, therefore it is invalid." );
    }

    familyName = header.getFamilyName();
    if ( familyName == null ) {
      familyName = fontName;
    }
  }

  private void parseFontFile( final FontDataInputSource inputSource )
    throws IOException {
    int parseState = PRE_HEADER;
    int sectionType = 0;
    final FontDataAsciiReader reader = new FontDataAsciiReader( inputSource );
    String line;
    while ( ( line = reader.readLine() ) != null ) {
      if ( line.length() == 0 ) {
        continue;
      }

      switch( parseState ) {
        case PRE_HEADER: {
          if ( line.startsWith( "StartFontMetrics" ) == false ) {
            throw new IOException( "Expected 'StartMetrics' as initial command line." );
          }
          parseState = IN_HEADER;
          break;
        }
        case IN_HEADER: {
          if ( line.startsWith( "EndFontMetrics" ) ) {
            parseState = END_OF_FILE;
          } else if ( line.startsWith( "StartDirection" ) ) {
            parseState = IN_DIRECTION;
            sectionType = AfmParseUtilities.parseInt( "StartDirection ", line );
          } else if ( line.startsWith( "StartCharMetrics" ) ) {
            parseState = IN_METRICS;
            sectionType = AfmParseUtilities.parseInt( "StartCharMetrics ", line );
          } else if ( line.startsWith( "StartKernData" ) ) {
            parseState = IN_KERNDATA;
          } else if ( line.startsWith( "StartComposites" ) ) {
            parseState = IN_COMPOSITES;
            compositeCharDataSection = new AfmCompositeCharDataSection();
          } else {
            header.addData( line );
            directionSections[ 0 ].add( line );
          }
          break;
        }
        case IN_METRICS: {
          if ( line.startsWith( "EndCharMetrics" ) ) {
            parseState = IN_HEADER;
            sectionType = 0;
          } else {
            charMetricsSection.add( line );
          }
          break;
        }
        case IN_KERNDATA: {
          if ( line.startsWith( "EndKernData" ) ) {
            parseState = IN_HEADER;
            sectionType = 0;
          } else {
            kernDataSection.add( line );
          }
          break;
        }
        case IN_COMPOSITES: {
          if ( line.startsWith( "EndComposites" ) ) {
            parseState = IN_HEADER;
            sectionType = 0;
          } else {
            compositeCharDataSection.add( line );
          }
          break;
        }
        case END_OF_FILE: {
          // Extra lines after the 'EndFontMetrics' line are ignored.
          break;
        }
        case IN_DIRECTION: {
          if ( line.startsWith( "EndDirection" ) ) {
            parseState = IN_HEADER;
            sectionType = 0;
          } else {
            switch( sectionType ) {
              case 0: {
                directionSections[ 0 ].add( line );
                break;
              }
              case 1: {
                directionSections[ 1 ].add( line );
                break;
              }
              case 2: {
                directionSections[ 0 ].add( line );
                directionSections[ 1 ].add( line );
                break;
              }
              default: {
                throw new IllegalStateException(
                  "The Type " + sectionType + " for the Direction-section was invalid." );
              }
            }
          }
          break;
        }
        default: {
          throw new IllegalStateException( "In Parse State " + parseState + ": Encountered line " + line );
        }
      }
    }
  }

  public int getMetricsSets() {
    return header.getMetricsSets();
  }

  public AfmDirectionSection getDirectionSection( final int index ) {
    return directionSections[ index ];
  }

  public FontDataInputSource getInput() {
    return input;
  }

  public AfmHeader getHeader() {
    return header;
  }

  public String getFilename() {
    return filename;
  }

  public String getFamilyName() {
    return familyName;
  }

  public String getFontName() {
    return fontName;
  }

  public boolean isEmbeddable() {
    return embeddable;
  }


  public void dispose() {
    input.dispose();
  }
}
