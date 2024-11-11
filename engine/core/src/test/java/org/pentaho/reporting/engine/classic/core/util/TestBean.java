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
