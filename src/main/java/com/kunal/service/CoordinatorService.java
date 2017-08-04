package com.kunal.service;


import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kunal.vo.StocksVO;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
@Service
public class CoordinatorService {

    @Autowired
    private WebPageService webPageService;

    @Autowired
    private OutputService outputService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(cron="0 0 8 ? * MON,TUE,WED,THU,FRI")
    public void start() {
        LocalDate date = LocalDate.now().plusDays(7);
        logger.info("Started for date: " + date);
        List<StocksVO> stocksVOList = webPageService.visitWebPage(date);
        logger.info("Stocks found above $1b: " + stocksVOList.size());
        logger.info("sorting...");
        Collections.sort(stocksVOList, comparing(StocksVO::getEps, reverseOrder()).thenComparing(StocksVO::getCap, reverseOrder()));
        logger.info("Sending email....");
        String html = outputService.sendMail(stocksVOList, date);
        logger.info("Email sent");
        logger.info("Tweeting...");
        outputService.tweetImage(date, html);
        logger.info("Tweet sent");
    }
}
