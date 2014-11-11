/**
 * 
 */
package blackdoor.struct;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.Arrays;

import blackdoor.util.Misc;

/**
 * @author nfischer3
 * A FIFO queue data structure for bytes implemented with a circular array.
 * The java.nio.ByteBuffer class offers slightly faster performance, 
 * but greatly increased difficulty of use if you want to remove elements between writes.
 *
 */
public class ByteQueue {
	private byte[] array;
	private int start;
	private int end;
	private boolean resizable;
	
	public static void main(){
		ByteQueue q = new ByteQueue(10);
		q.setResizable(true);
		for ( int value = 0; value < 9; ++ value)
	       q.enQueue(new byte[]{(byte) value});
		System.out.println(q);
		q.deQueue(5);
		System.out.println(q.details());
		q.enQueue(new byte[]{20, 21, 22});
		System.out.println(q.details());
		q.deQueue(5);
		System.out.println(q.details());
		for ( int value = 0; value < 30; ++ value)
			q.enQueue(new byte[]{(byte) value});
		System.out.println(q.details());
		q.deQueue(15);
		System.out.println(q.details());
		q.trim();
		System.out.println(q.details());
	}
	
	/**
	 * Create new non-resizable ByteQueue with default size of 99 bytes.
	 */
	public ByteQueue(){
		array = new byte[100];
		resizable = false;
		zero();
	}
	/**
	 * Create new non-resizable ByteQueue with size bytes.
	 */
	public ByteQueue(int size){
		array = new byte[size];
		resizable = false;
		zero();
	}
	/**
	 * check if the Queue is full
	 * @return true if the Queue would resize or throw a buffer overflow at the next enQueue call
	 */
	public boolean isFull(){
		int endmod = (end + 1) % array.length;
		//System.out.println("start : " + start + " end: " + end + " endmod: " + endmod + " size: " + size());	
		return endmod == start;
	}
	
	@SuppressWarnings("unused")
	private boolean isEmpty(){
		return end == start;
	}
	
	private void zero(){
		start = 0;
		end = 0;
	}
	
	/**
	 * Grow or shrink the queue to newSize bytes. If newSize < filled() an underflow exception will be thrown.
	 * @param newSize
	 */
	public void resize(int newSize){
		byte[] newArray = new byte[newSize];
		int filled = filled();
		deQueue(newArray, 0, filled);
		array = newArray;
		start = 0;
		end = filled;
	}
	
	/**
	 * Shrink the queue so that capacity() == filled()
	 */
	public void trim(){
		resize(filled());
	}
	
	/**
	 * Add an array of bytes into the queue.
	 * Equivalent to calling enQueue(src, 0, src.length)
	 * @param src - the array of bytes to add to the queue
	 */
	public void enQueue(byte[] src){
		enQueue(src, 0, src.length);
	}
	
	/**
	 * Add a part of an array of bytes into the queue.
	 * @param src - the source array of bytes to add to the queue.
	 * @param offset - the start index of src from which bytes should be enqueued.
	 * @param length - the number of bytes from offset in src that should be enqueued.
	 */
	public void enQueue(byte[] src, int offset, int length){
		if(length > capacity() - filled()){
			if(!resizable)
				throw new BufferOverflowException();
			else{
				resize(length + array.length);
			}
		}
		if(length > array.length - end){
			System.arraycopy(src, offset, array, end, array.length - end);
			//System.out.println(details());
			//System.out.println("src, " + (array.length - end) + " ," + (length - (array.length - end)));
			System.arraycopy(src, offset + array.length - end, array, 0, length - (array.length - end));
		}
		else
			System.arraycopy(src, offset, array, end, length);
		end = (end + length) % array.length;
		//System.out.println(Misc.bytesToHex(array));
	}
	
	/**
	 * Remove and return length bytes from the queue.
	 * @param length - the number of bytes to remove from the queue.
	 * @return the first length bytes that were entered into the queue.
	 */
	public byte[] deQueue(int length){
		byte[] ret = new byte[length];
		deQueue(ret, 0, length);
		return ret;
	}
	
	/**
	 * Remove length bytes from the queue and put them into an array.
	 * @param dest - the destination array to put the bytes in.
	 * @param offset - the start index of dest to which bytes should be dequeued.
	 * @param length - the number of bytes from offset in dest that bytes will be copied into.
	 */
	public void deQueue(byte[] dest, int offset, int length){
		//System.out.println(this);
		//System.out.println("len" +length);
		if(length > filled()){
			throw new BufferUnderflowException();
		}
		//System.out.println(filled());
		if(start > end && !(length <= (capacity() - start))){//length > filled()){
			System.arraycopy(array, start, dest, offset, (capacity() - start));
		//	System.out.println(""+(offset + capacity() - start)  +' '+ (length - (capacity() - start)));
			System.arraycopy(array, 0, dest, offset + capacity() - start , length - (capacity() - start));
		}
		else{
			//System.out.println(Misc.bytesToHex(array));
			//System.out.println("offset length " + offset + ' ' + length);
			//System.out.println("start end " + start + ' ' + end);
			System.arraycopy(array, start, dest, offset, length);
		}
		start = (start + length) % array.length;
	}
	
	/**
	 * 
	 * @return the number of bytes in the queue.
	 */
	public int filled(){
		if(start > end)
			return array.length-start+end;
		else return end - start;
	}
	
	/**
	 * 
	 * @return the number of elements that can be stored in this buffer
	 */
	public int capacity(){
		return array.length-1;
	}

	/**
	 * 
	 * @return true if the queue will grow instead of throwing BufferOverflowException on enQueue.
	 */
	public boolean isResizable() {
		return resizable;
	}

	/**
	 * change whether the queue will grow instead of throwing BufferOverflowException on enQueue.
	 * @param resizable - set to true for growing instead of throwing.
	 */
	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	
	public String details(){
		return "start = " + start + " end = " + end + " array = " + Misc.bytesToHex(array) + "\n" + toString();
	}
	
	@Override
	public String toString() {
		String ret = "ByteQueue [array.length = " + array.length + " capacity = " + capacity() + " filled = " + filled() +" buffer = ";
		if(end > start)
			ret = ret + Misc.bytesToHex(Arrays.copyOfRange(array, start, end));
		else{
			ret = ret + Misc.bytesToHex(Arrays.copyOfRange(array, start, array.length));
			ret = ret + Misc.bytesToHex(Arrays.copyOfRange(array, 0, end));
		}
		return ret + "]";
	}
	
}
