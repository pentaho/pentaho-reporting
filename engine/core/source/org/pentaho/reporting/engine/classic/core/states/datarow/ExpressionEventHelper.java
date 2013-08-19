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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states.datarow;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.Function;

public abstract class ExpressionEventHelper
{

  private static final Log logger = LogFactory.getLog(ExpressionEventHelper.class);

  protected void fireReportEvent(final ReportEvent event)
  {
    if ((event.getType() & ReportEvent.PAGE_STARTED) ==
        ReportEvent.PAGE_STARTED)
    {
      firePageStartedEvent(event);
    }
    else if ((event.getType() & ReportEvent.PAGE_FINISHED) ==
        ReportEvent.PAGE_FINISHED)
    {
      firePageFinishedEvent(event);
    }
    else if ((event.getType() & ReportEvent.ITEMS_ADVANCED) ==
        ReportEvent.ITEMS_ADVANCED)
    {
      fireItemsAdvancedEvent(event);
    }
    else if ((event.getType() & ReportEvent.ITEMS_FINISHED) ==
        ReportEvent.ITEMS_FINISHED)
    {
      fireItemsFinishedEvent(event);
    }
    else if ((event.getType() & ReportEvent.ITEMS_STARTED) ==
        ReportEvent.ITEMS_STARTED)
    {
      fireItemsStartedEvent(event);
    }
    else if ((event.getType() & ReportEvent.GROUP_FINISHED) ==
        ReportEvent.GROUP_FINISHED)
    {
      fireGroupFinishedEvent(event);
    }
    else if ((event.getType() & ReportEvent.GROUP_STARTED) ==
        ReportEvent.GROUP_STARTED)
    {
      fireGroupStartedEvent(event);
    }
    else if ((event.getType() & ReportEvent.REPORT_INITIALIZED) ==
        ReportEvent.REPORT_INITIALIZED)
    {
      fireReportInitializedEvent(event);
    }
    else if ((event.getType() & ReportEvent.REPORT_DONE) ==
        ReportEvent.REPORT_DONE)
    {
      fireReportDoneEvent(event);
    }
    else if ((event.getType() & ReportEvent.REPORT_FINISHED) ==
        ReportEvent.REPORT_FINISHED)
    {
      fireReportFinishedEvent(event);
    }
    else if ((event.getType() & ReportEvent.REPORT_STARTED) ==
        ReportEvent.REPORT_STARTED)
    {
      fireReportStartedEvent(event);
    }
    else if ((event.getType() & ReportEvent.SUMMARY_ROW) ==
        ReportEvent.SUMMARY_ROW)
    {
      fireSummaryRowEvent(event);
    }
    else if ((event.getType() & ReportEvent.GROUP_BODY_FINISHED) ==
        ReportEvent.GROUP_BODY_FINISHED)
    {
      // ignore, nothing we handle here
    }
    else
    {
      throw new IllegalArgumentException();
    }
  }

  protected abstract int getRunLevelCount();

  protected abstract LevelStorage getRunLevel(int index);

  protected abstract ExpressionRuntime getRuntime();

  protected int getProcessingLevel()
  {
    return getRuntime().getProcessingContext().getProcessingLevel();
  }

  private void fireItemsAdvancedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = getProcessingLevel();
    final ExpressionRuntime runtime = getRuntime();

    final int runlevelCount = getRunLevelCount();
    for (int levelIdx = 0; levelIdx < runlevelCount; levelIdx++)
    {
      final LevelStorage levelData = getRunLevel(levelIdx);
      final int level = levelData.getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final Iterator<Function> expressions = levelData.getFunctions();
      while (expressions.hasNext())
      {
        final Expression expression = expressions.next();
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.itemsAdvanced(event);
          evaluateSingleExpression(expression);
        }
        catch (InvalidReportStateException rse)
        {
          throw rse;
        }
        catch (Exception ex)
        {
          if (logger.isDebugEnabled())
          {
            logger.error("Failed to fire prepare event", ex);
          }
          else
          {
            logger.error("Failed to fire prepare event: " + ex);
          }
          evaluateToNull(expression);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireItemsStartedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = getProcessingLevel();
    final ExpressionRuntime runtime = getRuntime();

    final int runlevelCount = getRunLevelCount();
    for (int levelIdx = 0; levelIdx < runlevelCount; levelIdx++)
    {
      final LevelStorage levelData = getRunLevel(levelIdx);
      final int level = levelData.getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final Iterator<Function> expressions = levelData.getFunctions();
      while (expressions.hasNext())
      {
        final Expression expression = expressions.next();
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.itemsStarted(event);
          evaluateSingleExpression(expression);
        }
        catch (InvalidReportStateException rse)
        {
          throw rse;
        }
        catch (Exception ex)
        {
          if (logger.isDebugEnabled())
          {
            logger.error("Failed to fire prepare event", ex);
          }
          else
          {
            logger.error("Failed to fire prepare event: " + ex);
          }
          evaluateToNull(expression);
        }

        expression.setRuntime(oldRuntime);

      }
    }
  }

  private void fireItemsFinishedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = getProcessingLevel();
    final ExpressionRuntime runtime = getRuntime();

    final int runlevelCount = getRunLevelCount();
    for (int levelIdx = 0; levelIdx < runlevelCount; levelIdx++)
    {
      final LevelStorage levelData = getRunLevel(levelIdx);
      final int level = levelData.getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final Iterator<Function> expressions = levelData.getFunctions();
      while (expressions.hasNext())
      {
        final Expression expression = expressions.next();
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.itemsFinished(event);
          evaluateSingleExpression(expression);
        }
        catch (InvalidReportStateException rse)
        {
          throw rse;
        }
        catch (Exception ex)
        {
          if (logger.isDebugEnabled())
          {
            logger.error("Failed to fire prepare event", ex);
          }
          else
          {
            logger.error("Failed to fire prepare event: " + ex);
          }
          evaluateToNull(expression);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireGroupStartedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = getProcessingLevel();
    final ExpressionRuntime runtime = getRuntime();

    final int runlevelCount = getRunLevelCount();
    for (int levelIdx = 0; levelIdx < runlevelCount; levelIdx++)
    {
      final LevelStorage levelData = getRunLevel(levelIdx);
      final int level = levelData.getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final Iterator<Function> expressions = levelData.getFunctions();
      while (expressions.hasNext())
      {
        final Expression expression = expressions.next();
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.groupStarted(event);
          evaluateSingleExpression(expression);
        }
        catch (InvalidReportStateException rse)
        {
          throw rse;
        }
        catch (Exception ex)
        {
          if (logger.isDebugEnabled())
          {
            logger.error("Failed to fire group-started event", ex);
          }
          else
          {
            logger.error("Failed to fire group-started event: " + ex);
          }
          evaluateToNull(expression);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireGroupFinishedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = getProcessingLevel();
    final ExpressionRuntime runtime = getRuntime();

    final int runlevelCount = getRunLevelCount();
    for (int levelIdx = 0; levelIdx < runlevelCount; levelIdx++)
    {
      final LevelStorage levelData = getRunLevel(levelIdx);
      final int level = levelData.getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final Iterator<Function> expressions = levelData.getFunctions();
      while (expressions.hasNext())
      {
        final Expression expression = expressions.next();
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.groupFinished(event);
          evaluateSingleExpression(expression);
        }
        catch (InvalidReportStateException rse)
        {
          throw rse;
        }
        catch (Exception ex)
        {
          if (logger.isDebugEnabled())
          {
            logger.error("Failed to fire group-finished event", ex);
          }
          else
          {
            logger.error("Failed to fire group-finished event: " + ex);
          }
          evaluateToNull(expression);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireSummaryRowEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = getProcessingLevel();
    final ExpressionRuntime runtime = getRuntime();

    final int runlevelCount = getRunLevelCount();
    for (int levelIdx = 0; levelIdx < runlevelCount; levelIdx++)
    {
      final LevelStorage levelData = getRunLevel(levelIdx);
      final int level = levelData.getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final Iterator<Function> expressions = levelData.getFunctions();
      while (expressions.hasNext())
      {
        final Expression expression = expressions.next();
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.summaryRowSelection(event);
          evaluateSingleExpression(expression);
        }
        catch (InvalidReportStateException rse)
        {
          throw rse;
        }
        catch (Exception ex)
        {
          if (logger.isDebugEnabled())
          {
            logger.error("Failed to fire group-finished event", ex);
          }
          else
          {
            logger.error("Failed to fire group-finished event: " + ex);
          }
          evaluateToNull(expression);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireReportStartedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = getProcessingLevel();
    final ExpressionRuntime runtime = getRuntime();

    final int runlevelCount = getRunLevelCount();
    for (int levelIdx = 0; levelIdx < runlevelCount; levelIdx++)
    {
      final LevelStorage levelData = getRunLevel(levelIdx);
      final int level = levelData.getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final Iterator<Function> expressions = levelData.getFunctions();
      while (expressions.hasNext())
      {
        final Expression expression = expressions.next();
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.reportStarted(event);
          evaluateSingleExpression(expression);
        }
        catch (InvalidReportStateException rse)
        {
          throw rse;
        }
        catch (Exception ex)
        {
          if (logger.isDebugEnabled())
          {
            logger.error("Failed to fire report-started event", ex);
          }
          else
          {
            logger.error("Failed to fire report-started event: " + ex);
          }
          evaluateToNull(expression);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireReportDoneEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = getProcessingLevel();
    final ExpressionRuntime runtime = getRuntime();

    final int runlevelCount = getRunLevelCount();
    for (int levelIdx = 0; levelIdx < runlevelCount; levelIdx++)
    {
      final LevelStorage levelData = getRunLevel(levelIdx);
      final int level = levelData.getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final Iterator<Function> expressions = levelData.getFunctions();
      while (expressions.hasNext())
      {
        final Expression expression = expressions.next();
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.reportDone(event);
          evaluateSingleExpression(expression);
        }
        catch (InvalidReportStateException rse)
        {
          throw rse;
        }
        catch (Exception ex)
        {
          if (logger.isDebugEnabled())
          {
            logger.error("Failed to fire report-done event", ex);
          }
          else
          {
            logger.error("Failed to fire report-done event: " + ex);
          }
          evaluateToNull(expression);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireReportFinishedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = getProcessingLevel();
    final ExpressionRuntime runtime = getRuntime();

    final int runlevelCount = getRunLevelCount();
    for (int levelIdx = 0; levelIdx < runlevelCount; levelIdx++)
    {
      final LevelStorage levelData = getRunLevel(levelIdx);
      final int level = levelData.getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final Iterator<Function> expressions = levelData.getFunctions();
      while (expressions.hasNext())
      {
        final Expression expression = expressions.next();
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.reportFinished(event);
          evaluateSingleExpression(expression);
        }
        catch (InvalidReportStateException rse)
        {
          throw rse;
        }
        catch (Exception ex)
        {
          if (logger.isDebugEnabled())
          {
            logger.error("Failed to fire report-finished event", ex);
          }
          else
          {
            logger.error("Failed to fire report-finished event: " + ex);
          }
          evaluateToNull(expression);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireReportInitializedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = getProcessingLevel();
    final ExpressionRuntime runtime = getRuntime();

    final int runlevelCount = getRunLevelCount();
    for (int levelIdx = 0; levelIdx < runlevelCount; levelIdx++)
    {
      final LevelStorage levelData = getRunLevel(levelIdx);
      final int level = levelData.getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final Iterator<Function> expressions = levelData.getFunctions();
      while (expressions.hasNext())
      {
        final Expression expression = expressions.next();
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.reportInitialized(event);
          evaluateSingleExpression(expression);
        }
        catch (InvalidReportStateException rse)
        {
          throw rse;
        }
        catch (Exception ex)
        {
          if (logger.isDebugEnabled())
          {
            logger.error("Failed to fire report-initialized event", ex);
          }
          else
          {
            logger.error("Failed to fire report-initialized event: " + ex);
          }
          evaluateToNull(expression);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void firePageStartedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = getProcessingLevel();
    final ExpressionRuntime runtime = getRuntime();

    final int runlevelCount = getRunLevelCount();
    for (int levelIdx = 0; levelIdx < runlevelCount; levelIdx++)
    {
      final LevelStorage levelData = getRunLevel(levelIdx);
      final int level = levelData.getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final Iterator<Function> expressions = levelData.getPageFunctions();
      while (expressions.hasNext())
      {
        final Expression expression = expressions.next();
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final PageEventListener e = (PageEventListener) expression;
        try
        {
          e.pageStarted(event);
          evaluateSingleExpression(expression);
        }
        catch (InvalidReportStateException rse)
        {
          throw rse;
        }
        catch (Exception ex)
        {
          if (logger.isDebugEnabled())
          {
            logger.error("Failed to fire page-started event", ex);
          }
          else
          {
            logger.error("Failed to fire page-started event: " + ex);
          }
          evaluateToNull(expression);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void firePageFinishedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = getProcessingLevel();
    final ExpressionRuntime runtime = getRuntime();

    final int runlevelCount = getRunLevelCount();
    for (int levelIdx = 0; levelIdx < runlevelCount; levelIdx++)
    {
      final LevelStorage levelData = getRunLevel(levelIdx);
      final int level = levelData.getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final Iterator<Function> expressions = levelData.getPageFunctions();
      while (expressions.hasNext())
      {
        final Expression expression = expressions.next();
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final PageEventListener e = (PageEventListener) expression;
        try
        {
          e.pageFinished(event);
          evaluateSingleExpression(expression);
        }
        catch (InvalidReportStateException rse)
        {
          throw rse;
        }
        catch (Exception ex)
        {
          if (logger.isDebugEnabled())
          {
            logger.error("Failed to fire page-finished event: ", ex);
          }
          else
          {
            logger.error("Failed to fire page-finished event: " + ex);
          }
          evaluateToNull(expression);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  protected void reactivateExpressions(final boolean deepTraversing)
  {
    final int activeLevel = getProcessingLevel();
    final ExpressionRuntime runtime = getRuntime();

    final int runlevelCount = getRunLevelCount();
    for (int levelIdx = 0; levelIdx < runlevelCount; levelIdx++)
    {
      final LevelStorage levelData = getRunLevel(levelIdx);
      final int level = levelData.getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final Iterator<Expression> expressions = levelData.getActiveExpressions();
      while (expressions.hasNext())
      {
        final Expression expression = expressions.next();
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        evaluateSingleExpression(expression);
        expression.setRuntime(oldRuntime);
      }
    }
  }

  protected void evaluateSingleExpression(final Expression expression)
  {
    final int activeLevel = getProcessingLevel();
    final String name = expression.getName();
    Object value;
    try
    {
      if (activeLevel <= expression.getDependencyLevel())
      {
        value = expression.getValue();
      }
      else
      {
        value = null;
      }
    }
    catch (InvalidReportStateException fe)
    {
      throw fe;
    }
    catch (Exception e)
    {
      logger.info("Evaluation of expression '" + name + "'failed.", e);
      value = null;
    }

    if (name != null)
    {
//      DebugLog.log("Eval Expression: " + name + " " + value);
      updateMasterDataRow(name, value);
    }
  }

  protected void evaluateToNull(final Expression expression)
  {
    final String name = expression.getName();
    if (name != null)
    {
      updateMasterDataRow(name, null);
    }
  }

  protected void updateMasterDataRow(final String name, final Object value)
  {
  }

}
