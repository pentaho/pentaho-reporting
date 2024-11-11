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


package org.pentaho.reporting.engine.classic.core.metadata.builder;

import org.pentaho.reporting.engine.classic.core.ReportProcessTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportProcessTaskMetaDataBuilder extends MetaDataBuilder<ReportProcessTaskMetaDataBuilder> {
  private Class<? extends ReportProcessTask> implementation;
  private ArrayList<String> aliases;

  public ReportProcessTaskMetaDataBuilder() {
    aliases = new ArrayList<String>();
  }

  public ReportProcessTaskMetaDataBuilder implementation( final Class<? extends ReportProcessTask> implementation ) {
    this.implementation = implementation;
    return self();
  }

  public ReportProcessTaskMetaDataBuilder aliases( final Iterable<String> aliases ) {
    for ( final String alias : aliases ) {
      this.aliases.add( alias );
    }
    return self();
  }

  public ReportProcessTaskMetaDataBuilder alias( final String alias ) {
    this.aliases.add( alias );
    return self();
  }

  public ReportProcessTaskMetaDataBuilder aliases( final ArrayList<String> aliases ) {
    this.aliases = aliases;
    return self();
  }

  public Class<? extends ReportProcessTask> getImplementation() {
    return implementation;
  }

  public List<String> getAliases() {
    return Collections.unmodifiableList( (List<String>) aliases.clone() );
  }

  protected ReportProcessTaskMetaDataBuilder self() {
    return this;
  }
}
