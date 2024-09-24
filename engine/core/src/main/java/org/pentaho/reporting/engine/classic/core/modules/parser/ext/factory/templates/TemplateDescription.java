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

import org.pentaho.reporting.engine.classic.core.filter.templates.Template;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectDescription;

/**
 * A template description.
 *
 * @author Thomas Morgner
 */
public interface TemplateDescription extends ObjectDescription {
  /**
   * Creates a new template.
   *
   * @return The template.
   */
  public Template createTemplate();

  /**
   * Returns the name.
   *
   * @return The name.
   */
  public String getName();

  /**
   * Sets the name.
   *
   * @param name
   *          the name.
   */
  public void setName( String name );
}
