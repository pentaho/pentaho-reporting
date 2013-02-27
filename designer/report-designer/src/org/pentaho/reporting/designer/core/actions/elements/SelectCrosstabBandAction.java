/*
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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.actions.elements;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.Action;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabElement;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabHeader;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabSummaryHeader;
import org.pentaho.reporting.engine.classic.core.CrosstabTitleHeader;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.GroupHeader;

/**
 * This class handle the select crosstab action.  This action is invoked from the Crosstab subreport toolbar.
 * Each time user clicks on the button, a different section of the crosstab is selected where all elements
 * inside the section are selected,
 *
 * @author Sulaiman Karmali
 */
public class SelectCrosstabBandAction extends AbstractDesignerContextAction implements ToggleStateAction
{
  private ReportSelectionModel selectionModel;

  static private ArrayList<Element> otherGroupBodyList;
  static private ArrayList<Element> rowGroupBodyList;
  static private ArrayList<Element> columnGroupBodyList;
  static private ArrayList<Element> cellBodyList;
  static private ArrayList<Element> noneList;
  static private ArrayList<Element> allElementsList;

  static private CrosstabSelectionBandState selectionBandState;

  private enum CrosstabSelectionBandState
  {
    NONE()    {public CrosstabSelectionBandState getNextState() {return OTHER;}
               public CrosstabSelectionBandState getCurrentState() {return NONE;}},
    OTHER()   {public CrosstabSelectionBandState getNextState() {return ROW;}
               public CrosstabSelectionBandState getCurrentState() {return OTHER;}},
    ROW()     {public CrosstabSelectionBandState getNextState() {return COLUMN;}
               public CrosstabSelectionBandState getCurrentState() {return ROW;}},
    COLUMN()  {public CrosstabSelectionBandState getNextState() {return CELL;}
               public CrosstabSelectionBandState getCurrentState() {return COLUMN;}},
    CELL()    {public CrosstabSelectionBandState getNextState() {return ALL;}
               public CrosstabSelectionBandState getCurrentState() {return CELL;}},
    ALL()     {public CrosstabSelectionBandState getNextState() {return NONE;}
               public CrosstabSelectionBandState getCurrentState() {return ALL;}};

    public abstract CrosstabSelectionBandState getNextState();
    public abstract CrosstabSelectionBandState getCurrentState();

    public ArrayList<Element> getNextSelectionList()
    {
      selectionBandState = getNextState();
      switch (selectionBandState)
      {
        case NONE:
          return noneList;
       case OTHER:
          return otherGroupBodyList;
        case ROW:
          return rowGroupBodyList;
        case COLUMN:
          return columnGroupBodyList;
        case CELL:
          return cellBodyList;
        case ALL:
          return allElementsList;
      }

      return noneList;
    }
  }

  public SelectCrosstabBandAction()
  {
    putValue(Action.SELECTED_KEY, Boolean.TRUE);
    putValue(Action.NAME, ActionMessages.getString("SelectCrosstabBandAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("SelectCrosstabBandAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("SelectCrosstabBandAction.Mnemonic"));
    putValue(Action.SMALL_ICON, IconLoader.getInstance().getSelectCrosstabBandCommand());
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("SelectCrosstabBandAction.Accelerator"));

    selectionBandState = CrosstabSelectionBandState.NONE;

    otherGroupBodyList = new ArrayList<Element>();
    rowGroupBodyList = new ArrayList<Element>();
    columnGroupBodyList = new ArrayList<Element>();
    cellBodyList = new ArrayList<Element>();
    noneList = new ArrayList<Element>();    // This will always be empty
    allElementsList = new ArrayList<Element>();

    setEnabled(true);
  }

  public boolean isSelected()
  {
    return Boolean.TRUE.equals(getValue(Action.SELECTED_KEY));
  }

  public void setSelected(final boolean selected)
  {
    putValue(Action.SELECTED_KEY, selected);
  }


  /**
   * Invoked when an action occurs.  We are going to select all the elements inside
   * of a crosstab band (row, column, or cell) every time actionPerformed is called -
   * usually when user clicks on selection icon in subreport toolbar
   */
  public void actionPerformed(final ActionEvent e)
  {
    final ReportDesignerContext reportDesignerContext = getReportDesignerContext();
    if (reportDesignerContext == null)
    {
      return;
    }

    final ReportRenderContext activeContext = reportDesignerContext.getActiveContext();
    selectionModel = activeContext.getSelectionModel();

    final AbstractReportDefinition reportDefinition = activeContext.getReportDefinition();
    if (reportDefinition instanceof CrosstabElement)
    {
      final CrosstabElement crosstabElement = (CrosstabElement)reportDefinition;
      final Group group = crosstabElement.getRootGroup();
      if (group instanceof CrosstabGroup)
      {
        final CrosstabGroup crosstabGroup = (CrosstabGroup)group;
        final GroupBody crosstabGroupBody = crosstabGroup.getBody();

        // We want to build the row, column and cell lists only once.  These lists
        // contain all the elements for a particular section of the crosstab.
        if (allElementsList.isEmpty())
        {
          // Start with the other group and work our way deeper recursively.
          // Note: Other Group is optional.
          if (crosstabGroupBody instanceof CrosstabOtherGroupBody)
          {
            final CrosstabOtherGroup crosstabOtherGroup = ((CrosstabOtherGroupBody)crosstabGroupBody).getGroup();
            buildCrosstabOtherRowGroupBands(crosstabOtherGroup);
          }
          else if (crosstabGroupBody instanceof CrosstabRowGroupBody)
          {
            final CrosstabRowGroupBody crosstabRowGroupBody = (CrosstabRowGroupBody)crosstabGroup.getBody();
            buildCrosstabRowGroupBands(crosstabRowGroupBody);
          }

          // Create an array of all elements.
          allElementsList.addAll(otherGroupBodyList);
          allElementsList.addAll(rowGroupBodyList);
          allElementsList.addAll(columnGroupBodyList);
          allElementsList.addAll(cellBodyList);
        }

        // Select the next crosstab band
        selectCrosstabElements(selectionBandState.getNextSelectionList());
      }
    }
  }

  /**
   * Iterate over the elements in the main Crosstab Cell body.
   * This is where the data of a crosstab is presented in a row/column fashion
   * @param crosstabCellBody - Contains the body of all the crosstab cells
   */
  private void buildCrosstabCellBands(final CrosstabCellBody crosstabCellBody)
  {
    if (crosstabCellBody == null)
    {
      return;
    }

    buildCrosstabElementsList(crosstabCellBody.getHeader(), cellBodyList);

    final int count = crosstabCellBody.getElementCount();
    for (int i = 0; i < count; i++)
    {
      final Element element = crosstabCellBody.getElement(i);
      if (element instanceof CrosstabCell)
      {
        final CrosstabCell cell = (CrosstabCell)element;
        final int cellCount = cell.getElementCount();
        for (int c = 0; c < cellCount; c++)
        {
          final Element cellElement = cell.getElement(c);
          cellBodyList.add(cellElement);
        }
      }
    }
  }

  /**
   * Iterate over the Crosstab's Column Group Body.  This is where the Crosstab column title, column header
   * and column's summary header live ('crosstab-row-group-body')
   * @param crosstabColumnGroupBody
   */
  private void buildCrosstabColumnGroupBands(final CrosstabColumnGroupBody crosstabColumnGroupBody)
  {
    if (crosstabColumnGroupBody == null)
    {
      return;
    }

    final CrosstabColumnGroup crosstabColumnGroup = crosstabColumnGroupBody.getGroup();
    final CrosstabTitleHeader crosstabTitleHeader = crosstabColumnGroup.getTitleHeader();
    final CrosstabHeader crosstabHeader = crosstabColumnGroup.getHeader();
    final CrosstabSummaryHeader crosstabSummaryHeader = crosstabColumnGroup.getSummaryHeader();

    buildCrosstabElementsList(crosstabTitleHeader, columnGroupBodyList);
    buildCrosstabElementsList(crosstabHeader, columnGroupBodyList);
    buildCrosstabElementsList(crosstabSummaryHeader, columnGroupBodyList);

    final GroupBody body = crosstabColumnGroup.getBody();
    if (body instanceof CrosstabColumnGroupBody)
    {
      // Recurse to the next column-group
      buildCrosstabColumnGroupBands((CrosstabColumnGroupBody)body);
    }
    else if (body instanceof CrosstabCellBody)
    {
      // We are done with column-groups, lets start the cell band (row/col of cells containing
      // values of the crosstab).  This is the most granular we go.
      buildCrosstabCellBands((CrosstabCellBody)body);
    }

  }


  /**
   * Iterate over the Crosstab's Row Group Body.  This is where the Crosstab's row title header, row header,
   * and row summary live ('crosstab-row-group-body')
   * @param crosstabRowGroupBody
   */
  private void buildCrosstabRowGroupBands(final CrosstabRowGroupBody crosstabRowGroupBody)
  {
    if (crosstabRowGroupBody == null)
    {
      return;
    }

    final CrosstabRowGroup crosstabRowGroup = crosstabRowGroupBody.getGroup();
    final CrosstabTitleHeader crosstabTitleHeader = crosstabRowGroup.getTitleHeader();
    final CrosstabHeader crosstabHeader = crosstabRowGroup.getHeader();
    final CrosstabSummaryHeader crosstabSummaryHeader = crosstabRowGroup.getSummaryHeader();

    buildCrosstabElementsList(crosstabTitleHeader, rowGroupBodyList);
    buildCrosstabElementsList(crosstabHeader, rowGroupBodyList);
    buildCrosstabElementsList(crosstabSummaryHeader, rowGroupBodyList);

    final GroupBody body = crosstabRowGroup.getBody();
    if (body instanceof CrosstabRowGroupBody)
    {
      // Recurse to add additional row-groups
      buildCrosstabRowGroupBands((CrosstabRowGroupBody)body);
    }
    else if (body instanceof CrosstabColumnGroupBody)
    {
      // We are done with row-groups, let's now deal with column-groups
      buildCrosstabColumnGroupBands((CrosstabColumnGroupBody)body);
    }
  }

  /**
   * Iterate over the Crosstab's Other Group Body.  This is where the Crosstab's Group Header lives ('crosstab-other-group')
   * @param crosstabOtherGroup
   */

  private void buildCrosstabOtherRowGroupBands(final CrosstabOtherGroup crosstabOtherGroup)
  {
    if (crosstabOtherGroup == null)
    {
      return;
    }

    final GroupHeader otherGroupHeader = (GroupHeader)crosstabOtherGroup.getElement(0);
    for (int i = 0; i < otherGroupHeader.getElementCount(); i++)
    {
      final Element element = otherGroupHeader.getElement(i);
      otherGroupBodyList.add(element);
    }

    // We got a Other Group header.  If we have multiple group headers, we recurse.
    // Otherwise we can now get the elements from other parts of the crosstab
    final GroupBody groupBody = crosstabOtherGroup.getBody();
    if (groupBody instanceof CrosstabOtherGroupBody)
    {
      buildCrosstabOtherRowGroupBands(((CrosstabOtherGroupBody)groupBody).getGroup());
    }
    else if (groupBody instanceof CrosstabRowGroupBody)
    {
      buildCrosstabRowGroupBands((CrosstabRowGroupBody)groupBody);
    }
  }

  /**
   * Iterate over a Band's elements selecting each element.
   * @param elementsList - Element list containing a section of the crosstab that we want to select
   */
  private void selectCrosstabElements(final ArrayList<Element> elementsList)
  {
    if ((elementsList == null) || (selectionModel == null))
    {
      return;
    }

    // First clear any previously selected elements
    selectionModel.clearSelection();

    // Select elements
    selectionModel.setSelectedElements(elementsList.toArray());
  }

  /**
   * For caching purposes, we iterate over the elements in a band and add the elements to list
   * @param band
   * @param elementsList - list containing elements that are part of the band.
   */
  private void buildCrosstabElementsList(final Band band, final ArrayList<Element> elementsList)
  {
    if ((band == null) || (elementsList == null))
    {
      return;
    }

    final int count = band.getElementCount();
    for (int i = 0; i < count; i++)
    {
      final Element element = band.getElement(i);
      elementsList.add(element);
    }
  }
}