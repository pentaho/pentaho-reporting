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

package org.pentaho.reporting.libraries.base.boot;

/**
 * Provides a default implementation of the module info interface.
 *
 * @author Thomas Morgner
 */
public class DefaultModuleInfo implements ModuleInfo {
  /**
   * The name of the module class.
   */
  private String moduleClass;
  /**
   * The major version of the described module.
   */
  private String majorVersion;
  /**
   * The minor version of the described module.
   */
  private String minorVersion;
  /**
   * The patchlevel version of the described module.
   */
  private String patchLevel;

  /**
   * DefaultConstructor.
   */
  public DefaultModuleInfo() {
    // nothing required
  }

  /**
   * Creates a new module info an initalizes it with the given values.
   *
   * @param moduleClass  the class name of the module implementation holding the module description.
   * @param majorVersion the modules major version.
   * @param minorVersion the modules minor version.
   * @param patchLevel   the modules patchlevel.
   * @throws NullPointerException if the moduleClass is null.
   */
  public DefaultModuleInfo( final String moduleClass, final String majorVersion,
                            final String minorVersion, final String patchLevel ) {
    if ( moduleClass == null ) {
      throw new NullPointerException( "Module class must not be null." );
    }
    this.moduleClass = moduleClass;
    this.majorVersion = majorVersion;
    this.minorVersion = minorVersion;
    this.patchLevel = patchLevel;
  }

  /**
   * Returns the class name of the module described implementation.
   *
   * @return the module class name.
   * @see ModuleInfo#getModuleClass()
   */
  public String getModuleClass() {
    return this.moduleClass;
  }

  /**
   * Defines the module class name.
   *
   * @param moduleClass the class name of the module implementation.
   */
  public void setModuleClass( final String moduleClass ) {
    if ( moduleClass == null ) {
      throw new NullPointerException();
    }
    this.moduleClass = moduleClass;
  }

  /**
   * Returns the major version of the module. This property may be null to indicate that the module version is not
   * specified.
   *
   * @return the major version.
   * @see ModuleInfo#getMajorVersion()
   */
  public String getMajorVersion() {
    return this.majorVersion;
  }

  /**
   * Defines the major version of the module. This property may be null to indicate that the module version is not
   * specified.
   *
   * @param majorVersion the major version.
   * @see ModuleInfo#getMajorVersion()
   */
  public void setMajorVersion( final String majorVersion ) {
    this.majorVersion = majorVersion;
  }

  /**
   * Returns the minor version of the module. This property may be null to indicate that the module version is not
   * specified.
   *
   * @return the minor version.
   * @see ModuleInfo#getMajorVersion()
   */
  public String getMinorVersion() {
    return this.minorVersion;
  }

  /**
   * Defines the minor version of the module. This property may be null to indicate that the module version is not
   * specified.
   *
   * @param minorVersion the minor version.
   * @see ModuleInfo#getMajorVersion()
   */
  public void setMinorVersion( final String minorVersion ) {
    this.minorVersion = minorVersion;
  }

  /**
   * Returns the patch level version of the module. This property may be null to indicate that the module version is not
   * specified.
   *
   * @return the patch level version.
   * @see ModuleInfo#getMajorVersion()
   */
  public String getPatchLevel() {
    return this.patchLevel;
  }

  /**
   * Defines the patch level version of the module. This property may be null to indicate that the module version is not
   * specified.
   *
   * @param patchLevel the patch level version.
   * @see ModuleInfo#getMajorVersion()
   */
  public void setPatchLevel( final String patchLevel ) {
    this.patchLevel = patchLevel;
  }

  /**
   * Two moduleinfos are equal,if they have the same module class.
   *
   * @param o the other object to compare.
   * @return true, if the module points to the same module, false otherwise.
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof DefaultModuleInfo ) ) {
      return false;
    }

    final ModuleInfo defaultModuleInfo = (ModuleInfo) o;

    if ( !this.moduleClass.equals( defaultModuleInfo.getModuleClass() ) ) {
      return false;
    }
    return true;
  }

  /**
   * Computes an hashcode for this module information.
   *
   * @return the hashcode.
   * @see Object#hashCode()
   */
  public int hashCode() {
    final int result;
    result = this.moduleClass.hashCode();
    return result;
  }

  /**
   * Returns a string representation of this module information.
   *
   * @return a string describing this class.
   * @see Object#toString()
   */
  public String toString() {
    final StringBuilder buffer = new StringBuilder( 128 );
    buffer.append( getClass().getName() );
    buffer.append( "={ModuleClass=" );
    buffer.append( getModuleClass() );
    if ( getMajorVersion() != null ) {
      buffer.append( "; Version=" );
      buffer.append( getMajorVersion() );
      if ( getMinorVersion() != null ) {
        buffer.append( '-' );
        buffer.append( getMinorVersion() );
        if ( getPatchLevel() != null ) {
          buffer.append( '_' );
          buffer.append( getPatchLevel() );
        }
      }
    }
    buffer.append( '}' );
    return buffer.toString();
  }
}
