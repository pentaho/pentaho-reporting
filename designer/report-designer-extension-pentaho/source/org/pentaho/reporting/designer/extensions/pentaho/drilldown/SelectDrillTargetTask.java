package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import java.awt.Component;
import javax.swing.SwingUtilities;

import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.actions.AuthenticatedServerTask;
import org.pentaho.reporting.designer.extensions.pentaho.repository.actions.SelectFileFromRepositoryTask;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;

public class SelectDrillTargetTask implements AuthenticatedServerTask
{
  private PentahoPathModel wrapper;
  private AuthenticationData loginData;
  private boolean storeUpdates;
  private SelectFileFromRepositoryTask selectFileFromRepositoryTask;
  private Component uiContext;
  private Runnable triggerRefreshParameterTask;
  private ReportRenderContext activeContext;

  public SelectDrillTargetTask(final PentahoPathModel wrapper,
                               final Component uiContext,
                               final Runnable triggerRefreshParameterTask,
                               final ReportRenderContext activeContext)
  {
    this.uiContext = uiContext;
    this.triggerRefreshParameterTask = triggerRefreshParameterTask;
    this.activeContext = activeContext;
    this.selectFileFromRepositoryTask = new SelectFileFromRepositoryTask(uiContext);
    this.selectFileFromRepositoryTask.setFilters(wrapper.getExtensions());
    this.wrapper = wrapper;
  }

  public void setLoginData(final AuthenticationData loginData, final boolean storeUpdates)
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
      final String oldName = loginData.getOption("lastDrillFilename");
      final String selectedReport = selectFileFromRepositoryTask.selectFile(loginData, oldName);
      if (selectedReport == null)
      {
        return;
      }
      loginData.setOption("lastDrillFilename", selectedReport);
      if (storeUpdates)
      {
        if (activeContext != null)
        {
          activeContext.getAuthenticationStore().add(loginData, true);
        }
      }
      wrapper.setLocalPath(selectedReport);
      wrapper.setLoginData(loginData);
      
      SwingUtilities.invokeLater(triggerRefreshParameterTask);

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
