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

package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.AlignmentOptionsDialog;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.DefaultGuiContext;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.GuiContext;
import org.pentaho.reporting.engine.classic.core.modules.gui.print.PageSetupDialog;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

public final class PageSetupAction extends AbstractReportContextAction {

  public PageSetupAction() {
    putValue( Action.NAME, ActionMessages.getString( "PageSetupAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "PageSetupAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "PageSetupAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "PageSetupAction.Accelerator" ) );
  }

  public void actionPerformed( final ActionEvent e ) {

    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    final MasterReport report = activeContext.getContextRoot();
    final PageDefinition originalPageDef = report.getPageDefinition();

    if ( "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty // NON-NLS
      ( "org.pentaho.reporting.engine.classic.core.modules.gui.print.UseAlternatePageSetupDialog" ) ) ) // NON-NLS
    {
      final GuiContext context = new DefaultGuiContext();
      final PageSetupDialog dialog;
      final Window proxy = LibSwingUtil.getWindowAncestor( getReportDesignerContext().getView().getParent() );
      if ( proxy instanceof Frame ) {
        dialog = new PageSetupDialog( context, (Frame) proxy );
      } else if ( proxy instanceof Dialog ) {
        dialog = new PageSetupDialog( context, (Dialog) proxy );
      } else {
        dialog = new PageSetupDialog( context );
      }

      final PageDefinition definition = dialog.performSetup( originalPageDef );
      if ( dialog.isConfirmed() == false ) {
        return;
      }
      if ( ObjectUtilities.equal( definition, originalPageDef ) ) {
        return;
      }

      report.setPageDefinition( definition );
    } else {
      final PrinterJob pj = PrinterJob.getPrinterJob();
      final PageFormat original = originalPageDef.getPageFormat( 0 );
      final PageFormat pf = pj.validatePage( pj.pageDialog( original ) );
      if ( PageFormatFactory.isEqual( pf, original ) ) {
        return;
      }

      final PageDefinition pageDefinition = report.getPageDefinition();
      if ( pageDefinition instanceof SimplePageDefinition ) {
        final SimplePageDefinition spd = (SimplePageDefinition) pageDefinition;
        report.setPageDefinition( new SimplePageDefinition
          ( pf, spd.getPageCountHorizontal(), spd.getPageCountVertical() ) );
      } else {
        report.setPageDefinition( new SimplePageDefinition( pf ) );
      }
    }

    alignElements( originalPageDef );
  }

  private void alignElements( final PageDefinition original ) {
    final Component parent = getReportDesignerContext().getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final AlignmentOptionsDialog dialog;
    if ( window instanceof JDialog ) {
      dialog = new AlignmentOptionsDialog( (JDialog) window, getActiveContext(), original );
    } else if ( window instanceof JFrame ) {
      dialog = new AlignmentOptionsDialog( (JFrame) window, getActiveContext(), original );
    } else {
      dialog = new AlignmentOptionsDialog( getActiveContext(), original );
    }

    dialog.performEdit();
  }
}
