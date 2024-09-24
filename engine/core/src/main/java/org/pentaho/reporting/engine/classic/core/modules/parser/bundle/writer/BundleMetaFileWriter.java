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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineInfo;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentMetaData;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

import java.io.IOException;
import java.util.Date;

public class BundleMetaFileWriter implements BundleWriterHandler {
  public BundleMetaFileWriter() {
  }

  /**
   * Returns a relatively high processing order indicating this BundleWriterHandler should be one of the last processed
   *
   * @return the relative processing order for this BundleWriterHandler
   */
  public int getProcessingOrder() {
    return 100000;
  }

  public String writeReport( final WriteableDocumentBundle bundle, final BundleWriterState state ) throws IOException,
    BundleWriterException {
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( bundle == null ) {
      throw new NullPointerException();
    }

    final WriteableDocumentMetaData writeableMetaData = bundle.getWriteableDocumentMetaData();
    final String version =
        ClassicEngineInfo.getInstance().getName() + ' ' + ClassicEngineInfo.getInstance().getVersion();

    writeableMetaData.setBundleAttribute( ODFMetaAttributeNames.Meta.NAMESPACE, ODFMetaAttributeNames.Meta.GENERATOR,
        version );

    final Date currentDate = new Date();
    if ( writeableMetaData.getBundleAttribute( ODFMetaAttributeNames.Meta.NAMESPACE,
        ODFMetaAttributeNames.Meta.CREATION_DATE ) == null ) {
      writeableMetaData.setBundleAttribute( ODFMetaAttributeNames.Meta.NAMESPACE,
          ODFMetaAttributeNames.Meta.CREATION_DATE, currentDate );
    }

    writeableMetaData.setBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE,
        ODFMetaAttributeNames.DublinCore.DATE, currentDate );
    final MasterReport masterReport = state.getMasterReport();
    final Object visibleFlag = masterReport.getAttribute( AttributeNames.Pentaho.NAMESPACE, "visible" );
    if ( Boolean.FALSE.equals( visibleFlag ) ) {
      writeableMetaData.setBundleAttribute( ClassicEngineBoot.METADATA_NAMESPACE, "visible", "false" );
    } else {
      writeableMetaData.setBundleAttribute( ClassicEngineBoot.METADATA_NAMESPACE, "visible", "true" );
    }

    writeVersionMarker( writeableMetaData, masterReport );
    return null;
  }

  private void writeVersionMarker( final WriteableDocumentMetaData writeableMetaData, final MasterReport masterReport ) {
    final int releaseMajor = ParserUtil.parseInt( ClassicEngineInfo.getInstance().getReleaseMajor(), -1 );
    final int releaseMinor = ParserUtil.parseInt( ClassicEngineInfo.getInstance().getReleaseMinor(), -1 );
    final int releasePatch = ParserUtil.parseInt( ClassicEngineInfo.getInstance().getReleaseMilestone(), -1 );
    int versionId = ClassicEngineBoot.computeVersionId( releaseMajor, releaseMinor, releasePatch );
    if ( versionId > 0 && ClassicEngineBoot.VERSION_TRUNK != versionId ) {
      writeableMetaData.setBundleAttribute( ClassicEngineBoot.METADATA_NAMESPACE, "prpt-spec.version.major",
          releaseMajor );
      writeableMetaData.setBundleAttribute( ClassicEngineBoot.METADATA_NAMESPACE, "prpt-spec.version.minor",
          releaseMinor );
      writeableMetaData.setBundleAttribute( ClassicEngineBoot.METADATA_NAMESPACE, "prpt-spec.version.patch",
          releasePatch );
    } else {
      final Configuration configuration = masterReport.getConfiguration();
      if ( "true"
          .equals( configuration
              .getConfigProperty( "org.pentaho.reporting.engine.classic.core.designtime.PreserveOriginalCompatibilitySettingInTrunk" ) ) ) {
        final Integer reportCompatibility = masterReport.getCompatibilityLevel();
        if ( reportCompatibility != null && reportCompatibility > 0 ) {
          final int patch = reportCompatibility % 1000;
          final int minor = ( reportCompatibility / 1000 ) % 1000;
          final int major = ( reportCompatibility / 1000000 );
          writeableMetaData.setBundleAttribute( ClassicEngineBoot.METADATA_NAMESPACE, "prpt-spec.version.major", major );
          writeableMetaData.setBundleAttribute( ClassicEngineBoot.METADATA_NAMESPACE, "prpt-spec.version.minor", minor );
          writeableMetaData.setBundleAttribute( ClassicEngineBoot.METADATA_NAMESPACE, "prpt-spec.version.patch", patch );
          return;
        }
      }
      // trunk is just the strongest player in town
      writeableMetaData.setBundleAttribute( ClassicEngineBoot.METADATA_NAMESPACE, "prpt-spec.version.major", 999 );
      writeableMetaData.setBundleAttribute( ClassicEngineBoot.METADATA_NAMESPACE, "prpt-spec.version.minor", 999 );
      writeableMetaData.setBundleAttribute( ClassicEngineBoot.METADATA_NAMESPACE, "prpt-spec.version.patch", 999 );
    }
  }
}
