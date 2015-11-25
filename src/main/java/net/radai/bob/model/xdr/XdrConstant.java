/*
 * This file is part of Bob.
 *
 * Bob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with Bob. If not, see <http://www.gnu.org/licenses/>.
 */

package net.radai.bob.model.xdr;

import net.radai.bob.model.Identifiable;

import java.math.BigInteger;

/**
 * @author Radai Rosenblatt
 */
public class XdrConstant implements Identifiable {
    private final String identifier;
    private final BigInteger value; //might be unsigned hyper (long), which java cant handle

    public XdrConstant(String identifier, BigInteger value) {
        if (identifier == null || identifier.isEmpty()) {
            throw new IllegalArgumentException("xdr constant name cannot be empty");
        }
        if (value == null) {
            throw new IllegalArgumentException("xdr constant must have a value");
        }
        this.identifier = identifier;
        this.value = value;
    }

    public boolean fitsInt() {
        try{
            //noinspection ResultOfMethodCallIgnored
            value.intValueExact();
            return true;
        } catch (ArithmeticException e) {
            return false;
        }
    }

    public int asInt() {
        return value.intValueExact();
    }

    public boolean fitsLong() {
        try{
            //noinspection ResultOfMethodCallIgnored
            value.longValueExact();
            return true;
        } catch (ArithmeticException e) {
            return false;
        }
    }

    public long asLong() {
        return value.longValueExact();
    }

    public BigInteger asBigInteger() {
        return value;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public BigInteger getValue() {
        return value;
    }

    @Override
    public String toString() {
        return identifier + "=" + value;
    }
}
