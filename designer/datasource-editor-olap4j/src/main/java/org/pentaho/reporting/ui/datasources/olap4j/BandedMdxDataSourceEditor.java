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

package org.pentaho.reporting.ui.datasources.olap4j;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractNamedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.BandedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;

import java.awt.*;

/**
 * @author Michael D'Amour
 */
public class BandedMdxDataSourceEditor extends Olap4JDataSourceEditor {
  public BandedMdxDataSourceEditor( final DesignTimeContext context ) {
    super( context );
  }

  public BandedMdxDataSourceEditor( final DesignTimeContext context, final Dialog owner ) {
    super( context, owner );
  }

  public BandedMdxDataSourceEditor( final DesignTimeContext context, final Frame owner ) {
    super( context, owner );
  }

  protected void init( final DesignTimeContext context ) {
    super.init( context );
    setTitle( Messages.getString( "BandedMdxDataSourceEditor.Title" ) );
  }

  protected String getDialogId() {
    return "Olap4JDataSourceEditor.Banded";
  }

  protected AbstractNamedMDXDataFactory createDataFactory() {
    final OlapConnectionProvider connectionProvider = createConnectionProvider();
    if ( connectionProvider == null ) {
      return null;
    }
    final BandedMDXDataFactory returnDataFactory = new BandedMDXDataFactory( connectionProvider );
    configureQueries( returnDataFactory );
    return returnDataFactory;
  }
}
