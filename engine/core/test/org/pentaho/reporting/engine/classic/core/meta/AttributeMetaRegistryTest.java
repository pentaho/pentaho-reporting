package org.pentaho.reporting.engine.classic.core.meta;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultAttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;

public class AttributeMetaRegistryTest extends TestCase
{

  public static final String BUNDLE_LOCATION = "org.pentaho.reporting.engine.classic.core.meta.attributemetadatatest";

  public AttributeMetaRegistryTest()
  {
  }

  public void setUp()
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRegisterAttribute ()
  {
    final ElementMetaData metaData =
        ElementTypeRegistry.getInstance().getElementType(LabelType.INSTANCE.getMetaData().getName());
    final AttributeMetaData attrMeta = metaData.getAttributeDescription("namespace", "Name");
    assertNull(attrMeta);

    final AttributeRegistry attributeRegistry =
        ElementTypeRegistry.getInstance().getAttributeRegistry(LabelType.INSTANCE);

    final DefaultAttributeMetaData m = new DefaultAttributeMetaData
        ("namespace", "Name", BUNDLE_LOCATION, "prefix",
            String.class, false, ClassicEngineBoot.computeCurrentVersionId());
    attributeRegistry.setAttributeDescription(m);

    final AttributeMetaData attributeDescription = metaData.getAttributeDescription("namespace", "Name");
    assertEquals("prefix", attributeDescription.getKeyPrefix());
    assertEquals(BUNDLE_LOCATION, attributeDescription.getBundleLocation());
  }
}
