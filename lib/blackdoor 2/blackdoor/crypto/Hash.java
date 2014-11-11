/**
 *	the cleanness of my commit history was sacrificed in a great battle 
 *	of merge conflicts so that this class could live
 */
package blackdoor.crypto;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import blackdoor.util.Misc;

/**
 * @author kAG0
 * non-static methods in this class are not suitable for use by... anyone. really they should be killed.
 */
public class Hash {
	public static final byte[] shuffle = new byte[]{(byte) 0x4b, (byte) 0xb0, (byte) 0xa9, (byte) 0xc3, (byte) 0xdf, (byte) 0x34, (byte) 0x7c, (byte) 0xc0, (byte) 0x57, (byte) 0xb9, (byte) 0x11, (byte) 0x27, (byte) 0x9e, (byte) 0x6c, (byte) 0x92, (byte) 0x25, (byte) 0x2f, (byte) 0xc8, (byte) 0x70, (byte) 0x73, (byte) 0x22, (byte) 0xf4, (byte) 0x8d, (byte) 0xd1, (byte) 0x82, (byte) 0xe3, (byte) 0xb5, (byte) 0xed, (byte) 0xd7, (byte) 0x62, (byte) 0x7d, (byte) 0x5c, (byte) 0xc1, (byte) 0x2, (byte) 0xe1, (byte) 0xc9, (byte) 0x5, (byte) 0xb1, (byte) 0xae, (byte) 0xf7, (byte) 0x83, (byte) 0xfa, (byte) 0x74, (byte) 0x2e, (byte) 0x1, (byte) 0xf8, (byte) 0x75, (byte) 0x9c, (byte) 0xf2, (byte) 0xc5, (byte) 0xb6, (byte) 0xcd, (byte) 0xb2, (byte) 0x7e, (byte) 0x9d, (byte) 0x88, (byte) 0x10, (byte) 0x30, (byte) 0x65, (byte) 0xbd, (byte) 0xca, (byte) 0x6d, (byte) 0xf0, (byte) 0xd2, (byte) 0xcc, (byte) 0xeb, (byte) 0xf3, (byte) 0x3d, (byte) 0x26, (byte) 0x5d, (byte) 0xa7, (byte) 0x3c, (byte) 0xac, (byte) 0xb7, (byte) 0x44, (byte) 0x5e, (byte) 0x8e, (byte) 0x4c, (byte) 0x85, (byte) 0x4a, (byte) 0x1e, (byte) 0x66, (byte) 0x87, (byte) 0xd0, (byte) 0x23, (byte) 0x98, (byte) 0xc2, (byte) 0x48, (byte) 0xe8, (byte) 0x42, (byte) 0x8b, (byte) 0x15, (byte) 0xd8, (byte) 0x78, (byte) 0x79, (byte) 0x77, (byte) 0x80, (byte) 0x3e, (byte) 0xde, (byte) 0x5f, (byte) 0x8c, (byte) 0xe9, (byte) 0x49, (byte) 0xb8, (byte) 0x3f, (byte) 0x43, (byte) 0x93, (byte) 0x5b, (byte) 0xe, (byte) 0xcf, (byte) 0x6e, (byte) 0x52, (byte) 0x6f, (byte) 0x81, (byte) 0xb4, (byte) 0xd5, (byte) 0x2b, (byte) 0x14, (byte) 0x94, (byte) 0xa2, (byte) 0x55, (byte) 0xef, (byte) 0x41, (byte) 0xd, (byte) 0x4d, (byte) 0xad, (byte) 0x46, (byte) 0x59, (byte) 0xfc, (byte) 0xa8, (byte) 0x69, (byte) 0x8f, (byte) 0xda, (byte) 0xf6, (byte) 0xa3, (byte) 0x1a, (byte) 0xbf, (byte) 0xe7, (byte) 0x91, (byte) 0x12, (byte) 0x45, (byte) 0x20, (byte) 0xbe, (byte) 0xa, (byte) 0x16, (byte) 0xcb, (byte) 0x99, (byte) 0x1c, (byte) 0x7b, (byte) 0x1b, (byte) 0x47, (byte) 0x95, (byte) 0xc7, (byte) 0x18, (byte) 0xbb, (byte) 0x58, (byte) 0x13, (byte) 0x6b, (byte) 0xba, (byte) 0x90, (byte) 0xb3, (byte) 0x32, (byte) 0x17, (byte) 0x89, (byte) 0x86, (byte) 0x37, (byte) 0x84, (byte) 0xee, (byte) 0xa5, (byte) 0x1f, (byte) 0x9f, (byte) 0xd4, (byte) 0xf, (byte) 0xe5, (byte) 0x21, (byte) 0xe6, (byte) 0x38, (byte) 0xfb, (byte) 0x61, (byte) 0xd6, (byte) 0x4e, (byte) 0x96, (byte) 0xd3, (byte) 0xbc, (byte) 0xce, (byte) 0xb, (byte) 0x4f, (byte) 0xdb, (byte) 0x3, (byte) 0x35, (byte) 0x50, (byte) 0x56, (byte) 0xaf, (byte) 0xc6, (byte) 0xec, (byte) 0x2c, (byte) 0x97, (byte) 0x9a, (byte) 0xaa, (byte) 0xa1, (byte) 0x8, (byte) 0x63, (byte) 0x6, (byte) 0xf9, (byte) 0x3b, (byte) 0x33, (byte) 0xe4, (byte) 0x6a, (byte) 0x60, (byte) 0x2d, (byte) 0x67, (byte) 0x71, (byte) 0xdd, (byte) 0x72, (byte) 0xe2, (byte) 0xfe, (byte) 0x7, (byte) 0x3a, (byte) 0x19, (byte) 0x29, (byte) 0x0, (byte) 0x1d, (byte) 0x68, (byte) 0x54, (byte) 0x2a, (byte) 0xe0, (byte) 0xfd, (byte) 0x9, (byte) 0x51, (byte) 0xc, (byte) 0x39, (byte) 0x4, (byte) 0x7f, (byte) 0xff, (byte) 0xa6, (byte) 0x28, (byte) 0xf5, (byte) 0xf1, (byte) 0x7a, (byte) 0xa0, (byte) 0x53, (byte) 0x8a, (byte) 0xab, (byte) 0x76, (byte) 0x24, (byte) 0x5a, (byte) 0xdc, (byte) 0x40, (byte) 0xea, (byte) 0xc4, (byte) 0x9b, (byte) 0xd9, (byte) 0x31, (byte) 0x36, (byte) 0xa4, (byte) 0x64};
	//public static int[] shuffle2 = new int[]{153 ,150 ,105 , 52 , 55 ,206 ,218 ,216 , 82 ,201 , 83 ,111 ,118 ,129 , 97 ,  0 ,229 ,179 ,113 ,155 ,199 ,122 ,133 ,141 ,191 ,236 , 28 , 14 ,212 ,160 ,  6 ,226 ,173 ,  3 , 69 , 18 , 81 , 74 , 66 ,174 ,103 , 32 ,183 ,203 , 91 ,237 , 54 , 34 ,142 ,143 , 24 , 22 ,116 ,121 , 87 ,185 , 33 , 45 ,196 ,151 ,159 ,161 ,107 , 95 ,  8 ,240 ,250 ,243 ,233 , 85 ,182 ,100 ,104 ,249 ,168 ,125 ,192 ,176 ,180 , 13 , 53 ,154 , 43 , 92 ,175 ,248 , 36 ,140 ,178 ,217 ,230 ,112 ,181 ,252 , 37 , 99 ,253 ,184 ,156 ,189 ,224 , 23 , 20 ,198 ,102 , 89 , 88 ,247 , 12 ,136 ,255 ,165 ,171 ,166 ,128 , 78 , 77 , 51 ,187 ,120 , 59 , 16 , 42 ,170 ,  4 ,109 ,225 ,177 ,  1 ,197 , 49 ,221 ,219 ,108 ,195 ,169 ,193 , 39 ,222 ,132 ,227 ,131 ,234 , 47 , 60 , 15 ,162 , 27 ,214 ,246 , 76 ,145 ,  5 ,117 , 38 , 75 ,220 ,101 ,215 ,238 ,124 , 84 ,163 ,210 , 61 ,239 , 46 , 56 ,209 ,110 , 64 ,167 , 96 , 98 , 25 ,241 ,208 ,119 ,123 , 67 , 72 ,242 ,251 , 80 ,190 , 26 ,223 ,130 , 41 ,  9 ,148 , 71 , 21 , 73 ,144 , 57 ,244 ,164 , 48 , 86 ,137 , 93 , 62 , 40 ,126 ,114 , 68 , 19 ,  2 ,139 ,135 , 79 ,231 ,202 , 94 ,254 ,158 ,115 , 35 , 29 , 90 , 44 ,  7 , 58 ,235 ,204 ,186 ,211 ,188 ,157 ,200 ,149 ,106 ,232 ,245 ,152 ,228 ,146 , 70 ,138 , 17 , 30 ,172 ,194 , 65 , 31 ,213 , 63 ,127 ,207 ,147 ,134 , 10 , 50 , 11 ,205};
	//public static int[] shuffle3 = new int[]{72,  16, 200,  54, 219,   2, 188,  40, 220,  32, 222,  63,   5,  21,  34, 151, 144,  76, 121, 177, 129, 165,  84, 139,  35,  70, 238, 145, 236, 158, 207, 122,   4, 195, 197,  88, 111, 103,  10, 229, 221, 251, 107,  90,  98,   9, 102, 162, 178, 101, 223, 114, 225,  39, 194, 204, 140,  93, 124,  75, 125,  67, 210,  81, 199,  36,  99,  89,  14, 142,  29, 246,   7, 226,  58,  12,  37, 150, 156, 161,  38, 183, 253, 143, 172, 175, 209,  77, 113, 132, 152,   1, 216,  30,  71, 106, 243, 127, 241,  65, 135,  66,  73,  68, 148, 196,  45,  78,  55, 191, 252, 192, 163, 149, 160, 254,  52, 242, 215,  46,  28,  92, 153,  27, 131, 247, 157,  82,  15, 179,  61,   6, 227, 112, 159, 118, 117,  59,  33, 176,  80,  19, 205, 166, 198, 206, 217,  56,  49, 184, 141, 237, 186,  87,  48, 240, 193,  53, 211, 202, 147, 167,  95,  57, 169, 168, 233, 137,  13,  23, 126,  31, 230, 232, 245, 128, 231, 189, 123, 171, 154,  24, 180,   8, 255, 174,  42, 249, 235, 190, 187, 173,  83, 185,  64,  17,  74,  79, 212,  86, 134, 248,  18, 146,  50, 136,  62, 108, 133, 104, 119,  20, 228,  94, 138,  51, 224,  96, 250, 155,   0, 109,  85, 130, 105,   3,  44, 213,  22, 182, 218, 110,  69, 115, 201,  11,  47, 181, 164,  43,  26, 100, 239,  60, 244, 120, 214,  97, 170, 116,  91, 208,  25, 203,  41, 234};
	//private MessageDigest mD;
	
	/**
	 * 
	 * @param file
	 * @return the SHA256 hash of file
	 * @throws IOException 
	 */
	public static byte[] getFileHash(File file) throws IOException{
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream inStream = new BufferedInputStream(fis);
		byte[] buffer = new byte[32];
		MessageDigest mD = null;
		try {
			mD = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int k;
		while((k = inStream.read(buffer)) != -1){
			//inStream.read(buffer);
			mD.update(Arrays.copyOf(buffer, k));
		}
		inStream.close();
		return mD.digest();
		
		//return Hash.getSHA256(Files.readAllBytes(file.toPath()));
	}

	/**
	 * a minimal perfect hash function for a 32 byte input
	 * @param input
	 * @return
	 */
	public static byte[] shuffle(byte[] input){
		for(int i=0; i<input.length; i++){
			int i2 = input[i];
			if(i2 < 0)
				i2 = 127 + Math.abs(i2);
			input[i] = shuffle[i2];
			//result of more shuffles could just be mapped to the first
//			i2 = input[i];
//			if(i2 < 0)
//				i2 = 127 + Math.abs(i2);
//			input[i] = (byte) shuffle2[i2];
//			i2 = input[i];
//			if(i2 < 0)
//				i2 = 127 + Math.abs(i2);
//			input[i] = (byte) shuffle3[i2];
		}
		return input;
	}
	
	private static byte[] getHash(String algorithm, byte[] input){
//		if(input == null)
//			throw new RuntimeException("input not defined");
//		//byte[] output = null;
		MessageDigest mD = null;
		try {
			mD = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		mD.update(input);
		return mD.digest();
		//return output;
	}

	@Deprecated
	public static byte[] getSHA1(byte[] input){
		return getHash("SHA-1", input);
	}
	@Deprecated
	public static String getSHA1String(byte[] input){
		return Misc.bytesToHex(getSHA1(input));
	}

	/**
	 * Get the SHA256 Hash of input
	 * @param input - the bytes to hash
	 * @return 32 bytes that represent the SHA256 hash of input;
	 */
	public static byte[] getSHA256(byte[] input){
		return getSHA256(input, false);
	}
	
	private static MessageDigest SHA256_INSTANCE = null;
	
	/**
	 * Get the SHA256 Hash of input
	 * @param input - the bytes to hash
	 * @param asSingleton if true use a singleton MessageDigest instance, else create and destroy a MD instance just for this call.
	 * @return 32 bytes that represent the SHA256 hash of input;
	 */
	public static byte[] getSHA256(byte[] input, boolean asSingleton){
		if(SHA256_INSTANCE == null){
			if(asSingleton){
				try {
					SHA256_INSTANCE = MessageDigest.getInstance("SHA-256");
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}else{
				return getHash("SHA-256", input);
			}		
		}
		return SHA256_INSTANCE.digest(input);		
	}
	
	public static byte[] getStretchedSHA256(byte[] input, byte[] salt, int length){
		MessageDigest mD = null;
		try {
			mD = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		//long startTime = System.nanoTime();
		byte[] output = new byte[32];
		System.arraycopy(input, 0, output, 0, input.length);
		while(length-- > 0){
			output = shuffle(output);
			output = mD.digest(Misc.XOR(output, salt));
		}
		//long endTime = System.nanoTime();
		//long duration = endTime - startTime;
		//System.out.println(duration);
		return output;
	}

	@Deprecated
	public static String getSHA256String(byte[] input){
		return Misc.bytesToHex(getSHA256(input));
	}

}