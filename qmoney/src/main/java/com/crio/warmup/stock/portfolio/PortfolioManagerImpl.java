package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {




  private RestTemplate restTemplate;

  // Caution: Do not delete or modify the constructor, or else your build will
  // break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // Now we want to convert our code into a module, so we will not call it from main anymore.
  // Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and make sure that it
  // follows the method signature.
  // Logic to read Json file and convert them into Objects will not be required further as our
  // clients will take care of it, going forward.
  // Test your code using Junits provided.
  // Make sure that all of the tests inside PortfolioManagerTest using command below -
  // ./gradlew test --tests PortfolioManagerTest
  // This will guard you against any regressions.
  // run ./gradlew build in order to test yout code, and make sure that
  // the tests and static code quality pass.

  //CHECKSTYLE:OFF




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo thirdparty APIs to a separate function.
  //  It should be split into fto parts.
  //  Part#1 - Prepare the Url to call Tiingo based on a template constant,
  //  by replacing the placeholders.
  //  Constant should look like
  //  https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=?&endDate=?&token=?
  //  Where ? are replaced with something similar to <ticker> and then actual url produced by
  //  replacing the placeholders with actual parameters.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {

      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      //RestTemplate restTemplate = new RestTemplate();
      String url = buildUri(symbol, from, to);
      String result = restTemplate.getForObject(url, String.class);
      Candle[] coll = mapper.readValue(result,TiingoCandle[].class);
      
      /*
      List<Candle> collec = new ArrayList<Candle>();
      for(int i=0; i < coll.length; i++) {
        collec.add(coll[i]);
      } 
      */
    return Arrays.asList(coll);
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String api = "3363b903ca0b6da429d1fa9209f385716d4a6359";
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?"
            + "startDate=" + startDate.toString() + "&endDate=" + endDate.toString()
            + "&token=" + api;
    return uriTemplate;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) {
    
    List<AnnualizedReturn> annRetArr = new ArrayList<AnnualizedReturn>();
    List<Candle> collec = new ArrayList<Candle>();
    for(int i = 0; i < portfolioTrades.size(); i++) {
      PortfolioTrade trade = portfolioTrades.get(i);

      String stockname = trade.getSymbol();
      LocalDate startdate = trade.getPurchaseDate();      
      try {
        collec = getStockQuote(stockname, startdate, endDate);

      } catch (JsonProcessingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
      
      double buyPrice = collec.get(0).getOpen();
      double sellPrice = collec.get(collec.size() - 1).getClose();
      double totalReturns = (sellPrice - buyPrice) / buyPrice;
      LocalDate startDate = trade.getPurchaseDate();
      
      long days = ChronoUnit.DAYS.between(startDate, endDate);
      if (days == 0) {
        days = 1;
      } else if (days < 0) {
        days = 0; 
      }
      double tny = (double)365 / days;
      double annualizedReturns = (double)Math.pow((1 + totalReturns), tny) - 1;
  
      annRetArr.add(new AnnualizedReturn(stockname, annualizedReturns, totalReturns));    

    }

    for (int i = 0; i < annRetArr.size() - 1; i++) {
      for (int j = 0; j < annRetArr.size() - 1 - i; j++) {
        if (annRetArr.get(j).getAnnualizedReturn() <= annRetArr.get(j + 1).getAnnualizedReturn()) {
          AnnualizedReturn dswp = annRetArr.get(j);
          annRetArr.set(j,annRetArr.get(j + 1));
          annRetArr.set(j + 1,dswp);
        }
      }
    }
   
    return annRetArr;
  }
}
