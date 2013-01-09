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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata;

import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.DataSource;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

/**
 * A element type is a data-source/data-filter implementation much like the templates. But instead of having own
 * getter/setter properties, a element type implementation provides a structured meta-data object to describe the
 * purpose and properties of the element.
 *
 * @author Thomas Morgner
 */
public interface ElementType extends DataSource
{
  public ElementMetaData getMetaData();

  public Object getDesignValue(ExpressionRuntime runtime, final ReportElement element);

  public void configureDesignTimeDefaults(ReportElement element, Locale locale);

  public ReportElement create();
}
