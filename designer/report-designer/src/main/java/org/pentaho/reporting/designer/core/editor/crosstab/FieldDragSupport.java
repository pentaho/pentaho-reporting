/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.editor.crosstab;

import javax.swing.*;
import java.util.List;
import java.util.UUID;

public interface FieldDragSupport {
  public UUID getDragId();

  public void removeValues( List<IndexedTransferable.FieldTuple> fields );

  public List<IndexedTransferable.FieldTuple> getSelectedFields();

  public void insert( final TransferHandler.DropLocation point,
                      final List<IndexedTransferable.FieldTuple> items,
                      final boolean preventDuplicates );
}
