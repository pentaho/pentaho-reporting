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


package org.pentaho.reporting.engine.classic.core.layout.text;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.libraries.fonts.registry.BaselineInfo;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.*;

import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;


@RunWith( MockitoJUnitRunner.class )
public class TextUtilityTest {

  @Test
  public void testCreateCorrectBaselineInfoStayEqual() throws Exception {
    try( MockedStatic<TextUtility> mockedStatic = mockStatic(TextUtility.class) ) {
      mockedStatic.when(() -> TextUtility.createPaddedBaselineInfo(anyInt(), any(FontMetrics.class), any(BaselineInfo.class))).thenCallRealMethod();

      long maxHeight = (long) 15000;
      FontMetrics fontMetrics = mock(FontMetrics.class);
      when(fontMetrics.getMaxHeight()).thenReturn(maxHeight);
      when(fontMetrics.getMaxAscent()).thenReturn((long) 9000);
      when(fontMetrics.getMaxDescent()).thenReturn((long) 9000);
      BaselineInfo baseLineInfo = new BaselineInfo();
      baseLineInfo.setDominantBaseline(BaselineInfo.MIDDLE);
      when(fontMetrics.getBaselines(anyInt(), Mockito.<BaselineInfo>any())).thenReturn(baseLineInfo);

      DefaultExtendedBaselineInfo baseLine = (DefaultExtendedBaselineInfo) TextUtility.createPaddedBaselineInfo('x', fontMetrics, baseLineInfo);
      Assert.assertEquals(baseLine.getBaselines()[ExtendedBaselineInfo.AFTER_EDGE], RenderableText.convert(maxHeight));
    }
  }

  @Test
  public void testCreateCorrectBaselineInfoCorrect() throws Exception {
    try( MockedStatic<TextUtility> mockedStatic = mockStatic(TextUtility.class) ) {
      mockedStatic.when(() -> TextUtility.createPaddedBaselineInfo(anyInt(), any(FontMetrics.class), any(BaselineInfo.class))).thenCallRealMethod();

      long maxHeight = (long) 15000;
      FontMetrics fontMetrics = mock(FontMetrics.class);
      when(fontMetrics.getMaxHeight()).thenReturn(maxHeight);
      when(fontMetrics.getMaxAscent()).thenReturn((long) 7500);
      when(fontMetrics.getMaxDescent()).thenReturn((long) 7500);
      BaselineInfo baseLineInfo = new BaselineInfo();
      baseLineInfo.setDominantBaseline(BaselineInfo.MIDDLE);
      when(fontMetrics.getBaselines(anyInt(), Mockito.<BaselineInfo>any())).thenReturn(baseLineInfo);

      DefaultExtendedBaselineInfo baseLine = (DefaultExtendedBaselineInfo) TextUtility.createPaddedBaselineInfo('x', fontMetrics, baseLineInfo);
      Assert.assertTrue(baseLine.getBaselines()[ExtendedBaselineInfo.AFTER_EDGE] >= RenderableText.convert(maxHeight));
    }
  }

  @Test
  public void testCreateCorrectBaselineWithMaxHeightGreaterThanAscentPlusDescent() throws Exception {
    try( MockedStatic<TextUtility> mockedStatic = mockStatic(TextUtility.class) ) {
      mockedStatic.when(() -> TextUtility.createPaddedBaselineInfo(anyInt(), any(FontMetrics.class), any(BaselineInfo.class))).thenCallRealMethod();

      long maxHeight = (long) 15000;
      FontMetrics fontMetrics = mock(FontMetrics.class);
      when(fontMetrics.getMaxHeight()).thenReturn(maxHeight);
      when(fontMetrics.getMaxAscent()).thenReturn((long) 4500);
      when(fontMetrics.getMaxDescent()).thenReturn((long) 4500);
      BaselineInfo baseLineInfo = new BaselineInfo();
      baseLineInfo.setDominantBaseline(BaselineInfo.MIDDLE);
      when(fontMetrics.getBaselines(anyInt(), Mockito.<BaselineInfo>any())).thenReturn(baseLineInfo);

      DefaultExtendedBaselineInfo baseLine = (DefaultExtendedBaselineInfo) TextUtility.createPaddedBaselineInfo('x', fontMetrics, baseLineInfo);
      Assert.assertEquals(baseLine.getBaselines()[ExtendedBaselineInfo.AFTER_EDGE], RenderableText.convert(maxHeight));
    }
  }

  @Test
  public void testCreateCorrectBaselineWithMaxHeightLowerThanAscentPlusDescent() throws Exception {
    try( MockedStatic<TextUtility> mockedStatic = mockStatic(TextUtility.class) ) {
      mockedStatic.when(() -> TextUtility.createPaddedBaselineInfo(anyInt(), any(FontMetrics.class), any(BaselineInfo.class))).thenCallRealMethod();

      long maxHeight = (long) 15000;
      FontMetrics fontMetrics = mock(FontMetrics.class);
      when(fontMetrics.getMaxHeight()).thenReturn(maxHeight);
      when(fontMetrics.getMaxAscent()).thenReturn((long) 15000);
      when(fontMetrics.getMaxDescent()).thenReturn((long) 15000);
      BaselineInfo baseLineInfo = new BaselineInfo();
      baseLineInfo.setDominantBaseline(BaselineInfo.MIDDLE);
      when(fontMetrics.getBaselines(anyInt(), Mockito.<BaselineInfo>any())).thenReturn(baseLineInfo);

      DefaultExtendedBaselineInfo baseLine = (DefaultExtendedBaselineInfo) TextUtility.createPaddedBaselineInfo('x', fontMetrics, baseLineInfo);
      Assert.assertTrue(baseLine.getBaselines()[ExtendedBaselineInfo.AFTER_EDGE] >= RenderableText.convert(maxHeight));
    }
  }
}



