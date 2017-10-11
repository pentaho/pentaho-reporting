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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.base.config.Configuration;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * The cascading data factory is a collection of data-factories. Each of the child datafactories is queried in the order
 * of their addition to the collection. This is like the CompoundDataFactory but without deriving the datafactories on
 * addition and without forwarding the open and close calls.
 * <p/>
 * Implementation note: This is a purely internal class. Any attempt to use this class as a general datafactory
 * implementation will give you a lot of fun and happy exceptions. Just dont do it.
 *
 * @author Thomas Morgner
 */
public class CascadingDataFactory extends CompoundDataFactory {
  private static final Log logger = LogFactory.getLog( CascadingDataFactory.class );

  public CascadingDataFactory() {
  }

  public void add( final DataFactory factory ) {
    super.addRaw( factory );
  }

  protected TableModel handleFallThrough( final String query ) throws ReportDataFactoryException {
    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    if ( "warn".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.states.NullDataSourceHandling" ) ) ) {
      logger.warn( "Deprecated behavior: None of the data-factories was able to handle the query '" + query + "'. "
          + "Returning empty tablemodel instead of failing hard." );
      logger.warn( "Be aware that the default for this setting will change in version 0.8.11. "
          + "To avoid this warning, make sure that all data-sources are properly configured and "
          + "that no report references illegal queries." );
      return new DefaultTableModel();
    } else {
      throw new ReportDataFactoryException( "None of the data-factories was able to handle this query." );
    }
  }

  /**
   * Returns a copy of the data factory that is not affected by its anchestor and holds no connection to the anchestor
   * anymore. A data-factory will be derived at the beginning of the report processing.
   *
   * @return a copy of the data factory.
   */
  public DataFactory derive() {
    throw new UnsupportedOperationException( "Deriving this factory is not supported: This is a internal class." );
  }

  /**
   * Closes the data factory and frees all resources held by this instance.
   */
  public void close() {
    throw new UnsupportedOperationException( "Closing this factory is not supported: This is a internal class." );
  }
}
