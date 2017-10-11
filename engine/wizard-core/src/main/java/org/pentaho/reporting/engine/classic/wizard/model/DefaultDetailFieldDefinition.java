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
