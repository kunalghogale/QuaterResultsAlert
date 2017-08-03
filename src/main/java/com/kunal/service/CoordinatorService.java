package com.kunal.service;


import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kunal.vo.StocksVO;

@Service
public class CoordinatorService {

    @Autowired
    private WebPageService webPageService;

    @Autowired
    private SendMailService sendMailService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(cron="0 0 8 ? * MON,TUE,WED,THU,FRI")
    public void start() {
        LocalDate date = LocalDate.now().plusDays(7);
        logger.info("Started for date: " + date);
        List<StocksVO> stocksVOList = webPageService.visitWebPage(date);
        logger.info("Stocks found above $1b: " + stocksVOList.size());
        logger.info("Sending email....");
        sendMailService.sendMail(stocksVOList, date);
        logger.info("Email sent");
    }
}
