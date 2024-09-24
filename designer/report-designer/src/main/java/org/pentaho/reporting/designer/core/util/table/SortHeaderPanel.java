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
