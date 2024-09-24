/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
