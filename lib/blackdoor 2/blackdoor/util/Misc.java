package blackdoor.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;

import javax.xml.bind.DatatypeConverter;

public class Misc {
	
	public static final char NULL = '\u0000';
	
	/**
	 * Get the byte representation of num
	 * @param num
	 * @return
	 */
	public static byte[] getNumberInBytes(Integer num){
		byte[] result = new byte[Integer.SIZE/8];
		int x;
		for(int i = 0; i < result.length; i++){
			x = (Integer.SIZE - (8 + 8*i));
			//System.out.println(x);
			result[i] = (byte) (num.intValue() >> x);
		}
		return result;
	}
	
	/**
	 * Get the byte representation of num
	 * @param num
	 * @return
	 */
	public static byte[] getNumberInBytes(Long num){
		byte[] result = new byte[Long.SIZE/8];
		for(int i = 0; i < result.length; i++){
			result[i] = (byte) (num.longValue() >> (Long.SIZE - (8 + 8*i)));
		}
		return result;
	}
	
	
	/**
	 * 
	 * @author: Viral Patel
	 * @description: Prints JVM memory utilization statistics
	 * @param runtime
	 */
	public static void PrintMemInfo(Runtime runtime){
		int mb = 1024*1024;
		//Print used memory
	    System.out.println("Used Memory:"
	        + (runtime.totalMemory() - runtime.freeMemory()) / mb);
	    //Print free memory
	    System.out.println("Free Memory:"
	        + runtime.freeMemory() / mb);
	    //Print total available memory
	    System.out.println("Total Memory:" + runtime.totalMemory() / mb);
	}
	public static String getHexBytes(byte[] in, String space){
		return DatatypeConverter.printHexBinary(in);
//		String out = "";
//		int tmp;
//		for(int i = 0; i < in.length; i++){
//			if(in[i] < 0){
//
//				tmp = Math.abs(in[i]) + 127;
//			}
//			else tmp = in[i];
//			if(tmp < 16)
//				out += "0";
//			out += Integer.toHexString(tmp).toUpperCase() + space;
//		}
//		if(space.equals(""))
//			return out.substring(0, out.length());
//		return out.substring(0, out.length()-1);
	}
	/**
	 * Very fast method to get hex string from byte array.
	 * credit to maybeWeCouldStealAVan and others on stackoverflow 
	 */
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	/**
	 * Serialize an object into a byte array
	 * @param s a Serializable object
	 * @return s serialized into a byte array
	 * @throws IOException
	 */
	public static byte[] serialize(Serializable s) throws IOException{
		byte[] out;
		ByteArrayOutputStream byteOutS = new ByteArrayOutputStream();
		ObjectOutputStream objOutS = new ObjectOutputStream(byteOutS);
		objOutS.writeObject(s);
		out = byteOutS.toByteArray();
		objOutS.close();
		return out;
	}
	
	/**
	 * Performs an XOR operation on two arrays of bytes, byte by byte.
	 * The returned array will be the same length as the longest parameter array,
	 * the shorter array will be padded with 0's.
	 * @param array1
	 * @param array2
	 * @return array1 XOR array2.
	 */
	public static byte[] XOR(byte[] array1, byte[] array2){
		int length;
		boolean trueIfOne;
		if(array1.length >= array2.length){
			length = array1.length;
			trueIfOne = true;
		}
		else {
			length = array2.length;
			trueIfOne = false;
		}
		byte[] array3 = new byte[length];
		int i = 0;
		if(trueIfOne){
			for(byte a:array2){
				array3[i] = (byte) (a ^ array1[i++]);
			}
			//return array1;
			System.arraycopy(array1, i, array3, i, length-i);
		}
		else{
			for(byte a:array1){
				array3[i] = (byte) (a ^ array2[i++]);
			}
			//return array2;
			System.arraycopy(array2, i, array3, i, length-i);
		}
		return array3;
	}
	/*
	 * this xor's the parameters, which must be the same length. 
	 * after calling both a and b will be changed to the same xor
	 */
	public static void XORValues(byte[] a, byte[] b){
		if(a.length != b.length)
			throw new RuntimeException("parameters are not same length");
		for(int i = 0; i < a.length; i++){
			a[i] = b[i] = (byte) (a[i]^b[i]);
		}
	}
	/*
	 * this xor's the parameters, which must be the same length. 
	 * after calling only a will be changed to a xor b
	 */
	public static byte[] XORintoA(byte[] a, byte[] b){
		if(a.length != b.length)
			throw new RuntimeException("Parameters are not same length for xor.");
		for(int i = 0; i < a.length; i++){
			a[i] = (byte) (a[i]^b[i]);
		}
		return a;
	}
	/**
	 * returns a XOR b, leaves a and b unchanged
	 * uses 3n memory (or more depending on array size and JVM settings) where n is the length of a & b.
	 * does no error checking to make sure a & b are same length
	 * @param a
	 * @param b
	 * @return a XOR b
	 */
	public static byte[] cleanXOR(byte[] a, byte[] b){
		byte[] c = new byte[a.length];
		int i=0;
		for (byte d : b)
		    c[i] = (byte) (d ^ a[i++]);
		return c;
	}
	
	public static void arraycopy(byte[] source, int srcPos, byte[] dest, int destPos, int length){
		for(int i = 0; i < length; i++){
			dest[i + destPos] = source[i + srcPos];
		}
	}
	
	/**
	 * clear the terminal screen by overwriting it with 100 blank lines.
	 */
	public static void cls(){
		for(int i = 0; i < 100; i++){
			System.out.println();
		}
	}
	
	/**
	 * @param a
	 * @param b
	 * @param c
	 * @return (a^b)%c
	 */
	public static long longBMP(long a, long b, long c){
		return BigInteger.valueOf(a).modPow(BigInteger.valueOf(b), BigInteger.valueOf(c)).longValue();
	}
}
