package blackdoor.cqbe.addressing;

import blackdoor.util.DBP;
import blackdoor.util.Misc;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;

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

    private static byte[] getNullOverlay() {return new byte[ADDRESS_SIZE];}
    private static byte[] getFullOverlay(){
        byte[] b = new byte[ADDRESS_SIZE];
        Arrays.fill(b, (byte) 0xFF);
        return b;
    }
    
    /**
     * Returns true if address is definitely not the overlay address of a node
     * @param address
     * @return true if address is definitely not the overlay address of a node
     */
    public static boolean isNonNodeAddress(Address address){
    	if(address.equals(getFullAddress()) ||
    			address.equals(getNullAddress()))
    		return true;
    	return false;
    }

    protected byte[] overlayAddress;
    
    protected Address() {
    	this.overlayAddress = getNullOverlay();
    }
        
    /**
     * Constructs an Address object based on an overlay address.
     * @param overlayAddress a byte array representing an overlay address
     * @throws AddressException if overlayAddress is not the correct size (of the same length as ADDRESS_SIZE)
     */
    public Address(byte[] overlayAddress) throws AddressException {
        if (overlayAddress.length != ADDRESS_SIZE){
            throw new AddressException("overlay address is " + overlayAddress.length + " bytes and needs to be "
                    + ADDRESS_SIZE + " bytes.");
        }
        this.overlayAddress = overlayAddress;
    }

    /**
     * Constructs an Address object with an overlay made up of all 0 bits.
     * Equivalent to calling Address(new byte[ADDRESS_SIZE])
     */
    public static Address getNullAddress(){
        try {
            return new Address(getNullOverlay());
        } catch (AddressException e) {
            DBP.printException(e);
            return null;
        }
    }

    /**
     * Constructs an Address object with an overlay made up of all 1 bits.
     */
    public static Address getFullAddress(){
        try {
            return new Address(getFullOverlay());
        } catch (AddressException e) {
            DBP.printException(e);
            return null;
        }
    }

    /**
     * Construct an Address object from a String which contains a formatted address representation (':' separated hex).
     * @throws AddressException thrown if the address parameter is not in a valid format
     */
    public Address(String address) throws AddressException{
    	//Address this = new Address();
    	this.overlayAddress = getNullOverlay();
    	StringTokenizer tk = new StringTokenizer(address, ":");
    	if(tk.countTokens() != Address.ADDRESS_SIZE)
    		throw new AddressException("String representation of Address is improperly formatted. Wrong number of elements");
    	try{
    		for(int i = 0; i < Address.ADDRESS_SIZE; i++){
    			this.overlayAddress[i] = Integer.decode("0x" + tk.nextToken()).byteValue();
    		}
    		//return a;
    	}catch (NumberFormatException e){
    		throw new AddressException("String representation of Address is improperly formatted. Invalid hex format.");
    	}
    }
    
    /**
     * Returns an AddressComparator which has this Address as its reference.
     * @return an AddressComparator which has this Address as its reference.
     */
    public AddressComparator getComparator(){
    	return new AddressComparator(this);
    }
    
    protected void setOverlayAddress(byte[] overlayAddress){
    	this.overlayAddress = overlayAddress;
    }

    /**
     * returns the overlay address of this Address object as a byte[]
     * @return a deep copy of the overlay address of this Address object
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
    
    

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(overlayAddress);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Address))
			return false;
		Address other = (Address) obj;
		if (!Arrays.equals(getOverlayAddress(), other.getOverlayAddress()))
			return false;
		return true;
	}

    public String toString(){
        return overlayAddressToString();
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
            if(ref1Distance == ref0Distance){
            	BigInteger num1 = new BigInteger(1, arg1);
            	BigInteger num0 = new BigInteger(1, arg0);
            	return num0.compareTo(num1);
            }
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
	
	public Address getComplement(){
		BigInteger b = new BigInteger(this.getOverlayAddress());
		try {
			return new Address(b.xor(new BigInteger(Address.getFullOverlay())).toByteArray());
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			DBP.printerror(e);
		}
		return null;
	}

	/**
	 * A comparator for Addresses. Distance between Addresses is defined as the Hamming weight of the first address XOR'd with the second.
	 * Since two addresses can only have a distance between them a reference point is needed to determine which is greater than the other.
	 * @author nfischer3
	 *
	 */
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
         * Returns negative if arg0 is closer to reference than arg1
         * positive if o1 is farther from reference than o2
         * 0 if they are equal distant
         * @return negative if o1 is closer to reference than o2, 0 if they are equal distant, positive if o1 is farther from reference than o2
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
