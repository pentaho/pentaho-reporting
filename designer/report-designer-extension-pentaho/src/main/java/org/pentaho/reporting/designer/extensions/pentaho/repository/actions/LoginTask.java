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

package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.SwingUtilities;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.AuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryLoginDialog;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishSettings;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.background.GenericCancelHandler;

public class LoginTask implements Runnable {
  private final ReportDesignerContext designerContext;
  private final Component uiContext;
  private final AuthenticatedServerTask followUpTask;
  private final boolean loginForPublish;
  private boolean skipFirstShowDialog;
  private RepositoryLoginDialog loginDialog;
  private AuthenticationData loginData;

  public LoginTask( final ReportDesignerContext designerContext, final Component uiContext,
      final AuthenticatedServerTask followUpTask ) {
    this( designerContext, uiContext, followUpTask, null, false );
  }

  public LoginTask( final ReportDesignerContext designerContext, final Component uiContext,
      final AuthenticatedServerTask followUpTask, final AuthenticationData loginData ) {
    this( designerContext, uiContext, followUpTask, loginData, false );
  }

  public LoginTask( final ReportDesignerContext designerContext, final Component uiContext,
      final AuthenticatedServerTask followUpTask, final AuthenticationData loginData, final boolean loginForPublish ) {
    if ( designerContext == null ) {
      throw new NullPointerException();
    }
    if ( uiContext == null ) {
      throw new NullPointerException();
    }
    this.loginForPublish = loginForPublish;
    this.designerContext = designerContext;
    this.uiContext = uiContext;
    this.followUpTask = followUpTask;
    if ( loginData != null ) {
      this.loginData = loginData;
      this.skipFirstShowDialog = true;
    } else {
      final ReportDocumentContext reportRenderContext = designerContext.getActiveContext();
      if ( reportRenderContext != null ) {
        final Object o = reportRenderContext.getProperties().get( "pentaho-login-url" );
        if ( o != null ) {
          // prepopulate the dialog with the correct login data, but do not skip login completely.
          this.loginData = RepositoryLoginDialog.getStoredLoginData( String.valueOf( o ), designerContext );
        }
      }
      if ( this.loginData == null ) {
        this.loginData = RepositoryLoginDialog.getDefaultData( designerContext );
      }
      this.skipFirstShowDialog = false;
    }
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
    boolean loginComplete;
    do {
      if ( loginDialog == null ) {
        final Window window = LibSwingUtil.getWindowAncestor( uiContext );
        if ( window instanceof Frame ) {
          loginDialog = new RepositoryLoginDialog( (Frame) window, loginForPublish );
        } else if ( window instanceof Dialog ) {
          loginDialog = new RepositoryLoginDialog( (Dialog) window, loginForPublish );
        } else {
          loginDialog = new RepositoryLoginDialog( loginForPublish );
        }
      }

      if ( skipFirstShowDialog ) {
        skipFirstShowDialog = false;
      } else {
        this.loginData = loginDialog.performLogin( designerContext, loginData );
        if ( loginData == null ) {
          return;
        }
      }

      final ValidateLoginTask validateLoginTask = new ValidateLoginTask( this );
      final Thread loginThread = new Thread( validateLoginTask );
      loginThread.setDaemon( true );
      loginThread.setPriority( Thread.MIN_PRIORITY );

      final GenericCancelHandler cancelHandler = new GenericCancelHandler( loginThread );
      BackgroundCancellableProcessHelper.executeProcessWithCancelDialog( loginThread, cancelHandler, uiContext,
          Messages.getInstance().getString( "LoginTask.ValidateLoginMessage" ) );
      if ( cancelHandler.isCancelled() ) {
        return;
      }

      if ( validateLoginTask.getException() != null ) {
        final Exception exception = validateLoginTask.getException();
        ExceptionDialog.showExceptionDialog( uiContext, Messages.getInstance().getString(
            "LoadReportFromRepositoryAction.LoginError.Title" ), Messages.getInstance().formatMessage(
            "LoadReportFromRepositoryAction.LoginError.Message", exception.getMessage() ), exception );
        loginComplete = false;
      } else {
        loginComplete = validateLoginTask.isLoginComplete();
      }
    } while ( loginComplete == false );

    if ( loginDialog != null && loginDialog.isRememberSettings() ) {
      final ReportDocumentContext reportRenderContext = designerContext.getActiveContext();
      if ( reportRenderContext != null ) {
        final AuthenticationStore store = reportRenderContext.getAuthenticationStore();
        store.add( loginData, true );
      } else {
        designerContext.getGlobalAuthenticationStore().add( loginData, true );
      }
    }

    final boolean storeUpdates;
    if ( loginDialog == null ) {
      storeUpdates = PublishSettings.getInstance().isRememberSettings();
    } else {
      storeUpdates = loginDialog.isRememberSettings();
    }

    if ( followUpTask != null ) {
      followUpTask.setLoginData( loginData, storeUpdates );
      SwingUtilities.invokeLater( followUpTask );
    }

    UpdateReservedCharsTask updateReservedCharsTask = new UpdateReservedCharsTask( loginData );
    SwingUtilities.invokeLater( updateReservedCharsTask );
  }

  public AuthenticationData getLoginData() {
    return loginData;
  }
}
