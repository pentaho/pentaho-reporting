package org.pentaho.reporting.libraries.repository;

import java.util.HashSet;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.repository.dummy.DummyContentLocation;
import org.pentaho.reporting.libraries.repository.dummy.DummyRepository;

@SuppressWarnings("HardCodedStringLiteral")
public class PageSequenceNameGeneratorTest extends TestCase
{
  private class TestLocation extends DummyContentLocation
  {
    private HashSet<String> names;

    /**
     * Creates a new root DummyContentLocation with the given repository and name.
     *
     * @param name the name of this location.
     */
    private TestLocation(final String name)
    {
      super(new DummyRepository(), name);
      names = new HashSet<String>();
    }

    public void addExistingLocation(final String name)
    {
      names.add(name);
    }

    /**
     * A dummy location does not have children, therefore this method always returns false.
     *
     * @param name the name of the item.
     * @return false.
     */
    public boolean exists(final String name)
    {
      return names.contains(name);
    }
  }

  public PageSequenceNameGeneratorTest()
  {
  }

  public void testSequenceCounting() throws ContentIOException
  {
    final TestLocation testLocation = new TestLocation("name");
    final PageSequenceNameGenerator gen = new PageSequenceNameGenerator(testLocation, "test-file", "data");

    assertEquals("test-file-0.data", gen.generateName(null, "application/x-binary (not used)"));
    assertEquals("test-file-1.data", gen.generateName(null, "application/x-binary (not used)"));
    assertEquals("test-file-2.data", gen.generateName(null, "application/x-binary (not used)"));
  }

  public void testSequenceCountingError() throws ContentIOException
  {
    final TestLocation testLocation = new TestLocation("name");
    testLocation.addExistingLocation("test-file-2.data");
    final PageSequenceNameGenerator gen = new PageSequenceNameGenerator(testLocation, "test-file", "data");

    assertEquals("test-file-0.data", gen.generateName(null, "application/x-binary (not used)"));
    assertEquals("test-file-1.data", gen.generateName(null, "application/x-binary (not used)"));
    try
    {
      assertEquals("test-file-2.data", gen.generateName(null, "application/x-binary (not used)"));
      fail();
    }
    catch (ContentIOException e)
    {
      // expected
    }

    try
    {
      assertEquals("test-file-2.data", gen.generateName(null, "application/x-binary (not used)"));
      fail();
      // continue to fail
    }
    catch (ContentIOException e)
    {
      // expected
    }
  }
}
