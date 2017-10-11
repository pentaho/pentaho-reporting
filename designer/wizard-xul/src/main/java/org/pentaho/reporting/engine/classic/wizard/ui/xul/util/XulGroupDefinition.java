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
import org.pentaho.reporting.engine.classic.wizard.model.DefaultGroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupType;
import org.pentaho.reporting.engine.classic.wizard.model.Length;
import org.pentaho.reporting.engine.classic.wizard.model.RootBandDefinition;
import org.pentaho.ui.xul.XulEventSource;

import java.beans.PropertyChangeListener;

public class XulGroupDefinition implements XulEventSource {
  private DefaultGroupDefinition groupDefinition;
  private String metaDataDisplayName;
  private ElementAlignment metaDataHorizontalAlignment;

  public XulGroupDefinition( final DefaultGroupDefinition groupDefinition,
                             final DataSchema dataSchema ) {
    if ( groupDefinition == null ) {
      throw new NullPointerException();
    }
    this.groupDefinition = groupDefinition;

    final DataAttributes attributes = dataSchema.getAttributes( this.groupDefinition.getField() );
    if ( attributes != null ) {
      final DefaultDataAttributeContext dataAttributeContext = new DefaultDataAttributeContext();

      if ( groupDefinition.getHorizontalAlignment() == null ) {
        metaDataHorizontalAlignment = (ElementAlignment) attributes.getMetaAttribute
          ( MetaAttributeNames.Style.NAMESPACE, MetaAttributeNames.Style.HORIZONTAL_ALIGNMENT,
            ElementAlignment.class, dataAttributeContext );
      }

      if ( groupDefinition.getDisplayName() == null ) {
        metaDataDisplayName = (String) attributes.getMetaAttribute
          ( MetaAttributeNames.Formatting.NAMESPACE, MetaAttributeNames.Formatting.LABEL,
            String.class, dataAttributeContext );
        setDisplayName( metaDataDisplayName );
      }
    }
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {

  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {

  }

  public ElementAlignment getTotalsHorizontalAlignment() {
    if ( metaDataHorizontalAlignment != null && groupDefinition.getTotalsHorizontalAlignment() == null ) {
      return metaDataHorizontalAlignment;
    }
    return groupDefinition.getTotalsHorizontalAlignment();
  }

  public void setTotalsHorizontalAlignment( final ElementAlignment alignment ) {
    metaDataHorizontalAlignment = null;
    this.groupDefinition.setTotalsHorizontalAlignment( alignment );
  }

  public GroupType getGroupType() {
    return groupDefinition.getGroupType();
  }

  public void setGroupType( final GroupType groupType ) {
    groupDefinition.setGroupType( groupType );
  }

  public String getGroupName() {
    return groupDefinition.getGroupName();
  }

  public void setGroupName( final String groupName ) {
    groupDefinition.setGroupName( groupName );
  }

  public RootBandDefinition getHeader() {
    return groupDefinition.getHeader();
  }

  public RootBandDefinition getFooter() {
    return groupDefinition.getFooter();
  }

  public String getGroupTotalsLabel() {
    return groupDefinition.getGroupTotalsLabel();
  }

  public void setGroupTotalsLabel( final String groupTotalsLabel ) {
    groupDefinition.setGroupTotalsLabel( groupTotalsLabel );
  }

  public String getField() {
    return groupDefinition.getField();
  }

  public void setField( final String field ) {
    groupDefinition.setField( field );
  }

  public String getNullString() {
    return groupDefinition.getNullString();
  }

  public void setNullString( final String nullString ) {
    groupDefinition.setNullString( nullString );
  }

  public String getDisplayName() {
    if ( metaDataDisplayName != null ) {
      return metaDataDisplayName;
    }
    return groupDefinition.getDisplayName();
  }

  public void setDisplayName( final String displayName ) {
    metaDataDisplayName = null;
    groupDefinition.setDisplayName( displayName );
  }

  public String getDataFormat() {
    return groupDefinition.getDataFormat();
  }

  public void setDataFormat( final String dataFormat ) {
    groupDefinition.setDataFormat( dataFormat );
  }

  public Class getAggregationFunction() {
    return groupDefinition.getAggregationFunction();
  }

  public void setAggregationFunction( final Class aggregationFunction ) {
    groupDefinition.setAggregationFunction( aggregationFunction );
  }

  public Class getFieldTypeHint() {
    return groupDefinition.getFieldTypeHint();
  }

  public void setFieldTypeHint( final Class fieldTypeHint ) {
    groupDefinition.setFieldTypeHint( fieldTypeHint );
  }

  public Length getWidth() {
    return groupDefinition.getWidth();
  }

  public void setWidth( final Length width ) {
    groupDefinition.setWidth( width );
  }
}
