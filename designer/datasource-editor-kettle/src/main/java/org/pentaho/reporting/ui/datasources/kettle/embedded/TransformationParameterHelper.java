/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.ui.datasources.kettle.embedded;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.AbstractKettleTransformationProducer;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

import java.util.ArrayList;

public class TransformationParameterHelper {

  private AbstractKettleTransformationProducer transformationProducer;
  private DataFactoryContext dataFactoryContext;
  private KettleParameterInfo[] parameters;

  public TransformationParameterHelper( final AbstractKettleTransformationProducer transformationProducer,
                                        final DataFactoryContext dataFactoryContext ) {
    this.dataFactoryContext = dataFactoryContext;
    ArgumentNullException.validate( "transformationProducer", transformationProducer );
    this.transformationProducer = transformationProducer;
  }

  public KettleParameterInfo[] getDeclaredParameter() throws KettleException, ReportDataFactoryException {
    if ( parameters == null ) {
      TransMeta transMeta = transformationProducer.loadTransformation( dataFactoryContext );
      String[] parameterNames = transMeta.listParameters();
      ArrayList<KettleParameterInfo> infos = new ArrayList<KettleParameterInfo>();
      for ( String parameterName : parameterNames ) {
        String defaultValue = transMeta.getParameterDefault( parameterName );
        String description = transMeta.getParameterDescription( parameterName );
        infos.add( new KettleParameterInfo( parameterName, description, defaultValue ) );
      }
      parameters = infos.toArray( new KettleParameterInfo[ infos.size() ] );
    }
    return parameters;
  }


}
