package org.pentaho.reporting.engine.classic.core.function;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchema;
import org.pentaho.reporting.libraries.base.config.Configuration;

public class GenericExpressionRuntime implements ExpressionRuntime
{
  private DataRow dataRow;
  private Configuration configuration;
  private ResourceBundleFactory resourceBundleFactory;
  private TableModel data;
  private int currentRow;
  private ProcessingContext processingContext;
  private DataSchema dataSchema;
  private DataFactory dataFactory;

  public GenericExpressionRuntime()
  {
    this (new DefaultTableModel(), 0, new DefaultProcessingContext());
  }

  public GenericExpressionRuntime(final TableModel data,
                                final int currentRow,
                                final ProcessingContext processingContext)
  {
    this (new StaticDataRow(), data, currentRow, processingContext);
  }

  public GenericExpressionRuntime(final DataRow dataRow,
                                  final TableModel data,
                                  final int currentRow,
                                  final ProcessingContext processingContext)
  {
    if (processingContext == null)
    {
      throw new NullPointerException();
    }
    if (dataRow == null)
    {
      throw new NullPointerException();
    }
    if (data == null)
    {
      throw new NullPointerException();
    }

    this.processingContext = processingContext;
    this.data = data;
    this.currentRow = currentRow;
    this.dataRow = dataRow;
    this.configuration = this.processingContext.getConfiguration();
    this.resourceBundleFactory = processingContext.getResourceBundleFactory();
    this.dataSchema = new DefaultDataSchema();
    this.dataFactory = new CompoundDataFactory();
  }

  public GenericExpressionRuntime(final ExpressionRuntime runtime)
  {
       this(new StaticDataRow(runtime.getDataRow()),runtime.getData(),runtime.getCurrentRow(),runtime.getProcessingContext());
  }

  public DataFactory getDataFactory()
  {
    return dataFactory;
  }

  public DataSchema getDataSchema()
  {
    return dataSchema;
  }

  public DataRow getDataRow()
  {
    return dataRow;
  }

  public Configuration getConfiguration()
  {
    return configuration;
  }

  public ResourceBundleFactory getResourceBundleFactory()
  {
    return resourceBundleFactory;
  }

  /**
   * Access to the tablemodel was granted using report properties, now direct.
   */
  public TableModel getData()
  {
    return data;
  }

  /**
   * Where are we in the current processing.
   */
  public int getCurrentRow()
  {
    return currentRow;
  }

  public int getCurrentDataItem()
  {
    return currentRow;
  }

  /**
   * The output descriptor is a simple string collections consisting of the following components:
   * exportclass/type/subtype
   * <p/>
   * For example, the PDF export would be: pageable/pdf The StreamHTML export would return table/html/stream
   *
   * @return the export descriptor.
   */
  public String getExportDescriptor()
  {
    return processingContext.getExportDescriptor();
  }

  public ProcessingContext getProcessingContext()
  {
    return processingContext;
  }

  public int getCurrentGroup()
  {
    return 0;
  }

  public int getGroupStartRow(final String groupName)
  {
    return 0;
  }

  public int getGroupStartRow(final int groupIndex)
  {
    return 0;
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
