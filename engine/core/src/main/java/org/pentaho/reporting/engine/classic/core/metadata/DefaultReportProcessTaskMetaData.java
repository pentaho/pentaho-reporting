/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
