package io.pivotal.cf.service.app;

import io.pivotal.cf.service.connector.KafkaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by miyer on 10/26/16.
 */


@RestController
@Slf4j
public class KafkaSampleController {

    public KafkaSampleController(KafkaRepository kafkaRepository) {
        this.kafkaRepository = kafkaRepository;
    }

    private KafkaRepository kafkaRepository;

    @RequestMapping(value = "/send", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    ResponseEntity<Void> send(@RequestParam(value = "message") String message) throws ClassNotFoundException {
        kafkaRepository.sendMessage(message);

        log.info("sending message: " + message);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
