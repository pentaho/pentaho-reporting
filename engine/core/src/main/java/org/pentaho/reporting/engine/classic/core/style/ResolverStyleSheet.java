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


package org.pentaho.reporting.engine.classic.core.style;

import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class ResolverStyleSheet extends ElementStyleSheet {
  public ResolverStyleSheet() {
  }

  public ResolverStyleSheet clone() {
    return (ResolverStyleSheet) super.clone();
  }

  public void addAll( final ElementStyleSheet sourceStyleSheet ) {
    super.addAll( sourceStyleSheet );
    setChangeTrackerHash( getChangeTrackerHash() * 31 + sourceStyleSheet.getChangeTrackerHash() );
    setModificationCount( getModificationCount() * 31 + sourceStyleSheet.getModificationCount() );
  }

  public void addInherited( final ElementStyleSheet sourceStyleSheet ) {
    super.addInherited( sourceStyleSheet );
    setChangeTrackerHash( getChangeTrackerHash() * 31 + sourceStyleSheet.getChangeTrackerHash() );
    setModificationCount( getModificationCount() * 31 + sourceStyleSheet.getModificationCount() );
  }

  public void addInherited( final SimpleStyleSheet sourceStyleSheet ) {
    super.addInherited( sourceStyleSheet );
    setChangeTrackerHash( getChangeTrackerHash() * 31 + sourceStyleSheet.getChangeTrackerHash() );
    setModificationCount( getModificationCount() * 31 + sourceStyleSheet.getModificationCount() );
  }

  public void addDefault( final ElementStyleSheet sourceStyleSheet ) {
    super.addDefault( sourceStyleSheet );
    setChangeTrackerHash( getChangeTrackerHash() * 31 + sourceStyleSheet.getChangeTrackerHash() );
    setModificationCount( getModificationCount() * 31 + sourceStyleSheet.getModificationCount() );
  }

  public void setId( final InstanceID id ) {
    super.setId( id );
  }
}
