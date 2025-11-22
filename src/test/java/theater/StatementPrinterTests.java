package theater;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class StatementPrinterTests {

    public static String loadString(String path) {
        try {
            return new String(Objects.requireNonNull(StatementPrinterTests.class
                            .getClassLoader()
                            .getResourceAsStream(path))
                    .readAllBytes());
        }
        catch (IOException exception) {
            fail("resource file could not be loaded prior to test executing");
        }
        return "";
    }

    @Test
    public void exampleStatementTest() {

        String expected = loadString("ExampleStatement.txt");

        final JSONObject a = new JSONObject(loadString("plays.json"));

        final Map<String, Play> plays = new HashMap<>();

        for (String s : a.keySet()) {
            final JSONObject play = (JSONObject) a.get(s);
            plays.put(s, new Play(play.getString("name"), play.getString("type")));
        }

        final JSONArray ja = new JSONArray(loadString("invoices.json"));

        for (Object jo : ja) {
            final JSONObject jinvoice = (JSONObject) jo;
            final String customer = jinvoice.getString("customer");
            final JSONArray jperformances = jinvoice.getJSONArray("performances");
            final List<Performance> performances = new ArrayList<>();
            for (Object s : jperformances) {
                final JSONObject performance = (JSONObject) s;
                performances.add(new Performance(performance.getString("playID"),
                        performance.getInt("audience")));
            }

            final Invoice invoice = new Invoice(customer, performances);

            final StatementPrinter statementPrinter = new StatementPrinter(invoice, plays);
            String result = statementPrinter.statement();

            // ensure consistent line endings are being used
            result = result.replace("\r\n", "\n");
            expected = expected.replace("\r\n", "\n");

            assertEquals(String.format("Actual output:%n%s%nExpected:%s", result, expected), expected, result);
        }

    }
}
