package org.ethereumclassic.etherjar.rpc;

import org.ethereumclassic.etherjar.model.Address;
import org.ethereumclassic.etherjar.model.HexNumber;
import org.ethereumclassic.etherjar.model.Wei;
import org.ethereumclassic.etherjar.rpc.json.BlockJson;
import org.ethereumclassic.etherjar.rpc.json.BlockTag;
import org.ethereumclassic.etherjar.rpc.transport.RpcTransport;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Future;

/**
 * @author Igor Artamonov
 */
public class DefaultRpcClient implements RpcClient {

    private RpcTransport transport;
    private Extractor extractor;

    public DefaultRpcClient(RpcTransport transport) {
        this(transport, new Extractor());
    }

    public DefaultRpcClient(RpcTransport transport, Extractor extractor) {
        this.transport = transport;
        this.extractor = extractor;
    }

    @Override
    public NetworkDetails network() {
        return new NetworkDetailsImpl(transport, extractor);
    }

    public static class NetworkDetailsImpl implements NetworkDetails {

        private final RpcTransport transport;
        private Extractor extractor;

        public NetworkDetailsImpl(RpcTransport transport, Extractor extractor) {
            this.transport = transport;
            this.extractor = extractor;
        }

        @Override
        public Future<Integer> blockNumber() throws IOException {
            Future<String> resp = transport.execute("eth_blockNumber", Collections.emptyList(), String.class);
            return extractor.extractInteger(resp);
        }

        @Override
        public Future<Wei> getBalance(Address address, BlockTag block) throws IOException {
            Future<String> resp = transport.execute("eth_getBalance",
                Arrays.asList(address.toHex(), block.getCode()),
                String.class);
            return extractor.extractWei(resp);
        }

        @Override
        public Future<Wei> getBalance(Address address, Integer block) throws IOException {
            Future<String> resp = transport.execute("eth_getBalance",
                Arrays.asList(address.toHex(), HexNumber.valueOf(block)),
                String.class);
            return extractor.extractWei(resp);
        }

        public Future<BlockJson> getBlockByNumber(int blockNumber) throws IOException {
            Future<BlockJson> resp = transport.execute("eth_getBlockByNumber",
                Collections.singletonList(HexNumber.valueOf(blockNumber).toHex()),
                BlockJson.class);
            return resp;
        }

    }
}
