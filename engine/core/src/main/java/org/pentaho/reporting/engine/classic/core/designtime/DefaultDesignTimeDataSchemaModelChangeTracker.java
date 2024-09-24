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

package org.pentaho.reporting.engine.classic.core.designtime;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.HashMap;

public class DefaultDesignTimeDataSchemaModelChangeTracker implements DesignTimeDataSchemaModelChangeTracker {

  private final HashMap<InstanceID, Long> nonVisualChangeTrackers;
  private final HashMap<InstanceID, Long> dataFactoryChangeTrackers;
  private final AbstractReportDefinition parent;
  private String query;
  private int queryTimeout;

  public DefaultDesignTimeDataSchemaModelChangeTracker( final AbstractReportDefinition parent ) {
    this.parent = parent;
    this.nonVisualChangeTrackers = new HashMap<InstanceID, Long>();
    this.dataFactoryChangeTrackers = new HashMap<InstanceID, Long>();
    this.queryTimeout = parent.getQueryTimeout();
  }

  private boolean isNonVisualsChanged() {
    AbstractReportDefinition parent = this.parent;
    while ( parent != null ) {
      final InstanceID id = parent.getObjectID();
      final Long dataSourceChangeTracker = parent.getDatasourceChangeTracker();
      if ( dataSourceChangeTracker.equals( dataFactoryChangeTrackers.get( id ) ) == false ) {
        return true;
      }

      final Long nonVisualsChangeTracker = parent.getNonVisualsChangeTracker();
      if ( nonVisualsChangeTracker.equals( nonVisualChangeTrackers.get( id ) ) == false ) {
        return true;
      }

      final Section parentSection = parent.getParentSection();
      if ( parentSection == null ) {
        parent = null;
      } else {
        parent = (AbstractReportDefinition) parentSection.getReportDefinition();
      }
    }
    return false;
  }

  private boolean isDataFactoryChanged() {
    AbstractReportDefinition parent = this.parent;
    while ( parent != null ) {
      final InstanceID id = parent.getObjectID();
      final Long dataSourceChangeTracker = parent.getDatasourceChangeTracker();
      if ( dataSourceChangeTracker.equals( dataFactoryChangeTrackers.get( id ) ) == false ) {
        return true;
      }

      final Section parentSection = parent.getParentSection();
      if ( parentSection == null ) {
        parent = null;
      } else {
        parent = (AbstractReportDefinition) parentSection.getReportDefinition();
      }
    }
    return false;
  }

  public void updateChangeTrackers() {
    this.query = parent.getQuery();
    this.queryTimeout = parent.getQueryTimeout();
    AbstractReportDefinition parent = this.parent;
    while ( parent != null ) {
      final InstanceID id = parent.getObjectID();
      dataFactoryChangeTrackers.put( id, parent.getDatasourceChangeTracker() );
      nonVisualChangeTrackers.put( id, parent.getNonVisualsChangeTracker() );

      final Section parentSection = parent.getParentSection();
      if ( parentSection == null ) {
        parent = null;
      } else {
        parent = (AbstractReportDefinition) parentSection.getReportDefinition();
      }
    }
  }

  public boolean isReportQueryChanged() {
    return ObjectUtilities.equal( this.query, parent.getQuery() ) == false || queryTimeout != parent.getQueryTimeout()
        || isDataFactoryChanged();
  }

  public boolean isReportChanged() {
    return isNonVisualsChanged() || ObjectUtilities.equal( this.query, parent.getQuery() ) == false;
  }
}
