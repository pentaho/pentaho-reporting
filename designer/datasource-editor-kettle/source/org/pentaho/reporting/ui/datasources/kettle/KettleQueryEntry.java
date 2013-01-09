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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.ui.datasources.kettle;

import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromFileProducer;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class KettleQueryEntry
{
  private static class InternalKettleTransFromFileProducer extends KettleTransFromFileProducer
  {
    public InternalKettleTransFromFileProducer(final String transformationFile)
    {
      super(transformationFile, null, new String[0], new ParameterMapping[0]);
    }

    public TransMeta loadTransformation(final Repository repository,
                                        final ResourceManager resourceManager,
                                        final ResourceKey contextKey) throws ReportDataFactoryException, KettleException
    {
      return super.loadTransformation(repository, resourceManager, contextKey);
    }
  }

  private StepMeta[] cached;
  private String name;
  private String file;
  private String selectedStepName;
  private String[] arguments;
  private ParameterMapping[] parameters;
  private String[] declaredParameters;

  public KettleQueryEntry(final String aName)
  {
    this.name = aName;
    this.arguments = new String[0];
    this.parameters = new ParameterMapping[0];
  }

  public String getName()
  {
    return name;
  }

  public void setName(final String name)
  {
    this.name = name;
  }

  public String getFile()
  {
    return file;
  }

  public void setFile(final String file)
  {
    this.file = file;
    this.declaredParameters = null;
    this.cached = null;
  }

  public String getSelectedStep()
  {
    return selectedStepName;
  }

  public void setSelectedStep(final String selectedStep)
  {
    this.selectedStepName = selectedStep;
  }

  public String[] getArguments()
  {
    return arguments.clone();
  }

  public void setArguments(final String[] arguments)
  {
    this.arguments = arguments.clone();
  }

  public ParameterMapping[] getParameters()
  {
    return parameters.clone();
  }

  public void setParameters(final ParameterMapping[] parameters)
  {
    this.parameters = parameters.clone();
  }

  public String toString()
  {
    return name;
  }

  public String[] getDeclaredParameters(final ResourceManager resourceManager,
                                        final ResourceKey contextKey) throws KettleException, ReportDataFactoryException
  {
    if (file == null)
    {
      return new String[0];
    }
    if (cached == null)
    {
      loadTransformation(resourceManager, contextKey);
    }
    return declaredParameters;
  }

  private void loadTransformation(final ResourceManager resourceManager, final ResourceKey contextKey)
      throws ReportDataFactoryException, KettleException
  {
    final InternalKettleTransFromFileProducer transFromFileProducer =
        new InternalKettleTransFromFileProducer(getFile());
    final TransMeta transMeta = transFromFileProducer.loadTransformation(null, resourceManager, contextKey);
    final List<StepMeta> theSteps = transMeta.getSteps();
    declaredParameters = transMeta.listParameters();
    cached = theSteps.toArray(new StepMeta[theSteps.size()]);
  }

  public StepMeta[] getSteps(final ResourceManager resourceManager,
                             final ResourceKey contextKey) throws KettleException, ReportDataFactoryException
  {
    if (file == null)
    {
      return new StepMeta[0];
    }
    if (cached == null)
    {
      loadTransformation(resourceManager, contextKey);
    }
    return cached;
  }

  public KettleTransFromFileProducer createProducer()
  {
    final String[] argumentFields = getArguments();
    final ParameterMapping[] varNames = getParameters();
    final String file = getFile();
    final String selectedStep = getSelectedStep();
    return new KettleTransFromFileProducer(file, selectedStep, argumentFields, varNames);
  }

}
