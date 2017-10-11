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

package org.pentaho.reporting.engine.classic.core.designtime.compat;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.InternationalizedLoader;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public abstract class AbstractCompatibilityConverter implements CompatibilityConverter {
  private static class BinaryDataLoader extends InternationalizedLoader<byte[]> {
    private BinaryDataLoader() {
    }

    public byte[] load( final String name, final Locale locale ) {
      return super.load( name, locale );
    }

    protected byte[] loadData( final String name ) {
      final InputStream stream = ObjectUtilities.getResourceAsStream( name, getClass() );
      if ( stream == null ) {
        return null;
      }
      try {
        try {
          final ByteArrayOutputStream out = new ByteArrayOutputStream();
          IOUtils.getInstance().copyStreams( stream, out );
          return out.toByteArray();
        } finally {
          stream.close();
        }
      } catch ( IOException e ) {
        e.printStackTrace();
        return null;
      }
    }

    protected String getExtension() {
      return ".html";
    }
  }

  protected AbstractCompatibilityConverter() {
  }

  public String getUpgradeDescription( final Locale locale ) {
    try {
      final String name = getClass().getName();
      final String path = name.replace( '.', '/' );
      final byte[] bytes = new BinaryDataLoader().load( path, locale );
      if ( bytes == null ) {
        return "";
      }

      return new String( bytes, "UTF-8" );
    } catch ( UnsupportedEncodingException e ) {
      // should never happen in a standard-conforming JDK
      throw new IllegalStateException( "UTF-8 is not a supported encoding in this JDK." );
    }
  }

  public void inspectElement( final ReportElement element ) {

  }

  public void inspectAttributeExpression( final ReportElement element, final String attributeNamespace,
      final String attributeName, final Expression expression, final ExpressionMetaData expressionMetaData ) {

  }

  public void inspectStyleExpression( final ReportElement element, final StyleKey styleKey,
      final Expression expression, final ExpressionMetaData expressionMetaData ) {

  }

  public void inspectExpression( final AbstractReportDefinition report, final Expression expression ) {

  }

  public void inspectParameter( final AbstractReportDefinition report, final ReportParameterDefinition definition,
      final ParameterDefinitionEntry parameter ) {

  }

  public void inspectDataSource( final AbstractReportDefinition report, final DataFactory dataFactory ) {

  }
}
