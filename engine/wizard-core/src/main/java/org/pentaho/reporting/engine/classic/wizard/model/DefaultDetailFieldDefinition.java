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

public class DefaultDetailFieldDefinition extends AbstractFieldDefinition implements DetailFieldDefinition {
  private Boolean onlyShowChangingValues;

  public DefaultDetailFieldDefinition() {
  }

  public DefaultDetailFieldDefinition( final String field ) {
    super( field );
  }

  public DefaultDetailFieldDefinition( final DetailFieldDefinition src ) {
    this.setAggregationFunction( src.getAggregationFunction() );
    this.setBackgroundColor( src.getBackgroundColor() );
    this.setDataFormat( src.getDataFormat() );
    this.setDisplayName( src.getDisplayName() );
    this.setField( src.getField() );
    this.setFieldTypeHint( src.getFieldTypeHint() );
    this.setFontBold( src.getFontBold() );
    this.setFontColor( src.getFontColor() );
    this.setFontItalic( src.getFontItalic() );
    this.setFontName( src.getFontName() );
    this.setFontSize( src.getFontSize() );
    this.setFontStrikethrough( src.getFontStrikethrough() );
    this.setFontUnderline( src.getFontUnderline() );
    this.setHorizontalAlignment( src.getHorizontalAlignment() );
    this.setNullString( src.getNullString() );
    this.setOnlyShowChangingValues( src.getOnlyShowChangingValues() );
    this.setVerticalAlignment( src.getVerticalAlignment() );
    this.setWidth( src.getWidth() );
  }

  public Boolean getOnlyShowChangingValues() {
    return onlyShowChangingValues;
  }

  public void setOnlyShowChangingValues( final Boolean onlyShowChangingValues ) {
    this.onlyShowChangingValues = onlyShowChangingValues;
  }

}
