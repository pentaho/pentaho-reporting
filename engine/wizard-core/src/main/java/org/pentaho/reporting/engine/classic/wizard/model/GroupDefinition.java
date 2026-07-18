/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.wizard.model;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;

public interface GroupDefinition extends FieldDefinition {
  public GroupType getGroupType();

  public void setGroupType( GroupType groupType );

  public String getGroupName();

  public void setGroupName( String name );

  public RootBandDefinition getHeader();

  public RootBandDefinition getFooter();

  public String getGroupTotalsLabel();

  public void setGroupTotalsLabel( String groupTotalsLabel );

  public ElementAlignment getTotalsHorizontalAlignment();

  public void setTotalsHorizontalAlignment( ElementAlignment alignment );
}
