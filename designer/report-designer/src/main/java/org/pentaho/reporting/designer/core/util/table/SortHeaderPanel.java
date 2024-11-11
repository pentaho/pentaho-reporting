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


package org.pentaho.reporting.designer.core.util.table;

import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class SortHeaderPanel extends JPanel {
  private class ShowGroupedAction extends AbstractAction {
    private ShowGroupedAction() {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getGroupIcon() );
    }

    public void actionPerformed( final ActionEvent e ) {
      if ( dataModel != null ) {
        dataModel.setTableStyle( TableStyle.GROUPED );
      }
    }
  }

  private class ShowSortedAscendingAction extends AbstractAction {
    private ShowSortedAscendingAction() {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getSortAscendingIcon() );
    }

    public void actionPerformed( final ActionEvent e ) {
      if ( dataModel != null ) {
        dataModel.setTableStyle( TableStyle.ASCENDING );
      }
    }
  }

  private class ShowSortedDescendingAction extends AbstractAction {
    private ShowSortedDescendingAction() {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getSortDescendingIcon() );
    }

    public void actionPerformed( final ActionEvent e ) {
      if ( dataModel != null ) {
        dataModel.setTableStyle( TableStyle.DESCENDING );
      }
    }
  }

  private SortableTableModel dataModel;
  private SortHeaderPanel.ShowGroupedAction groupedAction;
  private SortHeaderPanel.ShowSortedAscendingAction ascendingAction;
  private SortHeaderPanel.ShowSortedDescendingAction descendingAction;

  public SortHeaderPanel() {
    setLayout( new FlowLayout( FlowLayout.LEADING ) );

    groupedAction = new ShowGroupedAction();
    ascendingAction = new ShowSortedAscendingAction();
    descendingAction = new ShowSortedDescendingAction();

    final JToggleButton groupButton = new JToggleButton( groupedAction );
    final JToggleButton sortAscendingButton = new JToggleButton( ascendingAction );
    final JToggleButton sortDescendingButton = new JToggleButton( descendingAction );

    final ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add( groupButton );
    buttonGroup.add( sortAscendingButton );
    buttonGroup.add( sortDescendingButton );

    groupButton.setSelected( true );

    add( groupButton );
    add( sortAscendingButton );
    add( sortDescendingButton );
  }

  public SortHeaderPanel( final SortableTableModel dataModel ) {
    this();
    this.dataModel = dataModel;
  }

  public SortableTableModel getDataModel() {
    return dataModel;
  }

  public void setDataModel( final SortableTableModel dataModel ) {
    this.dataModel = dataModel;
  }

  public void setEnabled( final boolean enabled ) {
    super.setEnabled( enabled );
    ascendingAction.setEnabled( enabled );
    descendingAction.setEnabled( enabled );
    groupedAction.setEnabled( enabled );
  }
}
