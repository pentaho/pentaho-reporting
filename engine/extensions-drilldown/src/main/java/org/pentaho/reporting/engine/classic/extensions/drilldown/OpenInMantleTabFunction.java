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

package org.pentaho.reporting.engine.classic.extensions.drilldown;

import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.engine.classic.core.function.formula.DashboardModeFunction;
import org.pentaho.reporting.engine.classic.core.function.formula.QuoteTextFunction;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlTableModule;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

import java.util.ResourceBundle;

public class OpenInMantleTabFunction implements Function {
  public OpenInMantleTabFunction() {
  }

  public String getCanonicalName() {
    return "OPENINMANTLETAB";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {

    if ( parameters.getParameterCount() < 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    if ( parameters.getParameterCount() > 3 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final String urlText = typeRegistry.convertToText( parameters.getType( 0 ), parameters.getValue( 0 ) );
    if ( urlText == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    String tabText = null;
    if ( parameters.getParameterCount() > 1 ) {
      tabText = typeRegistry.convertToText( parameters.getType( 1 ), parameters.getValue( 1 ) );
    }

    final ReportFormulaContext rfc = (ReportFormulaContext) context;
    if ( StringUtils.isEmpty( tabText ) ) {
      final DocumentMetaData documentMetaData = rfc.getProcessingContext().getDocumentMetaData();
      tabText = (String) documentMetaData.getBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.TITLE );
      if ( StringUtils.isEmpty( tabText ) ) {
        final Object o = rfc.getDataRow().get( "report.name" );
        if ( o != null ) {
          tabText = String.valueOf( o );
        }
      }
      if ( StringUtils.isEmpty( tabText ) ) {
        final ResourceBundle bundle = ResourceBundle.getBundle
          ( "org.pentaho.reporting.engine.classic.extensions.drilldown.messages" );
        tabText = bundle.getString( "UnnamedTab" );
      }
    }

    final boolean tabActive;
    if ( parameters.getParameterCount() == 3 ) {
      if ( Boolean.FALSE
        .equals( typeRegistry.convertToLogical( parameters.getType( 2 ), parameters.getValue( 2 ) ) ) ) {
        tabActive = false;
      } else {
        tabActive = true;
      }
    } else {
      tabActive = true;
    }

    if ( tabActive && isTabPossible( rfc ) ) {
      final StringBuffer b = new StringBuffer();
      b.append( "javascript:top.mantle_openTab('" );
      b.append( QuoteTextFunction.saveConvert( tabText ) );
      b.append( "','" );
      b.append( QuoteTextFunction.saveConvert( tabText ) );
      b.append( "','" );
      b.append( QuoteTextFunction.saveConvert( urlText ) );
      b.append( "')" );
      return new TypeValuePair( TextType.TYPE, b.toString() );
    } else {
      return new TypeValuePair( TextType.TYPE, urlText );
    }
  }

  private boolean isTabPossible( final ReportFormulaContext rfc ) {
    if ( DashboardModeFunction.isDashboardMode( rfc ) ) {
      return false;
    }
    final String exportType = rfc.getExportType();
    if ( exportType.startsWith( "table/html" ) &&
      HtmlTableModule.ZIP_HTML_EXPORT_TYPE.equals( exportType ) == false ) {
      return true;
    }
    return false;
  }
}
