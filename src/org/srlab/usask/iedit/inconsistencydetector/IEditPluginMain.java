package org.srlab.usask.iedit.inconsistencydetector;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// JSON + Markdown + HTML
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class IEditPluginMain {

    // --- DTOs -----------------------------------------------------
    static class InputPayload {
        String preText;
        String postText;
        String reputation;       // not used by detectors now, but kept
        String rollbackUserName; // full name "First Last"
    }

    static class OutputPayload {
        String status; // "accepted" | "rejected"
        String reason; // suggestion text
        double score;  // simple normalized score (0..1), here: 1 - (flags/6)
        OutputPayload(String status, String reason, double score) {
            this.status = status;
            this.reason = reason;
            this.score = score;
        }
    }

    private static final Gson GSON = new Gson();
    private static final Parser MARKDOWN_PARSER = Parser.builder().build();
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder().build();

    public static void main(String[] args) throws Exception {
        final int port = 8085;
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/analyze", new AnalyzeHandler());
        server.setExecutor(null);
        System.out.println("LocalAnalyzer running on http://127.0.0.1:" + port);
        server.start();
    }

    // --- HTTP handler ---------------------------------------------
    static class AnalyzeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // CORS preflight
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                addCors(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            String reqBody;
            try (InputStream is = exchange.getRequestBody()) {
                reqBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }

            InputPayload input;
            try {
                input = GSON.fromJson(reqBody, InputPayload.class);
            } catch (JsonSyntaxException ex) {
                sendJson(exchange, 400, "{\"error\":\"Invalid JSON\"}");
                return;
            }

            // Defensive null/empty handling
            String preMd = safe(input.preText);
            String postMd = safe(input.postText);
            String userName = safe(input.rollbackUserName);

            // Markdown -> HTML -> Jsoup Document & visible text (same approach as your local code)
            ParsedText pre = parseMarkdownToDocAndPlain(preMd);
            ParsedText post = parseMarkdownToDocAndPlain(postMd);

            // Name handling (first & last)
            String userFirstName = "";
            String userLastName = "-9999";
            if (!userName.isBlank()) {
                String[] parts = userName.trim().split("\\s+");
                if (parts.length >= 1) userFirstName = parts[0];
                if (parts.length >= 2) userLastName = parts[parts.length - 1];
            }

            // Run your detectors — EXACT calls preserved
            List<Integer> presentationInconsistency =
                DetectPresentationInconsistency.detectPresentationInconsistency(
                    pre.doc, post.doc, pre.text, post.text);

            List<Integer> gratitudeInconsistency =
                DetectGratitudeInconsistency.detectGratitudeInconsistency(
                    pre.text.toLowerCase(), post.text.toLowerCase());

            List<Integer> statusInconsistency =
                DetectStatusInconsistency.detectStatusInconsistency(
                    pre.text.toLowerCase(), post.text.toLowerCase());

            List<Integer> signatureInconsistency =
                DetectSignatureInconsistency.detectSignatureInconsistencyForTool(
                    pre.text, post.text, userFirstName, userLastName);

            List<Integer> deprecationInconsistency =
                DetectDeprecationInconsistency.detectDeprecationInconsistency(
                    pre.text.toLowerCase(), post.text.toLowerCase());

            List<Integer> duplicationInconsistency =
                DetectDuplicationInconsistency.detectDuplicationInconsistency(
                    pre.text.toLowerCase(), post.text.toLowerCase());

            // Build suggestion text consistent with your local logic
            SuggestionResult sres = buildSuggestion(
                presentationInconsistency,
                gratitudeInconsistency,
                statusInconsistency,
                signatureInconsistency,
                deprecationInconsistency,
                duplicationInconsistency
            );

            // status + score (simple: 1 - (flags/6))
            String status = sres.inconsistencyCount == 0 ? "accepted" : "rejected";
            double score = Math.max(0.0, Math.min(1.0, 1.0 - (sres.inconsistencyCount / 6.0)));

            OutputPayload out = new OutputPayload(status, sres.suggestion, score);
            sendJson(exchange, 200, GSON.toJson(out));
        }
    }

    // --- Helpers --------------------------------------------------

    static class ParsedText {
        final Document doc;
        final String text;
        ParsedText(Document doc, String text) {
            this.doc = doc;
            this.text = text;
        }
    }

    private static ParsedText parseMarkdownToDocAndPlain(String markdown) {
        try {
            Node docNode = MARKDOWN_PARSER.parse(markdown == null ? "" : markdown);
            String html = HTML_RENDERER.render(docNode);
            Document jsoupDoc = Jsoup.parse(html);

            // Close to your local approach: gather paragraph text as plain
            Elements paragraphs = jsoupDoc.select("p, li, code, pre");
            String plain = paragraphs.isEmpty()
                ? jsoupDoc.text()
                : paragraphs.text();

            return new ParsedText(jsoupDoc, plain == null ? "" : plain);
        } catch (Exception e) {
            // Never fail the server for parsing — degrade gracefully
            Document fallback = Jsoup.parse("<p></p>");
            return new ParsedText(fallback, "");
        }
    }

    static class SuggestionResult {
        final String suggestion;
        final int inconsistencyCount;
        SuggestionResult(String suggestion, int inconsistencyCount) {
            this.suggestion = suggestion;
            this.inconsistencyCount = inconsistencyCount;
        }
    }

    private static SuggestionResult buildSuggestion(
        List<Integer> presentationInconsistency,
        List<Integer> gratitudeInconsistency,
        List<Integer> statusInconsistency,
        List<Integer> signatureInconsistency,
        List<Integer> deprecationInconsistency,
        List<Integer> duplicationInconsistency
    ) {
        // Each list: [0]=inconsistency flag, [1]=added?, [2]=deleted? (your convention)
        int count = 0;
        StringBuilder sb = new StringBuilder();

        int p0 = safeGet(presentationInconsistency, 0);
        int p1 = safeGet(presentationInconsistency, 1);

        int g0 = safeGet(gratitudeInconsistency, 0);
        int g1 = safeGet(gratitudeInconsistency, 1);

        int s0 = safeGet(statusInconsistency, 0);
        int s1 = safeGet(statusInconsistency, 1);

        int sig0 = safeGet(signatureInconsistency, 0);
        int sig1 = safeGet(signatureInconsistency, 1);

        int d0 = safeGet(deprecationInconsistency, 0);
        int d1 = safeGet(deprecationInconsistency, 1);

        int dup0 = safeGet(duplicationInconsistency, 0);
        int dup1 = safeGet(duplicationInconsistency, 1);

        if ((p0 + g0 + s0 + sig0 + d0 + dup0) > 0) {
            sb.append("It looks like your suggested edits have the following inconsistency:\n");

            if (p0 == 1) {
                if (p1 == 1) {
                    sb.append("\nPresentation Inconsistency: You formatted text elements as code elements.")
                      .append("\nCould you please double-check that those formatted elements are code elements?")
                      .append("\nHowever, such formatting has 74.8% of rejection possibility.\n");
                } else {
                    sb.append("\nPresentation Inconsistency: You formatted code elements as text elements.")
                      .append("\nCould you please double-check that those formatted elements are text elements?")
                      .append("\nHowever, such formatting has 25.2% of rejection possibility.\n");
                }
                count++;
            }

            if (g0 == 1) {
                if (g1 == 1) {
                    sb.append("\nGratitudinal Inconsistency: Your suggested edit has a gratitude/emotion (e.g., thanks).")
                      .append("\nHowever, the addition of such signature has 49.7% of rejection possibility.\n");
                } else {
                    sb.append("\nGratitudinal Inconsistency: Your suggested edit has deleted a gratitude/emotion (e.g., thanks).")
                      .append("\nHowever, the deletion of such signature has 50.3% of rejection possibility.\n");
                }
                count++;
            }

            if (sig0 == 1) {
                if (sig1 == 1) {
                    sb.append("\nSignature Inconsistency: Your suggested edit has a signature (e.g., user name).")
                      .append("\nHowever, the addition of such signature has 57.9% of rejection possibility.\n");
                } else {
                    sb.append("\nSignature Inconsistency: Your suggested edit has deleted a signature (e.g., user name).")
                      .append("\nHowever, the deletion of such signature has 42.1% of rejection possibility.\n");
                }
                count++;
            }

            if (s0 == 1) {
                if (s1 == 1) {
                    sb.append("\nStatus Inconsistency: Your suggested edit has personal notes. Make sure the personal notes are necessary.")
                      .append("\nHowever, the addition of such personal notes has 70.5% of rejection possibility.\n");
                } else {
                    sb.append("\nStatus Inconsistency: Your suggested edit deletes personal notes. Make sure the personal notes are not necessary.")
                      .append("\nHowever, the deletion of such personal notes has 29.5% of rejection possibility.\n");
                }
                count++;
            }

            if (d0 == 1) {
                if (d1 == 1) {
                    sb.append("\nDeprecation Inconsistency: Your suggested edit has a deprecation note. Please make sure the code is deprecated.")
                      .append("\nHowever, the addition of such deprecation note inside the body of a post has 46.7% of rejection possibility.\n");
                } else {
                    sb.append("\nDeprecation Inconsistency: Your suggested edit has deleted a deprecation note. Please make sure the code is not deprecated.")
                      .append("\nHowever, the deletion of such deprecation note inside the body of a post has 53.3% of rejection possibility.\n");
                }
                count++;
            }

            if (dup0 == 1) {
                if (dup1 == 1) {
                    sb.append("\nDuplication Inconsistency: Your suggested edit has a duplication note. Please make sure that this is a duplicate of another post.")
                      .append("\nHowever, the addition of such duplication note inside the body of a post has 65.1% of rejection possibility.\n");
                } else {
                    sb.append("\nDuplication Inconsistency: Your suggested edit has deleted a duplication note. Please make sure that this is not a duplicate of another post.")
                      .append("\nHowever, the deletion of such duplication note inside the body of a post has 34.9% of rejection possibility.\n");
                }
                count++;
            }

            sb.append("\n\nWe hope the suggestion(s) help you to avoid inconsistent edits and rejections. Good Luck!");
            return new SuggestionResult(sb.toString(), count);
        } else {
            String ok = "Go ahead! There is no inconsistency in your suggested edits.";
            return new SuggestionResult(ok, 0);
        }
    }

    private static int safeGet(List<Integer> list, int idx) {
        if (list == null || idx < 0 || idx >= list.size() || list.get(idx) == null) return 0;
        return list.get(idx);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static void addCors(HttpExchange ex) {
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "https://stackoverflow.com");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
    }

    private static void sendJson(HttpExchange ex, int code, String body) throws IOException {
        addCors(ex);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}




















//package org.srlab.usask.iedit.inconsistencydetector;
//
//import com.sun.net.httpserver.HttpServer;
//import com.sun.net.httpserver.HttpHandler;
//import com.sun.net.httpserver.HttpExchange;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.InputStream;
//import java.net.InetSocketAddress;
//import java.nio.charset.StandardCharsets;
//
//public class IEditPluginMain {
//
//    public static void main(String[] args) throws Exception {
//        int port = 8085;
//        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
//
//        server.createContext("/analyze", new AnalyzeHandler());
//
//        server.setExecutor(null); // default executor
//        System.out.println("LocalAnalyzer running on http://127.0.0.1:" + port);
//        server.start();
//    }
//
//    static class AnalyzeHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange exchange) throws IOException {
//            // only allow POST
//            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
//                sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
//                return;
//            }
//
//            // read request body
//            InputStream is = exchange.getRequestBody();
//            String reqBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
//            System.out.println("Received payload: " + reqBody);
//
//            // TODO: parse reqBody (preText/postText/etc.) and run your ML / rule logic
//            //
//            // For now, we'll mock:
//            // - if postText contains "thanks" or "pls help", warn about "chatty"
//            // - else say it's fine
//
//            String status;
//            String reason;
//            double score;
//
//            if (reqBody.toLowerCase().contains("pls help")
//                    || reqBody.toLowerCase().contains("please help")
//                    || reqBody.toLowerCase().contains("thanks in advance")) {
//
//                status = "rejected";
//                reason = "Overly chatty / non-edit content. Keep technical and focus on changes.";
//                score  = 0.92;
//            } else {
//                status = "accepted";
//                reason = "Edit looks focused and technically relevant.";
//                score  = 0.81;
//            }
//
//            // build response JSON
//            String responseJson = String.format(
//                "{\"status\":\"%s\",\"reason\":\"%s\",\"score\":%.2f}",
//                escapeJson(status),
//                escapeJson(reason),
//                score
//            );
//
//            sendJson(exchange, 200, responseJson);
//        }
//
//        private static String escapeJson(String s) {
//            return s.replace("\\", "\\\\").replace("\"", "\\\"");
//        }
//
//        private static void sendJson(HttpExchange ex, int code, String body) throws IOException {
//            // CORS headers so browser fetch() won't get blocked
//            ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
//            ex.getResponseHeaders().add("Access-Control-Allow-Origin", "https://stackoverflow.com");
//            ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
//            ex.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
//
//            // handle OPTIONS preflight
//            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
//                ex.sendResponseHeaders(204, -1);
//                return;
//            }
//
//            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
//            ex.sendResponseHeaders(code, bytes.length);
//            try (OutputStream os = ex.getResponseBody()) {
//                os.write(bytes);
//            }
//        }
//    }
//}
