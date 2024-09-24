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

import org.pentaho.reporting.engine.classic.core.ElementAlignment;

public class DefaultGroupDefinition extends AbstractFieldDefinition implements GroupDefinition {
  private String groupName;
  private RootBandDefinition header;
  private RootBandDefinition footer;
  private String groupTotalsLabel;
  private GroupType groupType;
  private ElementAlignment totalsHorizontalAlignment;

  public DefaultGroupDefinition() {
    header = new DefaultRootBandDefinition();
    footer = new DefaultRootBandDefinition();
    groupType = GroupType.RELATIONAL;
  }

  public DefaultGroupDefinition( final GroupType groupType, final String field ) {
    this();
    this.groupType = groupType;
    setField( field );
  }

  public DefaultGroupDefinition( GroupDefinition src ) {
    this();

    this.setAggregationFunction( src.getAggregationFunction() );
    this.setDataFormat( src.getDataFormat() );
    this.setDisplayName( src.getDisplayName() );
    this.setField( src.getField() );
    this.setFieldTypeHint( src.getFieldTypeHint() );
    this.setGroupName( src.getGroupName() );
    this.setGroupTotalsLabel( src.getGroupTotalsLabel() );
    this.setGroupType( src.getGroupType() );
    this.setNullString( src.getNullString() );
    this.setWidth( src.getWidth() );
    this.setTotalsHorizontalAlignment( src.getTotalsHorizontalAlignment() );
  }

  public ElementAlignment getTotalsHorizontalAlignment() {
    return totalsHorizontalAlignment;
  }

  public void setTotalsHorizontalAlignment( final ElementAlignment alignment ) {
    this.totalsHorizontalAlignment = alignment;
  }

  public GroupType getGroupType() {
    return groupType;
  }

  public void setGroupType( final GroupType groupType ) {
    if ( groupType == null ) {
      throw new NullPointerException();
    }
    this.groupType = groupType;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName( final String groupName ) {
    this.groupName = groupName;
  }

  public RootBandDefinition getHeader() {
    return header;
  }

  public RootBandDefinition getFooter() {
    return footer;
  }

  public String getGroupTotalsLabel() {
    return groupTotalsLabel;
  }

  public void setGroupTotalsLabel( final String groupTotalsLabel ) {
    this.groupTotalsLabel = groupTotalsLabel;
  }

  public Object clone() throws CloneNotSupportedException {
    final DefaultGroupDefinition o = (DefaultGroupDefinition) super.clone();
    o.header = (RootBandDefinition) header.clone();
    o.footer = (RootBandDefinition) footer.clone();
    return o;
  }

}
