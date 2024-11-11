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


package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

public interface SheetPropertySource {
  int getFreezeTop();

  int getFreezeLeft();

  String getPageHeaderCenter();

  String getPageFooterCenter();

  String getPageHeaderLeft();

  String getPageFooterLeft();

  String getPageHeaderRight();

  String getPageFooterRight();

}
