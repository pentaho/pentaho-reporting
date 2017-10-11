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

//
///**
// * Creation-Date: 16.04.2006, 14:14:42
// *
// * @author Thomas Morgner
// */
//public class FormatValueFunction implements ContentFunction
//{
//  public FormatValueFunction()
//  {
//  }
//
//  // takes two or three parameters.
//  // param1: What shall we format
//  // param2: What data do we expect
//  // param3: What format string shall we use
//
//  public ContentToken evaluate(final DocumentContext layoutProcess,
//                               final LayoutElement element,
//                               final CSSFunctionValue function)
//          throws FunctionEvaluationException
//  {
//    final CSSValue[] params = function.getParameters();
//    if (params.length < 2)
//    {
//      throw new FunctionEvaluationException("Illegal parameter count");
//    }
//    final CSSValue rawValue = FunctionUtilities.resolveParameter(layoutProcess, element, params[0]);
//    final String typeValue = FunctionUtilities.resolveString(layoutProcess, element, params[1]);
//    final LocalizationContext localizationContext =
//            DocumentContextUtility.getLocalizationContext
//                    (layoutProcess.getDocumentContext());
//    final LayoutContext layoutContext = element.getLayoutContext();
//    if ("date".equals(typeValue))
//    {
//      if (params.length < 3)
//      {
//        return getDateValue(rawValue,
//                localizationContext.getDateFormat(layoutContext.getLanguage()));
//      }
//      else
//      {
//        final String format = FunctionUtilities.resolveString(layoutProcess, element, params[2]);
//        return getDateValue(rawValue,
//                localizationContext.getDateFormat(format, layoutContext.getLanguage()));
//      }
//    }
//    else if ("time".equals(typeValue))
//    {
//      if (params.length != 2)
//      {
//        throw new FunctionEvaluationException();
//      }
//      return getDateValue(rawValue,
//              localizationContext.getTimeFormat(layoutContext.getLanguage()));
//    }
//    else if ("number".equals(typeValue))
//    {
//      if (params.length < 3)
//      {
//        return getNumberValue(rawValue,
//                localizationContext.getDateFormat(layoutContext.getLanguage()));
//      }
//      else
//      {
//        final String format = FunctionUtilities.resolveString(layoutProcess, element, params[2]);
//        return getNumberValue(rawValue,
//                localizationContext.getNumberFormat(format, layoutContext.getLanguage()));
//      }
//    }
//    else if ("integer".equals(typeValue))
//    {
//      if (params.length != 2)
//      {
//        throw new FunctionEvaluationException();
//      }
//      return getNumberValue(rawValue,
//              localizationContext.getIntegerFormat(layoutContext.getLanguage()));
//    }
//    throw new FunctionEvaluationException("FormatType not recognized");
//  }
//
//
//  private ContentToken getNumberValue
//          (final CSSValue rawValue,
//           final Format format)
//          throws FunctionEvaluationException
//  {
//
//
//    final double number;
//    if (rawValue instanceof CSSStringValue)
//    {
//      final CSSStringValue strVal = (CSSStringValue) rawValue;
//      try
//      {
//        final CSSNumericValue nval = FunctionUtilities.parseNumberValue(strVal.getValue());
//        number = nval.getValue();
//      }
//      catch(FunctionEvaluationException fee)
//      {
//        return new StaticTextToken(strVal.getValue());
//      }
//    }
//    else if (rawValue instanceof CSSNumericValue)
//    {
//      final CSSNumericValue nval = (CSSNumericValue) rawValue;
//      number = nval.getValue();
//    }
//    else
//    {
//      // Raw-Values should not have been created for number values
//      throw new FunctionEvaluationException("Not a numeric value.");
//    }
//
//    final Double obj = new Double(number);
//    return new FormattedContentToken
//            (obj, format, format.format(obj));
//  }
//
//  private FormattedContentToken getDateValue
//          (final CSSValue rawValue,
//           final Format format)
//          throws FunctionEvaluationException
//  {
//    final Date date;
//    if (rawValue instanceof CSSRawValue)
//    {
//      final CSSRawValue cssRawValue = (CSSRawValue) rawValue;
//      final Object o = cssRawValue.getValue();
//      if (o instanceof Date == false)
//      {
//        throw new FunctionEvaluationException("Not a date value.");
//      }
//      date = (Date) o;
//    }
//    else if (rawValue instanceof CSSFormatedValue)
//    {
//      final CSSFormatedValue fval = (CSSFormatedValue) rawValue;
//      final Object o = fval.getRaw();
//      if (o instanceof Date == false)
//      {
//        throw new FunctionEvaluationException("Not a date value.");
//      }
//      date = (Date) o;
//    }
//    else
//    {
//      throw new FunctionEvaluationException("Not a date value.");
//    }
//
//    return new FormattedContentToken
//            (date, format, format.format(date));
//  }
//}
