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
