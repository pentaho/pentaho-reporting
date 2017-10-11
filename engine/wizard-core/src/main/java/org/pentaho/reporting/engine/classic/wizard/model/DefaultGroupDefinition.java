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
