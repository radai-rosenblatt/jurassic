package net.radai.bob.runtime.wire;

/**
 * @author Radai Rosenblatt
 */
public interface OncRpcv2Input {
    XdrInput nextRecord(); //record/fragment marking does not comply with xdr (rfc 5531, 11)
}
