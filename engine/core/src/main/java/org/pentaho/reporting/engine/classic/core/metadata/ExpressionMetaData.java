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

package org.pentaho.reporting.engine.classic.core.metadata;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.function.Expression;

/**
 * Provides meta-data for functions. This is basically an extension to the classical bean-info class, with some
 * additional documentations and properties, but without the setter methods (so this class is immutable) and without the
 * event-handler support (as functions cannot support event-handlers).
 * <p/>
 * The ExpressionMetaData makes the assumption, that the expression itself is a bean and that every property descriptor
 * that is retrieved here can also be retrieved using the standard bean-methods using a plain BeanInfo object.
 *
 * @author Thomas Morgner
 */
public interface ExpressionMetaData extends MetaData {
  /**
   * Checks whether the main purpose of the expression is to modify the layout of the report. This method returns true,
   * if the expression modifies one or more named elements.
   *
   * @return true, if this is a layout-processor that modifies named elements.
   */
  public boolean isElementLayoutProcessor();

  /**
   * Checks whether the main purpose of the expression is to modify the layout of the report. This method returns true,
   * if the expression modifies the global layout only.
   *
   * @return true, if this is a layout-processor that modifies the global layout.
   */
  public boolean isGlobalLayoutProcessor();

  public boolean isStatefull();

  public Image getIcon( final Locale locale, int iconKind );

  public Class getResultType();

  public Class getExpressionType();

  public BeanInfo getBeanDescriptor() throws IntrospectionException;

  public String[] getPropertyNames();

  public ExpressionPropertyMetaData[] getPropertyDescriptions();

  public ExpressionPropertyMetaData getPropertyDescription( String name );

  public Expression create();
}
