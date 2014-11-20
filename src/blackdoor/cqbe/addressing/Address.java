package blackdoor.cqbe.addressing;

import blackdoor.util.DBP;
import blackdoor.util.Misc;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Nathaniel Fischer
 * @version v1.0.0 - Nov 13, 2014
 * This class is the superclass of all addresses that might be encountered in an overlay network.
 * That is, all addresses have an overlay address.
 */
public class Address implements Serializable {

    /**
     * The size of an overlay address, in bytes.
     */
    public static final int ADDRESS_SIZE = 32;

    public static byte[] nullOverlay = new byte[ADDRESS_SIZE];
    public static byte[] getFullOverlay(){
        byte[] b = new byte[ADDRESS_SIZE];
        Arrays.fill(b, (byte) 0xFF);
        return b;
    }

    protected byte[] overlayAddress;

    /**
     * Constructs an Address object based on an overlay address.
     * @param overlayAddress a byte array representing an overlay address
     * @throws AddressException if overlayAddress is not the correct size (of the same length as ADDRESS_SIZE)
     */
    public Address(byte[] overlayAddress) throws AddressException {
        if (overlayAddress.length != ADDRESS_SIZE){
            throw new AddressException("overlay address is " + overlayAddress.length + " bytes and needs to be "
                    + ADDRESS_SIZE + " bytes");
        }
        this.overlayAddress = overlayAddress;
    }

    /**
     * Constructs an Address object with an overlay made up of all 0 bits.
     * Equivalent to calling Address(new byte[ADDRESS_SIZE])
     */
    public Address(){
       this.overlayAddress = nullOverlay;
    }

    /**
     * returns the overlay address of this Address object as a byte[]
     * @return a copy of the overlay address of this Address object
     */
    public byte[] getOverlayAddress() {
        return Arrays.copyOf(overlayAddress, overlayAddress.length);
    }

    /**
     * A pretty-printed representation of the overlay address for this Address object. Each byte is separated by a colon
     * @return a pretty-printed representation of the overlay address for this Address object
     */
    public String overlayAddressToString(){
        return Misc.getHexBytes(overlayAddress, ":");
    }

    public static class OverlayComparator implements Comparator<byte[]> {
        private byte[] ref;

        public OverlayComparator(byte[] refrence){
            this.ref = refrence;

        }

        public byte[] getReferenceAddress(){
            return ref;
        }

        @Override
        public int compare(byte[] arg0, byte[] arg1) {
            int ref0Distance = Misc.getHammingDistance(arg0, ref);
            int ref1Distance = Misc.getHammingDistance(arg1, ref);
            return ref0Distance - ref1Distance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OverlayComparator that = (OverlayComparator) o;

            if (!Arrays.equals(ref, that.ref)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(ref);
        }

        @Override
        public String toString() {
            return "OverlayComparator{" +
                    "ref=" + Misc.getHexBytes(ref, ":") +
                    '}';
        }
    }

    public static class AddressComparator implements Comparator<Address> {

        private OverlayComparator comp;

        public AddressComparator(Address refrence) {
            this.comp = new OverlayComparator(refrence.getOverlayAddress());
        }

        public Address getRefrenceAddress() {
            try {
                return new Address(comp.getReferenceAddress());
            } catch (AddressException e) {
                DBP.printException(e);
            }
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AddressComparator that = (AddressComparator) o;

            if (!comp.equals(that.comp)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return comp.hashCode();
        }

        /**
         * returns negative if arg0 is closer to reference than arg1
         * positive if arg0 is farther from reference than arg1
         * 0 if they are equal distant
         */
        public int compare(Address o1, Address o2) {
            return comp.compare(o1.getOverlayAddress(), o2.getOverlayAddress());
        }

        @Override
        public String toString() {
            return "AddressComparator{" +
                    "comp=" + comp +
                    '}';
        }
    }
}
