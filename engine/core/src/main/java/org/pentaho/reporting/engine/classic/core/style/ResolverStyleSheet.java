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
