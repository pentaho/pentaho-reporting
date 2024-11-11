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


package org.pentaho.reporting.engine.classic.core.metadata.propertyeditors;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.CrosstabDetailMode;
import org.pentaho.reporting.engine.classic.core.CrosstabHeaderMode;
import org.pentaho.reporting.engine.classic.core.CrosstabNormalizationMode;
import org.pentaho.reporting.engine.classic.core.CrosstabSummaryPosition;
import org.pentaho.reporting.engine.classic.core.modules.misc.survey.SurveyScaleShapeType;
import org.pentaho.reporting.engine.classic.core.modules.misc.survey.SurveyScaleShapeTypePropertyEditor;
import org.pentaho.reporting.engine.classic.core.style.TableLayout;

public class EnumPropertyEditorTest {

  @Test
  public void testCrosstabSummaryPositionPropertyEditor() {
    EnumPropertyEditorChecker checker =
        new EnumPropertyEditorChecker( new CrosstabSummaryPositionPropertyEditor(), CrosstabSummaryPosition.class );
    checker.checkAll();
  }

  @Test
  public void testCrosstabDetailModePropertyEditor() {
    EnumPropertyEditorChecker checker =
        new EnumPropertyEditorChecker( new CrosstabDetailModePropertyEditor(), CrosstabDetailMode.class );
    checker.checkAll();
  }

  @Test
  public void testCrosstabHeaderModePropertyEditor() {
    EnumPropertyEditorChecker checker =
        new EnumPropertyEditorChecker( new CrosstabHeaderModePropertyEditor(), CrosstabHeaderMode.class );
    checker.checkAll();
  }

  @Test
  public void testCrosstabNormalizationModePropertyEditor() {
    EnumPropertyEditorChecker checker =
        new EnumPropertyEditorChecker( new CrosstabNormalizationModePropertyEditor(), CrosstabNormalizationMode.class );
    checker.checkAll();
  }

  @Test
  public void testSurveyScaleShapeTypePropertyEditor() {
    EnumPropertyEditorChecker checker =
        new EnumPropertyEditorChecker( new SurveyScaleShapeTypePropertyEditor(), SurveyScaleShapeType.class );
    checker.checkAll();
  }

  @Test
  public void testTableLayoutPropertyEditor() {
    EnumPropertyEditorChecker checker =
        new EnumPropertyEditorChecker( new TableLayoutPropertyEditor(), TableLayout.class );
    checker.checkAll();
  }
}
