package blackdoor.cqbe.headcount;

import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.L3Address;

import java.util.Date;

/**
 * Created by nfischer3 on 4/21/15.
 */
public class Node {
	Date timestamp;
	AddressTable table;
	L3Address addr;
	int group;
}
