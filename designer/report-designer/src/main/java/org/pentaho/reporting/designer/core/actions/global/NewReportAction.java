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
* Copyright (c) 2002-2018 Hitachi Vantara.  All rights reserved.
*/

package org.pentaho.reporting.designer.core.actions.global;

import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.ReportDesignerParserModule;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentBundle;

import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.io.OutputStream;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class NewReportAction extends AbstractDesignerContextAction {

  private static final String TRANSLATIONS_PROPERTIES = "translations.properties";

  public NewReportAction() {
    putValue( Action.NAME, ActionMessages.getString( "NewReportAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "NewReportAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "NewReportAction.Mnemonic" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getNewIcon() );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "NewReportAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    try {
      final MasterReport report = prepareMasterReport();

      getReportDesignerContext().addMasterReport( report );
      getReportDesignerContext().getView().setWelcomeVisible( false );
    } catch ( ReportDataFactoryException e1 ) {
      UncaughtExceptionsModel.getInstance().addException( e1 );
    }
  }

  public static MasterReport prepareMasterReport() {
    final MasterReport report = new MasterReport();
    report.setAutoSort( Boolean.TRUE );
    report.setDataFactory( new CompoundDataFactory() );
    report.setQuery( null );
    report.setQueryLimitInheritance( Boolean.TRUE );
    report.getRelationalGroup( 0 ).getHeader()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
    report.getRelationalGroup( 0 ).getFooter()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
    report.getDetailsFooter()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
    report.getDetailsHeader()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
    report.getNoDataBand()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
    report.getWatermark()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );

    try {
      final MemoryDocumentBundle bundle = (MemoryDocumentBundle) report.getBundle();
      if ( bundle.isEntryExists( TRANSLATIONS_PROPERTIES ) == false ) {
        final String defaultMessage = ActionMessages.getString( "Translations.DefaultContent" );
        final OutputStream outputStream = bundle.createEntry( TRANSLATIONS_PROPERTIES, "text/plain" ); // NON-NLS
        outputStream.write( defaultMessage.getBytes( "ISO-8859-1" ) ); // NON-NLS
        outputStream.close();
        bundle.getWriteableDocumentMetaData()
          .setEntryAttribute( TRANSLATIONS_PROPERTIES, BundleUtilities.STICKY_FLAG, "true" ); // NON-NLS
      }
    } catch ( Exception ex ) {
      // ignore, its not that important ..
      DebugLog.log( "Failed to created default translation entry", ex ); // NON-NLS
    }
    return report;
  }
}
