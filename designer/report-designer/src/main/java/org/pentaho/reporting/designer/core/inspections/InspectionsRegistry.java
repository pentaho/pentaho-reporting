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


package org.pentaho.reporting.designer.core.inspections;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.ArrayList;
import java.util.Iterator;

public class InspectionsRegistry {
  static final String PREFIX = "org.pentaho.reporting.designer.inspections.";

  private static InspectionsRegistry instance;
  private ArrayList<Inspection> factories;

  public static synchronized InspectionsRegistry getInstance() {
    if ( instance == null ) {
      instance = new InspectionsRegistry();
      instance.register();
    }
    return instance;
  }

  public InspectionsRegistry() {
    factories = new ArrayList<Inspection>();
  }

  private void register() {
    final Configuration configuration = ReportDesignerBoot.getInstance().getGlobalConfig();
    final Iterator keys = configuration.findPropertyKeys( PREFIX );
    while ( keys.hasNext() ) {
      final String key = (String) keys.next();
      final String className = configuration.getConfigProperty( key );
      final Inspection plugin =
        ObjectUtilities.loadAndInstantiate( className, InspectionsRegistry.class, Inspection.class );
      if ( plugin != null ) {
        factories.add( plugin );
      }
    }
  }

  public void addInspection( final Inspection plugin ) {
    if ( plugin == null ) {
      throw new NullPointerException();
    }
    factories.add( plugin );
  }

  public Inspection[] getInspections() {
    return factories.toArray( new Inspection[ factories.size() ] );
  }
}
