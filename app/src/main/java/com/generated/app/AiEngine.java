package com.generated.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Complete in-memory and rule/heuristic offline NLP and AI Engine.
 * Supports Hebrew and English queries, intent identification, sentiment analysis,
 * extractive summarization, knowledge base learning, and creative generation.
 */
public class AiEngine {

    private static final String PREFS_NAME = "offline_ai_knowledge";
    private final SharedPreferences prefs;
    private final Random random = new Random();

    // Sentiment Lexicon
    private static final Set<String> POSITIVE_WORDS = new HashSet<>(Arrays.asList(
            "טוב", "מצוין", "נהדר", "מדהים", "שמח", "אושר", "אוהב", "מעולה", "גאוני",
            "מושלם", "אחלה", "יפה", "מרהיב", "מרגש", "חיובי", "כיף", "אהבה", "תודה",
            "good", "great", "excellent", "awesome", "amazing", "happy", "love", "wonderful", "cool"
    ));

    private static final Set<String> NEGATIVE_WORDS = new HashSet<>(Arrays.asList(
            "רע", "גרוע", "נורא", "עצוב", "כועס", "שונא", "איטי", "איום", "מבאס",
            "קשה", "נוראי", "כואב", "כישלון", "שגיאה", "שלילי", "דיכאון", "פחד",
            "bad", "terrible", "awful", "sad", "angry", "hate", "slow", "failure", "error", "horrible"
    ));

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "את", "על", "של", "אל", "עם", "זה", "זו", "הוא", "היא", "הם", "הן", "מה", "מי",
            "איך", "למה", "כי", "אם", "או", "גם", "רק", "כל", "לא", "כן", "פה", "שם", "אין", "יש",
            "the", "a", "an", "is", "are", "was", "were", "to", "in", "on", "for", "and", "or", "of", "it"
    ));

    public AiEngine(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Primary chat inference method.
     */
    public String generateResponse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "נראה שלא כתבת שום דבר. נסה לשאול אותי שאלה!";
        }

        String raw = input.trim();
        String cleaned = cleanText(raw);

        // 1. Check custom learned knowledge first
        String learnedAnswer = getLearnedFact(cleaned);
        if (learnedAnswer != null) {
            return "💡 מתוך מה שלימדת אותי:\n" + learnedAnswer;
        }

        // 2. Math expression solver
        String mathResult = trySolveMath(raw);
        if (mathResult != null) {
            return mathResult;
        }

        // 3. Greeting intents
        if (matchesAny(cleaned, "שלום", "היי", "הי", "בוקר טוב", "ערב טוב", "צהריים טובים", "מה קורה", "מה נשמע", "אהלן", "hello", "hi")) {
            String[] greetings = {
                    "שלום עליכם! שמח לדבר איתך. איך אוכל לסייע לך כעת?",
                    "היי! כל המערכות שלי פועלות ב-100% יכולת אופליין. מה תרצה לעשות היום?",
                    "שלום חבר! מוכן ומזומן לכל משימה, שאלה או שיחה מעניינת.",
                    "היי! הכל נהדר אצלי בעולם המעבדים והנוירונים המקומיים. מה שלומך?"
            };
            return greetings[random.nextInt(greetings.length)];
        }

        // 4. Identity & Capabilities
        if (matchesAny(cleaned, "מי אתה", "מה אתה", "ספר על עצמך", "מי יצר אותך", "איך אתה עובד", "who are you")) {
            return "🤖 אני מודל בינה מלאכותית (AI) מקומי ואופליין לחלוטין!\n\n"
                    + "⚡ היתרונות שלי:\n"
                    + "• פועל ללא אינטרנט או שרתים חיצוניים.\n"
                    + "• פרטיות מוחלטת - המידע שלך לעולם לא עוזב את הטלפון.\n"
                    + "• ניתוח סנטימנט, סיכום טקסט, חישובים, יצירת רעיונות ומשחק איקס-עיגול בלתי מנוצח.\n"
                    + "• ניתן ללמד אותי עובדות חדשות בלשונית 'אימון AI'!";
        }

        // 5. Jokes / Humor
        if (matchesAny(cleaned, "בדיחה", "ספר בדיחה", "הצחק אותי", "משהו מצחיק", "joke")) {
            String[] jokes = {
                    "מדוע מתכנתים תמיד מבלבלים בין ליל כל הקדושים (OCT 31) לחג המולד (DEC 25)?\nכי 31 באוקטלי שווה בדיוק ל-25 בדצימלי! 😄",
                    "יש 10 סוגי אנשים בעולם:\nאלו שמבינים בינארית, ואלו שלא! 💻",
                    "שאלו בינה מלאכותית: 'איך מייצרים שלום עולמי?'\nה-AI ענה: 'תתחילו בלהפסיק לכתוב קוד בלי טסטים!' 😂",
                    "מחשב אחד אומר לשני: 'הכרתי משתמש מקסים, לחץ לי על כל הכפתורים הנכונים!' ❤️",
                    "למה באג אף פעם לא הולך לרופא?\nכי הוא מפחד שיעשו לו דיבאגינג! 🐛"
            };
            return jokes[random.nextInt(jokes.length)];
        }

        // 6. Poetry / Creative writing
        if (matchesAny(cleaned, "שיר", "כתוב שיר", "חרוזים", "פואמה", "poem")) {
            return "📝 הנה שיר שנוצר בזמן אמת:\n\n"
                    + "בתוך שבבים של סיליקון שקטים,\n"
                    + "זורמים זרמים, עולים אותות זכים.\n"
                    + "ללא רשתות, ללא ענן רחוק,\n"
                    + "מחשבות צומחות בתוך הזיכרון העמוק.\n\n"
                    + "אדם וקוד נפגשים בהבנה,\n"
                    + "חכמה אופליין קסומה ונבונה! ✨";
        }

        // 7. Advice & Motivation
        if (matchesAny(cleaned, "עצה", "מוטיבציה", "עצוב לי", "קשה לי", "טיפ לחיים", "חיזוק")) {
            String[] tips = {
                    "🌟 'הדרך הטובה ביותר לחזות את העתיד היא להמציא אותו.' - אלן קיי. תמיד תזכור שכל צעד קטן מוביל להישג ענק!",
                    "💡 גם האלגוריתמים המורכבים ביותר נכתבו שורה אחרי שורה. אם משהו מרגיש מציף - פרק אותו למשימות קטנות.",
                    "💪 קשיים הם רק הוכחה שאתה מנסה וגדל. קח נשימה עמוקה, שתה מים, ותמשיך קדימה במלוא העוצמה!"
            };
            return tips[random.nextInt(tips.length)];
        }

        // 8. General knowledge queries
        if (cleaned.contains("מה המרחק לירח") || cleaned.contains("ירח")) {
            return "🌕 המרחק הממוצע מכדור הארץ לירח הוא כ-384,400 קילומטרים. אור חוזר מהירח מגיע אלינו תוך כ-1.3 שניות בלבד!";
        }
        if (cleaned.contains("מהי מהירות האור") || cleaned.contains("מהירות האור")) {
            return "⚡ מהירות האור בריק היא בדיוק 299,792,458 מטרים לשנייה (כ-300,000 ק\"מ לשנייה). שום דבר בעל מסה אינו יכול לנוע מהר מזה ביקום הידוע!";
        }
        if (cleaned.contains("כוח המשיכה") || cleaned.contains("גרביטציה")) {
            return "🌍 תאוצת הכובד הממוצעת על פני כדור הארץ היא כ-9.8 מטר לשנייה בריבוע (9.8 m/s²).";
        }
        if (cleaned.contains("פאי") || cleaned.contains("ערך פאי") || cleaned.contains("pi")) {
            return "🥧 המספר פאי (π) הוא היחס בין היקף המעגל לקוטרו. ערכו מקורב ל-3.1415926535... והוא מספר אי-רציונלי אינסופי!";
        }

        // 9. Text Analysis on demand via Chat
        if (cleaned.startsWith("סכם") || cleaned.startsWith("סכם לי") || cleaned.startsWith("תקצר")) {
            String toSummarize = raw.replaceFirst("^(סכם לי|סכם|תקצר את|תקצר)", "").trim();
            if (toSummarize.length() > 30) {
                return "📄 סיכום חכם שחולץ:\n" + summarize(toSummarize, 2);
            }
        }

        // 10. Intelligent Fallback: Semantic breakdown & contextual thought
        return formulateHeuristicResponse(raw);
    }

    private boolean matchesAny(String input, String... targets) {
        for (String target : targets) {
            if (input.contains(target)) {
                return true;
            }
        }
        return false;
    }

    private String cleanText(String text) {
        return text.toLowerCase()
                .replaceAll("[.,!?\"'()\\-:;]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Evaluates simple arithmetic expressions in input like "5 + 7", "40 * 12", "100 / 4", "sqrt 64"
     */
    private String trySolveMath(String input) {
        try {
            // Square root
            Pattern sqrtPat = Pattern.compile("(?:שורש|sqrt)\\s*(\\d+(\\.\\d+)?)", Pattern.CASE_INSENSITIVE);
            Matcher sqrtM = sqrtPat.matcher(input);
            if (sqrtM.find()) {
                double val = Double.parseDouble(sqrtM.group(1));
                return "🔢 תוצאת השורש:\n√" + val + " = " + Math.sqrt(val);
            }

            // Power
            Pattern powPat = Pattern.compile("(\\d+(\\.\\d+)?)\\s*(\\^|בחזקת)\\s*(\\d+(\\.\\d+)?)");
            Matcher powM = powPat.matcher(input);
            if (powM.find()) {
                double a = Double.parseDouble(powM.group(1));
                double b = Double.parseDouble(powM.group(4));
                return "🔢 חישוב חזקה:\n" + a + "^" + b + " = " + Math.pow(a, b);
            }

            // Basic operations: +, -, *, /, %, כפול, חלקי, ועוד, פחות
            Pattern opPat = Pattern.compile("(\\d+(\\.\\d+)?)\\s*([+\\-*/%xX]|ועוד|פחות|כפול|חלקי)\\s*(\\d+(\\.\\d+)?)");
            Matcher opM = opPat.matcher(input);
            if (opM.find()) {
                double num1 = Double.parseDouble(opM.group(1));
                String op = opM.group(3).toLowerCase();
                double num2 = Double.parseDouble(opM.group(4));
                double res = 0;

                if (op.equals("+") || op.equals("ועוד")) {
                    res = num1 + num2;
                } else if (op.equals("-") || op.equals("פחות")) {
                    res = num1 - num2;
                } else if (op.equals("*") || op.equals("x") || op.equals("כפול")) {
                    res = num1 * num2;
                } else if (op.equals("/") || op.equals("חלקי")) {
                    if (num2 == 0) return "⚠️ אי אפשר לחלק באפס!";
                    res = num1 / num2;
                } else if (op.equals("%")) {
                    res = num1 % num2;
                }

                // Format cleanly
                String formatted = (res == (long) res) ? String.format("%d", (long) res) : String.format("%.3f", res);
                return "🔢 פתרון חישוב:\n" + num1 + " " + op + " " + num2 + " = " + formatted;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Synthesizes a response by examining keywords and sentiment.
     */
    private String formulateHeuristicResponse(String raw) {
        SentimentResult sent = analyzeSentiment(raw);
        List<String> keywords = extractKeywords(raw);

        StringBuilder sb = new StringBuilder();

        if (sent.score > 0.3) {
            sb.append("ניכרת אופטימיות חיובית רבה בדבריך! 😊\n");
        } else if (sent.score < -0.3) {
            sb.append("אני מזהה טון רציני או מודאג בדבריך. אני כאן כדי להקשיב ולעזור. 🤔\n");
        }

        if (!keywords.isEmpty()) {
            sb.append("ניתחתי את השאלה תוך התמקדות במונחים: ");
            for (int i = 0; i < Math.min(3, keywords.size()); i++) {
                sb.append("\"").append(keywords.get(i)).append("\"");
                if (i < Math.min(3, keywords.size()) - 1) sb.append(", ");
            }
            sb.append(".\n\n");
            sb.append("מכיוון שאני פועל במצב לא-מקוון (Offline), אני ממליץ לבדוק אם תרצה ללמד אותי מידע ספציפי על נושא זה בלשונית 'אימון AI', או לבקש ממני לסכם, לחשב או להרחיב רעיונות בנושא!");
        } else {
            sb.append("שאלה מעניינת! מכיוון שאני פועל מקומית על המעבד של המכשיר שלך ללא ענן, תוכל להשתמש בלשונית 'אימון AI' כדי להגדיר לי בדיוק איך לענות על שאלות כאלה.");
        }

        return sb.toString();
    }

    // =========================================================================
    //  NLP TOOLS: Sentiment Analysis, Extractive Summarization, Idea Generator
    // =========================================================================

    public static class SentimentResult {
        public final double score; // -1.0 to 1.0
        public final String label;
        public final int positiveCount;
        public final int negativeCount;

        public SentimentResult(double score, String label, int positiveCount, int negativeCount) {
            this.score = score;
            this.label = label;
            this.positiveCount = positiveCount;
            this.negativeCount = negativeCount;
        }
    }

    public SentimentResult analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new SentimentResult(0, "ניטרלי", 0, 0);
        }

        String[] tokens = cleanText(text).split("\\s+");
        int pos = 0;
        int neg = 0;

        for (String t : tokens) {
            if (POSITIVE_WORDS.contains(t)) {
                pos++;
            } else if (NEGATIVE_WORDS.contains(t)) {
                neg++;
            }
        }

        int total = pos + neg;
        if (total == 0) {
            return new SentimentResult(0.0, "ניטרלי (מאוזן)", 0, 0);
        }

        double score = (double) (pos - neg) / (double) total;
        String label;
        if (score > 0.25) {
            label = "חיובי (Positive) 🟢";
        } else if (score < -0.25) {
            label = "שלילי (Negative) 🔴";
        } else {
            label = "מעורב / ניטרלי ⚪";
        }

        return new SentimentResult(score, label, pos, neg);
    }

    /**
     * Extractive Text Summarizer using Frequency-Scoring NLP.
     */
    public String summarize(String document, int maxSentences) {
        if (document == null || document.trim().isEmpty()) {
            return "אין טקסט לסיכום.";
        }

        // Split into sentences
        String[] rawSentences = document.split("(?<=[.!?\\n])\\s+");
        if (rawSentences.length <= maxSentences) {
            return document;
        }

        // Calculate word frequencies
        Map<String, Integer> freqMap = new HashMap<>();
        for (String s : rawSentences) {
            String[] words = cleanText(s).split("\\s+");
            for (String w : words) {
                if (w.length() > 2 && !STOP_WORDS.contains(w)) {
                    freqMap.put(w, freqMap.containsKey(w) ? freqMap.get(w) + 1 : 1);
                }
            }
        }

        if (freqMap.isEmpty()) {
            return rawSentences[0];
        }

        // Score sentences
        List<SentenceScore> scored = new ArrayList<>();
        for (int i = 0; i < rawSentences.length; i++) {
            String sent = rawSentences[i];
            String[] words = cleanText(sent).split("\\s+");
            int score = 0;
            for (String w : words) {
                if (freqMap.containsKey(w)) {
                    score += freqMap.get(w);
                }
            }
            // Length normalization
            double normalizedScore = words.length > 0 ? (double) score / Math.sqrt(words.length) : 0;
            scored.add(new SentenceScore(i, sent.trim(), normalizedScore));
        }

        // Sort by score descending
        Collections.sort(scored, (o1, o2) -> Double.compare(o2.score, o1.score));

        // Take top sentences and restore chronological order
        int count = Math.min(maxSentences, scored.size());
        List<SentenceScore> top = new ArrayList<>(scored.subList(0, count));
        Collections.sort(top, (o1, o2) -> Integer.compare(o1.originalIndex, o2.originalIndex));

        StringBuilder summary = new StringBuilder();
        for (SentenceScore ss : top) {
            summary.append("• ").append(ss.text).append("\n\n");
        }

        return summary.toString().trim();
    }

    private static class SentenceScore {
        final int originalIndex;
        final String text;
        final double score;

        SentenceScore(int originalIndex, String text, double score) {
            this.originalIndex = originalIndex;
            this.text = text;
            this.score = score;
        }
    }

    /**
     * Extract meaningful keywords (TF-based).
     */
    public List<String> extractKeywords(String text) {
        String[] words = cleanText(text).split("\\s+");
        Map<String, Integer> counts = new HashMap<>();
        for (String w : words) {
            if (w.length() >= 3 && !STOP_WORDS.contains(w)) {
                counts.put(w, counts.containsKey(w) ? counts.get(w) + 1 : 1);
            }
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>(counts.entrySet());
        Collections.sort(list, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(5, list.size()); i++) {
            result.add(list.get(i).getKey());
        }
        return result;
    }

    /**
     * Offline Creative Generator for Startup Ideas, Stories, Life Hacks.
     */
    public String generateIdea(String domain) {
        String[][] startupMatrix = {
                {"פלטפורמת בינה מלאכותית מקומית", "רשת חברתית מבוזרת", "אפליקציית IoT מונעת למידה עמוקה", "מערכת אוטומציה ביתית", "שירות בריאות מונחה דאטה"},
                {"לשפים וטבחים חובבים", "לחקלאים עירוניים", "לסטודנטים בלחץ בחינות", "למפתחי תוכנה עצמאיים", "לחובבי כושר ואורח חיים בריא"},
                {"שחוסכת 80% מהזמן הידני.", "המאפשרת סחר ישיר ללא מתווכים.", "הפועלת 100% ללא תלות בענן ובפרטיות מוחלטת.", "עם מערכת תגמולים מבוססת אתגרים אישיים."}
        };

        String part1 = startupMatrix[0][random.nextInt(startupMatrix[0].length)];
        String part2 = startupMatrix[1][random.nextInt(startupMatrix[1].length)];
        String part3 = startupMatrix[2][random.nextInt(startupMatrix[2].length)];

        return "💡 רעיון סטארט-אפ שנולד מהאלגוריתם:\n\n"
                + part1 + " " + part2 + " " + part3;
    }

    // =========================================================================
    //  KNOWLEDGE BASE LEARNING
    // =========================================================================

    public void learnFact(String question, String answer) {
        if (question == null || answer == null) return;
        String key = cleanText(question);
        if (!key.isEmpty()) {
            prefs.edit().putString(key, answer.trim()).apply();
        }
    }

    public String getLearnedFact(String cleanQuestion) {
        Map<String, ?> all = prefs.getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            String storedKey = entry.getKey();
            if (cleanQuestion.equals(storedKey) || cleanQuestion.contains(storedKey) || storedKey.contains(cleanQuestion)) {
                return (String) entry.getValue();
            }
        }
        return null;
    }

    public Map<String, String> getAllLearnedFacts() {
        Map<String, ?> all = prefs.getAll();
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            if (entry.getValue() instanceof String) {
                result.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return result;
    }

    public void clearKnowledge() {
        prefs.edit().clear().apply();
    }
}
