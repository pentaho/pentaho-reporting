package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import java.awt.Component;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;

public class OpenFileFromRepositoryTask implements AuthenticatedServerTask
{
  private AuthenticationData loginData;
  private boolean storeUpdates;
  private ReportDesignerContext designerContext;
  private Component uiContext;
  private SelectFileFromRepositoryTask selectFileFromRepositoryTask;

  public OpenFileFromRepositoryTask(final ReportDesignerContext designerContext,
                                    final Component uiContext)
  {

    this.designerContext = designerContext;
    this.uiContext = uiContext;

    selectFileFromRepositoryTask = new SelectFileFromRepositoryTask(uiContext);
  }

  public void setLoginData(final AuthenticationData loginData,
                           final boolean storeUpdates)
  {
    this.loginData = loginData;
    this.storeUpdates = storeUpdates;
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used
   * to create a thread, starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may
   * take any action whatsoever.
   *
   * @see Thread#run()
   */
  public void run()
  {
    try
    {
      final String oldName = loginData.getOption("lastFilename");
      final String selectedReport = selectFileFromRepositoryTask.selectFile(loginData, oldName);
      if (selectedReport == null)
      {
        return;
      }
      loginData.setOption("lastFilename", selectedReport);
      if (storeUpdates)
      {
        designerContext.getGlobalAuthenticationStore().add(loginData, true);
      }

      final ReportRenderContext context = PublishUtil.openReport(designerContext, loginData, selectedReport);
      if (context != null)
      {
        context.setProperty("pentaho-login-url", loginData.getUrl());
        context.getAuthenticationStore().add(loginData, true);
      }

      designerContext.getView().setWelcomeVisible(false);
    }
    catch (Exception exception)
    {
      // ignore .. repeat whole process as we assume it is an authentication error
      ExceptionDialog.showExceptionDialog(uiContext,
          Messages.getInstance().getString("LoadReportFromRepositoryAction.Error.Title"),
          Messages.getInstance().formatMessage("LoadReportFromRepositoryAction.Error.Message",
              exception.getMessage()), exception);
      UncaughtExceptionsModel.getInstance().addException(exception);
    }
  }
}
