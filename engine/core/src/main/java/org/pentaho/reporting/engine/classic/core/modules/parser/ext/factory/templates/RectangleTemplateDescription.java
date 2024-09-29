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


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates;

import org.pentaho.reporting.engine.classic.core.filter.templates.RectangleTemplate;

/**
 * A rectangle template description.
 *
 * @author Thomas Morgner
 */
public class RectangleTemplateDescription extends AbstractTemplateDescription {
  /**
   * Creates a new template description.
   *
   * @param name
   *          the name.
   */
  public RectangleTemplateDescription( final String name ) {
    super( name, RectangleTemplate.class, true );
  }
}
