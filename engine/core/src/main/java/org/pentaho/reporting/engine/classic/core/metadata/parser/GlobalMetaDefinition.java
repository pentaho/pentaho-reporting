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


package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.metadata.builder.StyleMetaDataBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalMetaDefinition implements Cloneable {
  private LinkedHashMap<String, StyleGroup> styleGroups;
  private LinkedHashMap<String, AttributeGroup> attributeGroups;

  public GlobalMetaDefinition() {
    styleGroups = new LinkedHashMap<String, StyleGroup>();
    attributeGroups = new LinkedHashMap<String, AttributeGroup>();
  }

  public void addAttributeGroup( final AttributeGroup group ) {
    if ( group == null ) {
      throw new NullPointerException();
    }

    attributeGroups.put( group.getName(), group );
  }

  public void addStyleGroup( final StyleGroup group ) {
    if ( group == null ) {
      throw new NullPointerException();
    }
    styleGroups.put( group.getName(), group );
  }

  public StyleGroup getStyleGroup( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    return styleGroups.get( name );
  }

  public AttributeGroup getAttributeGroup( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    return attributeGroups.get( name );
  }

  public Object clone() throws CloneNotSupportedException {
    final GlobalMetaDefinition definition = (GlobalMetaDefinition) super.clone();
    // noinspection unchecked
    definition.styleGroups = (LinkedHashMap<String, StyleGroup>) styleGroups.clone();
    // noinspection unchecked
    definition.attributeGroups = (LinkedHashMap<String, AttributeGroup>) attributeGroups.clone();
    return definition;
  }

  public void merge( final GlobalMetaDefinition definition ) {
    if ( definition == null ) {
      throw new NullPointerException();
    }
    mergeStyles( definition );

    for ( final Map.Entry<String, AttributeGroup> entry : definition.attributeGroups.entrySet() ) {
      final AttributeGroup styleGroup = this.attributeGroups.get( entry.getKey() );
      if ( styleGroup == null ) {
        addAttributeGroup( entry.getValue() );
        continue;
      }
      final AttributeGroup entryGroup = entry.getValue();
      final String name = styleGroup.getName();
      final LinkedHashMap<String, AttributeDefinition> styles = new LinkedHashMap<String, AttributeDefinition>();
      for ( final AttributeDefinition handler : styleGroup.getMetaData() ) {
        styles.put( handler.getName(), handler );
      }

      for ( final AttributeDefinition handler : entryGroup.getMetaData() ) {
        styles.put( handler.getName(), handler );
      }

      addAttributeGroup( new AttributeGroup( name, styles.values().toArray( new AttributeDefinition[styles.size()] ) ) );
    }

  }

  private void mergeStyles( final GlobalMetaDefinition definition ) {
    for ( final Map.Entry<String, StyleGroup> entry : definition.styleGroups.entrySet() ) {
      final StyleGroup styleGroup = this.styleGroups.get( entry.getKey() );
      if ( styleGroup == null ) {
        addStyleGroup( entry.getValue() );
        continue;
      }

      final LinkedHashMap<String, StyleMetaDataBuilder> styles = new LinkedHashMap<String, StyleMetaDataBuilder>();
      for ( final StyleMetaDataBuilder handler : styleGroup.getMetaData() ) {
        styles.put( handler.getName(), handler );
      }

      for ( final StyleMetaDataBuilder handler : entry.getValue().getMetaData() ) {
        styles.put( handler.getName(), handler );
      }

      addStyleGroup( new StyleGroup( styleGroup.getName(), styles.values() ) );
    }
  }
}
