package com.kolychev.utils.blocklogger;

import com.kolychev.utils.blocklogger.logger.LogBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    
    public static void main(String[] args) throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(Main.class);
        
        try (LogBlock log = LogBlock.info(Main.class, "block1 with info")) {
            logger.info("inside");
            try {
                throw new RuntimeException("error message");
            } catch (Exception e) {
                log.withException(e).skip();
            }
        }
    }

}
