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
