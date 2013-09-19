package org.pentaho.reporting.engine.classic.core.parameters;

import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.GenericOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.states.datarow.EmptyTableModel;
import org.pentaho.reporting.libraries.base.config.Configuration;

public class ParameterExpressionRuntime implements ExpressionRuntime
{
  private DefaultDataSchema dataSchema;
  private DataRow dataRow;
  private ProcessingContext processingContext;
  private TableModel model;
  private DataFactory dataFactory;

  public ParameterExpressionRuntime(final ParameterContext parameterContext,
                                    final DataRow dataRow)
      throws ReportProcessingException
  {
    if (parameterContext == null)
    {
      throw new NullPointerException();
    }
    if (dataRow == null)
    {
      throw new NullPointerException();
    }

    this.dataFactory = parameterContext.getDataFactory();
    this.model = new EmptyTableModel();
    this.dataSchema = new DefaultDataSchema();
    this.dataRow = dataRow;
    this.processingContext = new DefaultProcessingContext
        (new GenericOutputProcessorMetaData(),
            parameterContext.getResourceBundleFactory(),
            parameterContext.getConfiguration(),
            parameterContext.getResourceManager(),
            parameterContext.getContentBase(),
            parameterContext.getDocumentMetaData(),
            parameterContext.getReportEnvironment(), -1);
  }

  public DataFactory getDataFactory()
  {
    return dataFactory;
  }

  /**
   * Returns the current data-row. The datarow can be used to access the computed values of all expressions and
   * functions and the current row in the tablemodel.
   *
   * @return the data-row.
   */
  public DataRow getDataRow()
  {
    return dataRow;
  }

  public DataSchema getDataSchema()
  {
    return dataSchema;
  }

  /**
   * Returns the report configuration that was used to initiate this processing run.
   *
   * @return the report configuration.
   */
  public Configuration getConfiguration()
  {
    return getProcessingContext().getConfiguration();
  }

  /**
   * Returns the resource-bundle factory of current processing context.
   *
   * @return the current resource-bundle factory.
   */
  public ResourceBundleFactory getResourceBundleFactory()
  {
    return getProcessingContext().getResourceBundleFactory();
  }

  /**
   * Grants access to the tablemodel was granted using report properties, now direct.
   *
   * @return the current tablemodel used in the report.
   */
  public TableModel getData()
  {
    return model;
  }

  /**
   * Returns the number of the row in the tablemodel that is currently being processed.
   *
   * @return the current row number.
   */
  public int getCurrentRow()
  {
    return -1;
  }

  public int getCurrentDataItem()
  {
    return -1;
  }

  public int getCurrentGroup()
  {
    return -1;
  }

  public int getGroupStartRow(final String groupName)
  {
    return 0;
  }

  public int getGroupStartRow(final int groupIndex)
  {
    return 0;
  }

  /**
   * Returns the current export descriptor as returned by the OutputProcessorMetaData object. The output descriptor is
   * a simple string collections consisting of the following components: exportclass/type/subtype
   * <p/>
   * For example, the PDF export would be: pageable/pdf and the StreamHTML export would return table/html/stream
   *
   * @return the export descriptor.
   */
  public String getExportDescriptor()
  {
    return getProcessingContext().getExportDescriptor();
  }

  /**
   * Returns the current processing context.
   *
   * @return the processing context.
   */
  public ProcessingContext getProcessingContext()
  {
    return processingContext;
  }

  public boolean isStructuralComplexReport()
  {
    return false;
  }

  public boolean isCrosstabActive()
  {
    return false;
  }
}
