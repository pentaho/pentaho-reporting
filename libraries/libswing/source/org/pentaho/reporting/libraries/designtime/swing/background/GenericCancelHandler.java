package org.pentaho.reporting.libraries.designtime.swing.background;

public class GenericCancelHandler implements CancelListener
{
  private Thread thread;
  private boolean cancelled;

  public GenericCancelHandler(final Thread thread)
  {
    this.thread = thread;
  }

  /**
   * Requests that the thread stop processing as soon as possible.
   */
  public void cancelProcessing(final CancelEvent event)
  {
    thread.interrupt();
    this.cancelled = true;
  }

  public boolean isCancelled()
  {
    return cancelled;
  }
}
