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
 * Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import java.awt.Component;
import java.awt.Cursor;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentMetaData;
import org.pentaho.reporting.libraries.pensol.JCRSolutionFileSystem;
import org.pentaho.reporting.libraries.pensol.PublishRestUtil;

public class PublishToServerTask implements AuthenticatedServerTask {
  private static final Log logger = LogFactory.getLog( PublishToServerTask.class );

  private ReportDesignerContext reportDesignerContext;
  private Component uiContext;
  private AuthenticationData loginData;
  private boolean storeUpdates;

  public PublishToServerTask( final ReportDesignerContext reportDesignerContext, final Component uiContext ) {

    this.reportDesignerContext = reportDesignerContext;
    this.uiContext = uiContext;
  }

  public void setLoginData( final AuthenticationData loginData, final boolean storeUpdates ) {
    this.loginData = loginData;
    this.storeUpdates = storeUpdates;
  }

  public void run() {
    final MasterReport report = reportDesignerContext.getActiveContext().getContextRoot();
    final DocumentMetaData metaData = report.getBundle().getMetaData();

    try {
      final String oldName = extractLastFileName( report );

      SelectFileForPublishTask selectFileForPublishTask = new SelectFileForPublishTask( uiContext );
      readBundleMetaData( report, metaData, selectFileForPublishTask );

      final String selectedReport = selectFileForPublishTask.selectFile( loginData, oldName );
      if ( selectedReport == null ) {
        return;
      }

      loginData.setOption( "lastFilename", selectedReport );
      storeBundleMetaData( report, selectedReport, selectFileForPublishTask );

      reportDesignerContext.getActiveContext().getAuthenticationStore().add( loginData, storeUpdates );

      //populate all properties from file which loaded in report designer before publish it to server
      Properties fileProperties = new Properties();
      String reportTitle = selectFileForPublishTask.getReportTitle();
      if ( reportTitle != null ) {
        fileProperties.setProperty( PublishRestUtil.REPORT_TITLE_KEY, reportTitle );
      }

      final byte[] data = PublishUtil.createBundleData( report );
      int responseCode = PublishUtil.publish( data, selectedReport, loginData, fileProperties );

      if ( responseCode == 200 ) {
        final Component glassPane = SwingUtilities.getRootPane( uiContext ).getGlassPane();
        try {
          glassPane.setVisible( true );
          glassPane.setCursor( new Cursor( Cursor.WAIT_CURSOR ) );

          FileObject fileSystemRoot = PublishUtil.createVFSConnection( loginData );
          final JCRSolutionFileSystem fileSystem = (JCRSolutionFileSystem) fileSystemRoot.getFileSystem();
          fileSystem.getLocalFileModel().refresh();
        } catch ( Exception e1 ) {
          UncaughtExceptionsModel.getInstance().addException( e1 );
        } finally {
          glassPane.setVisible( false );
          glassPane.setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
        }
        if ( JOptionPane.showConfirmDialog( uiContext, Messages.getInstance().getString(
            "PublishToServerAction.Successful.LaunchNow" ), Messages.getInstance().getString(
            "PublishToServerAction.Successful.LaunchTitle" ), JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION ) {
          PublishUtil.launchReportOnServer( loginData.getUrl(), selectedReport );
        }
      } else if ( responseCode == 403 ) {
        logger.error( "Publish failed. Server responded with status-code " + responseCode );
        JOptionPane.showMessageDialog( uiContext, Messages.getInstance().getString(
            "PublishToServerAction.FailedAccess" ), Messages.getInstance().getString(
            "PublishToServerAction.FailedAccessTitle" ), JOptionPane.ERROR_MESSAGE );
      } else {
        logger.error( "Publish failed. Server responded with status-code " + responseCode );
        showErrorMessage();
      }
    } catch ( Exception exception ) {
      logger.error( "Publish failed. Unexpected error:", exception );
      showErrorMessage();
    }
  }

  private String extractLastFileName( final MasterReport report ) {
    final Object lastFilenameAttr =
        report.getAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.LAST_FILENAME );
    final String oldName;
    if ( lastFilenameAttr != null ) {
      oldName = (String) lastFilenameAttr;
    } else {
      oldName = null;
    }
    return oldName;
  }

  private void readBundleMetaData( final MasterReport report, final DocumentMetaData metaData,
      final SelectFileForPublishTask selectFileForPublishTask ) {
    final String oldDescription =
        (String) metaData.getBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE,
            ODFMetaAttributeNames.DublinCore.DESCRIPTION );
    final String oldTitle =
        (String) metaData.getBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE,
            ODFMetaAttributeNames.DublinCore.TITLE );

    final boolean oldLockOutput =
        Boolean.TRUE.equals( report.getAttribute( AttributeNames.Core.NAMESPACE,
            AttributeNames.Core.LOCK_PREFERRED_OUTPUT_TYPE ) );
    final String oldExportType =
        (String) report.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.PREFERRED_OUTPUT_TYPE );

    selectFileForPublishTask.setDescription( oldDescription );
    selectFileForPublishTask.setReportTitle( oldTitle );
    selectFileForPublishTask.setLockOutputType( oldLockOutput );
    selectFileForPublishTask.setExportType( oldExportType );
  }

  private void storeBundleMetaData( final MasterReport report, final String selectedReport,
      final SelectFileForPublishTask selectFileForPublishTask ) {
    final DocumentMetaData metaData = report.getBundle().getMetaData();
    report.setAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.LAST_FILENAME, selectedReport );

    if ( metaData instanceof WriteableDocumentMetaData ) {
      final WriteableDocumentMetaData writeableDocumentMetaData = (WriteableDocumentMetaData) metaData;
      writeableDocumentMetaData.setBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE,
          ODFMetaAttributeNames.DublinCore.DESCRIPTION, selectFileForPublishTask.getDescription() );
      writeableDocumentMetaData.setBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE,
          ODFMetaAttributeNames.DublinCore.TITLE, selectFileForPublishTask.getReportTitle() );
    }

    report.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.LOCK_PREFERRED_OUTPUT_TYPE, Boolean
        .valueOf( selectFileForPublishTask.isLockOutputType() ) );
    report.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.PREFERRED_OUTPUT_TYPE,
        selectFileForPublishTask.getExportType() );
  }

  private void showErrorMessage() {
    JOptionPane.showMessageDialog( uiContext, Messages.getInstance().getString( "PublishToServerAction.Failed" ),
        Messages.getInstance().getString( "PublishToServerAction.FailedTitle" ), JOptionPane.ERROR_MESSAGE );
  }
}
