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

package org.pentaho.reporting.designer.core.util;

import org.pentaho.openformula.ui.FunctionParameterEditor;
import org.pentaho.reporting.designer.core.ReportDesignerContext;

/**
 * Todo: Document me!
 * <p/>
 * Date: 07.10.2010 Time: 18:52:50
 *
 * @author Thomas Morgner.
 */
public interface ReportDesignerFunctionParameterEditor extends FunctionParameterEditor {
  public void setReportDesignerContext( final ReportDesignerContext reportDesignerContext );
}
