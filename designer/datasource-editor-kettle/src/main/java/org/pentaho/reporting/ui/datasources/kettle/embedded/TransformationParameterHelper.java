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
