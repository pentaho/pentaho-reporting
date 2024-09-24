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

package org.pentaho.reporting.engine.classic.wizard.model;

/**
 * Describes a detail field. Any aggregation defined here is always relative to the innermost group (at runtime).
 *
 * @author Thomas Morgner
 */
public interface DetailFieldDefinition extends FieldDefinition, ElementFormatDefinition {
  public Boolean getOnlyShowChangingValues();

  public void setOnlyShowChangingValues( Boolean b );
}
