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

package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import java.awt.Component;

import javax.swing.SwingUtilities;

import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.actions.AuthenticatedServerTask;
import org.pentaho.reporting.designer.extensions.pentaho.repository.actions.SelectFileFromRepositoryTask;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;

public class SelectDrillTargetTask implements AuthenticatedServerTask {
  private PentahoPathModel wrapper;
  private AuthenticationData loginData;

  private boolean storeUpdates;
  private SelectFileFromRepositoryTask selectFileFromRepositoryTask;
  private Component uiContext;
  private Runnable triggerRefreshParameterTask;
  private ReportDocumentContext activeContext;

  public SelectDrillTargetTask( final PentahoPathModel wrapper, final Component uiContext,
      final Runnable triggerRefreshParameterTask, final ReportDocumentContext activeContext ) {
    this.uiContext = uiContext;
    this.triggerRefreshParameterTask = triggerRefreshParameterTask;
    this.activeContext = activeContext;
    this.selectFileFromRepositoryTask = new SelectFileFromRepositoryTask( uiContext );
    this.selectFileFromRepositoryTask.setFilters( wrapper.getExtensions() );
    this.wrapper = wrapper;
  }

  public void setLoginData( final AuthenticationData loginData, final boolean storeUpdates ) {
    this.loginData = loginData;
    this.storeUpdates = storeUpdates;
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread causes
   * the object's <code>run</code> method to be called in that separately executing thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  public void run() {
    try {
      final String oldName = loginData.getOption( "lastDrillFilename" );
      final String selectedReport = selectFileFromRepositoryTask.selectFile( loginData, oldName );
      if ( selectedReport == null ) {
        return;
      }
      loginData.setOption( "lastDrillFilename", selectedReport );
      if ( storeUpdates ) {
        if ( activeContext != null ) {
          activeContext.getAuthenticationStore().add( loginData, true );
        }
      }
      wrapper.setLocalPath( selectedReport );
      wrapper.setLoginData( loginData );

      SwingUtilities.invokeLater( triggerRefreshParameterTask );

    } catch ( Exception exception ) {
      // ignore .. repeat whole process as we assume it is an authentication error
      ExceptionDialog.showExceptionDialog( uiContext, Messages.getInstance().getString(
          "LoadReportFromRepositoryAction.Error.Title" ), Messages.getInstance().formatMessage(
          "LoadReportFromRepositoryAction.Error.Message", exception.getMessage() ), exception );
      UncaughtExceptionsModel.getInstance().addException( exception );
    }
  }

  /**
   * @return AuthenticationData
   * 
   *         Used to facilitate testing.
   */
  AuthenticationData getLoginData() {
    return loginData;
  }

}
