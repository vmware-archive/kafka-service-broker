package io.pivotal.cf.servicebroker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;

@Slf4j
public class KafkaBrokerException extends ServiceBrokerException {

    public KafkaBrokerException(String message, Throwable cause) {
        super(message, cause);
        log.error(message, cause);
    }

    public KafkaBrokerException(Throwable cause) {
        this(cause.getMessage(), cause);

    }
}
