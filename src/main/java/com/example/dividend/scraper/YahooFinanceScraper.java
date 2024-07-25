package com.example.dividend.scraper;

import com.example.dividend.model.Company;
import com.example.dividend.model.Dividend;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.model.constants.Month;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class YahooFinanceScraper implements Scraper {

  private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history/?period1=%d&period2=%d&interval=1mo";
  private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";

  private static final long START_TIME = 86400;

  @Override
  public ScrapedResult scrap(Company company) {
    ScrapedResult scrapedResult = new ScrapedResult();
    scrapedResult.setCompany(company);

    try {
      long now = System.currentTimeMillis() / 1000;

      String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
      Connection connection = Jsoup.connect(url);
      Document document = connection.get();

      String text = document.text();
      String[] sArray = text.split(" ");
      List<Dividend> dividends = new ArrayList<>();

      for (int i = 0; i < sArray.length; i++) {
        if ("Dividend".equals(sArray[i]) && sArray[i - 2].startsWith("20")) {
          int day = Integer.parseInt(sArray[i - 3].split(",")[0]);
          int month = Month.strToNumber(sArray[i - 4]);
          int year = Integer.parseInt(sArray[i - 2]);
          String dividend = sArray[i - 1];

          if (month < 0) {
            throw new RuntimeException("Unexpected Month enum value -> " + sArray[i - 4]);
          }
          dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend));
        }
      }
      scrapedResult.setDividends(dividends);

    } catch (IOException e) {
      e.printStackTrace();
    }

    return scrapedResult;
  }

  @Override
  public Company scrapCompanyByTicker(String ticker) {
    String url = String.format(SUMMARY_URL, ticker, ticker);
    try {
      Document document = Jsoup.connect(url).get();
      Element titleEle = document.getElementsByTag("h1").get(1);
      String title = titleEle.text().trim();
      return new Company(ticker, title);

    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
