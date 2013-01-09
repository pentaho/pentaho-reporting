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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.states.datarow.ExpressionEventHelper;
import org.pentaho.reporting.engine.classic.core.states.datarow.InlineDataRowRuntime;
import org.pentaho.reporting.engine.classic.core.states.datarow.LevelStorage;
import org.pentaho.reporting.engine.classic.core.states.datarow.OutputFunctionLevelStorage;

public class InitialLayoutProcess extends ExpressionEventHelper implements LayoutProcess
{
  private static final StructureFunction[] EMPTY_FUNCTIONS = new StructureFunction[0];

  private InlineDataRowRuntime inlineDataRowRuntime;
  private OutputFunction outputFunction;
  private boolean outputFunctionIsPageListener;

  public InitialLayoutProcess(final OutputFunction outputFunction)
  {
    if (outputFunction == null)
    {
      throw new NullPointerException();
    }

    this.outputFunction = outputFunction;
    this.outputFunctionIsPageListener = (outputFunction instanceof PageEventListener);
  }

  public LayoutProcess getParent()
  {
    return null;
  }

  public boolean isPageListener()
  {
    return outputFunctionIsPageListener;
  }

  public OutputFunction getOutputFunction()
  {
    return outputFunction;
  }

  public void restart(final ReportState state) throws ReportProcessingException
  {
    if (inlineDataRowRuntime == null)
    {
      inlineDataRowRuntime = new InlineDataRowRuntime();
    }
    inlineDataRowRuntime.setState(state);

    final ExpressionRuntime oldRuntime;
    final OutputFunction outputFunction = getOutputFunction();
    if (outputFunction != null)
    {
      oldRuntime = outputFunction.getRuntime();
      outputFunction.setRuntime(inlineDataRowRuntime);
    }
    else
    {
      oldRuntime = null;
    }

    try
    {
      if (outputFunction != null)
      {
        outputFunction.restart(state);
      }
    }
    finally
    {
      if (outputFunction != null)
      {
        outputFunction.setRuntime(oldRuntime);
      }
    }
  }

  public StructureFunction[] getCollectionFunctions()
  {
    return EMPTY_FUNCTIONS;
  }

  public LayoutProcess deriveForStorage()
  {
    try
    {
      final InitialLayoutProcess lp = (InitialLayoutProcess) super.clone();
      lp.inlineDataRowRuntime = null;
      lp.outputFunction = outputFunction.deriveForStorage();
      return lp;
    }
    catch (final CloneNotSupportedException e)
    {
      throw new IllegalStateException();
    }
  }

  public LayoutProcess deriveForPagebreak()
  {
    try
    {
      final InitialLayoutProcess lp = (InitialLayoutProcess) super.clone();
      lp.inlineDataRowRuntime = null;
      lp.outputFunction = outputFunction.deriveForPagebreak();
      return lp;
    }
    catch (final CloneNotSupportedException e)
    {
      throw new IllegalStateException();
    }
  }

  public Object clone()
  {
    try
    {
      final InitialLayoutProcess lp = (InitialLayoutProcess) super.clone();
      lp.inlineDataRowRuntime = null;
      lp.outputFunction = (OutputFunction) outputFunction.clone();
      return lp;
    }
    catch (CloneNotSupportedException cne)
    {
      throw new IllegalStateException(cne);
    }
  }

  protected int getRunLevelCount()
  {
    return 1;
  }

  protected LevelStorage getRunLevel(final int index)
  {
    if (index != 0)
    {
      throw new IndexOutOfBoundsException();
    }
    return new OutputFunctionLevelStorage(LayoutProcess.LEVEL_PAGINATE, outputFunction, outputFunctionIsPageListener);
  }

  protected ExpressionRuntime getRuntime()
  {
    return inlineDataRowRuntime;
  }

  public void fireReportEvent(final ReportEvent event)
  {
    if (inlineDataRowRuntime == null)
    {
      inlineDataRowRuntime = new InlineDataRowRuntime();
    }
    final ReportState state = inlineDataRowRuntime.getState();
    inlineDataRowRuntime.setState(event.getState());

    try
    {
      super.fireReportEvent(event);
    }
    catch (InvalidReportStateException exception)
    {
      throw exception;
    }
    catch (Throwable t)
    {
      throw new InvalidReportStateException("Failed to fire report event for sub-layout-process", t);
    }
    finally
    {
      inlineDataRowRuntime.setState(state);
    }
  }
}
