package blackdoor.cqbe.test;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.util.Misc;

/**
 * Created by nfischer3 on 11/19/14.
 */
public class AddressTester {
    public static void main(String[] arg0) throws AddressException {
        Address a = new Address(Address.nullOverlay);
        Address b = new Address(Address.getFullOverlay());
        Address.AddressComparator comp1 = new Address.AddressComparator(a);
        Address.OverlayComparator comp2 = new Address.OverlayComparator(a.getOverlayAddress());
        System.out.println(comp1.compare(a, b));
        System.out.println(comp1.compare(a, a));
        System.out.println(Misc.bytesToHex(L3Address.v426(new byte[]{1, 2, 3, 4})));
    }
}
