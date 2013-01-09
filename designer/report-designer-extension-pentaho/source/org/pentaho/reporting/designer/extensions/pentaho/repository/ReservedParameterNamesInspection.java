package org.pentaho.reporting.designer.extensions.pentaho.repository;

import java.util.HashSet;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.inspections.Inspection;
import org.pentaho.reporting.designer.core.inspections.InspectionResult;
import org.pentaho.reporting.designer.core.inspections.InspectionResultListener;
import org.pentaho.reporting.designer.core.inspections.ParameterLocationInfo;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;

public class ReservedParameterNamesInspection implements Inspection
{
  private HashSet<String> reservedParameterNames;

  public ReservedParameterNamesInspection()
  {
    reservedParameterNames = new HashSet<String>();
    reservedParameterNames.add("solution");
    reservedParameterNames.add("name");
    reservedParameterNames.add("path");
    reservedParameterNames.add("action");
    reservedParameterNames.add("renderMode");
    reservedParameterNames.add("paginate");
    reservedParameterNames.add("autoSubmit");
    reservedParameterNames.add("subscribe");
    reservedParameterNames.add("subscription-id");
    reservedParameterNames.add("subscription-name");
    reservedParameterNames.add("destination");
    reservedParameterNames.add("output-type");
    reservedParameterNames.add("output-target");
    reservedParameterNames.add("report-definition");
    reservedParameterNames.add("useContentRepository");
    reservedParameterNames.add("accepted-page");
    reservedParameterNames.add("print");
    reservedParameterNames.add("printer-name");
    reservedParameterNames.add("content-handler-pattern");
    reservedParameterNames.add("workbook");
    reservedParameterNames.add("res-url");

  }

  /**
   * The inspection is cheap enough to be run constantly while editing.
   *
   * @return true, if it can run while the editing is running, false otherwise.
   */
  public boolean isInlineInspection()
  {
    return true;
  }

  public void inspect(final ReportDesignerContext designerContext,
                      final ReportRenderContext reportRenderContext,
                      final InspectionResultListener resultHandler) throws ReportDataFactoryException
  {
    final AbstractReportDefinition abstractReportDefinition = reportRenderContext.getReportDefinition();
    if (abstractReportDefinition instanceof MasterReport == false)
    {
      return;
    }

    final MasterReport report = (MasterReport) abstractReportDefinition;
    final ReportParameterDefinition definition = report.getParameterDefinition();
    final ParameterDefinitionEntry[] parameterDefinitionEntries = definition.getParameterDefinitions();
    for (int i = 0; i < parameterDefinitionEntries.length; i++)
    {
      final ParameterDefinitionEntry definitionEntry = parameterDefinitionEntries[i];
      if (reservedParameterNames.contains(definitionEntry.getName()))
      {
        resultHandler.notifyInspectionResult(new InspectionResult(this, InspectionResult.Severity.WARNING,
            Messages.getInstance().formatMessage
                ("ReservedParameterNamesInspection.ReservedParameterNameUsed", definitionEntry.getName()),
            new ParameterLocationInfo(definitionEntry)));
      }
    }
  }
}
