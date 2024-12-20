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


package org.pentaho.reporting.designer.core.editor.crosstab;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTable;
import org.pentaho.reporting.designer.core.util.table.GroupedMetaTableModel;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.CrosstabDetailMode;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabBuilder;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabGroupType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

import javax.swing.*;
import java.awt.*;

public class CrosstabOptionsPane extends JComponent {
  private final ElementMetaDataTable metaDataTable;
  private CrosstabOptionsTableModel tableModel;

  public CrosstabOptionsPane() {
    tableModel = createOptions();
    metaDataTable = new ElementMetaDataTable();
    metaDataTable.setModel( new GroupedMetaTableModel( tableModel ) );

    setLayout( new BorderLayout() );
    add( new JScrollPane( metaDataTable ) );
  }

  private CrosstabOptionsTableModel createOptions() {
    final CrosstabOptionsTableModel crosstabOptionsTableModel = new CrosstabOptionsTableModel();
    crosstabOptionsTableModel.addAttributeOption( CrosstabGroupType.INSTANCE,
      AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.DETAIL_MODE, CrosstabDetailMode.last );
    crosstabOptionsTableModel.addAttributeOption( CrosstabGroupType.INSTANCE,
      AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.NORMALIZATION_MODE, null );
    crosstabOptionsTableModel.addAttributeOption( CrosstabGroupType.INSTANCE,
      AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.PRINT_DETAIL_HEADER, null );
    crosstabOptionsTableModel.addAttributeOption( CrosstabGroupType.INSTANCE,
      AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.PADDING_FIELDS, null );
    crosstabOptionsTableModel.addAttributeOption( AutoLayoutBoxType.INSTANCE,
      AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING, null );
    crosstabOptionsTableModel.addAttributeOption( AutoLayoutBoxType.INSTANCE,
      AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES, null );
    crosstabOptionsTableModel.addStyleOption( AutoLayoutBoxType.INSTANCE,
      ElementStyleKeys.MIN_WIDTH, -100f );
    crosstabOptionsTableModel.addStyleOption( AutoLayoutBoxType.INSTANCE,
      ElementStyleKeys.MIN_HEIGHT, -100f );
    crosstabOptionsTableModel.addStyleOption( AutoLayoutBoxType.INSTANCE,
      ElementStyleKeys.WIDTH, null );
    crosstabOptionsTableModel.addStyleOption( AutoLayoutBoxType.INSTANCE,
      ElementStyleKeys.HEIGHT, null );
    crosstabOptionsTableModel.addStyleOption( AutoLayoutBoxType.INSTANCE,
      ElementStyleKeys.MAX_WIDTH, null );
    crosstabOptionsTableModel.addStyleOption( AutoLayoutBoxType.INSTANCE,
      ElementStyleKeys.MAX_HEIGHT, null );
    return crosstabOptionsTableModel;
  }

  public Float getMinWidth() {
    return (Float) tableModel.getStyleOption( AutoLayoutBoxType.INSTANCE, ElementStyleKeys.MIN_WIDTH );
  }

  public void setMinWidth( final Float minWidth ) {
    tableModel.setStyleOption( AutoLayoutBoxType.INSTANCE, ElementStyleKeys.MIN_WIDTH, minWidth );
  }

  public Float getMinHeight() {
    return (Float) tableModel.getStyleOption( AutoLayoutBoxType.INSTANCE, ElementStyleKeys.MIN_HEIGHT );
  }

  public void setMinHeight( final Float minHeight ) {
    tableModel.setStyleOption( AutoLayoutBoxType.INSTANCE, ElementStyleKeys.MIN_HEIGHT, minHeight );
  }

  public Float getPrefWidth() {
    return (Float) tableModel.getStyleOption( AutoLayoutBoxType.INSTANCE, ElementStyleKeys.WIDTH );
  }

  public void setPrefWidth( final Float width ) {
    tableModel.setStyleOption( AutoLayoutBoxType.INSTANCE, ElementStyleKeys.WIDTH, width );
  }

  public Float getPrefHeight() {
    return (Float) tableModel.getStyleOption( AutoLayoutBoxType.INSTANCE, ElementStyleKeys.HEIGHT );
  }

  public void setPrefHeight( final Float height ) {
    tableModel.setStyleOption( AutoLayoutBoxType.INSTANCE, ElementStyleKeys.HEIGHT, height );
  }

  public Float getMaxWidth() {
    return (Float) tableModel.getStyleOption( AutoLayoutBoxType.INSTANCE, ElementStyleKeys.MAX_WIDTH );
  }

  public void setMaxWidth( final Float maxWidth ) {
    tableModel.setStyleOption( AutoLayoutBoxType.INSTANCE, ElementStyleKeys.MAX_WIDTH, maxWidth );
  }

  public Float getMaxHeight() {
    return (Float) tableModel.getStyleOption( AutoLayoutBoxType.INSTANCE, ElementStyleKeys.MAX_HEIGHT );
  }

  public void setMaxHeight( final Float maxHeight ) {
    tableModel.setStyleOption( AutoLayoutBoxType.INSTANCE, ElementStyleKeys.MAX_HEIGHT, maxHeight );
  }

  public Boolean getAllowMetaDataStyling() {
    return (Boolean) tableModel.getAttributeOption
      ( AutoLayoutBoxType.INSTANCE, AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING );
  }

  public void setAllowMetaDataStyling( final Boolean allowMetaDataStyling ) {
    tableModel.setAttributeOption( AutoLayoutBoxType.INSTANCE,
      AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING, allowMetaDataStyling );
  }

  public Boolean getAllowMetaDataAttributes() {
    return (Boolean) tableModel.getAttributeOption
      ( AutoLayoutBoxType.INSTANCE, AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES );
  }

  public void setAllowMetaDataAttributes( final Boolean allowMetaDataAttributes ) {
    tableModel.setAttributeOption( AutoLayoutBoxType.INSTANCE,
      AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES, allowMetaDataAttributes );
  }

  public void setValuesFromGroup( final CrosstabGroup crosstabGroup ) {
    tableModel.copyFrom( crosstabGroup );
  }

  public void setValuesOnGroup( final CrosstabGroup crosstabGroup ) {
    tableModel.copyInto( crosstabGroup );
  }

  public void configureCrosstabBuilder( final CrosstabBuilder builder ) {
    builder.setMinimumWidth( getMinWidth() );
    builder.setMinimumHeight( getMinHeight() );
    builder.setPrefWidth( getPrefWidth() );
    builder.setPrefHeight( getPrefHeight() );
    builder.setMaximumWidth( getMaxWidth() );
    builder.setMaximumHeight( getMaxHeight() );
    builder.setAllowMetaDataAttributes( getAllowMetaDataAttributes() );
    builder.setAllowMetaDataStyling( getAllowMetaDataStyling() );
  }

  public void configureFromCrosstabBuilder( final CrosstabBuilder builder ) {
    if ( builder.getMaximumHeight() != null ) {
      setMaxHeight( builder.getMaximumHeight() );
    }
    if ( builder.getMaximumWidth() != null ) {
      setMaxWidth( builder.getMaximumWidth() );
    }
    if ( builder.getPrefHeight() != null ) {
      setPrefHeight( builder.getPrefHeight() );
    }
    if ( builder.getPrefWidth() != null ) {
      setPrefWidth( builder.getPrefWidth() );
    }
    if ( builder.getMinimumHeight() != null ) {
      setMinHeight( builder.getMinimumHeight() );
    }
    if ( builder.getMinimumWidth() != null ) {
      setMinWidth( builder.getMinimumWidth() );
    }
    if ( builder.getAllowMetaDataAttributes() != null ) {
      setAllowMetaDataAttributes( builder.getAllowMetaDataAttributes() );
    }
    if ( builder.getAllowMetaDataStyling() != null ) {
      setAllowMetaDataStyling( builder.getAllowMetaDataStyling() );
    }
  }

  public void setReportDesignerContext( final ReportDesignerContext newContext ) {
    metaDataTable.setReportDesignerContext( newContext );
  }

  public ReportDesignerContext getReportDesignerContext() {
    return metaDataTable.getReportDesignerContext();
  }
}
