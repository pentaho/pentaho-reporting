/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast.validator;

import java.util.HashSet;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.LayoutProcessorFunction;
import org.pentaho.reporting.engine.classic.core.function.PageFunction;
import org.pentaho.reporting.engine.classic.core.function.RowBandingFunction;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.AbstractStructureVisitor;
import org.pentaho.reporting.engine.classic.core.wizard.RelationalAutoGeneratorPreProcessor;

/**
 * Filter out reports that have any kind of visible style expressions, inline subreport or graphical elements.
 * Also filter reports that utilize any of the formatting functions, except for the row-banding function.
 */
public class ReportStructureValidator extends AbstractStructureVisitor
{
  private boolean valid;
  private HashSet<String> preProcessorWhiteList;

  public ReportStructureValidator()
  {
    preProcessorWhiteList = new HashSet<String>();
    preProcessorWhiteList.add("org.pentaho.reporting.engine.classic.wizard.WizardProcessor");
    preProcessorWhiteList.add(RelationalAutoGeneratorPreProcessor.class.getName());
  }

  public boolean isValidForFastProcessing(MasterReport report)
  {
    valid = true;
    traverseSection(report);
    return valid;
  }

  protected void traverseSection(final Section section)
  {
    traverseSectionWithSubReports(section);
  }

  private boolean checkInlineSubReport(final AbstractReportDefinition reportDefinition)
  {
    Section parentSection = reportDefinition.getParentSection();
    if (parentSection instanceof RootLevelBand == false)
    {
      return true;
    }

    RootLevelBand rlb = (RootLevelBand) parentSection;
    for (SubReport s : rlb.getSubReports())
    {
      if (s == reportDefinition)
      {
        return false;
      }
    }

    return true;
  }

  protected void inspectElement(final ReportElement element)
  {
    traverseStyleExpressions(element);

    if (element instanceof AbstractReportDefinition)
    {
      AbstractReportDefinition report = (AbstractReportDefinition) element;
      for (ReportPreProcessor reportPreProcessor : report.getPreProcessors())
      {
        if (preProcessorWhiteList.contains(reportPreProcessor.getClass().getName()))
        {
          continue;
        }
        valid = false;
      }

      if (report instanceof SubReport)
      {
        if (checkInlineSubReport(report))
        {
          valid = false;
          return;
        }
        return;
      }
    }

    if (element instanceof CrosstabGroup)
    {
      valid = false;
    }
  }

  protected void inspectStyleExpression(final ReportElement element,
                                        final StyleKey styleKey,
                                        final Expression expression,
                                        final ExpressionMetaData expressionMetaData)
  {
    if (isCriticalLayoutStyle(styleKey))
    {
      valid = false;
      return;
    }

    if (element instanceof RootLevelBand)
    {
      return;
    }

    if (styleKey.equals(ElementStyleKeys.VISIBLE))
    {
      valid = false;
    }
  }

  private boolean isCriticalLayoutStyle(final StyleKey styleKey)
  {
    if (styleKey.equals(BandStyleKeys.LAYOUT))
    {
      return true;
    }
    if (styleKey.equals(BandStyleKeys.FIXED_POSITION))
    {
      return true;
    }
    if (styleKey.equals(ElementStyleKeys.BORDER_BOTTOM_STYLE))
    {
      return true;
    }
    if (styleKey.equals(ElementStyleKeys.BORDER_TOP_STYLE))
    {
      return true;
    }
    if (styleKey.equals(ElementStyleKeys.BORDER_LEFT_STYLE))
    {
      return true;
    }
    if (styleKey.equals(ElementStyleKeys.BORDER_RIGHT_STYLE))
    {
      return true;
    }
    if (styleKey.equals(ElementStyleKeys.BORDER_BOTTOM_WIDTH))
    {
      return true;
    }
    if (styleKey.equals(ElementStyleKeys.BORDER_TOP_WIDTH))
    {
      return true;
    }
    if (styleKey.equals(ElementStyleKeys.BORDER_LEFT_WIDTH))
    {
      return true;
    }
    if (styleKey.equals(ElementStyleKeys.BORDER_RIGHT_WIDTH))
    {
      return true;
    }

    if (styleKey.equals(ElementStyleKeys.PADDING_TOP))
    {
      return true;
    }
    if (styleKey.equals(ElementStyleKeys.PADDING_LEFT))
    {
      return true;
    }
    if (styleKey.equals(ElementStyleKeys.PADDING_BOTTOM))
    {
      return true;
    }
    if (styleKey.equals(ElementStyleKeys.PADDING_RIGHT))
    {
      return true;
    }

    return false;
  }

  protected void inspectExpression(final AbstractReportDefinition report, final Expression expression)
  {
    super.inspectExpression(report, expression);
    if (expression instanceof RowBandingFunction)
    {
      // later we can add code to handle row-banding safely.
      valid = false;
      return;
    }
    if (expression instanceof LayoutProcessorFunction)
    {
      valid = false;
    }
    if (expression instanceof PageEventListener)
    {
      if (expression instanceof PageFunction == false)
      {
        valid = false;
      }
    }
  }
}
