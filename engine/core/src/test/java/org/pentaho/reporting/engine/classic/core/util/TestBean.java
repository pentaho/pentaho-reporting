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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Needed for the BeanUtilityTest class.
 */
@SuppressWarnings( "UnusedDeclaration" )
public class TestBean {
  private String simpleString;
  private int simpleInt;
  private boolean simpleBool;
  private double simpleDouble;
  private Color simpleColor;
  private ArrayList<String> fullyIndexed;
  private String[] arrayOnly;
  private ArrayList<String> indexOnly;

  public TestBean() {
    indexOnly = new ArrayList<String>();
    fullyIndexed = new ArrayList<String>();
  }

  public String[] getArrayOnly() {
    return arrayOnly;
  }

  public void setArrayOnly( final String[] arrayOnly ) {
    this.arrayOnly = arrayOnly;
  }

  public String[] getFullyIndexed() {
    return fullyIndexed.toArray( new String[fullyIndexed.size()] );
  }

  public void setFullyIndexed( final String[] fullyIndexed ) {
    this.fullyIndexed.clear();
    if ( fullyIndexed != null ) {
      this.fullyIndexed.addAll( Arrays.asList( fullyIndexed ) );
    }
  }

  public String getFullyIndexed( final int idx ) {
    return fullyIndexed.get( idx );
  }

  public void setFullyIndexed( final int idx, final String indexOnly ) {
    if ( this.fullyIndexed.size() == idx ) {
      this.fullyIndexed.add( indexOnly );
    } else {
      this.fullyIndexed.set( idx, indexOnly );
    }
  }

  public String getIndexOnly( final int idx ) {
    return indexOnly.get( idx );
  }

  public void setIndexOnly( final int idx, final String indexOnly ) {
    if ( this.indexOnly.size() == idx ) {
      this.indexOnly.add( indexOnly );
    } else {
      this.indexOnly.set( idx, indexOnly );
    }
  }

  public boolean isSimpleBool() {
    return simpleBool;
  }

  public void setSimpleBool( final boolean simpleBool ) {
    this.simpleBool = simpleBool;
  }

  public Color getSimpleColor() {
    return simpleColor;
  }

  public void setSimpleColor( final Color simpleColor ) {
    this.simpleColor = simpleColor;
  }

  public double getSimpleDouble() {
    return simpleDouble;
  }

  public void setSimpleDouble( final double simpleDouble ) {
    this.simpleDouble = simpleDouble;
  }

  public int getSimpleInt() {
    return simpleInt;
  }

  public void setSimpleInt( final int simpleInt ) {
    this.simpleInt = simpleInt;
  }

  public String getSimpleString() {
    return simpleString;
  }

  public void setSimpleString( final String simpleString ) {
    this.simpleString = simpleString;
  }
}
