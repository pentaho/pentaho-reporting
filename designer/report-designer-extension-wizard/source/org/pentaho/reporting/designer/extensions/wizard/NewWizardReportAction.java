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

package org.pentaho.reporting.designer.extensions.wizard;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.OutputStream;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.EmbeddedWizard;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentBundle;

public class NewWizardReportAction extends AbstractDesignerContextAction
{

  private static final String TRANSLATIONS_PROPERTIES = "translations.properties";

  public NewWizardReportAction()
  {
    putValue(Action.NAME, Messages.getString("NewWizardReportAction.MenuTitle")); //$NON-NLS-1$
    putValue("WIZARD.BUTTON.TEXT", Messages.getString("NewWizardReportAction.ButtonTitle")); //$NON-NLS-1$ //$NON-NLS-2$
    putValue(Action.ACCELERATOR_KEY, Messages.getOptionalKeyStroke("NewWizardReportAction.Accelerator")); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void actionPerformed(final ActionEvent e)
  {
    final ReportDesignerContext designerContext = getReportDesignerContext();
    if (designerContext == null)
    {
      return;
    }
    final Component parent = designerContext.getParent();
    final Window window = LibSwingUtil.getWindowAncestor(parent);
    final EmbeddedWizard dialog;
    if (window instanceof JDialog)
    {
      dialog = new EmbeddedWizard((JDialog) window);
    }
    else if (window instanceof JFrame)
    {
      dialog = new EmbeddedWizard((JFrame) window);
    }
    else
    {
      dialog = new EmbeddedWizard();
    }

    // final MasterReport report = new MasterReport();
    // report.setDataFactory(new CompoundDataFactory());
    // report.setQuery(null);

    try
    {
      final MasterReport def = (MasterReport) dialog.run(null);
      if (def == null)
      {
        return;
      }

      try
      {
        final MemoryDocumentBundle bundle = (MemoryDocumentBundle) def.getBundle();
        if (bundle.isEntryExists(TRANSLATIONS_PROPERTIES) == false)
        {
          final String defaultMessage = ActionMessages.getString("Translations.DefaultContent");
          final OutputStream outputStream = bundle.createEntry(TRANSLATIONS_PROPERTIES, "text/plain");
          outputStream.write(defaultMessage.getBytes("ISO-8859-1"));
          outputStream.close();
          bundle.getWriteableDocumentMetaData().setEntryAttribute(TRANSLATIONS_PROPERTIES, BundleUtilities.STICKY_FLAG, "true");
        }
      }
      catch (Exception ex)
      {
        // ignore, its not that important ..
        DebugLog.log("Failed to created default translation entry", ex);
      }

      designerContext.addMasterReport(def);
    }
    catch (ReportProcessingException e1)
    {
      UncaughtExceptionsModel.getInstance().addException(e1);
    }
  }
}
