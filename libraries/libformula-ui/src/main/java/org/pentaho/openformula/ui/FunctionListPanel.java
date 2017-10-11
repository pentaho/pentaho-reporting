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

package org.pentaho.openformula.ui;

import org.pentaho.openformula.ui.util.FunctionCategoryCellRenderer;
import org.pentaho.openformula.ui.util.FunctionDescriptionCellRenderer;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.function.FunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class FunctionListPanel extends JPanel {
  private class CategorySelectionAction implements ActionListener {
    protected CategorySelectionAction() {
    }

    public void actionPerformed( final ActionEvent e ) {
      final JList list = getFormulaList();
      list.setListData( getDescriptionsForCategory( getSelectedCategoryName() ) );
      list.invalidate();
    }
  }

  private class FormulaListMouseHandler extends MouseAdapter {
    protected FormulaListMouseHandler() {
    }

    public void mouseClicked( final MouseEvent e ) {
      if ( e.getClickCount() > 1 ) {
        setSelectedValue( (FunctionDescription) getFormulaList().getSelectedValue() );
        fireActionPerformed();
      }
    }

  }

  private class FormulaListSelectionHandler implements ListSelectionListener {
    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final ListSelectionEvent e ) {
      setSelectedValue( (FunctionDescription) getFormulaList().getSelectedValue() );
    }
  }

  private JList formulaList;
  private JComboBox categoryComboBox;
  private FormulaContext formulaContext;
  private FunctionDescription selectedValue;
  private static final FunctionDescription[] EMPTY_DESCRIPTIONS = new FunctionDescription[ 0 ];

  public FunctionListPanel() {
    setLayout( new GridBagLayout() );
    setBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 5 ) );

    categoryComboBox = new JComboBox();
    categoryComboBox.addActionListener( new CategorySelectionAction() );
    categoryComboBox.setRenderer( new FunctionCategoryCellRenderer() );

    formulaList = new JList();
    formulaList.setCellRenderer( new FunctionDescriptionCellRenderer() );
    formulaList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    formulaList.addMouseListener( new FormulaListMouseHandler() );
    formulaList.getSelectionModel().addListSelectionListener( new FormulaListSelectionHandler() );

    add( new JLabel( Messages.getInstance().getString( "FunctionListPanel.Category" ) ),
      new GridBagConstraints( 0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH,
        GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
    add( categoryComboBox,
      new GridBagConstraints( 0, 1, 1, 1, 0, 0, GridBagConstraints.NORTH,
        GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 5, 0 ), 0, 0 ) );


    final JScrollPane formulaListScrollPane = new JScrollPane( formulaList );
    // formulaListScrollPane.setPreferredSize(new Dimension(120, 100));

    add( new JLabel( Messages.getInstance().getString( "FunctionListPanel.Function" ) ),
      new GridBagConstraints( 0, 2, 1, 1, 0, 0, GridBagConstraints.NORTH,
        GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
    add( formulaListScrollPane,
      new GridBagConstraints( 0, 3, 1, 1, 1, 1, GridBagConstraints.NORTH,
        GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
  }

  public void setFormulaContext( final FormulaContext formulaContext ) {
    final FormulaContext old = this.formulaContext;
    this.formulaContext = formulaContext;
    if ( formulaContext != null && formulaContext != old ) {
      final DefaultComboBoxModel aModel = new DefaultComboBoxModel();
      aModel.addElement( null );

      final FunctionCategory[] functionCategories = formulaContext.getFunctionRegistry().getCategories();
      for ( int i = 0; i < functionCategories.length; i++ ) {
        aModel.addElement( functionCategories[ i ] );
      }
      categoryComboBox.setModel( aModel );
      categoryComboBox.setSelectedItem( null );
    }
  }

  protected void fireActionPerformed() {
    final ActionEvent event = new ActionEvent( this, -1, null );
    final ActionListener[] listeners = listenerList.getListeners( ActionListener.class );
    for ( int i = 0; i < listeners.length; i++ ) {
      final ActionListener listener = listeners[ i ];
      listener.actionPerformed( event );
    }
  }

  public void addActionListener( final ActionListener listener ) {
    listenerList.add( ActionListener.class, listener );
  }

  public void removeActionListener( final ActionListener listener ) {
    listenerList.remove( ActionListener.class, listener );
  }

  protected JList getFormulaList() {
    return formulaList;
  }

  protected FunctionCategory getSelectedCategoryName() {
    return (FunctionCategory) categoryComboBox.getSelectedItem();
  }

  protected FunctionDescription[] getDescriptionsForCategory( final FunctionCategory category ) {
    if ( formulaContext == null ) {
      return EMPTY_DESCRIPTIONS;
    }

    final String[] functionNames;
    final FunctionRegistry functionRegistry = formulaContext.getFunctionRegistry();
    if ( category == null ) {
      functionNames = functionRegistry.getFunctionNames();
    } else {
      functionNames = functionRegistry.getFunctionNamesByCategory( category );
    }

    Arrays.sort( functionNames );

    final FunctionDescription[] fds = new FunctionDescription[ functionNames.length ];
    for ( int i = 0; i < functionNames.length; i++ ) {
      fds[ i ] = functionRegistry.getMetaData( functionNames[ i ] );
    }
    return fds;
  }

  public FunctionDescription getSelectedValue() {
    return selectedValue;
  }

  public void setSelectedValue( final FunctionDescription selectedValue ) {
    final FunctionDescription old = this.selectedValue;
    this.selectedValue = selectedValue;
    firePropertyChange( "selectedValue", old, selectedValue ); // NON-NLS
  }
}
