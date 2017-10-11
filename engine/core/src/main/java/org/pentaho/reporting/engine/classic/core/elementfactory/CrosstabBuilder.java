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

package org.pentaho.reporting.engine.classic.core.elementfactory;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.filter.types.NumberFieldType;
import org.pentaho.reporting.engine.classic.core.filter.types.TextFieldType;
import org.pentaho.reporting.engine.classic.core.function.AggregationFunction;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.wizard.AutoGeneratorUtility;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CrosstabBuilder implements Cloneable {
  private ArrayList<CrosstabDimension> rows;
  private ArrayList<CrosstabDimension> columns;
  private ArrayList<String> others;
  private ArrayList<CrosstabDetail> details;
  private ContextAwareDataSchemaModel dataSchemaModel;
  private String groupNamePrefix;
  private Float minimumWidth;
  private Float minimumHeight;
  private Float maximumWidth;
  private Float maximumHeight;
  private Float prefWidth;
  private Float prefHeight;
  private Boolean allowMetaDataStyling;
  private Boolean allowMetaDataAttributes;

  @Deprecated
  public CrosstabBuilder( final DesignTimeDataSchemaModel dataSchemaModel ) {
    this( (ContextAwareDataSchemaModel) dataSchemaModel );
  }

  public CrosstabBuilder( final ContextAwareDataSchemaModel dataSchemaModel ) {
    rows = new ArrayList<CrosstabDimension>();
    columns = new ArrayList<CrosstabDimension>();
    others = new ArrayList<String>();
    details = new ArrayList<CrosstabDetail>();
    this.dataSchemaModel = dataSchemaModel;
    this.groupNamePrefix = "";
    this.minimumHeight = 20f;
    this.maximumHeight = 20f;
    this.maximumWidth = 80f;
    this.minimumWidth = 80f;
  }

  public Float getMinimumWidth() {
    return minimumWidth;
  }

  public void setMinimumWidth( final Float minimumWidth ) {
    this.minimumWidth = minimumWidth;
  }

  public Float getMinimumHeight() {
    return minimumHeight;
  }

  public void setMinimumHeight( final Float minimumHeight ) {
    this.minimumHeight = minimumHeight;
  }

  public Float getMaximumWidth() {
    return maximumWidth;
  }

  public void setMaximumWidth( final Float maximumWidth ) {
    this.maximumWidth = maximumWidth;
  }

  public Float getMaximumHeight() {
    return maximumHeight;
  }

  public void setMaximumHeight( final Float maximumHeight ) {
    this.maximumHeight = maximumHeight;
  }

  public Float getPrefWidth() {
    return prefWidth;
  }

  public void setPrefWidth( final Float prefWidth ) {
    this.prefWidth = prefWidth;
  }

  public Float getPrefHeight() {
    return prefHeight;
  }

  public void setPrefHeight( final Float prefHeight ) {
    this.prefHeight = prefHeight;
  }

  public Boolean getAllowMetaDataStyling() {
    return allowMetaDataStyling;
  }

  public void setAllowMetaDataStyling( final Boolean allowMetaDataStyling ) {
    this.allowMetaDataStyling = allowMetaDataStyling;
  }

  public Boolean getAllowMetaDataAttributes() {
    return allowMetaDataAttributes;
  }

  public void setAllowMetaDataAttributes( final Boolean allowMetaDataAttributes ) {
    this.allowMetaDataAttributes = allowMetaDataAttributes;
  }

  public String getGroupNamePrefix() {
    return groupNamePrefix;
  }

  public void setGroupNamePrefix( final String groupNamePrefix ) {
    this.groupNamePrefix = groupNamePrefix;
  }

  public void addOtherDimension( final String field ) {
    others.add( field );
  }

  public void addRowDimension( final CrosstabDimension dimension ) {
    rows.add( dimension );
  }

  public void addRowDimension( final String field ) {
    addRowDimension( new CrosstabDimension( field, field, false, "Summary" ) );
  }

  public void addRowDimension( final String field, final boolean addSummary ) {
    addRowDimension( new CrosstabDimension( field, field, addSummary, "Summary" ) );
  }

  public void addColumnDimension( final CrosstabDimension dimension ) {
    columns.add( dimension );
  }

  public void addColumnDimension( final String field ) {
    addColumnDimension( new CrosstabDimension( field, field, false, "Summary" ) );
  }

  public void addColumnDimension( final String field, final boolean addSummary ) {
    addColumnDimension( new CrosstabDimension( field, field, addSummary, "Summary" ) );
  }

  public void addDetails( final CrosstabDetail detail ) {
    details.add( detail );
  }

  public void addDetails( final String field, final Class<? extends AggregationFunction> aggregation ) {
    details.add( new CrosstabDetail( field, field, aggregation ) );
  }

  public List<CrosstabDimension> getRows() {
    return Collections.unmodifiableList( rows );
  }

  public List<CrosstabDimension> getColumns() {
    return Collections.unmodifiableList( columns );
  }

  public List<String> getOthers() {
    return Collections.unmodifiableList( others );
  }

  public List<CrosstabDetail> getDetails() {
    return Collections.unmodifiableList( details );
  }

  public MasterReport createReport() {
    final MasterReport report = new MasterReport();
    report.setRootGroup( create() );
    return report;
  }

  public CrosstabGroup create() {
    if ( columns.size() == 0 ) {
      throw new IllegalStateException();
    }
    if ( rows.size() == 0 ) {
      throw new IllegalStateException();
    }

    final CrosstabCellBody cellBody = createCellBody();

    GroupBody body = createColumnGroups( cellBody );
    body = createRowGroups( cellBody, body );
    body = createOtherGroups( body );

    return new CrosstabGroup( body );
  }

  protected CrosstabCellBody createCellBody() {
    final CrosstabCellBody cellBody = new CrosstabCellBody();
    cellBody.addElement( createDetailsCell( "details-cell", null, null ) );
    setupDetailsHeader( cellBody.getHeader() );
    return cellBody;
  }

  private GroupBody createOtherGroups( GroupBody body ) {
    for ( int other = others.size() - 1; other >= 0; other -= 1 ) {
      final String column = others.get( other );
      final CrosstabOtherGroup columnGroup = createOtherGroup( body, column );

      body = new CrosstabOtherGroupBody( columnGroup );
    }
    return body;
  }

  protected CrosstabOtherGroup createOtherGroup( final GroupBody body, final String column ) {
    final CrosstabOtherGroup columnGroup = new CrosstabOtherGroup( body );
    columnGroup.setField( column );
    columnGroup.getHeader().addElement( createFieldItem( column ) );
    return columnGroup;
  }

  private GroupBody createRowGroups( final CrosstabCellBody cellBody, GroupBody body ) {
    for ( int row = rows.size() - 1; row >= 0; row -= 1 ) {
      final CrosstabDimension rowDimension = rows.get( row );
      final CrosstabRowGroup rowGroup = createRowGroup( cellBody, body, rowDimension );
      body = new CrosstabRowGroupBody( rowGroup );
    }
    return body;
  }

  protected CrosstabRowGroup createRowGroup( final CrosstabCellBody cellBody, final GroupBody innerBody,
      final CrosstabDimension rowDimension ) {
    final CrosstabRowGroup rowGroup = new CrosstabRowGroup( innerBody );
    rowGroup.setName( computeGroupName( rowDimension ) );
    rowGroup.setField( rowDimension.getField() );
    rowGroup.getTitleHeader().getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, -100f );
    rowGroup.getTitleHeader().addElement( createLabel( rowDimension.getTitle(), rowDimension.getField() ) );
    rowGroup.getHeader().getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, -100f );
    rowGroup.getHeader().addElement( createFieldItem( rowDimension.getField() ) );
    rowGroup.getSummaryHeader().getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, -100f );
    rowGroup.getSummaryHeader().addElement( createLabel( rowDimension.getSummaryTitle(), rowDimension.getField() ) );
    rowGroup.setPrintSummary( rowDimension.isPrintSummary() );

    createSummaryCells( cellBody, rowDimension );
    return rowGroup;
  }

  protected void createSummaryCells( final CrosstabCellBody cellBody, final CrosstabDimension rowDimension ) {
    if ( rowDimension.isPrintSummary() ) {
      cellBody.addElement( createDetailsCell( rowDimension.getField(), rowDimension.getField(), null ) );

      for ( int col = columns.size() - 1; col >= 0; col -= 1 ) {
        final CrosstabDimension column = columns.get( col );
        if ( column.isPrintSummary() ) {
          cellBody.addElement( createDetailsCell( column.getField() + "," + rowDimension.getField(), rowDimension
              .getField(), column.getField() ) );
        }
      }
    }
  }

  protected void createColumnSummaryCells( final CrosstabCellBody cellBody, final CrosstabDimension column ) {
    if ( column.isPrintSummary() ) {
      cellBody.addElement( createDetailsCell( column.getField(), null, column.getField() ) );
    }
  }

  protected String computeGroupName( final CrosstabDimension rowDimension ) {
    return groupNamePrefix + rowDimension.getField();
  }

  private GroupBody createColumnGroups( final CrosstabCellBody cellBody ) {
    GroupBody body = cellBody;
    for ( int col = columns.size() - 1; col >= 0; col -= 1 ) {
      final CrosstabDimension column = columns.get( col );
      final CrosstabColumnGroup columnGroup = createColumnGroup( cellBody, body, column );
      body = new CrosstabColumnGroupBody( columnGroup );
    }
    return body;
  }

  protected CrosstabColumnGroup createColumnGroup( final CrosstabCellBody cellBody, final GroupBody body,
      final CrosstabDimension column ) {
    final CrosstabColumnGroup columnGroup = new CrosstabColumnGroup( body );
    columnGroup.setName( computeGroupName( column ) );
    columnGroup.setField( column.getField() );
    columnGroup.getTitleHeader().getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, -100f );
    columnGroup.getTitleHeader().addElement( createLabel( column.getTitle(), column.getField() ) );
    columnGroup.getHeader().getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, -100f );
    columnGroup.getHeader().addElement( createFieldItem( column.getField() ) );
    columnGroup.getSummaryHeader().getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, -100f );
    columnGroup.getSummaryHeader().addElement( createLabel( column.getSummaryTitle(), column.getField() ) );
    columnGroup.setPrintSummary( column.isPrintSummary() );

    createColumnSummaryCells( cellBody, column );
    return columnGroup;
  }

  protected CrosstabCell createDetailsCell( final String name, final String rowDim, final String colDim ) {
    final CrosstabCell cell = createDetailsCell();
    cell.setColumnField( colDim );
    cell.setName( name );
    return cell;
  }

  protected CrosstabCell createDetailsCell() {
    final CrosstabCell cell = new CrosstabCell();
    cell.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, -100f );
    cell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_ROW );
    for ( int i = 0; i < details.size(); i += 1 ) {
      final CrosstabDetail crosstabDetail = details.get( i );
      cell.addElement( createDetailCellContent( crosstabDetail ) );
    }
    return cell;
  }

  protected Element createDetailCellContent( final CrosstabDetail crosstabDetail ) {
    return createFieldItem( crosstabDetail.getField(), crosstabDetail.getAggregation(), true );
  }

  protected void setupDetailsHeader( final DetailsHeader cell ) {
    cell.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, -100f );
    cell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_ROW );
    for ( int i = 0; i < details.size(); i += 1 ) {
      final CrosstabDetail crosstabDetail = details.get( i );
      String title = crosstabDetail.getTitle();
      if ( StringUtils.isEmpty( title ) ) {
        title = crosstabDetail.getField();
      }
      cell.addElement( createLabel( title, crosstabDetail.getField(), true ) );
    }
  }

  protected Element createFieldItem( final String text ) {
    return createFieldItem( text, null, false );
  }

  protected Element createFieldItem( final String fieldName,
      final Class<? extends AggregationFunction> aggregationType, final boolean split ) {
    final ElementType targetType;
    if ( dataSchemaModel != null ) {
      final DataAttributeContext context = dataSchemaModel.getDataAttributeContext();
      final DataAttributes attributes = dataSchemaModel.getDataSchema().getAttributes( fieldName );
      targetType = AutoGeneratorUtility.createFieldType( attributes, context );
    } else {
      targetType = TextFieldType.INSTANCE;
    }

    final Element element = new Element();
    element.setElementType( targetType );
    element.getElementType().configureDesignTimeDefaults( element, Locale.getDefault() );

    if ( targetType instanceof NumberFieldType ) {
      element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING, "0.00;-0.00" );
    }

    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, fieldName );
    element.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, split( split, minimumWidth ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, minimumHeight );
    element.getStyle().setStyleProperty( ElementStyleKeys.WIDTH, split( split, prefWidth ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.HEIGHT, prefHeight );
    element.getStyle().setStyleProperty( ElementStyleKeys.MAX_WIDTH, split( split, maximumWidth ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.MAX_HEIGHT, maximumHeight );
    element.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.AGGREGATION_TYPE, aggregationType );
    element.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING,
        allowMetaDataStyling );
    return element;
  }

  protected Element createLabel( final String text, final String labelFor ) {
    return createLabel( text, labelFor, false );
  }

  protected Element createLabel( final String text, final String labelFor, final boolean splitArea ) {
    final Element element = new Element();
    element.setElementType( LabelType.INSTANCE );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text );
    element.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, split( splitArea, minimumWidth ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, minimumHeight );
    element.getStyle().setStyleProperty( ElementStyleKeys.WIDTH, split( splitArea, prefWidth ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.HEIGHT, prefHeight );
    element.getStyle().setStyleProperty( ElementStyleKeys.MAX_WIDTH, split( splitArea, maximumWidth ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.MAX_HEIGHT, maximumHeight );
    element.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING,
        allowMetaDataStyling );
    if ( StringUtils.isEmpty( labelFor ) ) {
      element.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES,
          allowMetaDataAttributes );
    } else {
      element.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES, true );
    }
    element.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR, labelFor );
    return element;
  }

  private Float split( final boolean split, final Float value ) {
    if ( split == false ) {
      return value;
    }

    if ( value == null ) {
      return null;
    }
    final float f = value;
    return f / Math.max( 1, details.size() );
  }

  public CrosstabBuilder clone() {
    try {
      CrosstabBuilder clone = (CrosstabBuilder) super.clone();
      clone.columns = (ArrayList<CrosstabDimension>) columns.clone();
      clone.rows = (ArrayList<CrosstabDimension>) rows.clone();
      clone.others = (ArrayList<String>) others.clone();
      clone.details = (ArrayList<CrosstabDetail>) details.clone();
      return clone;
    } catch ( final CloneNotSupportedException e ) {
      throw new IllegalStateException( e );
    }
  }

  public CrosstabBuilder clearDimensions() {
    CrosstabBuilder clone = clone();
    clone.columns.clear();
    clone.rows.clear();
    clone.others.clear();
    clone.details.clear();
    return clone;
  }
}
