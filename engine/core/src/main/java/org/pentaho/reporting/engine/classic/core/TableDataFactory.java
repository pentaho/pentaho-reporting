/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core;

import javax.swing.table.TableModel;
import java.util.LinkedHashMap;

/**
 * The TableDataFactory provides keyed access to predefined tablemodels. The factory does not accept parameters and
 * therefore cannot be used for parametrized queries. The queryname is used to lookup the table by its previously
 * registered name.
 *
 * @author Thomas Morgner
 */
public class TableDataFactory extends AbstractDataFactory {
  /**
   * A unique identifier for long term persistance.
   */
  private static final long serialVersionUID = -238954878318943053L;

  /**
   * The tables for this factory.
   */
  private LinkedHashMap<String, TableModel> tables;

  /**
   * Default Constructor.
   */
  public TableDataFactory() {
    this.tables = new LinkedHashMap<String, TableModel>();
  }

  /**
   * Creates a new TableDataFactory and registers the tablemodel with the given name.
   *
   * @param name
   *          the name of the table.
   * @param tableModel
   *          the tablemodel.
   */
  public TableDataFactory( final String name, final TableModel tableModel ) {
    this();
    addTable( name, tableModel );
  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query
   * @param parameters
   * @return
   */
  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return tables.containsKey( query );
  }

  /**
   * Registers a tablemodel with the given name. If a different tablemodel has been previously registered with the same
   * name, this table will replace the existing one.
   *
   * @param name
   *          the name of the table.
   * @param tableModel
   *          the tablemodel that should be registered.
   */
  public void addTable( final String name, final TableModel tableModel ) {
    if ( tableModel == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    tables.put( name, tableModel );
  }

  /**
   * Removes the table that has been registered by the given name.
   *
   * @param name
   *          the name of the table to be removed.
   */
  public void removeTable( final String name ) {
    tables.remove( name );
  }

  public TableModel getTable( final String name ) {
    return tables.get( name );
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed.
   * <p/>
   * The dataset may change between two calls, do not assume anything!
   *
   * @param query
   *          the name of the table.
   * @param parameters
   *          are ignored for this factory.
   * @return the report data or null.
   */
  public TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    final TableModel tableModel = tables.get( query );
    if ( tableModel == null ) {
      throw new ReportDataFactoryException( "The specified query '" + query + "' is not recognized." );
    }
    return tableModel;
  }

  /**
   * Closes the data factory. Actually, this one does nothing at all.
   */
  public void close() {
  }

  /**
   * Returns a copy of the data factory that is not affected by its anchestor and holds no connection to the anchestor
   * anymore. A data-factory will be derived at the beginning of the report processing.
   *
   * @return a copy of the data factory.
   */
  public DataFactory derive() {
    return (DataFactory) clone();
  }

  /**
   * Creates a copy of this data-factory.
   *
   * @return the copy of the data-factory, never null.
   */
  public TableDataFactory clone() {
    final TableDataFactory dataFactory = (TableDataFactory) super.clone();
    dataFactory.tables = (LinkedHashMap<String, TableModel>) tables.clone();
    return dataFactory;
  }

  public String[] getQueryNames() {
    return tables.keySet().toArray( new String[tables.size()] );
  }
}
