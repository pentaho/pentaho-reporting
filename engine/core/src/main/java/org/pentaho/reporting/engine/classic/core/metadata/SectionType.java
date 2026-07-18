/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.core.metadata;

/**
 * The section type interface is a marker interface to separate data-elements from sections (which contain other
 * elements but produce no content on their own).
 *
 * @author Thomas Morgner
 */
public interface SectionType extends ElementType {
  /**
   * A band that serves a specific purpose within a slotted parent should return "true" here. Plain elemetns and bands
   * that can be freely combined should return false. </p>
   *
   * @return true, if the usage is restricted.
   */
  public boolean isRestricted();
}
