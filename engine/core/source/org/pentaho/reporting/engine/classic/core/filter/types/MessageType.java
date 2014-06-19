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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.filter.types;

import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.filter.MessageFormatSupport;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class MessageType extends AbstractElementType
{
  public static final MessageType INSTANCE = new MessageType();

  public static class MessageTypeContext
  {
    public MessageTypeContext()
    {
      messageFormatFilter = new MessageFormatSupport();
    }

    public MessageFormatSupport messageFormatFilter;
  }

  public MessageType()
  {
    super("message");
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

    final Object nullValue = element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE);
    final Object message = ElementTypeUtils.queryStaticValue(element);
    if (message == null)
    {
      return nullValue;
    }
    
    final MessageTypeContext context = element.getElementContext(MessageTypeContext.class);
    final MessageFormatSupport messageFormatFilter = context.messageFormatFilter;
    messageFormatFilter.setFormatString(String.valueOf(message));

    final Object messageNullValue = element.getAttribute
        (AttributeNames.Core.NAMESPACE, AttributeNames.Core.MESSAGE_NULL_VALUE);
    if (messageNullValue != null)
    {
      messageFormatFilter.setNullString(String.valueOf(messageNullValue));
    }
    else if (nullValue != null)
    {
      messageFormatFilter.setNullString(String.valueOf(nullValue));
    }
    else
    {
      messageFormatFilter.setNullString(null);
    }

    final ResourceBundleFactory resourceBundleFactory = runtime.getResourceBundleFactory();
    final Locale newLocale = resourceBundleFactory.getLocale();
    if (ObjectUtilities.equal(newLocale, messageFormatFilter.getLocale()) == false)
    {
      messageFormatFilter.setLocale(resourceBundleFactory.getLocale());
    }
    
    final Object value = messageFormatFilter.performFormat(runtime.getDataRow());
    if (value == null)
    {
      return nullValue;
    }
    return String.valueOf(value);
  }

  public Object getDesignValue(final ExpressionRuntime runtime, final ReportElement element)
  {
    final Object message = ElementTypeUtils.queryStaticValue(element);
    if (message == null || String.valueOf(message).length() == 0)
    {
      return element.getElementType().getMetaData().getName();
    }
    return message;
  }

  public void configureDesignTimeDefaults(final ReportElement element, final Locale locale)
  {
    element.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "Message");
  }
}
