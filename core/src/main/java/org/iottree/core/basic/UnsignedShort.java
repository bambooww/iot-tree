package org.iottree.core.basic;


import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.primitives.UnsignedInts;

import java.math.BigInteger;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A wrapper class for unsigned {@code int} values, supporting arithmetic operations.
 *
 * <p>In some cases, when speed is more important than code readability, it may be faster simply to
 * treat primitive {@code int} values as unsigned, using the methods from {@link UnsignedInts}.
 *
 * <p>See the Guava User Guide article on <a
 * href="https://github.com/google/guava/wiki/PrimitivesExplained#unsigned-support">unsigned
 * primitive utilities</a>.
 *
 * @author Louis Wasserman
 * @since 11.0
 */

@GwtCompatible(emulated = true)
public final class UnsignedShort extends Number implements Comparable<UnsignedShort> {
  public static final UnsignedShort ZERO = fromShortBits((short)0);
  public static final UnsignedShort ONE = fromShortBits((short)1);
  public static final UnsignedShort MAX_VALUE = fromShortBits((short)-1);

  static final int SHORT_MASK = 0xffff;
  
  private final short value;

  private UnsignedShort(short value) {
    // GWT doesn't consistently overflow values to make them 32-bit, so we need to force it.
    this.value = (short)(value & 0xffff);
  }

  /**
   * Returns an {@code UnsignedShort} corresponding to a given bit representation. The argument is
   * interpreted as an unsigned 32-bit value. Specifically, the sign bit of {@code bits} is
   * interpreted as a normal bit, and all other bits are treated as usual.
   *
   * <p>If the argument is nonnegative, the returned result will be equal to {@code bits},
   * otherwise, the result will be equal to {@code 2^32 + bits}.
   *
   * <p>To represent unsigned decimal constants, consider {@link #valueOf(long)} instead.
   *
   * @since 14.0
   */
  public static UnsignedShort fromShortBits(short bits) {
    return new UnsignedShort(bits);
  }

  /**
   * Returns an {@code UnsignedShort} that is equal to {@code value}, if possible. The inverse
   * operation of {@link #longValue()}.
   */
  public static UnsignedShort valueOf(long value) {
    checkArgument(
        (value & SHORT_MASK) == value,
        "value (%s) is outside the range for an unsigned short value",
        value);
    return fromShortBits((short) value);
  }

  /**
   * Returns a {@code UnsignedShort} representing the same value as the specified {@link
   * BigInteger}. This is the inverse operation of {@link #bigIntegerValue()}.
   *
   * @throws IllegalArgumentException if {@code value} is negative or {@code value >= 2^32}
   */
  public static UnsignedShort valueOf(BigInteger value) {
    checkNotNull(value);
    checkArgument(
        value.signum() >= 0 && value.bitLength() <= Short.SIZE,
        "value (%s) is outside the range for an unsigned short value",
        value);
    return fromShortBits(value.shortValue());
  }

  /**
   * Returns an {@code UnsignedShort} holding the value of the specified {@code String}, parsed as
   * an unsigned {@code int} value.
   *
   * @throws NumberFormatException if the string does not contain a parsable unsigned {@code int}
   *     value
   */
  public static UnsignedShort valueOf(String str) {
    return valueOf(str, 10);
  }

  /**
   * Returns an {@code UnsignedShort} holding the value of the specified {@code String}, parsed as
   * an unsigned {@code int} value in the specified radix.
   *
   * @throws NumberFormatException if the string does not contain a parsable unsigned {@code int}
   *     value
   */
  public static UnsignedShort valueOf(String string, int radix) {
    return fromShortBits((short)UnsignedInts.parseUnsignedInt(string, radix));
  }

  /**
   * Returns the result of adding this and {@code val}. If the result would have more than 32 bits,
   * returns the low 32 bits of the result.
   *
   * @since 14.0
   */
  public UnsignedShort plus(UnsignedShort val) {
    return fromShortBits((short)(this.value + checkNotNull(val).value));
  }

  /**
   * Returns the result of subtracting this and {@code val}. If the result would be negative,
   * returns the low 32 bits of the result.
   *
   * @since 14.0
   */
  public UnsignedShort minus(UnsignedShort val) {
    return fromShortBits((short)(value - checkNotNull(val).value));
  }

  /**
   * Returns the result of multiplying this and {@code val}. If the result would have more than 32
   * bits, returns the low 32 bits of the result.
   *
   * @since 14.0
   */
  @GwtIncompatible // Does not truncate correctly
  public UnsignedShort times(UnsignedShort val) {
    // TODO(lowasser): make this GWT-compatible
    return fromShortBits((short)(value * checkNotNull(val).value));
  }

  /**
   * Returns the result of dividing this by {@code val}.
   *
   * @throws ArithmeticException if {@code val} is zero
   * @since 14.0
   */
  public UnsignedShort dividedBy(UnsignedShort val) {
    return fromShortBits((short)UnsignedInts.divide(value, checkNotNull(val).value));
  }

  /**
   * Returns this mod {@code val}.
   *
   * @throws ArithmeticException if {@code val} is zero
   * @since 14.0
   */
  public UnsignedShort mod(UnsignedShort val) {
    return fromShortBits((short)UnsignedInts.remainder(value, checkNotNull(val).value));
  }

  /**
   * Returns the value of this {@code UnsignedShort} as an {@code int}. This is an inverse
   * operation to {@link #fromIntBits}.
   *
   * <p>Note that if this {@code UnsignedShort} holds a value {@code >= 2^31}, the returned value
   * will be equal to {@code this - 2^32}.
   */
  @Override
  public int intValue() {
    return value & SHORT_MASK;
  }

  /** Returns the value of this {@code UnsignedShort} as a {@code long}. */
  @Override
  public long longValue() {
    return intValue();// UnsignedInts.toLong(value);
  }

  /**
   * Returns the value of this {@code UnsignedShort} as a {@code float}, analogous to a widening
   * primitive conversion from {@code int} to {@code float}, and correctly rounded.
   */
  @Override
  public float floatValue() {
    return longValue();
  }

  /**
   * Returns the value of this {@code UnsignedShort} as a {@code float}, analogous to a widening
   * primitive conversion from {@code int} to {@code double}, and correctly rounded.
   */
  @Override
  public double doubleValue() {
    return longValue();
  }

  /** Returns the value of this {@code UnsignedShort} as a {@link BigInteger}. */
  public BigInteger bigIntegerValue() {
    return BigInteger.valueOf(longValue());
  }

  /**
   * Compares this unsigned integer to another unsigned integer. Returns {@code 0} if they are
   * equal, a negative number if {@code this < other}, and a positive number if {@code this >
   * other}.
   */
  @Override
  public int compareTo(UnsignedShort other) {
    checkNotNull(other);
    return UnsignedInts.compare(value, other.value);
  }

  @Override
  public int hashCode() {
    return value;
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    if (obj instanceof UnsignedShort) {
      UnsignedShort other = (UnsignedShort) obj;
      return value == other.value;
    }
    return false;
  }

  /** Returns a string representation of the {@code UnsignedShort} value, in base 10. */
  @Override
  public String toString() {
    return toString(10);
  }

  /**
   * Returns a string representation of the {@code UnsignedShort} value, in base {@code radix}. If
   * {@code radix < Character.MIN_RADIX} or {@code radix > Character.MAX_RADIX}, the radix {@code
   * 10} is used.
   */
  public String toString(int radix) {
    return UnsignedInts.toString(value&0xFFFF, radix);
  }
}
