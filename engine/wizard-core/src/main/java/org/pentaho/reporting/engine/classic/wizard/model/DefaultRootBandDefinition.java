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

package org.pentaho.reporting.engine.classic.wizard.model;

public class DefaultRootBandDefinition extends AbstractElementFormatDefinition implements RootBandDefinition {
  private Boolean repeat;
  private boolean visible;

  public DefaultRootBandDefinition() {
    visible = true;
  }

  public Boolean getRepeat() {
    return repeat;
  }

  public void setRepeat( final Boolean repeat ) {
    this.repeat = repeat;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible( final boolean visible ) {
    this.visible = visible;
  }
}
