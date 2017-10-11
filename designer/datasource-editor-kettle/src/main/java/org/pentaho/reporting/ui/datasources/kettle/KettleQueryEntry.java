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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.ui.datasources.kettle;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.AbstractKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaArgument;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaParameter;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.ui.datasources.kettle.embedded.KettleParameterInfo;
import org.pentaho.reporting.ui.datasources.kettle.embedded.TransformationParameterHelper;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class KettleQueryEntry {
  private String name;
  private FormulaArgument[] arguments;
  private FormulaParameter[] parameters;
  private TransformationParameterHelper parameterHelper;
  private boolean validated;
  private PropertyChangeSupport propertyChangeSupport;
  private boolean stopOnErrors;

  public KettleQueryEntry( final String aName ) {
    this.propertyChangeSupport = new PropertyChangeSupport( this );
    this.name = aName;
    this.arguments = new FormulaArgument[ 0 ];
    this.parameters = new FormulaParameter[ 0 ];
    this.stopOnErrors = true;
  }

  public void setStopOnErrors( final boolean stopOnErrors ) {
    this.stopOnErrors = stopOnErrors;
  }

  public boolean isStopOnErrors() {
    return stopOnErrors;
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( listener );
  }

  public void addPropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( propertyName, listener );
  }

  public void removePropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( propertyName, listener );
  }

  public boolean isValidated() {
    return validated;
  }

  public void setValidated( final boolean validated ) {
    boolean oldValid = this.validated;
    this.validated = validated;
    propertyChangeSupport.firePropertyChange( "validated", oldValid, validated );
  }

  protected boolean validate() {
    return ( !StringUtils.isEmpty( name ) );
  }

  protected void clearCachedEntries() {
    this.parameterHelper = null;
  }

  public String getName() {
    return name;
  }

  public void setName( final String name ) {
    this.name = name;
    setValidated( validate() );
  }

  public FormulaArgument[] getArguments() {
    return arguments.clone();
  }

  public void setArguments( final FormulaArgument[] arguments ) {
    this.arguments = arguments.clone();
  }

  public FormulaParameter[] getParameters() {
    return parameters.clone();
  }

  public void setParameters( final FormulaParameter[] parameters ) {
    this.parameters = parameters.clone();
  }

  public String toString() {
    return name;
  }

  public KettleParameterInfo[] getDeclaredParameters( final DataFactoryContext context )
    throws KettleException, ReportDataFactoryException {
    if ( parameterHelper == null ) {
      parameterHelper = new TransformationParameterHelper( loadTransformation( context ), context );
    }
    return parameterHelper.getDeclaredParameter();
  }

  protected abstract AbstractKettleTransformationProducer loadTransformation( final DataFactoryContext context )
    throws KettleException;

  public abstract KettleTransformationProducer createProducer() throws KettleException;

}
