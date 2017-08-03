package com.kunal;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.kunal.vo.StocksVO;
import org.apache.commons.lang3.tuple.Pair;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SpringBootApplication
public class SpringBootConsoleApplication implements CommandLineRunner {

    private static String key = "";

    public static void main(String[] args) throws Exception {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("SUNRISETOMORROW");
        Properties props = new EncryptableProperties(encryptor);
        props.load(new FileInputStream("src/main/resources/application.properties"));
        key = props.getProperty("key");
        SpringApplication.run(SpringBootConsoleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LocalDate date = LocalDate.now().plusDays(7);
        if (args.length > 0) {
            date = LocalDate.parse(args[0]);
        }
        visitWebPage(date);
    }

    private void visitWebPage(LocalDate date) {
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        try {
            String searchUrl = "http://www.nasdaq.com/earnings/earnings-calendar.aspx?date=" + date.getYear() + "-" + date.getMonth().name() + "-" + date.getDayOfMonth();
            HtmlPage page = client.getPage(searchUrl);
            HtmlTable table = page.getHtmlElementById("ECCompaniesTable");
            boolean head = true;
            Map<String, StocksVO> stocks = new HashMap<>();
            for (final HtmlTableRow row : table.getRows()) {
                if (head) {
                    head = false;
                    continue;
                }
                StocksVO stocksVO = processRow(row);
                if ((stocksVO.getMarketCap().contains("B") || stocksVO.getMarketCap().contains("T")) && !stocks.containsKey(stocksVO.getCompanyName())) {
                    stocks.put(stocksVO.getCompanyName(), stocksVO);
                }
            }
        } catch (IOException e) {
            System.out.print("Error: " + e.getMessage());
        } finally {
            client.close();
        }
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