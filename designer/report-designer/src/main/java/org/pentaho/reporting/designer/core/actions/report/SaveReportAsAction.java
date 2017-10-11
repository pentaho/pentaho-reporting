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

import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public final class SaveReportAsAction extends AbstractSaveReportAction {

  @Override
  protected void init() {
    putValue( Action.NAME, ActionMessages.getString( "SaveAsReport.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "SaveAsReport.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getMnemonic( "SaveAsReport.Mnemonic" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getSaveIcon() );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getKeyStroke( "SaveAsReport.Accelerator" ) );
  }

  @Override
  protected File getTarget( MasterReport report, Component parent ) {
    ResourceKey definitionSource = report.getDefinitionSource();
    File target = SaveReportUtilities.getCurrentFile( definitionSource );

    // Prompt for the filename
    target = SaveReportUtilities.promptReportFilename( parent, null );

    return target;
  }

}
