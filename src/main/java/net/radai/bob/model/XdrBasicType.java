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

package net.radai.bob.model;

/**
 * @author Radai Rosenblatt
 */
public class XdrBasicType extends XdrType {

    public final static XdrBasicType UNSIGNED_INT   = new XdrBasicType(XdrTypes.UNSIGNED_INT);
    public final static XdrBasicType INT            = new XdrBasicType(XdrTypes.INT);
    public final static XdrBasicType UNSIGNED_HYPER = new XdrBasicType(XdrTypes.UNSIGNED_HYPER);
    public final static XdrBasicType HYPER          = new XdrBasicType(XdrTypes.HYPER);
    public final static XdrBasicType FLOAT          = new XdrBasicType(XdrTypes.FLOAT);
    public final static XdrBasicType DOUBLE         = new XdrBasicType(XdrTypes.DOUBLE);
    public final static XdrBasicType QUADRUPLE      = new XdrBasicType(XdrTypes.QUADRUPLE);
    public final static XdrBasicType BOOL           = new XdrBasicType(XdrTypes.BOOL);
    public final static XdrBasicType OPAQUE         = new XdrBasicType(XdrTypes.OPAQUE);
    public final static XdrBasicType STRING         = new XdrBasicType(XdrTypes.STRING);
    public final static XdrBasicType VOID           = new XdrBasicType(XdrTypes.VOID);

    private XdrBasicType(XdrTypes type) {
        this.type = type;
    }

    private final XdrTypes type;

    @Override
    public XdrTypes getType() {
        return type;
    }
}
