/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;

/**
 * Carries the global state while writing.
 *
 * @author Thomas Morgner
 */
public class BundleWriterState {
  private MasterReport masterReport;
  private DocumentBundle globalBundle;
  private BundleWriter bundleWriter;
  private AbstractReportDefinition report;
  private String bundleFileName;

  public BundleWriterState( final MasterReport masterReport, final DocumentBundle globalBundle,
      final BundleWriter writer ) {
    if ( masterReport == null ) {
      throw new NullPointerException();
    }
    if ( globalBundle == null ) {
      throw new NullPointerException();
    }
    if ( writer == null ) {
      throw new NullPointerException();
    }

    this.report = masterReport;
    this.masterReport = masterReport;
    this.globalBundle = globalBundle;
    this.bundleWriter = writer;
    this.bundleFileName = "";
  }

  public BundleWriterState( final BundleWriterState parent, final String bundleFileName ) {
    this( parent, parent.getReport(), bundleFileName );
  }

  public BundleWriterState( final BundleWriterState parent, final AbstractReportDefinition report,
      final String bundleFileName ) {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( parent == null ) {
      throw new NullPointerException();
    }
    if ( bundleFileName == null ) {
      throw new NullPointerException();
    }

    this.masterReport = parent.masterReport;
    this.globalBundle = parent.globalBundle;
    this.bundleWriter = parent.bundleWriter;
    this.report = report;
    this.bundleFileName = IOUtils.getInstance().getAbsolutePath( bundleFileName, parent.getFileName() );
  }

  public MasterReport getMasterReport() {
    return masterReport;
  }

  public DocumentBundle getGlobalBundle() {
    return globalBundle;
  }

  // Either subreport or jfreereport object
  public AbstractReportDefinition getReport() {
    return report;
  }

  public String getFileName() {
    return bundleFileName;
  }

  public BundleWriter getBundleWriter() {
    return bundleWriter;
  }
}
