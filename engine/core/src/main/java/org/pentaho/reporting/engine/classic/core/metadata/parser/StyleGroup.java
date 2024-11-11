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


package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.metadata.builder.StyleMetaDataBuilder;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A internal helper object to store an attribute-group definition.
 *
 * @author Thomas Morgner
 */
public class StyleGroup {
  private String name;
  private ArrayList<StyleMetaDataBuilder> metaData;

  public StyleGroup( final String name, final Collection<StyleMetaDataBuilder> metaData ) {
    ArgumentNullException.validate( "name", name );
    ArgumentNullException.validate( "metaData", metaData );

    this.name = name;
    this.metaData = new ArrayList<StyleMetaDataBuilder>( metaData );
  }

  public String getName() {
    return name;
  }

  public List<StyleMetaDataBuilder> getMetaData() {
    return (List<StyleMetaDataBuilder>) metaData.clone();
  }
}
