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

package org.pentaho.reporting.designer.core.editor.structuretree;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;


public class ReportFieldNode {
  private DataFactory source;
  private String fieldName;
  private Class fieldClass;
  private ReportDocumentContext context;

  public ReportFieldNode( final ReportDocumentContext context,
                          final String fieldName,
                          final Class fieldClass ) {
    this( context, null, fieldName, fieldClass );
  }

  public ReportFieldNode( final ReportDocumentContext context,
                          final DataFactory source,
                          final String fieldName,
                          final Class fieldClass ) {
    ArgumentNullException.validate( "context", context );
    ArgumentNullException.validate( "fieldName", fieldName );
    ArgumentNullException.validate( "fieldClass", fieldClass );

    this.context = context;
    this.fieldName = fieldName;
    this.fieldClass = fieldClass;
    this.source = source;
  }

  public ContextAwareDataSchemaModel getDataSchemaModel() {
    return context.getReportDataSchemaModel();
  }

  public DataFactory getSource() {
    return source;
  }

  @Override
  public String toString() {
    return "ReportFieldNode{" + // NON-NLS
      "source=" + source + // NON-NLS
      ", fieldName='" + fieldName + '\'' +// NON-NLS
      ", fieldClass=" + fieldClass +// NON-NLS
      '}';// NON-NLS
  }

  public String getFieldName() {
    return fieldName;
  }

  public Class getFieldClass() {
    return fieldClass;
  }

  public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final ReportFieldNode that = (ReportFieldNode) o;

    if ( fieldClass != null ? !fieldClass.equals( that.fieldClass ) : that.fieldClass != null ) {
      return false;
    }
    if ( !fieldName.equals( that.fieldName ) ) {
      return false;
    }

    if ( source != null ? !source.equals( that.source ) : that.source != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = source != null ? source.hashCode() : 0;
    result = 31 * result + fieldName.hashCode();
    result = 31 * result + ( fieldClass != null ? fieldClass.hashCode() : 0 );
    return result;
  }
}
