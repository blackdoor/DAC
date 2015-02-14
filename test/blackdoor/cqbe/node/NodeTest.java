/**
 * 
 */
package blackdoor.cqbe.node;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import blackdoor.cqbe.node.Node.NodeBuilder;

/**
 * @author Cj Buresch
 * @version 0.0.1 -- Feb 14, 2015
 */
public class NodeTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Test
  public void adamNodeTest() {
    // NodeBuilder adamNode = new NodeBuilder();
    // adamNode.setAdam(true);
    // adamNode.setPort(1778);
    // try {
    // adamNode.buildNode();
    // } catch (Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
  }

  @Test
  public void nodeTest() {
    NodeBuilder adamNode = new NodeBuilder();
    Node node;
    adamNode.setAdam(true);
    // adamNode.setPort(1778);
    try {
      node = adamNode.buildNode();
      Thread.sleep(100);
      Node.getInstance();
      Node.shutdown();
    } catch (Exception e) {
      e.printStackTrace();
      fail("could not build correctly");

    }
  }



}
