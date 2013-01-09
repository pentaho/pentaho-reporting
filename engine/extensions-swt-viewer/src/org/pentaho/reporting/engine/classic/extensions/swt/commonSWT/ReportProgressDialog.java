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

package org.pentaho.reporting.engine.classic.extensions.swt.commonSWT;

import java.text.MessageFormat;
import java.util.Locale;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingCommonModule;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A progress monitor dialog component that visualizes the report processing
 * progress. It will receive update events from the report processors and
 * updates the UI according to the latest event data. <p/> The progress will be
 * computed according to the currently processed table row. This approach
 * provides relatively accurate data, but assumes that processing all bands
 * consumes roughly the same time.
 * 
 * Creation-Date: 8/17/2008
 * 
 * @author Baochuan Lu
 */
public class ReportProgressDialog extends Dialog implements
ReportProgressListener
{
  /**
   * Handles the update event processing as a separate thread
   */
  private class ScreenUpdateRunnable implements Runnable
  {
    /**
     * The event upon which this update event processing will occur
     */
    private ReportProgressEvent event;

    /**
     * Initializes the update event processing thread with the event information
     */
    protected ScreenUpdateRunnable()
    {
    }

    /**
     * Performs the process of updating all the pieces of the progress dialog
     * with the update event information.
     */
    public synchronized void run()
    {
      if (event == null)
      {
        return;
      }
      updatePageMessage(event.getPage());
      updateRowsMessage(event.getRow(), event.getMaximumRow());
      updateActivityMessage(event.getActivity());
      updateProgressBar(event);
      ReportProgressDialog.this.getShell().pack();
      this.event = null;
    }

    public synchronized boolean update(final ReportProgressEvent event)
    {
      final boolean retval = (this.event == null);
      this.event = event;
      return retval;
    }
  }

  /**
   * A label that carries the global message that describes the current task.
   */
  private Label messageCarrier;

  /**
   * A label containing the report processing pass count.
   */
  private Label passCountMessage;

  /**
   * A label containing the current page.
   */
  private Label pageCountMessage;

  /**
   * A label containing the currently processed row.
   */
  private Label rowCountMessage;

  /**
   * The progress bar that is used to visualize the progress.
   */
  private ProgressBar progressBar;

  /**
   * The reusable message format for the page label.
   */
  private MessageFormat pageMessageFormatter;

  /**
   * The reusable message format for the rows label.
   */
  private MessageFormat rowsMessageFormatter;

  /**
   * The reusable message format for the pass label.
   */
  private MessageFormat passMessageFormatter;

  /**
   * The last page received.
   */
  private int lastPage;

  /**
   * The last pass values received.
   */
  private int lastActivity;

  /**
   * The last max-row received.
   */
  private int lastMaxRow;

  /**
   * the cached value for the max-row value as integer.
   */
  private Integer lastMaxRowInteger; // this values doesnt change much, so
  // reduce GC work

  /**
   * a text which describes the layouting process.
   */
  private String layoutText;

  /**
   * a text that describes the export phase of the report processing.
   */
  private String outputText;

  /**
   * Localised messages.
   */
  private Messages messages;

  private boolean onlyPagination;

  private ScreenUpdateRunnable updateRunnable;

  /**
   * Creates a non-modal dialog without a title and with the specified Dialog
   * owner.
   * 
   * @param parent
   *          the owner of the dialog
   */
  public ReportProgressDialog(final Shell parent)
  {
    super(parent);
    updateRunnable = new ScreenUpdateRunnable();
    initConstructor();
  }

  // protected void configureShell(Shell shell){
  // super.configureShell(shell);
  // shell.setSize(300, 50);
  // }

  public boolean isOnlyPagination()
  {
    return onlyPagination;
  }

  public void setOnlyPagination(final boolean onlyPagination)
  {
    this.onlyPagination = onlyPagination;
  }

  /**
   * Initializes the dialog (Non-GUI stuff).
   */
  private void initConstructor()
  {
    messages = new Messages(Locale.getDefault(), SwingCommonModule.BUNDLE_NAME,
        ObjectUtilities.getClassLoader(SwingCommonModule.class));
    pageMessageFormatter = new MessageFormat(messages
        .getString("progress-dialog.page-label")); //$NON-NLS-1$
    rowsMessageFormatter = new MessageFormat(messages
        .getString("progress-dialog.rows-label")); //$NON-NLS-1$
    passMessageFormatter = new MessageFormat(messages
        .getString("progress-dialog.pass-label-0")); //$NON-NLS-1$

    setOutputText(messages.getString("progress-dialog.perform-output")); //$NON-NLS-1$
    setLayoutText(messages.getString("progress-dialog.prepare-layout")); //$NON-NLS-1$

    lastActivity = -1;
    lastMaxRow = -1;
    lastPage = -1;
  }

  /**
   * Initializes the GUI components of this dialog.
   */
  protected Control createDialogArea(final Composite parent)
  {
    // getShell().setSize(100, 50);
    final Composite composite = new Composite(parent, SWT.NONE);
    final GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    composite.setLayout(layout);

    messageCarrier = new Label(composite, SWT.LEFT | SWT.WRAP); //$NON-NLS-1$
    messageCarrier.setText(" ");
    GridData data = new GridData();
    data.horizontalAlignment = SWT.FILL;
    data.verticalAlignment = SWT.FILL;
    data.horizontalSpan = 2;
    data.verticalSpan = 1;
    messageCarrier.setLayoutData(data);

    passCountMessage = new Label(composite, SWT.LEFT | SWT.WRAP); //$NON-NLS-1$
    passCountMessage.setText(" ");
    data = new GridData();
    data.horizontalAlignment = SWT.FILL;
    data.verticalAlignment = SWT.FILL;
    data.horizontalSpan = 2;
    data.verticalSpan = 1;
    passCountMessage.setLayoutData(data);

    progressBar = new ProgressBar(composite, SWT.SMOOTH | SWT.HORIZONTAL);
    progressBar.setMinimum(0);
    progressBar.setMaximum(100);
    data = new GridData();
    data.horizontalAlignment = SWT.FILL;
    data.verticalAlignment = SWT.FILL;
    data.horizontalSpan = 2;
    data.verticalSpan = 1;
    progressBar.setLayoutData(data);

    pageCountMessage = new Label(composite, SWT.LEFT | SWT.WRAP); //$NON-NLS-1$
    pageCountMessage.setText(" ");
    data = new GridData();
    data.horizontalAlignment = SWT.LEFT;
    data.verticalAlignment = SWT.FILL;
    data.horizontalSpan = 1;
    data.verticalSpan = 1;
    passCountMessage.setLayoutData(data);

    rowCountMessage = new Label(composite, SWT.LEFT | SWT.WRAP); //$NON-NLS-1$
    rowCountMessage.setText(" ");
    data = new GridData();
    data.horizontalAlignment = SWT.LEFT;
    data.verticalAlignment = SWT.FILL;
    data.horizontalSpan = 1;
    data.verticalSpan = 1;
    rowCountMessage.setLayoutData(data);

    SwtUtil.centerDialogInParent(parent.getShell(), getShell());
    parent.pack();

    return parent;
  }

  protected Control createButtonBar(final Composite parent)
  {
    /* set no button bar */
    return null;
  }

  /**
   * Returns the current message.
   * 
   * @return the current global message.
   */
  public String getMessage()
  {
    return messageCarrier.getText();
  }

  /**
   * Defines the current message.
   * 
   * @param message
   *          the current global message.
   */
  public void setMessage(final String message)
  {
    messageCarrier.setText(message);
  }

  /**
   * Updates the page message label if the current page has changed.
   * 
   * @param page
   *          the new page parameter.
   */
  protected void updatePageMessage(final int page)
  {
    if (lastPage != page)
    {
      final Object[] parameters = new Object[] { new Integer(page) };
      pageCountMessage.setText(pageMessageFormatter.format(parameters));
      lastPage = page;
    }
  }

  /**
   * Updates the rows message label if either the rows or maxrows changed.
   * 
   * @param rows
   *          the currently processed rows.
   * @param maxRows
   *          the maximum number of rows in the report.
   */
  protected void updateRowsMessage(final int rows, final int maxRows)
  {
    if (maxRows != lastMaxRow)
    {
      lastMaxRowInteger = new Integer(maxRows);
      lastMaxRow = maxRows;
    }
    final Object[] parameters = new Object[] { new Integer(rows),
        lastMaxRowInteger };
    rowCountMessage.setText(rowsMessageFormatter.format(parameters));
  }

  /**
   * Updates the pass message label if either the pass or prepare state changed.
   * The pass reflects the current processing level, one level for every
   * function dependency level.
   * 
   * @param activity
   *          the current reporting pass.
   */
  protected void updateActivityMessage(final int activity)
  {
    if (lastActivity != activity)
    {
      lastActivity = activity;
      final Object[] parameters = new Object[] { new Integer(activity) };
      passCountMessage.setText(passMessageFormatter.format(parameters));
    }
  }

  /**
   * Updates the progress bar to show the current progress
   * 
   * @param event
   *          the event data used to update the progress bar
   */
  protected void updateProgressBar(final ReportProgressEvent event)
  {
    progressBar.setSelection((int) computePercentageComplete(event));
  }

  /**
   * Returns the current pass message component.
   * 
   * @return the pass message component.
   */
  protected final Label getPassCountMessage()
  {
    return passCountMessage;
  }

  /**
   * Returns the current pagecount message component.
   * 
   * @return the page message component.
   */
  protected final Label getPageCountMessage()
  {
    return pageCountMessage;
  }

  /**
   * Returns the current row message component.
   * 
   * @return the row message component.
   */
  protected final Label getRowCountMessage()
  {
    return rowCountMessage;
  }

  /**
   * Returns the current pass message component.
   * 
   * @return the pass message component.
   */
  protected final MessageFormat getPageMessageFormatter()
  {
    return pageMessageFormatter;
  }

  /**
   * Returns the current pass message component.
   * 
   * @return the pass message component.
   */
  protected final MessageFormat getRowsMessageFormatter()
  {
    return rowsMessageFormatter;
  }

  /**
   * Returns the current pass message component.
   * 
   * @return the pass message component.
   */
  protected final MessageFormat getPassMessageFormatter()
  {
    return passMessageFormatter;
  }

  /**
   * Returns the output text message. This text describes the export phases of
   * the report processing.
   * 
   * @return the output phase description.
   */
  public String getOutputText()
  {
    return outputText;
  }

  /**
   * Defines the output text message. This text describes the export phases of
   * the report processing.
   * 
   * @param outputText
   *          the output message.
   */
  public void setOutputText(final String outputText)
  {
    if (outputText == null)
    {
      throw new NullPointerException(messages
          .getErrorString("ReportProgressDialog.ERROR_0001_OUTPUT_TEXT_NULL")); //$NON-NLS-1$
    }
    this.outputText = outputText;
  }

  /**
   * Returns the layout text. This text describes the prepare phases of the
   * report processing.
   * 
   * @return the layout text.
   */
  public String getLayoutText()
  {
    return layoutText;
  }

  /**
   * Defines the layout text message. This text describes the prepare phases of
   * the report processing.
   * 
   * @param layoutText
   *          the layout message.
   */
  public void setLayoutText(final String layoutText)
  {
    if (layoutText == null)
    {
      throw new NullPointerException(messages
          .getErrorString("ReportProgressDialog.ERROR_0002_LAYOUT_TEXT_NULL")); //$NON-NLS-1$
    }
    this.layoutText = layoutText;
  }

  protected boolean isSameMaxRow(final int row)
  {
    return lastMaxRow == row;
  }

  public void reportProcessingStarted(final ReportProgressEvent event)
  {
    postUpdate(event);
  }

  public void reportProcessingUpdate(final ReportProgressEvent event)
  {
    postUpdate(event);
  }

  public void reportProcessingFinished(final ReportProgressEvent event)
  {
    postUpdate(event);
  }

  private void postUpdate(final ReportProgressEvent event)
  {
    if (this.updateRunnable.update(event))
    {
      if (this.getShell().getDisplay().getThread() != Thread.currentThread())
      {
        this.getShell().getDisplay().asyncExec(new Runnable()
        {
          public void run()
          {
            updateRunnable.run();
          }
        });
      } else
      {
        updateRunnable.run();
      }
    }
  }

  /**
   * Computes the percentage complete (on a scale from 0.0 to 100.0) based on
   * the information found in the report progress event.
   * 
   * @param event
   *          the data used to calculate the percentage complete
   * @return the calculated percentage complete
   */
  protected double computePercentageComplete(final ReportProgressEvent event)
  {
    final double maximumLevel;
    final double level;
    if (isOnlyPagination())
    {
      maximumLevel = event.getMaximumLevel();
      level = event.getLevel();
    } else
    {
      maximumLevel = event.getMaximumLevel() + 1;
      if (event.getActivity() == ReportProgressEvent.GENERATING_CONTENT)
      {
        level = event.getLevel() + 1;
      } else
      {
        level = event.getLevel();
      }
    }
    final double levelPercentage = level / maximumLevel;
    final double levelSizePercentage = 1.0 / maximumLevel;
    final double subPercentage = levelSizePercentage
    * (event.getRow() / (double) event.getMaximumRow());
    final double percentage = 100.0 * (levelPercentage + subPercentage);
    return Math.max(0.0, Math.min(100.0, percentage));
  }
}
