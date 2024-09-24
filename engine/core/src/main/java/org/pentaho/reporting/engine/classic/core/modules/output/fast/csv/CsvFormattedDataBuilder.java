/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast.csv;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.filter.MessageFormatSupport;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.AbstractFormattedDataBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastTextExtractor;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.CSVQuoter;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class CsvFormattedDataBuilder extends AbstractFormattedDataBuilder {
  private HashMap<InstanceID, String> idMapping;
  private HashMap<String, Object> values;
  private final MessageFormatSupport messageFormatSupport;
  private CSVQuoter csvQuoter;
  private String encoding;
  private FastTextExtractor textExtractor;

  public CsvFormattedDataBuilder( HashMap<InstanceID, String> idMapping, MessageFormatSupport messageFormatSupport,
      CSVQuoter csvQuoter, String encoding ) {
    this.idMapping = idMapping;
    this.messageFormatSupport = messageFormatSupport;
    this.csvQuoter = csvQuoter;
    this.encoding = encoding;
    this.values = new HashMap<String, Object>();
  }

  public void compute( final Band band, final ExpressionRuntime runtime, final OutputStream out )
    throws ReportProcessingException, ContentProcessingException, IOException {
    String text = messageFormatSupport.performFormat( computeData( band, runtime ) );
    out.write( text.getBytes( encoding ) );
  }

  protected DataRow computeData( Band band, ExpressionRuntime runtime ) {
    if ( band.getComputedStyle().getBooleanStyleProperty( ElementStyleKeys.VISIBLE ) == false ) {
      return new StaticDataRow();
    }

    this.values.clear();
    compute( band, runtime );
    return new StaticDataRow( values );
  }

  protected void inspectElement( final ReportElement element ) {
    String uuid = idMapping.get( element.getObjectID() );
    if ( uuid == null ) {
      return;
    }
    if ( element.getComputedStyle().getBooleanStyleProperty( ElementStyleKeys.VISIBLE ) == false ) {
      return;
    }

    if ( textExtractor == null ) {
      textExtractor = new FastTextExtractor();
    }

    try {
      textExtractor.compute( element, getRuntime() );
      String text = textExtractor.getText();
      if ( StringUtils.isEmpty( text ) == false ) {
        values.put( uuid, csvQuoter.doQuoting( text ) );
      }
    } catch ( ContentProcessingException rse ) {
      throw new InvalidReportStateException( rse );
    }
  }
}
