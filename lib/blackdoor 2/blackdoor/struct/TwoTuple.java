package blackdoor.struct;

public class TwoTuple<T1, T2> {
	private T1 t1;
	private T2 t2;
	public TwoTuple(T1 one, T2 two){
		t1 = one;
		t2 = two;
	}
	/**
	 * @return the first element in the 2-tuple
	 */
	public T1 getT1() {
		return t1;
	}
	/**
	 * @param one - the first element in the 2-tuple to set
	 */
	public void setT1(T1 one) {
		this.t1 = one;
	}
	/**
	 * @return the second element in the 2-tuple
	 */
	public T2 getT2() {
		return t2;
	}
	/**
	 * @param two - the second element in the 2-tuple to set
	 */
	public void setT2(T2 two) {
		this.t2 = two;
	}
	
}
