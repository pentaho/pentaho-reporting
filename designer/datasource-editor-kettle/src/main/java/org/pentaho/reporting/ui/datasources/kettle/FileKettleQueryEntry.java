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
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.ui.datasources.kettle;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.AbstractKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaArgument;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaParameter;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromFileProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.ui.datasources.kettle.embedded.KettleParameterInfo;

import java.util.Collections;
import java.util.List;

public class FileKettleQueryEntry extends KettleQueryEntry {
  private static class InternalKettleTransFromFileProducer extends KettleTransFromFileProducer {
    public InternalKettleTransFromFileProducer( final String transformationFile ) {
      super( transformationFile, null, new FormulaArgument[ 0 ], new FormulaParameter[ 0 ] );
    }

    public TransMeta loadTransformation( final Repository repository,
                                         final ResourceManager resourceManager,
                                         final ResourceKey contextKey )
      throws ReportDataFactoryException, KettleException {
      return super.loadTransformation( repository, resourceManager, contextKey );
    }
  }

  private String file;
  private String selectedStepName;
  private List<StepMeta> cachedSteps;

  public FileKettleQueryEntry( final String aName ) {
    super( aName );
  }

  public FileKettleQueryEntry( final String aName, final KettleTransformationProducer producer ) {
    super( aName );
    this.file = producer.getTransformationFile();
    this.selectedStepName = producer.getStepName();

    if ( producer instanceof AbstractKettleTransformationProducer ) {
      AbstractKettleTransformationProducer p = (AbstractKettleTransformationProducer) producer;
      setArguments( p.getArguments() );
      setParameters( p.getParameter() );
      setStopOnErrors( p.isStopOnError() );
    }
  }

  public boolean validate() {
    return super.validate() && ( !StringUtils.isEmpty( selectedStepName ) ) && ( !StringUtils.isEmpty( file ) );
  }

  public void setFile( final String file ) {
    this.file = file;
    clearCachedEntries();
    setValidated( validate() );
  }

  protected void clearCachedEntries() {
    super.clearCachedEntries();
    cachedSteps = null;
  }

  public String getFile() {
    return file;
  }

  public String getSelectedStep() {
    return selectedStepName;
  }

  public void setSelectedStep( final String selectedStep ) {
    this.selectedStepName = selectedStep;
    setValidated( validate() );
  }

  public KettleParameterInfo[] getDeclaredParameters( final DataFactoryContext context )
    throws KettleException, ReportDataFactoryException {
    if ( StringUtils.isEmpty( file ) ) {
      return new KettleParameterInfo[ 0 ];
    }
    return super.getDeclaredParameters( context );
  }

  protected AbstractKettleTransformationProducer loadTransformation( final DataFactoryContext context ) {
    return new InternalKettleTransFromFileProducer( file );
  }

  public List<StepMeta> getSteps( final DataFactoryContext context )
    throws KettleException, ReportDataFactoryException {
    if ( StringUtils.isEmpty( file ) ) {
      return Collections.emptyList();
    }
    if ( cachedSteps == null ) {
      AbstractKettleTransformationProducer trans = loadTransformation( context );
      TransMeta transMeta = trans.loadTransformation( context );
      cachedSteps = Collections.unmodifiableList( transMeta.getSteps() );
    }
    return cachedSteps;
  }

  public KettleTransformationProducer createProducer() {
    final FormulaArgument[] argumentFields = getArguments();
    final FormulaParameter[] varNames = getParameters();
    final String file = getFile();
    final String selectedStep = getSelectedStep();
    KettleTransFromFileProducer kettleTransFromFileProducer =
      new KettleTransFromFileProducer( file, selectedStep, argumentFields, varNames );
    kettleTransFromFileProducer.setStopOnError( isStopOnErrors() );
    return kettleTransFromFileProducer;
  }
}
