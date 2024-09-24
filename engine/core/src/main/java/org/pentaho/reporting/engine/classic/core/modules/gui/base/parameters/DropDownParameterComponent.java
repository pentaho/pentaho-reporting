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

package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.parameters.ListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;

public class DropDownParameterComponent extends JComboBox implements ParameterComponent {
  private class ComboBoxUpdateHandler implements ChangeListener {
    private ComboBoxUpdateHandler() {
    }

    public void stateChanged( final ChangeEvent e ) {
      try {
        initialize();
      } catch ( ReportDataFactoryException rdfe ) {
        throw new IndexOutOfBoundsException( "Failed: " + rdfe.getMessage() );
      }
    }
  }

  private class SingleValueListParameterHandler implements ActionListener {
    private String key;

    public SingleValueListParameterHandler( final String key ) {
      this.key = key;
    }

    public void actionPerformed( final ActionEvent e ) {
      if ( adjustingToExternalInput ) {
        return;
      }

      final Object theSource = e.getSource();
      if ( theSource instanceof JComboBox ) {
        final JComboBox theComboBox = (JComboBox) theSource;
        final KeyedComboBoxModel theModel = (KeyedComboBoxModel) theComboBox.getModel();
        updateContext.setParameterValue( key, theModel.getSelectedKey() );
      }
    }

  }

  private ListParameter listParameter;
  private ParameterUpdateContext updateContext;
  private ParameterContext parameterContext;
  private boolean adjustingToExternalInput;

  public DropDownParameterComponent( final ListParameter listParameter, final ParameterUpdateContext updateContext,
      final ParameterContext parameterContext ) {
    if ( listParameter == null ) {
      throw new NullPointerException();
    }
    if ( updateContext == null ) {
      throw new NullPointerException();
    }
    if ( parameterContext == null ) {
      throw new NullPointerException();
    }

    this.listParameter = listParameter;
    this.updateContext = updateContext;
    this.parameterContext = parameterContext;

    addActionListener( new SingleValueListParameterHandler( listParameter.getName() ) );
    updateContext.addChangeListener( new ComboBoxUpdateHandler() );
  }

  public void initialize() throws ReportDataFactoryException {
    adjustingToExternalInput = true;
    try {
      final KeyedComboBoxModel<Object, Object> keyedComboBoxModel =
          DefaultParameterComponentFactory.createModel( listParameter, parameterContext );
      final Object value = updateContext.getParameterValue( listParameter.getName() );
      setSelectedValue( value, keyedComboBoxModel );

      setModel( keyedComboBoxModel );
    } finally {
      adjustingToExternalInput = false;
    }
  }

  private void setSelectedValue( final Object key, final KeyedComboBoxModel<Object, Object> model ) {
    final int size = model.getSize();
    for ( int i = 0; i < size; i++ ) {
      final Object value = model.getKeyAt( i );
      if ( key == value ) {
        model.setSelectedKey( value );
        return;
      }
      if ( key != null && key.equals( value ) ) {
        model.setSelectedKey( value );
        return;
      }
      if ( key instanceof Number && value instanceof Number ) {
        final BigDecimal bdK = new BigDecimal( key.toString() );
        final BigDecimal bdV = new BigDecimal( value.toString() );
        if ( bdK.compareTo( bdV ) == 0 ) {
          model.setSelectedKey( value );
          return;
        }
      }
      if ( key instanceof Date && value instanceof Date ) {
        final Date d1 = (Date) key;
        final Date d2 = (Date) value;
        if ( d1.getTime() == d2.getTime() ) {
          model.setSelectedKey( value );
          return;
        }
      }
    }
    model.setSelectedKey( null );
  }

  public JComponent getUIComponent() {
    return this;
  }
}
