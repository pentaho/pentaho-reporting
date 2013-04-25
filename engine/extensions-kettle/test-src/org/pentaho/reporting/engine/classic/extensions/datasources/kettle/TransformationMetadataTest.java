package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

public class TransformationMetadataTest {

  @Test
  public void testRegisterDatasources() {
    try {
      TransformationDatasourceMetadata.registerDatasources();
    } catch (ReportDataFactoryException e) {
    }
  }

}
