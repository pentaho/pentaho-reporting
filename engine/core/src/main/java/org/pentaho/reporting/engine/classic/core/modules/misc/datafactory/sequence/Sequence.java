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

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence;

import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

import javax.swing.table.TableModel;
import java.io.Serializable;

public interface Sequence extends Cloneable, Serializable {
  public SequenceDescription getSequenceDescription();

  public Object getParameter( String name );

  public void setParameter( String name, Object value );

  public TableModel produce( final DataRow parameters, final DataFactoryContext dataFactoryContext )
    throws ReportDataFactoryException;

  public Object clone();
}
