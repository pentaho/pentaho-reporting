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

package org.pentaho.reporting.designer.core.welcome;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;
import java.io.Serializable;

/**
 * Used by the WelcomePane to display available sample reports
 *
 * @author NBaker
 */
public class SampleReport implements Serializable {
  private String fileName;
  private String reportName;
  private long lastAccessTime;
  private long fileSize;

  public SampleReport( final File reportFile,
                       final ResourceManager resourceManager ) {
    this.lastAccessTime = reportFile.lastModified();
    this.fileSize = reportFile.length();
    this.fileName = reportFile.getAbsolutePath();

    try {
      final ResourceKey resourceKey = resourceManager.createKey( reportFile );
      this.reportName = computeNameFromMetadata( resourceManager, resourceKey );
      if ( StringUtils.isEmpty( this.reportName ) ) {
        this.reportName = computeNameFromReport( resourceManager, resourceKey );
      }
    } catch ( ResourceException re ) {
      // ignore ..
      this.reportName = null;
    }
  }

  private String computeNameFromMetadata( final ResourceManager resourceManager, final ResourceKey key ) {
    try {
      final Resource res = resourceManager.create( key, null, new Class[] { DocumentBundle.class } );
      final DocumentBundle rawResource = (DocumentBundle) res.getResource();
      final Object possibleTitle = rawResource.getMetaData().getBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.TITLE );
      if ( possibleTitle != null ) {
        return possibleTitle.toString();
      }
      return null;
    } catch ( ResourceException re ) {
      return null;
    }
  }

  private String computeNameFromReport( final ResourceManager resourceManager, final ResourceKey key ) {
    try {
      final Resource res = resourceManager.create( key, null, new Class[] { MasterReport.class } );
      final MasterReport rawResource = (MasterReport) res.getResource();
      final Object possibleTitle = rawResource.getName();
      if ( possibleTitle != null ) {
        return possibleTitle.toString();
      }
      return null;
    } catch ( ResourceException re ) {
      return null;
    }
  }

  public String getFileName() {
    return fileName;
  }

  public String getReportName() {
    return reportName;
  }

  public long getLastAccessTime() {
    return lastAccessTime;
  }

  public long getFileSize() {
    return fileSize;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof SampleReport ) ) {
      return false;
    }

    final SampleReport that = (SampleReport) o;

    if ( fileSize != that.fileSize ) {
      return false;
    }
    if ( lastAccessTime != that.lastAccessTime ) {
      return false;
    }
    if ( !fileName.equals( that.fileName ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = fileName.hashCode();
    result = 31 * result + (int) ( lastAccessTime ^ ( lastAccessTime >>> 32 ) );
    result = 31 * result + (int) ( fileSize ^ ( fileSize >>> 32 ) );
    return result;
  }
}
