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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.radai.bob.model;

/**
 * @author Radai Rosenblatt
 */
public class XdrTypeDefDeclaration {
    private boolean optional;
    private boolean array;
    private boolean fixedSize;
    private int sizeLimit; //-1 for none
    private String identifier;
    private XdrBasicType resolvesToType;
    private XdrTypeDefDeclaration resolvesTo; //underlying def this resolves to.

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isArray() {
        return array;
    }

    public void setArray(boolean array) {
        this.array = array;
    }

    public boolean isFixedSize() {
        return fixedSize;
    }

    public void setFixedSize(boolean fixedSize) {
        this.fixedSize = fixedSize;
    }

    public int getSizeLimit() {
        return sizeLimit;
    }

    public void setSizeLimit(int sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public XdrBasicType getResolvesToType() {
        return resolvesToType;
    }

    public void setResolvesToType(XdrBasicType resolvesToType) {
        this.resolvesToType = resolvesToType;
    }

    public XdrTypeDefDeclaration getResolvesTo() {
        return resolvesTo;
    }

    public void setResolvesTo(XdrTypeDefDeclaration resolvesTo) {
        this.resolvesTo = resolvesTo;
    }
}
