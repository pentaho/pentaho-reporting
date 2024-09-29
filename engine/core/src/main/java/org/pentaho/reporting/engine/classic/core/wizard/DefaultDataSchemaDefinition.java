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


package org.pentaho.reporting.engine.classic.core.wizard;

import java.util.ArrayList;
import java.util.Arrays;

public class DefaultDataSchemaDefinition implements DataSchemaDefinition {
  private ArrayList<DataSchemaRule> backend;

  public DefaultDataSchemaDefinition() {
    backend = new ArrayList<DataSchemaRule>();
  }

  public void addRule( final DataSchemaRule rule ) {
    if ( rule == null ) {
      throw new NullPointerException();
    }
    backend.add( rule );
  }

  public DataSchemaRule getRule( final int index ) {
    return backend.get( index );
  }

  public int getRuleCount() {
    return backend.size();
  }

  /**
   * Returns all known rules.
   *
   * @return
   */
  public GlobalRule[] getGlobalRules() {
    final ArrayList<GlobalRule> retval = new ArrayList<GlobalRule>();
    for ( int i = 0; i < backend.size(); i++ ) {
      final DataSchemaRule rule = backend.get( i );
      if ( rule instanceof GlobalRule ) {
        retval.add( (GlobalRule) rule );
      }
    }
    return retval.toArray( new GlobalRule[retval.size()] );
  }

  public MetaSelectorRule[] getIndirectRules() {
    final ArrayList<MetaSelectorRule> retval = new ArrayList<MetaSelectorRule>();
    for ( int i = 0; i < backend.size(); i++ ) {
      final DataSchemaRule rule = backend.get( i );
      if ( rule instanceof MetaSelectorRule ) {
        retval.add( (MetaSelectorRule) rule );
      }
    }
    return retval.toArray( new MetaSelectorRule[retval.size()] );
  }

  public DirectFieldSelectorRule[] getDirectRules() {
    final ArrayList<DirectFieldSelectorRule> retval = new ArrayList<DirectFieldSelectorRule>();
    for ( int i = 0; i < backend.size(); i++ ) {
      final DataSchemaRule rule = backend.get( i );
      if ( rule instanceof DirectFieldSelectorRule ) {
        retval.add( (DirectFieldSelectorRule) rule );
      }
    }
    return retval.toArray( new DirectFieldSelectorRule[retval.size()] );
  }

  /**
   * Returns all known rules.
   *
   * @return
   */
  public DataSchemaRule[] getRules() {
    return backend.toArray( new DataSchemaRule[backend.size()] );
  }

  public void merge( final DataSchemaDefinition schemaDefinition ) {
    if ( schemaDefinition == null ) {
      throw new NullPointerException();
    }
    final GlobalRule[] globalRules = schemaDefinition.getGlobalRules();
    final MetaSelectorRule[] indirectRules = schemaDefinition.getIndirectRules();
    final DirectFieldSelectorRule[] directRules = schemaDefinition.getDirectRules();
    backend.ensureCapacity( backend.size() + globalRules.length + indirectRules.length + directRules.length );
    backend.addAll( Arrays.asList( globalRules ) );
    backend.addAll( Arrays.asList( indirectRules ) );
    backend.addAll( Arrays.asList( directRules ) );
  }

  public Object clone() {
    try {
      final DefaultDataSchemaDefinition def = (DefaultDataSchemaDefinition) super.clone();
      def.backend = (ArrayList<DataSchemaRule>) backend.clone();
      return def;
    } catch ( CloneNotSupportedException cne ) {
      throw new IllegalStateException( "Clone should always be supported in this class" );
    }
  }
}
