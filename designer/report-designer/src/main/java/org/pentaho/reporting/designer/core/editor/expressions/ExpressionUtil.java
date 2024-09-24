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

package org.pentaho.reporting.designer.core.editor.expressions;

import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.engine.classic.core.function.Function;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A helper util that filter all known function and expressions and returns only the expressions, but not the
 * functions.
 *
 * @author Thomas Morgner
 */
public class ExpressionUtil {
  private static ExpressionUtil instance;

  public static synchronized ExpressionUtil getInstance() {
    if ( instance == null ) {
      instance = new ExpressionUtil();
    }
    return instance;
  }

  private ExpressionMetaData[] expressions;
  private ExpressionMetaData[] functions;

  public ExpressionMetaData[] getKnownFunctions() {
    return functions.clone();
  }

  public ExpressionMetaData[] getKnownExpressions() {
    return expressions.clone();
  }

  private ExpressionUtil() {
    final ArrayList<ExpressionMetaData> allRealExpressions = new ArrayList<ExpressionMetaData>();
    final ArrayList<ExpressionMetaData> allExpressions = new ArrayList<ExpressionMetaData>();
    for ( final ExpressionMetaData metaData : ExpressionRegistry.getInstance().getAllExpressionMetaDatas() ) {
      if ( metaData.isHidden() ) {
        continue;
      }
      if ( !WorkspaceSettings.getInstance().isVisible( metaData ) ) {
        continue;
      }

      if ( Function.class.isAssignableFrom( metaData.getExpressionType() ) == false ) {
        allRealExpressions.add( metaData );
      }
      allExpressions.add( metaData );
    }

    Collections.sort( allRealExpressions, new GroupedMetaDataComparator() );
    Collections.sort( allExpressions, new GroupedMetaDataComparator() );
    this.expressions = allRealExpressions.toArray( new ExpressionMetaData[ allRealExpressions.size() ] );
    this.functions = allExpressions.toArray( new ExpressionMetaData[ allExpressions.size() ] );
  }

}
