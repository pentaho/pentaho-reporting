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
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author David Kincade
 */
public class KettleDataSourcePlugin implements DataSourcePlugin {
  public KettleDataSourcePlugin() {
  }

  public boolean canHandle( final DataFactory dataFactory ) {
    return dataFactory instanceof KettleDataFactory;
  }

  public DataFactory performEdit( final DesignTimeContext context,
                                  final DataFactory input,
                                  final String queryName,
                                  final DataFactoryChangeRecorder changeRecorder ) {
    try {
      final KettleDataSourceDialog editor = createKettleDataSourceDialog( context );
      return editor.performConfiguration( context, (KettleDataFactory) input, queryName );
    } catch ( KettleException e ) {
      context.error( e );
      return input;
    }
  }

  protected KettleDataSourceDialog createKettleDataSourceDialog( final DesignTimeContext context ) {
    final KettleDataSourceDialog editor;
    final Window window = context.getParentWindow();
    if ( window instanceof JDialog ) {
      editor = new KettleDataSourceDialog( context, (JDialog) window );
    } else if ( window instanceof JFrame ) {
      editor = new KettleDataSourceDialog( context, (JFrame) window );
    } else {
      editor = new KettleDataSourceDialog( context );
    }
    return editor;
  }

  public DataFactoryMetaData getMetaData() {
    return DataFactoryRegistry.getInstance().getMetaData( KettleDataFactory.class.getName() );
  }

}
