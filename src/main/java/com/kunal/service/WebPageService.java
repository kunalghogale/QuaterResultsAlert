package com.kunal.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.kunal.vo.StocksVO;

@Service
public class WebPageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<StocksVO> visitWebPage(LocalDate date) {
        List<StocksVO> stocks = new ArrayList<>();
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        try {
            String searchUrl = "http://www.nasdaq.com/earnings/earnings-calendar.aspx?date=" + date.getYear() + "-" + date.getMonth().name() + "-" + date.getDayOfMonth();
            HtmlPage page = client.getPage(searchUrl);
            HtmlTable table = page.getHtmlElementById("ECCompaniesTable");
            boolean head = true;
            for (final HtmlTableRow row : table.getRows()) {
                if (head) {
                    head = false;
                    continue;
                }
                StocksVO stocksVO = processRow(row);
                if (stocksVO.getMarketCap().contains("B") || stocksVO.getMarketCap().contains("T")) {
                    stocks.add(stocksVO);
                }
            }
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage(), e);
        } finally {
            client.close();
        }
        return stocks;
    }

    private StocksVO processRow(HtmlTableRow row) {
        String time = processTimeCell(row.getCell(0));
        Pair<String, String> nameAndCap = processNameCell(row.getCell(1));
        String forecastEps = row.getCell(4).asText();
        return new StocksVO(time, nameAndCap.getLeft(), nameAndCap.getRight(), forecastEps);
    }

    private String processTimeCell(HtmlTableCell cell) {
        for (DomNode node: cell.getChildren()) {
            if (node instanceof HtmlAnchor) {
                return ((HtmlAnchor) node).getAttribute("title");
            }
        }
        return "";
    }

    private Pair<String, String> processNameCell(HtmlTableCell cell) {
        for (DomNode node: cell.getChildren()) {
            if (node instanceof HtmlAnchor) {
                String titleAndMarketCap = node.asText();
                String[] data = titleAndMarketCap.split("\n");
                return Pair.of(data[0], data[1].substring(data[1].indexOf("Market Cap:") + 11));
            }
        }
        return Pair.of("", "");
    }
}
