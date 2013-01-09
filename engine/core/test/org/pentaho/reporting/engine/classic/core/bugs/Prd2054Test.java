package org.pentaho.reporting.engine.classic.core.bugs;

import java.net.URL;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.EventMonitorFunction;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * As we fire a page-finished event whenever we are about to paginate a page, this test is no
 * longer very sane. It still validates that the report-init event is fired before any of the
 * page events.
 *
 * We have to fire page-finished before each layout calculation to update the page-footer contents
 * to the latest data version.
 */
public class Prd2054Test extends TestCase
{
  private static class ValidateEventOrderFunction extends AbstractFunction implements PageEventListener
  {
    private boolean pageOpen;

    private ValidateEventOrderFunction()
    {
    }

    /**
     * Receives notification that a new page is being started.
     *
     * @param event The event.
     */
    public void pageStarted(final ReportEvent event)
    {
      pageOpen = true;
    }

    /**
     * Receives notification that a page is completed.
     *
     * @param event The event.
     */
    public void pageFinished(final ReportEvent event)
    {
//      pageOpen = false;
    }

    /**
     * Return the current expression value.
     * <p/>
     * The value depends (obviously) on the expression implementation.
     *
     * @return the value of the function.
     */
    public Object getValue()
    {
      return pageOpen;
    }

    /**
     * Receives notification that report generation initializes the current run. <P> The event carries a
     * ReportState.Started state.  Use this to initialize the report.
     *
     * @param event The event.
     */
    public void reportInitialized(final ReportEvent event)
    {
      assertFalse(pageOpen);
    }

    /**
     * Receives notification that the report has started.
     *
     * @param event the event.
     */
    public void reportStarted(final ReportEvent event)
    {
      assertTrue(pageOpen);
    }

    /**
     * Receives notification that the report has finished.
     *
     * @param event the event.
     */
    public void reportFinished(final ReportEvent event)
    {
      assertTrue(pageOpen);
    }

    /**
     * Receives notification that a group has started.
     *
     * @param event the event.
     */
    public void groupStarted(final ReportEvent event)
    {
      assertTrue(pageOpen);
    }

    /**
     * Receives notification that a group has finished.
     *
     * @param event the event.
     */
    public void groupFinished(final ReportEvent event)
    {
      assertTrue(pageOpen);
    }

    /**
     * Receives notification that a row of data is being processed.
     *
     * @param event the event.
     */
    public void itemsAdvanced(final ReportEvent event)
    {
      assertTrue(pageOpen);
    }

    /**
     * Receives notification that a group of item bands is about to be processed. <P> The next events will be
     * itemsAdvanced events until the itemsFinished event is raised.
     *
     * @param event The event.
     */
    public void itemsStarted(final ReportEvent event)
    {
      assertTrue(pageOpen);
    }

    /**
     * Receives notification that a group of item bands has been completed. <P> The itemBand is finished, the report
     * starts to close open groups.
     *
     * @param event The event.
     */
    public void itemsFinished(final ReportEvent event)
    {
      assertTrue(pageOpen);
    }

    /**
     * Receives notification that report generation has completed, the report footer was printed, no more output is done.
     * This is a helper event to shut down the output service.
     *
     * @param event The event.
     */
    public void reportDone(final ReportEvent event)
    {
      assertTrue(pageOpen);
    }

    /**
     * Returns the dependency level for the expression (controls evaluation order for expressions and functions).
     *
     * @return the level.
     */
    public int getDependencyLevel()
    {
      return LayoutProcess.LEVEL_PAGINATE;
    }
  }

  public Prd2054Test()
  {
  }

  public Prd2054Test(final String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  /**
   *
   * @throws Exception
   */
  public void testRunSample() throws Exception
  {
    final URL url = getClass().getResource("Prd-2054.prpt");
    assertNotNull(url);
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly(url, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();
    report.addExpression(new EventMonitorFunction());
    report.addExpression(new ValidateEventOrderFunction());

    DebugReportRunner.createPDF(report);
  }
}
