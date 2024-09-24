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
