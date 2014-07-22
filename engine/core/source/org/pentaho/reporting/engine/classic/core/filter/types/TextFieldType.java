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

package org.pentaho.reporting.engine.classic.core.filter.types;

import javax.swing.text.Document;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.util.ReportDrawableRotatedComponent;
import org.pentaho.reporting.engine.classic.core.util.RotationUtils;

public class TextFieldType extends AbstractElementType
{
  public static final ElementType INSTANCE = new TextFieldType();

  public TextFieldType()
  {
    super("text-field");
  }

  public Object getDesignValue(final ExpressionRuntime runtime, final ReportElement element)
  {
    final Object staticValue = ElementTypeUtils.queryStaticValue(element);
    if (staticValue != null)
    {
      return staticValue;
    }
    return ElementTypeUtils.queryFieldName(element);
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime the expression runtime that is used to evaluate formulas and expressions when computing the value of
   *                this filter.
   * @param element the element from which to read attribute.
   * @return the value.
   */
  public Object getValue(final ExpressionRuntime runtime, final ReportElement element)
  {
    if (runtime == null)
    {
      throw new NullPointerException("Runtime must never be null.");
    }
    if (element == null)
    {
      throw new NullPointerException("Element must never be null.");
    }

    final Object o = ElementTypeUtils.queryFieldOrValue(runtime, element);
    if (o instanceof Document)
    {
      return o;
    }

    final String retval = ElementTypeUtils.toString(o);
    if (retval == null)
    {
      return element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE);
    }

    final float rotation = RotationUtils.getRotation(element);
    
    final boolean isPdf = AbstractReportProcessor.isPdf.get() == null || AbstractReportProcessor.isPdf.get();

    return rotation == RotationUtils.NO_ROTATION ? String.valueOf(retval) :
      isPdf ? new ReportDrawableRotatedComponent( String.valueOf(retval), rotation, element ) : String.valueOf(retval);
  }
}
