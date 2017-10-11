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

package org.pentaho.reporting.engine.classic.wizard.ui.xul.util;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultDetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.Length;
import org.pentaho.ui.xul.XulEventSource;

import java.awt.*;
import java.beans.PropertyChangeListener;

public class XulDetailFieldDefinition implements XulEventSource {
  private DefaultDetailFieldDefinition parent;

  private ElementAlignment metaDataHorizontalAlignment;
  private String metaDataDisplayName;
  private String metaDataDataFormat;

  public XulDetailFieldDefinition( final DefaultDetailFieldDefinition parent,
                                   final DataSchema dataSchema ) {
    if ( parent == null ) {
      throw new NullPointerException();
    }
    this.parent = parent;


    final DataAttributes attributes = dataSchema.getAttributes( this.parent.getField() );
    if ( attributes != null ) {
      final DefaultDataAttributeContext dataAttributeContext = new DefaultDataAttributeContext();

      if ( parent.getHorizontalAlignment() == null ) {
        metaDataHorizontalAlignment = (ElementAlignment) attributes.getMetaAttribute
          ( MetaAttributeNames.Style.NAMESPACE, MetaAttributeNames.Style.HORIZONTAL_ALIGNMENT,
            ElementAlignment.class, dataAttributeContext );
      }

      if ( parent.getDisplayName() == null ) {
        metaDataDisplayName = (String) attributes.getMetaAttribute
          ( MetaAttributeNames.Formatting.NAMESPACE, MetaAttributeNames.Formatting.LABEL,
            String.class, dataAttributeContext );
      }

      if ( parent.getDataFormat() == null ) {
        metaDataDataFormat = (String) attributes.getMetaAttribute
          ( MetaAttributeNames.Formatting.NAMESPACE, MetaAttributeNames.Formatting.FORMAT,
            String.class, dataAttributeContext );
      }
    }
  }

  public Boolean getOnlyShowChangingValues() {
    return parent.getOnlyShowChangingValues();
  }

  public void setOnlyShowChangingValues( final Boolean onlyShowChangingValues ) {
    parent.setOnlyShowChangingValues( onlyShowChangingValues );
  }

  public String getField() {
    return parent.getField();
  }

  public void setField( final String field ) {
    parent.setField( field );
  }

  public String getNullString() {
    return parent.getNullString();
  }

  public void setNullString( final String nullString ) {
    parent.setNullString( nullString );
  }

  public String getDisplayName() {
    if ( metaDataDisplayName != null ) {
      return metaDataDisplayName;
    }
    return parent.getDisplayName();
  }

  public void setDisplayName( final String displayName ) {
    metaDataDisplayName = null;
    parent.setDisplayName( displayName );
  }

  public String getDataFormat() {
    if ( metaDataDataFormat != null ) {
      return metaDataDataFormat;
    }
    return parent.getDataFormat();
  }

  public void setDataFormat( final String dataFormat ) {
    metaDataDataFormat = null;
    parent.setDataFormat( dataFormat );
  }

  public Class getAggregationFunction() {
    return parent.getAggregationFunction();
  }

  public void setAggregationFunction( final Class aggregationFunction ) {
    parent.setAggregationFunction( aggregationFunction );
  }

  public Class getFieldTypeHint() {
    return parent.getFieldTypeHint();
  }

  public void setFieldTypeHint( final Class fieldTypeHint ) {
    parent.setFieldTypeHint( fieldTypeHint );
  }

  public Length getWidth() {
    return parent.getWidth();
  }

  public void setWidth( final Length width ) {
    parent.setWidth( width );
  }

  public ElementAlignment getHorizontalAlignment() {
    if ( metaDataHorizontalAlignment != null ) {
      return metaDataHorizontalAlignment;
    }
    return parent.getHorizontalAlignment();
  }

  public void setHorizontalAlignment( final ElementAlignment horizontalAlignment ) {
    metaDataHorizontalAlignment = null;
    parent.setHorizontalAlignment( horizontalAlignment );
  }

  public ElementAlignment getVerticalAlignment() {
    return parent.getVerticalAlignment();
  }

  public void setVerticalAlignment( final ElementAlignment verticalAlignment ) {
    parent.setVerticalAlignment( verticalAlignment );
  }

  public String getFontName() {
    return parent.getFontName();
  }

  public void setFontName( final String fontName ) {
    parent.setFontName( fontName );
  }

  public Boolean getFontBold() {
    return parent.getFontBold();
  }

  public void setFontBold( final Boolean fontBold ) {
    parent.setFontBold( fontBold );
  }

  public Boolean getFontItalic() {
    return parent.getFontItalic();
  }

  public void setFontItalic( final Boolean fontItalic ) {
    parent.setFontItalic( fontItalic );
  }

  public Boolean getFontUnderline() {
    return parent.getFontUnderline();
  }

  public void setFontUnderline( final Boolean fontUnderline ) {
    parent.setFontUnderline( fontUnderline );
  }

  public Boolean getFontStrikethrough() {
    return parent.getFontStrikethrough();
  }

  public void setFontStrikethrough( final Boolean fontStrikethrough ) {
    parent.setFontStrikethrough( fontStrikethrough );
  }

  public Integer getFontSize() {
    return parent.getFontSize();
  }

  public void setFontSize( final Integer fontSize ) {
    parent.setFontSize( fontSize );
  }

  public Color getFontColor() {
    return parent.getFontColor();
  }

  public void setFontColor( final Color fontColor ) {
    parent.setFontColor( fontColor );
  }

  public Color getBackgroundColor() {
    return parent.getBackgroundColor();
  }

  public void setBackgroundColor( final Color backgroundColor ) {
    parent.setBackgroundColor( backgroundColor );
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {

  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {

  }
}
