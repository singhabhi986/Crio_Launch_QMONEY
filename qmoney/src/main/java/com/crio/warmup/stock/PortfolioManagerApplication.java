
package com.crio.warmup.stock;

//import com.crio.warmup.stock.dto.AnnualizedReturn;
//import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

//import static org.junit.jupiter.api.DynamicTest.stream;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
//import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
//import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;

//import com.crio.warmup.stock.dto.PortfolioTrade;
//import com.crio.warmup.stock.log.UncaughtExceptionHandler;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

//import java.io.File;
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//import java.util.logging.Logger;

//import org.apache.logging.log4j.ThreadContext;

public class PortfolioManagerApplication {

  // TODO: CRIO_TASK_MODULE_JSON_PARSING.
  // Read the json file provided in the argument[0]. The file will be avilable in
  // the classpath.
  // 1. Use #resolveFileFromResources to get actual file from classpath.
  // 2. parse the json file using ObjectMapper provided with #getObjectMapper,
  // and extract symbols provided in every trade.
  // return the list of all symbols in the same order as provided in json.
  // Test the function using gradle commands below
  // ./gradlew run --args="trades.json"
  // Make sure that it prints below String on the console -
  // ["AAPL","MSFT","GOOGL"]
  // Now, run
  // ./gradlew build and make sure that the build passes successfully
  // There can be few unused imports, you will need to fix them to make the build
  // pass.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {

    ObjectMapper mapper = getObjectMapper();
    String finalpath = resolveFileFromResources(args[0]) + "";
    PortfolioTrade[] symbol = mapper.readValue(
      new File(finalpath), PortfolioTrade[].class);
    List<String> res = new ArrayList<String>();

    for (int i = 0; i < symbol.length; i++) {
      String val = symbol[i].getSymbol();
      res.add(val);
    }
    return res;
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(
        PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
      Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Follow the instructions provided in the task documentation and fill up the
  // correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in
  // PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the
  // output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0 (This is implemented for your
  // reference.)
  // 2. In the same window, evaluate the value of expression below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the
  // function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the
  // stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 =
        "/home/crio-user/workspace/singhabhi9865-ME_QMONEY/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = 
        "com.fasterxml.jackson.databind.ObjectMapper@66ac5762";
    String functionNameFromTestFileInStackTrace = "mainReadFile(args)";
    String lineNumberFromTestFileInStackTrace = "125:1";

    return Arrays.asList(new String[] { valueOfArgument0,
      resultOfResolveFilePathArgs0, toStringOfObjectMapper,
        functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace });
  }

  // TODO: CRIO_TASK_MODULE_REST_API
  // Copy the relavent code from #mainReadFile to parse the Json into
  // PortfolioTrade list.
  // Now That you have the list of PortfolioTrade already populated in module#1
  // For each stock symbol in the portfolio trades,
  // Call Tiingo api
  // (https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=&endDate=&token=)
  // with
  // 1. ticker = symbol in portfolio_trade
  // 2. startDate = purchaseDate in portfolio_trade.
  // 3. endDate = args[1]
  // Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>
  // Note - You may have to register on Tiingo to get the api_token.
  // Please refer the the module documentation for the steps.
  // Find out the closing price of the stock on the end_date and
  // return the list of all symbols in ascending order by its close value on
  // endDate
  // Test the function using gradle commands below
  // ./gradlew run --args="trades.json 2020-01-01"
  // ./gradlew run --args="trades.json 2019-07-01"
  // ./gradlew run --args="trades.json 2019-12-03"
  // And make sure that its printing correct results.

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    String finalpath = resolveFileFromResources(args[0]) + "";
    LocalDate endDate = LocalDate.parse(args[1]);

    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] portfolioTrades = objectMapper.readValue(
      new File(finalpath), PortfolioTrade[].class);
    String token = "f0f7d3f3ae00a5dae7ec73c6ab65d787b6de3d15";

    List<TotalReturnsDto> totalReturnsDtosList = new ArrayList<>();

    for (int i = 0; i < portfolioTrades.length; i++) {
      PortfolioTrade pft = portfolioTrades[i];

      String symbol = pft.getSymbol();
      String startdate = pft.getPurchaseDate().toString();
      String enddat = endDate.toString();
      String url = "https://api.tiingo.com/tiingo/daily/" 
          + symbol + "/prices?startDate=" + startdate + "&endDate="
          + enddat + "&token=" + token;

      TiingoCandle[] tiingoCandles = new RestTemplate().getForObject(url, TiingoCandle[].class);

      double value = Stream.of(tiingoCandles).filter(
          candle -> candle.getDate().equals(endDate)).findFirst().get()
          .getClose();
      TotalReturnsDto totalReturn = new TotalReturnsDto(pft.getSymbol(), value);
      totalReturnsDtosList.add(totalReturn);
    }
    totalReturnsDtosList.sort(Comparator.comparing(TotalReturnsDto::getClosingPrice));

    List<String> collections = new ArrayList<String>();
    for (int i = 0; i < totalReturnsDtosList.size(); i++) {
      String val = totalReturnsDtosList.get(i).getSymbol();
      collections.add(val);
    }
    return collections;
  }

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainReadQuotes(args));

  }
}
