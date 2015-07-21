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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package generators;

import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Deprecated;
import org.apache.bcel.classfile.JavaClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class ExpressionDeprecationValidator {
  private static final Log logger = LogFactory.getLog( ExpressionDeprecationValidator.class );

  public static void main( final String[] args ) throws IOException {
    ClassicEngineBoot.getInstance().start();

    final ExpressionRegistry expressionRegistry = ExpressionRegistry.getInstance();
    final ExpressionMetaData[] allExpressions = expressionRegistry.getAllExpressionMetaDatas();
    for ( int i = 0; i < allExpressions.length; i++ ) {
      final ExpressionMetaData expression = allExpressions[ i ];
      if ( expression == null ) {
        logger.warn( "Null Expression encountered" );
        continue;
      }
      if ( isDeprecated( expression ) ) {
        if ( expression.isDeprecated() == false ) {
          logger.warn( "Expression code is deprecated, but metadata is not:" + expression.getExpressionType() );
        }

        if ( "Deprecated Function".equals( expression.getGrouping( Locale.US ) ) == false ) {
          logger.warn( "Expression metadata is not in deprecated group:" + expression.getExpressionType() );
        }
      } else {
        if ( expression.isDeprecated() == true ) {
          logger.warn( "Expression metadata is deprecated, but code is not:" + expression.getExpressionType() );
        }
      }

    }

  }

  private static boolean isDeprecated( final ExpressionMetaData expression )
    throws IOException {
    try {
      final Class type = expression.getExpressionType();
      final String resourcePath = type.getName().replace( '.', '/' );
      final InputStream classStream = ObjectUtilities.getResourceAsStream
        ( resourcePath + ".class", ExpressionDeprecationValidator.class );
      try {
        final ClassParser parser = new ClassParser( classStream, resourcePath );
        final JavaClass javaClass = parser.parse();
        final Attribute[] attributes = javaClass.getAttributes();
        for ( int j = 0; j < attributes.length; j++ ) {
          if ( attributes[ j ] instanceof Deprecated ) {
            return true;
          }

        }
        return false;
      } finally {
        classStream.close();
      }
    } catch ( Throwable e ) {
      e.printStackTrace();
      return false;
    }
  }

}
