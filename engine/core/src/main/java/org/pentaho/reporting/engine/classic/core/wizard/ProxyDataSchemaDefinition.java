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


package org.pentaho.reporting.engine.classic.core.wizard;

public class ProxyDataSchemaDefinition implements DataSchemaDefinition {
  private DataSchemaDefinition rootDefinition;
  private DataSchemaDefinition overlayDefinition;

  public ProxyDataSchemaDefinition( final DataSchemaDefinition overlay, final DataSchemaDefinition root ) {
    this.overlayDefinition = overlay;
    this.rootDefinition = root;
  }

  public GlobalRule[] getGlobalRules() {
    final GlobalRule[] overlayRules = overlayDefinition.getGlobalRules();
    final GlobalRule[] rootRules = rootDefinition.getGlobalRules();

    final GlobalRule[] allRules = new GlobalRule[overlayRules.length + rootRules.length];
    System.arraycopy( overlayRules, 0, allRules, 0, overlayRules.length );
    System.arraycopy( rootRules, 0, allRules, overlayRules.length, rootRules.length );
    return allRules;
  }

  public MetaSelectorRule[] getIndirectRules() {
    final MetaSelectorRule[] overlayRules = overlayDefinition.getIndirectRules();
    final MetaSelectorRule[] rootRules = rootDefinition.getIndirectRules();

    final MetaSelectorRule[] allRules = new MetaSelectorRule[rootRules.length + overlayRules.length];
    System.arraycopy( overlayRules, 0, allRules, 0, overlayRules.length );
    System.arraycopy( rootRules, 0, allRules, overlayRules.length, rootRules.length );
    return allRules;
  }

  public DirectFieldSelectorRule[] getDirectRules() {
    final DirectFieldSelectorRule[] overlayRules = overlayDefinition.getDirectRules();
    final DirectFieldSelectorRule[] rootRules = rootDefinition.getDirectRules();

    final DirectFieldSelectorRule[] allRules = new DirectFieldSelectorRule[rootRules.length + overlayRules.length];
    System.arraycopy( overlayRules, 0, allRules, 0, overlayRules.length );
    System.arraycopy( rootRules, 0, allRules, overlayRules.length, rootRules.length );
    return allRules;
  }

  public Object clone() {
    try {
      final ProxyDataSchemaDefinition dataSchemaDefinition = (ProxyDataSchemaDefinition) super.clone();
      dataSchemaDefinition.overlayDefinition = (DataSchemaDefinition) overlayDefinition.clone();
      dataSchemaDefinition.rootDefinition = (DataSchemaDefinition) rootDefinition.clone();
      return dataSchemaDefinition;
    } catch ( CloneNotSupportedException cne ) {
      throw new IllegalStateException( "Clone should always be supported in this class" );
    }
  }
}
