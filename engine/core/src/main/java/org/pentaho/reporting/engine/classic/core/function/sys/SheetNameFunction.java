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

package org.pentaho.reporting.engine.classic.core.function.sys;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractElementFormatFunction;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;

/**
 * This function is used to generate sheet names into table exports. Sheet names are generated on page breaks and have
 * different representations depending the export type.<br/>
 * To use this functionnality report configuration must set the property {@link #DECALRED_SHEETNAME_FUNCTION_KEY} to
 * point to an existing function or property accessible within the report.
 * <p/>
 * As for example using simple report definition:<br/>
 * 
 * <pre>
 * &lt;report&gt;
 *   &lt;configuration&gt;
 *     &lt;!-- where sheetNameExpression is pointing to a valid function declared in this report --&gt;
 *     &lt;property name="org.pentaho.reporting.engine.classic.core.targets.table.TableWriter
 *     .SheetNameFunction"&gtsheetNameExpression&lt;/property&gt;
 *   &lt;/configuration&gt;
 *   ...
 * &lt;/report&gt;
 * </pre>
 *
 * @author Cedric Pronzato
 * @deprecated This way of defining a sheetname for elements is deprecated. Sheetnames should be declared or computed
 *             directly on the bands by specifiing a sheetname using the "computed-sheetname" style-property.
 */
@SuppressWarnings( "deprecation" )
public class SheetNameFunction extends AbstractElementFormatFunction implements StructureFunction {
  private static final Log logger = LogFactory.getLog( SheetNameFunction.class );

  /**
   * The configuration property declaring the function name to call in order to generate sheet names.<br/>
   */
  private static final String DECALRED_SHEETNAME_FUNCTION_KEY =
      "org.pentaho.reporting.engine.classic.core.targets.table.TableWriter.SheetNameFunction";

  /**
   * A property that holds the last computed value of the sheetname function.
   */
  private transient String lastValue;
  /**
   * A property that holds the name of the column from where to receive the sheetname.
   */
  private transient String functionToCall;

  /**
   * Default constructor.
   */
  public SheetNameFunction() {
  }

  public void reportInitialized( final ReportEvent event ) {
    functionToCall =
        this.getReportConfiguration().getConfigProperty( SheetNameFunction.DECALRED_SHEETNAME_FUNCTION_KEY );
    super.reportInitialized( event );
  }

  public int getProcessingPriority() {
    return 3000;
  }

  protected boolean isExecutable() {
    if ( functionToCall == null ) {
      return false;
    }

    if ( !getRuntime().getExportDescriptor().startsWith( "table/" ) ) {
      return false;
    }
    return true;
  }

  protected boolean evaluateElement( final ReportElement e ) {
    lastValue = null;
    // if exporting to a table/* export
    final Object value = this.getDataRow().get( functionToCall );
    if ( value != null ) {
      lastValue = value.toString();
      e.getStyle().setStyleProperty( BandStyleKeys.COMPUTED_SHEETNAME, lastValue );
    }

    return true;
  }

  /**
   * Structure functions do not care of the result so this method should never be called.
   *
   * @return <code>null</code>
   */
  public Object getValue() {
    return lastValue;
  }
}
