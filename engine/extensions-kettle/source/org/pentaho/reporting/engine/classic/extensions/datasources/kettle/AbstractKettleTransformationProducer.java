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
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.table.TableModel;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.RowListener;
import org.pentaho.di.trans.step.StepMetaDataCombi;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public abstract class AbstractKettleTransformationProducer implements KettleTransformationProducer
{
  private static final long serialVersionUID = -2287953597208384424L;

  protected static class TableProducer implements RowListener
  {
    private TypedTableModel tableModel;
    private int rowsWritten;
    private RowMetaInterface rowMeta;
    private int queryLimit;
    private boolean stopOnError;

    private boolean firstCall;
    private boolean error;

    private TableProducer(final RowMetaInterface rowMeta, final int queryLimit, final boolean stopOnError)
    {
      this.rowMeta = rowMeta;
      this.queryLimit = queryLimit;
      this.stopOnError = stopOnError;
      this.firstCall = true;
    }

    /**
     * This method is called when a row is written to another step (even if there is no next step)
     *
     * @param rowMeta the metadata of the row
     * @param row     the data of the row
     * @throws KettleStepException an exception that can be thrown to hard stop the step
     */
    public void rowWrittenEvent(final RowMetaInterface rowMeta, final Object[] row) throws KettleStepException
    {
      if (firstCall)
      {
        this.tableModel = createTableModel(rowMeta);
        firstCall = false;
      }

      if (queryLimit > 0 && rowsWritten > queryLimit)
      {
        return;
      }

      try
      {
        rowsWritten += 1;

        final int count = tableModel.getColumnCount();
        final Object dataRow[] = new Object[count];
        for (int columnNo = 0; columnNo < count; columnNo++)
        {
          final ValueMetaInterface valueMeta = rowMeta.getValueMeta(columnNo);

          switch (valueMeta.getType())
          {
            case ValueMetaInterface.TYPE_BIGNUMBER:
              dataRow[columnNo] = rowMeta.getBigNumber(row, columnNo);
              break;
            case ValueMetaInterface.TYPE_BOOLEAN:
              dataRow[columnNo] = rowMeta.getBoolean(row, columnNo);
              break;
            case ValueMetaInterface.TYPE_DATE:
              dataRow[columnNo] = rowMeta.getDate(row, columnNo);
              break;
            case ValueMetaInterface.TYPE_INTEGER:
              dataRow[columnNo] = rowMeta.getInteger(row, columnNo);
              break;
            case ValueMetaInterface.TYPE_NONE:
              dataRow[columnNo] = rowMeta.getString(row, columnNo);
              break;
            case ValueMetaInterface.TYPE_NUMBER:
              dataRow[columnNo] = rowMeta.getNumber(row, columnNo);
              break;
            case ValueMetaInterface.TYPE_STRING:
              dataRow[columnNo] = rowMeta.getString(row, columnNo);
              break;
            case ValueMetaInterface.TYPE_BINARY:
              dataRow[columnNo] = rowMeta.getBinary(row, columnNo);
              break;
            default:
              dataRow[columnNo] = rowMeta.getString(row, columnNo);
          }
        }
        tableModel.addRow(dataRow);
      }
      catch (KettleValueException kve)
      {
        throw new KettleStepException(kve);
      }
      catch (Exception e)
      {
        throw new KettleStepException(e);
      }
    }

    private TypedTableModel createTableModel(final RowMetaInterface rowMeta)
    {
      final int colCount = rowMeta.size();
      final String fieldNames[] = new String[colCount];
      final Class<?> fieldTypes[] = new Class<?>[colCount];
      for (int columnNo = 0; columnNo < colCount; columnNo++)
      {
        final ValueMetaInterface valueMeta = rowMeta.getValueMeta(columnNo);
        fieldNames[columnNo] = valueMeta.getName();

        switch (valueMeta.getType())
        {
          case ValueMetaInterface.TYPE_BIGNUMBER:
            fieldTypes[columnNo] = BigDecimal.class;
            break;
          case ValueMetaInterface.TYPE_BOOLEAN:
            fieldTypes[columnNo] = Boolean.class;
            break;
          case ValueMetaInterface.TYPE_DATE:
            fieldTypes[columnNo] = Date.class;
            break;
          case ValueMetaInterface.TYPE_INTEGER:
            fieldTypes[columnNo] = Integer.class;
            break;
          case ValueMetaInterface.TYPE_NONE:
            fieldTypes[columnNo] = String.class;
            break;
          case ValueMetaInterface.TYPE_NUMBER:
            fieldTypes[columnNo] = Double.class;
            break;
          case ValueMetaInterface.TYPE_STRING:
            fieldTypes[columnNo] = String.class;
            break;
          case ValueMetaInterface.TYPE_BINARY:
            fieldTypes[columnNo] = byte[].class;
            break;
          default:
            fieldTypes[columnNo] = String.class;
        }

      }
      return new TypedTableModel(fieldNames, fieldTypes);
    }

    /**
     * This method is called when a row is read from another step
     *
     * @param rowMeta the metadata of the row
     * @param row     the data of the row
     * @throws KettleStepException an exception that can be thrown to hard stop the step
     */
    public void rowReadEvent(final RowMetaInterface rowMeta, final Object[] row) throws KettleStepException
    {
    }

    /**
     * This method is called when the error handling of a row is writing a row to the error stream.
     *
     * @param rowMeta the metadata of the row
     * @param row     the data of the row
     * @throws KettleStepException an exception that can be thrown to hard stop the step
     */
    public void errorRowWrittenEvent(final RowMetaInterface rowMeta, final Object[] row) throws KettleStepException
    {
      if (stopOnError)
      {
        throw new KettleStepException("Aborting transformation due to error detected");
      }
      error = true;
    }

    public TableModel getTableModel() throws ReportDataFactoryException
    {
      if (stopOnError && error)
      {
        throw new ReportDataFactoryException("Transformation produced an error.");
      }

      if (tableModel == null)
      {
        return createTableModel(rowMeta);
      }
      return tableModel;
    }
  }

  private String stepName;
  private String username;
  private String password;
  private String repositoryName;
  private String[] definedArgumentNames;
  private ParameterMapping[] definedVariableNames;
  private transient Trans currentlyRunningTransformation;
  private boolean stopOnError;

  public AbstractKettleTransformationProducer(final String repositoryName,
                                              final String stepName,
                                              final String username,
                                              final String password,
                                              final String[] definedArgumentNames,
                                              final ParameterMapping[] definedVariableNames)
  {
    if (repositoryName == null)
    {
      throw new NullPointerException();
    }
    if (definedArgumentNames == null)
    {
      throw new NullPointerException();
    }
    if (definedVariableNames == null)
    {
      throw new NullPointerException();
    }

    this.repositoryName = repositoryName;
    this.stepName = stepName;
    this.username = username;
    this.password = password;
    this.definedArgumentNames = definedArgumentNames.clone();
    this.definedVariableNames = definedVariableNames.clone();

  }

  public boolean isStopOnError()
  {
    return stopOnError;
  }

  public void setStopOnError(final boolean stopOnError)
  {
    this.stopOnError = stopOnError;
  }

  public String getStepName()
  {
    return stepName;
  }

  public String getUsername()
  {
    return username;
  }

  public String getPassword()
  {
    return password;
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public String[] getDefinedArgumentNames()
  {
    return definedArgumentNames.clone();
  }

  public ParameterMapping[] getDefinedVariableNames()
  {
    return definedVariableNames.clone();
  }

  public Object clone()
  {
    try
    {
      final AbstractKettleTransformationProducer prod = (AbstractKettleTransformationProducer) super.clone();
      prod.definedArgumentNames = definedArgumentNames.clone();
      prod.definedVariableNames = definedVariableNames.clone();
      prod.currentlyRunningTransformation = null;
      return prod;
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public TableModel performQuery(final DataRow parameters,
                                 final int queryLimit,
                                 final ResourceManager resourceManager,
                                 final ResourceKey resourceKey)
      throws KettleException, ReportDataFactoryException
  {
    if (getStepName() == null)
    {
      throw new ReportDataFactoryException("No step name defined.");
    }

    final String[] params = fillArguments(parameters);

    try
    {
      final Repository repository = connectToRepository();
      try
      {
        final TransMeta transMeta = loadTransformation(repository, resourceManager, resourceKey);
        transMeta.setArguments(params);
        final Trans trans = new Trans(transMeta);
        for (int i = 0; i < definedVariableNames.length; i++)
        {
          final ParameterMapping mapping = definedVariableNames[i];
          final String sourceName = mapping.getName();
          final String variableName = mapping.getAlias();
          final Object value = parameters.get(sourceName);
          if (value != null)
          {
            trans.setParameterValue(variableName, String.valueOf(value));
          }
        }

        transMeta.setInternalKettleVariables();
        trans.prepareExecution(transMeta.getArguments());

        TableProducer tableProducer = null;
      final List<StepMetaDataCombi> stepList = trans.getSteps();
        for (int i = 0; i < stepList.size(); i++)
        {
          final StepMetaDataCombi metaDataCombi = (StepMetaDataCombi) stepList.get(i);
          if (stepName.equals(metaDataCombi.stepname) == false)
          {
            continue;
          }
          final RowMetaInterface row = transMeta.getStepFields(stepName);
          tableProducer = new TableProducer(row, queryLimit, stopOnError);
          metaDataCombi.step.addRowListener(tableProducer);
          break;
        }

        if (tableProducer == null)
        {
          throw new ReportDataFactoryException("Cannot find the specified transformation step " + stepName);
        }

        currentlyRunningTransformation = trans;
        trans.startThreads();
        trans.waitUntilFinished();
        trans.cleanup();
        return tableProducer.getTableModel();
      }
      finally
      {
        currentlyRunningTransformation = null;
        if (repository != null)
        {
          repository.disconnect();
        }
      }
    }
    finally
    {
    }
  }

  private String[] fillArguments(final DataRow parameters)
  {
    final String[] params = new String[definedArgumentNames.length];
    for (int i = 0; i < definedArgumentNames.length; i++)
    {
      final String name = definedArgumentNames[i];
      final Object value = parameters.get(name);
      if (value == null)
      {
        params[i] = null;
      }
      else
      {
        params[i] = String.valueOf(value);
      }
    }
    return params;
  }


  private Repository connectToRepository()
      throws ReportDataFactoryException, KettleException
  {
    if (repositoryName == null)
    {
      throw new NullPointerException();
    }

    final RepositoriesMeta repositoriesMeta = new RepositoriesMeta();
    try
    {
      repositoriesMeta.readData();
    }
    catch (KettleException ke)
    {
      // we're a bit low to bubble a dialog to the user here..
      // when ramaiz fixes readData() to stop throwing exceptions
      // even when successful we can remove this and use
      // the more favorable repositoriesMeta.getException() or something
      // like it (I'm guessing on the method name)
    }

    // Find the specified repository.
    final RepositoryMeta repositoryMeta = repositoriesMeta.findRepository(repositoryName);

    if (repositoryMeta == null)
    {
      // repository object is not necessary for filesystem transformations
      return null;
      // throw new ReportDataFactoryException("The specified repository " + repositoryName + " is not defined.");
    }

    final Repository repository = PluginRegistry.getInstance().loadClass(RepositoryPluginType.class, repositoryMeta.getId(), Repository.class);
    repository.init(repositoryMeta);
    repository.connect(username, password);
    return repository;
  }

  protected abstract TransMeta loadTransformation(Repository repository,
                                                  ResourceManager resourceManager,
                                                  ResourceKey contextKey)
      throws ReportDataFactoryException, KettleException;

  public void cancelQuery()
  {
    final Trans currentlyRunningTransformation = this.currentlyRunningTransformation;
    if (currentlyRunningTransformation != null)
    {
      currentlyRunningTransformation.stopAll();
      this.currentlyRunningTransformation = null;
    }
  }

  public String[] getReferencedFields()
  {
    final LinkedHashSet<String> retval = new LinkedHashSet<String>();
    retval.addAll(Arrays.asList(definedArgumentNames));
    for (final ParameterMapping parameter : definedVariableNames)
    {
      retval.add(parameter.getName());
    }
    retval.add(DataFactory.QUERY_LIMIT);
    return retval.toArray(new String[retval.size()]);
  }

  protected ArrayList<Object> internalGetQueryHash()
  {
    final ArrayList<Object> retval = new ArrayList<Object>();
    retval.add(getClass().getName());
    retval.add(getUsername());
    retval.add(getPassword());
    retval.add(getStepName());
    retval.add(isStopOnError());
    retval.add(getRepositoryName());
    retval.add(new ArrayList<String>(Arrays.asList(getDefinedArgumentNames())));
    retval.add(new ArrayList<ParameterMapping>(Arrays.asList(getDefinedVariableNames())));
    return retval;
  }
}
