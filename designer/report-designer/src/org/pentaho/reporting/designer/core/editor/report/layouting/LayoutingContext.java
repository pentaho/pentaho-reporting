package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchema;

public class LayoutingContext
{
  private DesignerOutputProcessor outputProcessor;
  private OutputProcessorMetaData metaData;
  private DesignerExpressionRuntime runtime;

  public LayoutingContext(final MasterReport report)
  {
    this.outputProcessor = new DesignerOutputProcessor();
    this.metaData = outputProcessor.getMetaData();
    this.metaData.initialize(report.getConfiguration());
    
    final DefaultDataSchema schema = new DefaultDataSchema();
    final DataRow dataRow = new StaticDataRow();
    this.runtime = new DesignerExpressionRuntime(dataRow, schema, report);
  }

  public DesignerOutputProcessor getOutputProcessor()
  {
    return outputProcessor;
  }

  public OutputProcessorMetaData getMetaData()
  {
    return metaData;
  }

  public DesignerExpressionRuntime getRuntime()
  {
    return runtime;
  }
}
