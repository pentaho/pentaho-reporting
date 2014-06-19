/*!
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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.table.TableModel;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMetaDataCombi;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.parser.ParseException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public abstract class AbstractKettleTransformationProducer implements KettleTransformationProducer
{
  private static final long serialVersionUID = -2287953597208384424L;

  private String stepName;
  private String username;
  private String password;
  private String repositoryName;
  private FormulaArgument[] arguments;
  private FormulaParameter[] parameter;
  private transient Trans currentlyRunningTransformation;
  private boolean stopOnError;

  @Deprecated
  public AbstractKettleTransformationProducer(final String repositoryName,
                                              final String stepName,
                                              final String username,
                                              final String password,
                                              final String[] definedArgumentNames,
                                              final ParameterMapping[] definedVariableNames)
  {
    this(repositoryName, stepName, username, password,
        FormulaArgument.convert(definedArgumentNames),
        FormulaParameter.convert(definedVariableNames));
  }

  protected AbstractKettleTransformationProducer(final String repositoryName,
                                                 final String stepName,
                                                 final String username,
                                                 final String password,
                                                 final FormulaArgument[] definedArgumentNames,
                                                 final FormulaParameter[] definedVariableNames)
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
    this.arguments = definedArgumentNames.clone();
    this.parameter = definedVariableNames.clone();

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
    return FormulaArgument.convert(arguments);
  }

  public ParameterMapping[] getDefinedVariableNames()
  {
    return FormulaParameter.convert(parameter);
  }

  public FormulaArgument[] getArguments()
  {
    return arguments.clone();
  }

  public FormulaParameter[] getParameter()
  {
    return parameter.clone();
  }

  public Object clone()
  {
    try
    {
      final AbstractKettleTransformationProducer prod = (AbstractKettleTransformationProducer) super.clone();
      prod.arguments = arguments.clone();
      prod.parameter = parameter.clone();
      prod.currentlyRunningTransformation = null;
      return prod;
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public TransMeta loadTransformation(final DataFactoryContext context)
      throws KettleException, ReportDataFactoryException
  {
    final Repository repository = connectToRepository();
    try
    {
      return loadTransformation(repository, context.getResourceManager(), context.getContextKey());
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

  public TableModel performQuery(final DataRow parameters,
                                 final int queryLimit,
                                 final DataFactoryContext context)
      throws KettleException, ReportDataFactoryException
  {
    if (getStepName() == null)
    {
      throw new ReportDataFactoryException("No step name defined.");
    }

    final Repository repository = connectToRepository();
    try
    {
      final TransMeta transMeta = loadTransformation(repository, context.getResourceManager(), context.getContextKey());
      final FormulaContext formulaContext = new WrappingFormulaContext(context.getFormulaContext(), parameters);
      final String[] params = fillArguments(formulaContext);

      final Trans trans = new Trans(transMeta);
      trans.setArguments(params);
      updateTransformationParameter(formulaContext, trans);
      transMeta.setInternalKettleVariables();
      trans.prepareExecution(params);

      StepInterface targetStep = findTargetStep(trans);
      if (targetStep == null)
      {
        throw new ReportDataFactoryException("Cannot find the specified transformation step " + stepName);
      }

      final RowMetaInterface row = transMeta.getStepFields(stepName);
      TableProducer tableProducer = new TableProducer(row, queryLimit, stopOnError);
      targetStep.addRowListener(tableProducer);


      currentlyRunningTransformation = trans;
      try
      {
        trans.startThreads();
        trans.waitUntilFinished();
      }
      finally
      {
        trans.cleanup();
      }
      return tableProducer.getTableModel();
    }
    catch (EvaluationException e)
    {
      throw new ReportDataFactoryException("Failed to evaluate parameter", e);
    }
    catch (ParseException e)
    {
      throw new ReportDataFactoryException("Failed to evaluate parameter", e);
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

  private StepInterface findTargetStep(Trans trans)
  {
    final List<StepMetaDataCombi> stepList = trans.getSteps();
    for (int i = 0; i < stepList.size(); i++)
    {
      final StepMetaDataCombi metaDataCombi = stepList.get(i);
      if (stepName.equals(metaDataCombi.stepname))
      {
        return metaDataCombi.step;
      }
    }
    return null;
  }

  private void updateTransformationParameter(final FormulaContext formulaContext,
                                             final Trans trans)
      throws UnknownParamException, EvaluationException, ParseException
  {
    for (int i = 0; i < this.parameter.length; i++)
    {
      final FormulaParameter mapping = this.parameter[i];
      final String sourceName = mapping.getName();
      final Object value = mapping.compute(formulaContext);
      if (value != null)
      {
        trans.setParameterValue(sourceName, String.valueOf(value));
      }
      else
      {
        trans.setParameterValue(sourceName, null);
      }
    }
  }

  private String[] fillArguments(final FormulaContext context) throws EvaluationException, ParseException
  {
    final String[] params = new String[arguments.length];
    for (int i = 0; i < arguments.length; i++)
    {
      final FormulaArgument arg = arguments[i];
      Object compute = arg.compute(context);
      if (compute == null)
      {
        params[i] = null;
      }
      else
      {
        params[i] = String.valueOf(compute);
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

  public String[] getReferencedFields() throws ParseException
  {
    final LinkedHashSet<String> retval = new LinkedHashSet<String>();
    HashSet<String> args = new HashSet<String>();
    for (FormulaArgument argument : arguments)
    {
      args.addAll(Arrays.asList(argument.getReferencedFields()));
    }
    for (FormulaParameter formulaParameter : parameter)
    {
      args.addAll(Arrays.asList(formulaParameter.getReferencedFields()));
    }
    retval.addAll(args);
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
    retval.add(new ArrayList<FormulaArgument>(Arrays.asList(getArguments())));
    retval.add(new ArrayList<FormulaParameter>(Arrays.asList(getParameter())));
    return retval;
  }

  protected String computeFullFilename(ResourceKey key)
  {
    while (key != null)
    {
      final Object identifier = key.getIdentifier();
      if (identifier instanceof File)
      {
        final File file = (File) identifier;
        return file.getAbsolutePath();
      }
      key = key.getParent();
    }
    return null;
  }
}
