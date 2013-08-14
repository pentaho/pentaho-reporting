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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.actions.elements;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.util.Locale;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.ElementEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.DetailsFooter;
import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.Watermark;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.designtime.swing.FocusTracker;

/**
 * Inserts an element within a band
 *
 * @author Ezequiel Cuellar
 */
public class InsertElementAction extends AbstractElementSelectionAction implements SettingsListener
{
  private static final long serialVersionUID = 4113715870254584033L;

  private class FocusUpdateHandler extends FocusTracker
  {
    protected void focusChanged(final Component c)
    {
      updateSelection();
    }
  }

  protected static final Float DEFAULT_WIDTH = new Float(100);
  protected static final Float DEFAULT_HEIGHT = new Float(20);

  private ElementMetaData metaData;
  private boolean expert;
  private boolean experimental;
  private boolean deprecated;

  /**
   * This is a listener on the global focus manager and gets called whenever the focus changed.
   * The inspection thinks that this reference should be removed, but as long as the hard-reference
   * is here, this listener will not be garbage collected.
   * 
   * @noinspection FieldCanBeLocal
   */
  private FocusUpdateHandler focusTracker;

  public InsertElementAction(final ElementMetaData metaData)
  {
    this.metaData = metaData;
    putValue(Action.NAME, ActionMessages.getString("InsertElementAction.Name", metaData.getDisplayName(Locale.getDefault())));
    
    final Image image = metaData.getIcon(Locale.getDefault(), BeanInfo.ICON_COLOR_32x32);
    if (image != null)
    {
      putValue(Action.SMALL_ICON, new ImageIcon(image));
    }

    setEnabled(false);

    experimental = metaData.isExperimental();
    expert = metaData.isExpert();
    deprecated = metaData.isDeprecated();

    // update from system clipboard status
    focusTracker = new FocusUpdateHandler();
    
    settingsChanged();
    WorkspaceSettings.getInstance().addSettingsListener(this);
  }

  public void settingsChanged()
  {
    if (WorkspaceSettings.getInstance().isShowExpertItems() == false && expert)
    {
      setVisible(false);
      return;
    }
    if (WorkspaceSettings.getInstance().isShowDeprecatedItems() == false && deprecated)
    {
      setVisible(false);
      return;
    }
    if (WorkspaceSettings.getInstance().isExperimentalFeaturesVisible() == false && experimental)
    {
      setVisible(false);
      return;
    }

    setVisible(true);
  }

  protected void selectedElementPropertiesChanged(ReportModelEvent event)
  {
  }

  protected void updateSelection()
  {
    final ReportRenderContext activeContext = getActiveContext();
    if (activeContext == null)
    {
      setEnabled(false);
      return;
    }

    Object selectedElement = null;
    final ReportSelectionModel selectionModel1 = getSelectionModel();
    if (selectionModel1 == null)
    {
      setEnabled(false);
      return;
    }

    if (selectionModel1.getSelectionCount() > 0)
    {
      selectedElement = selectionModel1.getSelectedElement(0);
    }
    if (selectedElement instanceof Band)
    {
      if ("sub-report".equals(metaData.getName())) // NON-NLS
      {
        final Element rootBand = findRootBand((Element) selectedElement);
        if (rootBand == null ||
            rootBand instanceof PageHeader ||
            rootBand instanceof PageFooter ||
            rootBand instanceof DetailsHeader ||
            rootBand instanceof DetailsFooter ||
            rootBand instanceof Watermark)
        {
          setEnabled(false);
        }
        else
        {
          setEnabled(true);
        }
        return;
      }
      setEnabled(true);
    }
    else
    {
      setEnabled(false);
    }
  }

  public void actionPerformed(final ActionEvent e)
  {
    Object selectedElement = null;
    final ReportSelectionModel selectionModel1 = getSelectionModel();
    if (selectionModel1 == null)
    {
      return;
    }

    if (selectionModel1.getSelectionCount() > 0)
    {
      selectedElement = selectionModel1.getSelectedElement(0);
    }
    final Band band;
    if (selectedElement instanceof Band)
    {
      band = (Band) selectedElement;
    }
    else
    {
      return;
    }

    try
    {
      final ElementType type = metaData.create();
      final Element visualElement = (Element) type.create();
      if (visualElement instanceof SubReport)
      {
        final Element rootBand = findRootBand(band);
        if (rootBand == null ||
            rootBand instanceof PageHeader ||
            rootBand instanceof PageFooter ||
            rootBand instanceof DetailsHeader ||
            rootBand instanceof DetailsFooter ||
            rootBand instanceof Watermark)
        {
          return;
        }
      }
      
      final ElementStyleSheet styleSheet = visualElement.getStyle();
      styleSheet.setStyleProperty(ElementStyleKeys.MIN_WIDTH, DEFAULT_WIDTH);
      styleSheet.setStyleProperty(ElementStyleKeys.MIN_HEIGHT, DEFAULT_HEIGHT);

      type.configureDesignTimeDefaults(visualElement, Locale.getDefault());


      final ReportRenderContext context = getActiveContext();
      final UndoManager undo = context.getUndo();
      undo.addChange(ActionMessages.getString("InsertElementAction.UndoName"),
          new ElementEditUndoEntry(band.getObjectID(), band.getElementCount(), null, visualElement));
      band.addElement(visualElement);
    }
    catch (Exception ex)
    {
      UncaughtExceptionsModel.getInstance().addException(ex);
    }
  }

  private Element findRootBand(Element element)
  {
    while (element != null && ((element instanceof RootLevelBand) == false))
    {
      element = element.getParent();
    }

    if (element != null)
    {
      return element;
    }

    return null;
  }

}
