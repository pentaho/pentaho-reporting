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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.actions.global;

import java.awt.event.ActionEvent;
import javax.swing.Action;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.IconLoader;

/**
 * Todo: Document Me
 *
 * @author Ezequiel Cuellar
 */
public final class ToggleStructureToolWindowAction extends AbstractToolWindowStateAction implements ToggleStateAction
{
  public ToggleStructureToolWindowAction()
  {
    putValue(Action.NAME, ActionMessages.getString("ToggleStructureToolWindowAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("ToggleStructureToolWindowAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("ToggleStructureToolWindowAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("ToggleStructureToolWindowAction.Accelerator"));
    putValue(Action.SMALL_ICON, IconLoader.getInstance().getGenericSquareDisabled());
    putValue(Action.SELECTED_KEY, Boolean.FALSE);
  }

  public boolean isSelected()
  {
    return Boolean.TRUE.equals(getValue(Action.SELECTED_KEY));
  }

  public void setSelected(final boolean selected)
  {
    putValue(Action.SELECTED_KEY, selected);
  }

  public void settingsChanged()
  {
    putValue(Action.SELECTED_KEY, WorkspaceSettings.getInstance().isShowGrid());
  }

  public void actionPerformed(final ActionEvent e)
  {
    final ReportDesignerContext reportDesignerContext1 = getReportDesignerContext();
    if (reportDesignerContext1 == null)
    {
      return;
    }
    reportDesignerContext1.getView().setStructureVisible(
        reportDesignerContext1.getView().isStructureVisible() == false);
  }

  protected String getPropertyName()
  {
    return ReportDesignerView.STRUCTURE_VISIBLE_PROPERTY;
  }

  protected boolean recomputeEnabled()
  {
    final ReportDesignerContext reportDesignerContext1 = getReportDesignerContext();
    if (reportDesignerContext1 == null)
    {
      return false;
    }
    setSelected(true);
    return true;
  }

}
