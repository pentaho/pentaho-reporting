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
