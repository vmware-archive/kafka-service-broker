package io.pivotal.cf.servicebroker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaBrokerException  extends RuntimeException {

    public KafkaBrokerException(String message, Throwable cause) {
        super(message, cause);
        log.error(message, cause);
    }
}
