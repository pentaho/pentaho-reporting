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


package org.pentaho.reporting.designer.core.util;

import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;

public class GridSizeDialog extends CommonDialog {
  private class UnitChangeListener implements ListDataListener {
    public void intervalAdded( final ListDataEvent e ) {

    }

    public void intervalRemoved( final ListDataEvent e ) {

    }

    public void contentsChanged( final ListDataEvent e ) {
      final Unit unit1 = (Unit) getUnitModel().getSelectedItem();
      if ( unit1 == null ) {
        throw new IllegalStateException( "Unit cannot be set to null" );
      }
      setUnit( unit1 );
    }
  }

  private JSpinner gridSizeSpinner;
  private JSpinner gridDivisionSpinner;
  private ComboBoxModel unitModel;
  private Unit unit;
  private static final Double ZERO = new Double( 0 );
  private static final Double STEP_SIZE = new Double( 1 );

  public GridSizeDialog()
    throws HeadlessException {
    init();
  }

  public GridSizeDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public GridSizeDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  private ComboBoxModel createUnitModel() {
    final DefaultComboBoxModel model = new DefaultComboBoxModel( Unit.values() );
    model.setSelectedItem( Unit.POINTS );
    model.addListDataListener( new UnitChangeListener() );
    return model;
  }

  protected ComboBoxModel getUnitModel() {
    return unitModel;
  }

  protected void init() {
    unit = Unit.POINTS;

    setTitle( UtilMessages.getInstance().getString( "GridSizeDialog.Title" ) );
    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
    setModal( true );

    gridSizeSpinner = new JSpinner( new SpinnerNumberModel( ZERO, ZERO, null, STEP_SIZE ) );
    unitModel = createUnitModel();
    gridDivisionSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, Integer.MAX_VALUE, 1 ) );

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.GridSize";
  }

  protected JPanel createContentPane() {
    final JPanel tablesPane = new JPanel();
    tablesPane.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    gbc.anchor = GridBagConstraints.WEST;
    tablesPane.add( new JLabel( UtilMessages.getInstance().getString( "GridSizeDialog.GridSize" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( gridSizeSpinner, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    gbc.anchor = GridBagConstraints.WEST;
    tablesPane.add( new JLabel( UtilMessages.getInstance().getString( "GridSizeDialog.GridDivision" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( gridDivisionSpinner, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    gbc.anchor = GridBagConstraints.WEST;
    tablesPane.add( new JLabel( UtilMessages.getInstance().getString( "GridSizeDialog.GridUnit" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( new JComboBox( unitModel ), gbc );

    return tablesPane;
  }

  public boolean showDialog() {
    return performEdit();
  }

  public Unit getUnit() {
    return unit;
  }

  public void setUnit( final Unit newUnit ) {
    if ( newUnit == null ) {
      throw new NullPointerException();
    }
    final double gridSize = getGridSize();
    final int gridDivision = getGridDivisionSize();

    if ( newUnit != this.unitModel.getSelectedItem() ) {
      this.unitModel.setSelectedItem( newUnit );
    }
    this.unit = newUnit;
    setGridSize( gridSize );
    setGridDivisionSize( gridDivision );
  }

  public void setGridSize( final double gridSize ) {
    final Unit unit = getUnit();
    this.gridSizeSpinner.setValue( new Double( unit.convertFromPoints( gridSize ) ) );
  }

  public void setGridDivisionSize( final int gridDivisionSize ) {
    this.gridDivisionSpinner.setValue( gridDivisionSize );
  }

  public double getGridSize() {
    final Object value = this.gridSizeSpinner.getValue();
    if ( value instanceof Number ) {
      final Number n = (Number) value;
      return getUnit().convertToPoints( n.doubleValue() );
    }
    return 0;
  }

  public int getGridDivisionSize() {
    final Object value = this.gridDivisionSpinner.getValue();
    if ( value instanceof Number ) {
      final Number n = (Number) value;
      return n.intValue();
    }
    return 0;
  }
}
