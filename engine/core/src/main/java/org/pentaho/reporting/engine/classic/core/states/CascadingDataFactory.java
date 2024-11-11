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
