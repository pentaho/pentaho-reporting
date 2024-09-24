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

import java.io.Serializable;

/**
 * Represents an uncompiled (raw) data-schema definition. The schema definition contains rules which, when applied to
 * the report enrich the report's existing meta-data.
 * <p/>
 * The schema definition rules never override the data-source meta-data attributes. External meta-data always wins.
 * Meta-Data will never be applied without an explicit definition - the element in question must have the attribute
 * "core:meta-data-formatting" set to true to start the automatic reconfiguration.
 *
 * @author Thomas Morgner
 */
public interface DataSchemaDefinition extends Serializable, Cloneable {
  /**
   * Returns all known rules.
   *
   * @return
   */
  public GlobalRule[] getGlobalRules();

  public MetaSelectorRule[] getIndirectRules();

  public DirectFieldSelectorRule[] getDirectRules();

  public Object clone();
}
