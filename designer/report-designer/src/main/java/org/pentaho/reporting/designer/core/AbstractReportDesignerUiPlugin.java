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

package org.pentaho.reporting.designer.core;

import org.pentaho.ui.xul.impl.XulEventHandler;

import java.util.Collections;
import java.util.Map;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public abstract class AbstractReportDesignerUiPlugin implements ReportDesignerUiPlugin {
  @SuppressWarnings( { "StaticCollection" } )
  private static final Map<String, String> EMPTY = Collections.EMPTY_MAP;
  private static final String[] EMPTY_SOURCES = new String[ 0 ];
  private static final XulEventHandler[] EMPTY_EVENTHANDLERS = new XulEventHandler[ 0 ];

  protected AbstractReportDesignerUiPlugin() {
  }

  public Map<String, String> getXulAdditionalHandlers() {
    //noinspection ReturnOfCollectionOrArrayField
    return EMPTY;
  }

  public String[] getOverlaySources() {
    return EMPTY_SOURCES;
  }

  public XulEventHandler[] createEventHandlers() {
    return EMPTY_EVENTHANDLERS;
  }
}
