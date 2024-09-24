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

import org.pentaho.reporting.engine.classic.core.layout.output.GenericOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;

import java.util.Locale;

public class DefaultDataAttributeContext implements DataAttributeContext {
  private OutputProcessorMetaData outputProcessorMetaData;
  private Locale locale;

  public DefaultDataAttributeContext() {
    this.outputProcessorMetaData = new GenericOutputProcessorMetaData();
    this.locale = Locale.getDefault();
  }

  public DefaultDataAttributeContext( final Locale locale ) {
    if ( locale == null ) {
      throw new NullPointerException();
    }
    this.outputProcessorMetaData = new GenericOutputProcessorMetaData();
    this.locale = locale;
  }

  public DefaultDataAttributeContext( final OutputProcessorMetaData outputProcessorMetaData, final Locale locale ) {
    if ( outputProcessorMetaData == null ) {
      throw new NullPointerException();
    }
    if ( locale == null ) {
      throw new NullPointerException();
    }

    this.outputProcessorMetaData = outputProcessorMetaData;
    this.locale = locale;
  }

  public Locale getLocale() {
    return locale;
  }

  public OutputProcessorMetaData getOutputProcessorMetaData() {
    return outputProcessorMetaData;
  }
}
