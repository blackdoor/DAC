package blackdoor.cqbe.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import blackdoor.cqbe.unit.NodeComponentTester;
import blackdoor.cqbe.unit.RPCComponentTester;

@RunWith(Suite.class)
@SuiteClasses({ NodeComponentTester.class, RPCComponentTester.class })
public class MetaTester {

}
