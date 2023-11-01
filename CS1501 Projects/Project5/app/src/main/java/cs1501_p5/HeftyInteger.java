/**
 * HeftyInteger for CS1501 Project 5
 * @author	Dr. Farnan
 */
package cs1501_p5;

import java.util.Random;

import com.google.common.html.HtmlEscapers;

public class HeftyInteger {

	private final byte[] ONE = { (byte) 1 };
	private final byte[] ZERO = { (byte) 0 };

	private byte[] val;

	/**
	 * Construct the HeftyInteger from a given byte array
	 * 
	 * @param b the byte array that this HeftyInteger should represent
	 */
	public HeftyInteger(byte[] b) {
		val = b;
	}

	/**
	 * Return this HeftyInteger's val
	 * 
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * 
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/**
	 * Add a new byte as the most significant in this
	 * 
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most
	 * significant byte will be a negative signed number
	 * 
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * 
	 * @param other the other HeftyInteger to sum with this
	 */
	public HeftyInteger add(HeftyInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		} else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			// introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		HeftyInteger res_li = new HeftyInteger(res);

		// If both operands are positive, magnitude could increase as a result
		// of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			// bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		// (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * 
	 * @return negation of this
	 */
	public HeftyInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		// (e.g., -128 can be represented in 8 bits using two's
		// complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		HeftyInteger neg_li = new HeftyInteger(neg);

		// add 1 to complete two's complement negation
		return neg_li.add(new HeftyInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * 
	 * @param other HeftyInteger to subtract from this
	 * @return difference of this and other
	 */
	public HeftyInteger subtract(HeftyInteger other) {
		return this.add(other.negate());
	}

	/**
	 * Compute the product of this and other
	 * 
	 * @param other HeftyInteger to multiply by this
	 * @return product of this and other
	 */
	public HeftyInteger multiply(HeftyInteger other) {
		byte[] thisArr;
		byte[] otherArr;
		byte[] partialArr;
		HeftyInteger total = new HeftyInteger(new byte[1]);

		if(this.isNegative()){
			thisArr = this.negate().getVal();
		} 
		else{
			thisArr = this.getVal();
		}

		if(other.isNegative()){
			otherArr = other.negate().getVal();
		} 
		else{
			otherArr = other.getVal();
		}

		for(int i = thisArr.length-1; i >= 0; i--){
			for(int j = otherArr.length-1; j >= 0; j--){
				int product = ((int) thisArr[i] & 0xFF) * ((int) otherArr[j] & 0xFF); // & 0xFF extracts last 8 bits
				int shiftPos = (thisArr.length-1) - i + (otherArr.length-1) - j;
				byte lower = (byte) (product & 0xFF);
				product = product >>> 8; // logically shift right 8 bits
				byte upper = (byte) (product & 0xFF);

				if(upper < 0){
					partialArr = new byte[3 + shiftPos];
				} 
				else{
					partialArr = new byte[2 + shiftPos];
				}

				partialArr[partialArr.length - 1 - shiftPos] = lower;
				partialArr[partialArr.length - 2 - shiftPos] = upper;
				HeftyInteger partialInt = new HeftyInteger(partialArr);
				total = total.add(partialInt);
			}
		}

		if(this.isNegative() && !other.isNegative()){
			total = total.negate();
		} 
		else if(!this.isNegative() && other.isNegative()){
			total = total.negate();
		}

		return total;
	}

	/**
	 * Run the extended Euclidean algorithm on this and other
	 * 
	 * @param other another HeftyInteger
	 * @return an array structured as follows:
	 *         0: the GCD of this and other
	 *         1: a valid x value
	 *         2: a valid y value
	 *         such that this * x + other * y == GCD in index 0
	 */
	public HeftyInteger[] XGCD(HeftyInteger other) {
		if(other.isZero()){
			return new HeftyInteger[] {this, new HeftyInteger(ONE), new HeftyInteger(ZERO)};
		}

		HeftyInteger[] vals = other.XGCD(this.mod(other));
		HeftyInteger GCD = vals[0];
		HeftyInteger x = vals[2];
		HeftyInteger div = this.divide(other)[0];
		HeftyInteger y = vals[1].subtract(div.multiply(x));
		return new HeftyInteger[] {GCD, x, y};
	}

	public boolean isZero(){
		for(byte b : val){
			if(b != 0){
				return false;
			}
		}
		return true;
	}

	public HeftyInteger mod(HeftyInteger divisor){
		return this.divide(divisor)[1];
	}

	public HeftyInteger[] divide(HeftyInteger divisor){
		HeftyInteger dividend = this;
		boolean quotient_is_neg = (this.isNegative() && !divisor.isNegative()) || (!this.isNegative() && divisor.isNegative());
		boolean remainder_is_neg = dividend.isNegative();

		if(divisor.isNegative()){
			divisor = divisor.negate();
		}
		if(dividend.isNegative()){
			dividend = dividend.negate();
		}

		if(dividend.equals(divisor)){
			HeftyInteger one = new HeftyInteger(ONE);
			if(quotient_is_neg)
				one = one.negate();
			return new HeftyInteger[] {one, new HeftyInteger(ZERO)};
		}

		if(dividend.less(divisor)) {
			if(remainder_is_neg)
				dividend = dividend.negate();
			return new HeftyInteger[] {new HeftyInteger(ZERO), dividend};
		}

		int shift_amt = dividend.length() * 8 - 1;
		divisor = divisor.leftShift(shift_amt);
		HeftyInteger remainder = dividend;
		HeftyInteger quotient = new HeftyInteger(ZERO);
		
		int i = 0;
		while(i <= shift_amt){
			HeftyInteger difference = remainder.subtract(divisor);
			if(difference.isNegative()){
				quotient = quotient.leftShiftWithZero(1);
			} 
			else{
				quotient = quotient.leftShiftWithOne(1);
				remainder = difference;
			}
			divisor = divisor.rightShiftByOne();
			i++;
		}

		quotient = quotient.trimZeroBytes();
		remainder = remainder.trimZeroBytes();

		if(remainder_is_neg && !remainder.isZero()){
			remainder = remainder.negate();
		}
		if(quotient_is_neg && !quotient.isZero()){
			quotient = quotient.negate();
		}

		return new HeftyInteger[] {quotient, remainder};
	}

	private HeftyInteger leftShift(int shiftAmount, boolean shiftWithOne){
		if(shiftAmount == 0){
			return this;
		}

		int pad_amount = getPadAmount() % 8;
		int remainder_shift = shiftAmount % 8;
		int byteShiftAmount = shiftAmount / 8;
		if((shiftAmount % 8 == 0 || remainder_shift < pad_amount)){
			byteShiftAmount += 0;
		}
		else{
			byteShiftAmount += 1;
		}

		if(remainder_shift == 0){
			remainder_shift = 8;
		}
		int right_shift_amount = 8 - remainder_shift;

		byte[] shifted = new byte[val.length + byteShiftAmount];

		
		byte[] onesShiftMask = {0, 0b00000001, 0b00000011, 0b00000111, 0b00001111, 0b00011111, 0b00111111, 0b01111111, -1};
		byte[] leftBitMask = {-1, -2, -4, -8, -16, -32, -64, -128, 0};
		byte[] rightBitMask = {-1, 127, 63, 31, 15, 7, 3, 1};

		if(remainder_shift < pad_amount){
			for(int i = 0; i < val.length - 1; i++){
				byte left_piece = (byte) ((val[i] << remainder_shift) & leftBitMask[remainder_shift]);
				byte right_piece = (byte) ((val[i + 1] >> right_shift_amount) & rightBitMask[right_shift_amount]);
				shifted[i] = (byte) (left_piece | right_piece);
			}
			shifted[val.length - 1] = (byte) (val[val.length - 1] << remainder_shift);

			if(shiftWithOne){
				shifted[val.length - 1] = (byte) (shifted[val.length - 1] | onesShiftMask[remainder_shift]);
				for(int i = val.length; i < shifted.length; i++){
					shifted[i] = (byte) 0xFF;
				}
			}
		} 

		else{
			shifted[0] = (byte) (val[0] >>> right_shift_amount);

			for(int i = 1; i < val.length; i++){
				byte left_piece = (byte) ((val[i - 1] << remainder_shift) & leftBitMask[remainder_shift]);
				byte right_piece = ((byte) ((val[i] >>> right_shift_amount) & rightBitMask[right_shift_amount]));
				shifted[i] = (byte) (left_piece | right_piece);
			}
			shifted[val.length] = (byte) (val[val.length - 1] << remainder_shift);

			if(shiftWithOne){
				shifted[val.length] = (byte) (shifted[val.length] | onesShiftMask[remainder_shift]);
				for(int i = val.length + 1; i < shifted.length; i++){
					shifted[i] = (byte) 0xFF;
				}
			}
		}
		return new HeftyInteger(shifted);
	}

	public HeftyInteger leftShiftWithZero(int shiftAmount){
		return leftShift(shiftAmount, false);
	}

	public HeftyInteger leftShiftWithOne(int shiftAmount){
		return leftShift(shiftAmount, true);
	}

	public HeftyInteger leftShift(int shiftAmount){
		return leftShift(shiftAmount, false);
	}

	public HeftyInteger rightShiftByOne(){
		byte[] shifted = new byte[val.length];
		int mem = val[0] & 1;
		shifted[0] = (byte) (val[0] >> 1);

		for(int i = 1; i < val.length; i++){
			byte current = val[i];
			int lsb = current & 1;
			byte mem_mask;

			if(mem == 1){		  //-128
				mem_mask = (byte) 0b10000000; 
			}
			else{
				mem_mask = (byte) 0;
			}
												//127
			shifted[i] = (byte) (current >> 1 & 0b01111111 | mem_mask); 
			mem = lsb;
		}
		return new HeftyInteger(shifted);
	}

	public int compareTo(HeftyInteger other){
		HeftyInteger heftyA = this.trimZeroBytes();
		HeftyInteger heftyB = other.trimZeroBytes();

		if(heftyA.isNegative() && !heftyB.isNegative()){
			return -1;
		}
		if(!heftyA.isNegative() && heftyB.isNegative()){
			return 1;
		}

		int ifNegFlip; 
		if(heftyA.isNegative()){
			ifNegFlip = -1;
		}
		else{
			ifNegFlip = 1;
		}

		if(heftyA.length() > heftyB.length()){
			return 1 * ifNegFlip;
		}
		if(heftyA.length() < heftyB.length()){
			return -1 * ifNegFlip;
		}

		byte[] a = heftyA.getVal();
		byte[] b = heftyB.getVal();

		if(heftyA.isNegative()){
			a = heftyA.negate().getVal();
			b = heftyB.negate().getVal();
		}

		int res = 0;

		for(int i = 0; i < a.length; i++){
			//& 0xFF converts to unsigned int to compare just magnitudes
			int curr_byte_a = a[i] & 0xFF;
			int curr_byte_b = b[i] & 0xFF;
			if(curr_byte_a < curr_byte_b){
				res = -1;
				break;
			} 
			else if(curr_byte_a > curr_byte_b){
				res = 1;
				break;
			}
		}
		return res * ifNegFlip;
	}

	public boolean less(HeftyInteger other){
		return this.compareTo(other) < 0;
	}
 
	public HeftyInteger trimZeroBytes(){
		int pad = getPadAmount() - 1;
		int leading = pad / 8;
		if(pad > 8){
			byte[] newb = new byte[val.length - leading];
			System.arraycopy(val, leading, newb, 0, val.length - leading);
			return new HeftyInteger(newb);
		}
		return this;
	}

	public int getPadAmount(){
		int i = 0;
		byte curr = val[i];
		int first_bit = curr >> 7 & 1;

		byte padded;
		if(first_bit == 1){
			padded = (byte) 0xFF;
		}
		else{
			padded = 0;
		}

		int res = 0;
		// Add 8 bits for every byte of padding
		while(curr == padded && i + 1 < val.length){
			curr = val[++i];
			res += 8;
		}
		int curr_bit = 7;
		while((curr >> curr_bit & 1) == first_bit && curr_bit >= 0){ 
			curr_bit--;
			res++;
		}
		return res;
	}
}
