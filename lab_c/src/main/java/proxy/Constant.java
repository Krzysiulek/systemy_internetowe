package proxy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Constant {
    static final String VIA = "Via";
    static final String TRANSFER_ENCODING = "Transfer-Encoding";
}
