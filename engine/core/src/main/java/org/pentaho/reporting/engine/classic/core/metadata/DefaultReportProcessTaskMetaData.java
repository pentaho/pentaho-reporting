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


package org.pentaho.reporting.engine.classic.core.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportProcessTask;
import org.pentaho.reporting.engine.classic.core.metadata.builder.ReportProcessTaskMetaDataBuilder;

import java.util.List;

public class DefaultReportProcessTaskMetaData extends AbstractMetaData implements ReportProcessTaskMetaData {
  private static final Log logger = LogFactory.getLog( DefaultReportProcessTaskMetaData.class );

  private Class<? extends ReportProcessTask> implementation;
  private String[] aliases;

  @Deprecated
  public DefaultReportProcessTaskMetaData( final String name, final String bundleLocation, final boolean expert,
      final boolean preferred, final boolean hidden, final boolean deprecated, final MaturityLevel maturityLevel,
      final int compatibilityLevel, final Class<? extends ReportProcessTask> implementation,
      final String configurationPrefix, final String[] aliases ) {
    super( name, bundleLocation, "", expert, preferred, hidden, deprecated, maturityLevel, compatibilityLevel );
    this.implementation = implementation;
    this.aliases = aliases.clone();
  }

  public DefaultReportProcessTaskMetaData( final ReportProcessTaskMetaDataBuilder builder ) {
    super( builder );
    this.implementation = builder.getImplementation();
    List<String> aliasList = builder.getAliases();
    this.aliases = aliasList.toArray( new String[aliasList.size()] );
  }

  protected String computePrefix( final String keyPrefix, final String name ) {
    return "";
  }

  public String[] getAlias() {
    return aliases.clone();
  }

  public ReportProcessTask create() {
    try {
      return implementation.newInstance();
    } catch ( final Throwable e ) {
      logger.warn( "Unable to instantiate ReportProcessTask", e );
      throw new IllegalStateException( e );
    }
  }
}
