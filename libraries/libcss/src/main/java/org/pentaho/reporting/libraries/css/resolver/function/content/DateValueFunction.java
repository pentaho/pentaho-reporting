/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.resolver.function.content;

//// todo: This definitely needs a global context. Therefore: Not yet!
///**
// * Creation-Date: 15.04.2006, 18:33:56
// *
// * @author Thomas Morgner
// */
//public class DateValueFunction implements ContentFunction
//{
//  public DateValueFunction()
//  {
//  }
//
//  public ContentToken evaluate(final LayoutProcess layoutProcess,
//                               final LayoutElement element,
//                               final CSSFunctionValue function)
//          throws FunctionEvaluationException
//  {
//
//    final Date date = DocumentContextUtility.getDate
//            (layoutProcess.getDocumentContext());
//    final CSSValue[] parameters = function.getParameters();
//    final LocalizationContext localizationContext =
//            DocumentContextUtility.getLocalizationContext
//                    (layoutProcess.getDocumentContext());
//
//    final DateFormat format = getDateFormat
//            (parameters, localizationContext,
//                    element.getLayoutContext().getLanguage());
//    return new FormattedContentToken(date, format, format.format(date));
//  }
//
//  private DateFormat getDateFormat(final CSSValue[] parameters,
//                                   final LocalizationContext localizationContext,
//                                   final Locale locale)
//  {
//    if (parameters.length < 1)
//    {
//      return localizationContext.getDateFormat(locale);
//    }
//
//    final CSSValue formatValue = parameters[0];
//    if (formatValue instanceof CSSStringValue == false)
//    {
//      return localizationContext.getDateFormat(locale);
//    }
//
//    final CSSStringValue sval = (CSSStringValue) formatValue;
//    final DateFormat format = localizationContext.getDateFormat
//            (sval.getValue(), locale);
//    if (format != null)
//    {
//      return format;
//    }
//    return localizationContext.getDateFormat(locale);
//  }
//
//
//}
