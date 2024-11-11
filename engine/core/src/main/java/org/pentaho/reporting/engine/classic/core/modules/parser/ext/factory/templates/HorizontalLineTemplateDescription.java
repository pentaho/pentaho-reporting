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


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates;

import org.pentaho.reporting.engine.classic.core.filter.templates.HorizontalLineTemplate;

/**
 * A horizontal line template description.
 *
 * @author Thomas Morgner
 */
public class HorizontalLineTemplateDescription extends AbstractTemplateDescription {
  /**
   * Creates a new template description.
   *
   * @param name
   *          the name.
   */
  public HorizontalLineTemplateDescription( final String name ) {
    super( name, HorizontalLineTemplate.class, true );
  }
}
