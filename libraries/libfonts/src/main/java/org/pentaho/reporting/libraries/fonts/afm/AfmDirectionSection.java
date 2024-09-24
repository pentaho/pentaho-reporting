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
 * Creation-Date: 22.07.2007, 15:41:29
 *
 * @author Thomas Morgner
 */
public class AfmDirectionSection {
  private static final String START_LINE = "StartDirection ";
  private static final String UNDERLINE_POSITION = "UnderlinePosition ";
  private static final String UNDERLINE_THICKNESS = "UnderlineThickness ";
  private static final String ITALIC_ANGLE = "ItalicAngle ";
  private static final String CHARWIDTH = "CharWidth ";
  private static final String IS_FIXED_PITCH = "IsFixedPitch ";

  private double underlinePosition;
  private double underlineThickness;
  private double italicAngle;
  private double[] charWidth;
  private boolean fixedPitch;

  public AfmDirectionSection() throws IOException {
  }

  public void add( final String line ) throws IOException {
    if ( line.startsWith( UNDERLINE_POSITION ) ) {
      underlinePosition = AfmParseUtilities.parseDouble( UNDERLINE_POSITION, line );
    } else if ( line.startsWith( UNDERLINE_THICKNESS ) ) {
      underlineThickness = AfmParseUtilities.parseDouble( UNDERLINE_THICKNESS, line );
    } else if ( line.startsWith( ITALIC_ANGLE ) ) {
      italicAngle = AfmParseUtilities.parseDouble( ITALIC_ANGLE, line );
    } else if ( line.startsWith( CHARWIDTH ) ) {
      charWidth = AfmParseUtilities.parseDoubleArray( line, 2 );
    } else if ( line.startsWith( IS_FIXED_PITCH ) ) {
      final String boolText = line.substring( IS_FIXED_PITCH.length() );
      fixedPitch = "true".equalsIgnoreCase( boolText );
    }
  }

  public double getUnderlinePosition() {
    return underlinePosition;
  }

  public double getUnderlineThickness() {
    return underlineThickness;
  }

  public double getItalicAngle() {
    return italicAngle;
  }

  public double[] getCharWidth() {
    return charWidth;
  }

  public boolean isFixedPitch() {
    return fixedPitch;
  }
}
