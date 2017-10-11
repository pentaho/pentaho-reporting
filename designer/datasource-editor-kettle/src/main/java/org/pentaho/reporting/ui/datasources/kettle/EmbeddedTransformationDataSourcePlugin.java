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
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.EmbeddedKettleDataFactoryEditor;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactory;

import javax.swing.*;
import java.awt.*;

public class EmbeddedTransformationDataSourcePlugin extends KettleDataSourcePlugin
  implements EmbeddedKettleDataFactoryEditor {
  private String metaDataId;

  public EmbeddedTransformationDataSourcePlugin() {
  }

  public void configure( final String metaDataId ) {
    this.metaDataId = metaDataId;
  }


  public DataFactory performEdit( final DesignTimeContext context,
                                  final DataFactory dataFactory,
                                  final String queryName,
                                  final DataFactoryChangeRecorder changeRecorder ) {

    try {
      KettleDataFactory factory;
      if ( dataFactory == null ) {
        factory = new KettleDataFactory();
      } else {
        factory = (KettleDataFactory) dataFactory;
      }
      factory.setMetadata( getMetaData() );

      final KettleDataSourceDialog editor = createEmbeddedKettleDataSourceDialog( context );
      return editor.performConfiguration( context, factory, queryName );
    } catch ( KettleException e ) {
      context.error( e );
      return dataFactory;
    }

  }

  protected KettleDataSourceDialog createEmbeddedKettleDataSourceDialog( final DesignTimeContext context ) {
    final KettleDataSourceDialog editor;
    final Window window = context.getParentWindow();
    if ( window instanceof JDialog ) {
      editor = new EmbeddedKettleDataSourceDialog( context, (JDialog) window, metaDataId );
    } else if ( window instanceof JFrame ) {
      editor = new EmbeddedKettleDataSourceDialog( context, (JFrame) window, metaDataId );
    } else {
      editor = new EmbeddedKettleDataSourceDialog( context, metaDataId );
    }
    return editor;
  }

  public DataFactoryMetaData getMetaData() {
    return DataFactoryRegistry.getInstance().getMetaData( metaDataId );
  }
}
