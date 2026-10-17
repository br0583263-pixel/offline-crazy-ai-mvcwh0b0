package com.generated.app;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private AiEngine aiEngine;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Navigation state
    private static final int TAB_CHAT = 0;
    private static final int TAB_TOOLS = 1;
    private static final int TAB_TEACH = 2;
    private static final int TAB_GAME = 3;

    private int currentTab = TAB_CHAT;

    // Root views
    private LinearLayout contentContainer;
    private Button btnTabChat;
    private Button btnTabTools;
    private Button btnTabTeach;
    private Button btnTabGame;

    // Chat Tab Data & Views
    private final List<ChatMessage> chatMessages = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private ListView chatListView;
    private EditText editChatMessage;
    private TextView typingIndicator;

    // Game (Minimax Tic Tac Toe) state
    private int[] board = new int[9]; // 0: empty, 1: User (X), 2: AI (O)
    private Button[] boardButtons = new Button[9];
    private TextView gameStatusText;
    private boolean gameActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aiEngine = new AiEngine(this);

        // Build entire UI cleanly with code for maximum flexibility and elegance
        View root = buildRootLayout();
        setContentView(root);

        // Initial AI greeting
        chatMessages.add(new ChatMessage(getString(R.string.ai_welcome), ChatMessage.TYPE_AI));
        chatAdapter.notifyDataSetChanged();

        selectTab(TAB_CHAT);
    }

    private View buildRootLayout() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#F8FAFC"));
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Top App Bar
        LinearLayout appBar = new LinearLayout(this);
        appBar.setOrientation(LinearLayout.HORIZONTAL);
        appBar.setBackgroundColor(Color.parseColor("#1E1B4B"));
        int pad16 = dpToPx(16);
        int pad12 = dpToPx(12);
        appBar.setPadding(pad16, pad12, pad16, pad12);
        appBar.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout titleBlock = new LinearLayout(this);
        titleBlock.setOrientation(LinearLayout.VERTICAL);
        titleBlock.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(this);
        title.setText(R.string.app_name);
        title.setTextColor(Color.WHITE);
        title.setTextSize(19f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        titleBlock.addView(title);

        TextView subTitle = new TextView(this);
        subTitle.setText("בינה מלאכותית עצמאית 100% על המכשיר");
        subTitle.setTextColor(Color.parseColor("#A5B4FC"));
        subTitle.setTextSize(12f);
        titleBlock.addView(subTitle);

        appBar.addView(titleBlock);

        // Status badge
        TextView offlineBadge = new TextView(this);
        offlineBadge.setText("● אופליין");
        offlineBadge.setTextColor(Color.parseColor("#34D399"));
        offlineBadge.setTextSize(12f);
        offlineBadge.setTypeface(Typeface.DEFAULT_BOLD);
        offlineBadge.setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4));
        offlineBadge.setBackgroundResource(R.drawable.btn_quick);
        appBar.addView(offlineBadge);

        root.addView(appBar);

        // Tab Navigation Bar
        LinearLayout tabsBar = new LinearLayout(this);
        tabsBar.setOrientation(LinearLayout.HORIZONTAL);
        tabsBar.setBackgroundColor(Color.parseColor("#312E81"));
        tabsBar.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6));

        btnTabChat = createTabButton(getString(R.string.tab_chat), v -> selectTab(TAB_CHAT));
        btnTabTools = createTabButton(getString(R.string.tab_tools), v -> selectTab(TAB_TOOLS));
        btnTabTeach = createTabButton(getString(R.string.tab_teach), v -> selectTab(TAB_TEACH));
        btnTabGame = createTabButton(getString(R.string.tab_game), v -> selectTab(TAB_GAME));

        tabsBar.addView(btnTabChat);
        tabsBar.addView(btnTabTools);
        tabsBar.addView(btnTabTeach);
        tabsBar.addView(btnTabGame);
        root.addView(tabsBar);

        // Dynamic Content Container
        contentContainer = new LinearLayout(this);
        contentContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        contentContainer.setLayoutParams(containerParams);
        root.addView(contentContainer);

        return root;
    }

    private Button createTabButton(String text, View.OnClickListener listener) {
        Button btn = new Button(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(42), 1f);
        params.setMargins(dpToPx(2), 0, dpToPx(2), 0);
        btn.setLayoutParams(params);
        btn.setText(text);
        btn.setTextSize(13f);
        btn.setTextColor(Color.parseColor("#C7D2FE"));
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setOnClickListener(listener);
        return btn;
    }

    private void selectTab(int tab) {
        currentTab = tab;
        contentContainer.removeAllViews();

        // Update Tab visual state
        Button[] buttons = {btnTabChat, btnTabTools, btnTabTeach, btnTabGame};
        for (int i = 0; i < buttons.length; i++) {
            if (i == tab) {
                buttons[i].setTextColor(Color.WHITE);
                buttons[i].setTypeface(Typeface.DEFAULT_BOLD);
                buttons[i].setBackgroundColor(Color.parseColor("#4F46E5"));
            } else {
                buttons[i].setTextColor(Color.parseColor("#A5B4FC"));
                buttons[i].setTypeface(Typeface.DEFAULT);
                buttons[i].setBackgroundColor(Color.TRANSPARENT);
            }
        }

        switch (tab) {
            case TAB_CHAT:
                buildChatTab();
                break;
            case TAB_TOOLS:
                buildToolsTab();
                break;
            case TAB_TEACH:
                buildTeachTab();
                break;
            case TAB_GAME:
                buildGameTab();
                break;
        }
    }

    // =========================================================================
    //  TAB 1: SMART CHAT
    // =========================================================================
    private void buildChatTab() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Quick prompts scroll view
        HorizontalScrollView quickScroll = new HorizontalScrollView(this);
        quickScroll.setHorizontalScrollBarEnabled(false);
        quickScroll.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(4));

        LinearLayout quickLayout = new LinearLayout(this);
        quickLayout.setOrientation(LinearLayout.HORIZONTAL);

        String[] quickPrompts = {
                "ספר בדיחה",
                "כתוב שיר מעורר השראה",
                "מה המרחק לירח?",
                "חשב: 25 * 14",
                "תן לי עצה לחיים",
                "מהי מהירות האור?"
        };

        for (final String prompt : quickPrompts) {
            Button qb = new Button(this);
            qb.setText(prompt);
            qb.setTextSize(12f);
            qb.setTextColor(Color.parseColor("#3730A3"));
            qb.setBackgroundResource(R.drawable.btn_quick);
            LinearLayout.LayoutParams qbParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, dpToPx(36));
            qbParams.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            qb.setLayoutParams(qbParams);
            qb.setPadding(dpToPx(12), 0, dpToPx(12), 0);
            qb.setOnClickListener(v -> handleSendMessage(prompt));
            quickLayout.addView(qb);
        }
        quickScroll.addView(quickLayout);
        layout.addView(quickScroll);

        // Chat ListView
        chatListView = new ListView(this);
        chatListView.setDivider(null);
        chatListView.setDividerHeight(dpToPx(8));
        chatListView.setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8));
        chatListView.setClipToPadding(false);
        LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        chatListView.setLayoutParams(listParams);

        chatAdapter = new ChatAdapter();
        chatListView.setAdapter(chatAdapter);
        layout.addView(chatListView);

        // Typing indicator
        typingIndicator = new TextView(this);
        typingIndicator.setText("ה-AI חושב ומעבד נתונים...");
        typingIndicator.setTextSize(12f);
        typingIndicator.setTextColor(Color.parseColor("#4F46E5"));
        typingIndicator.setPadding(dpToPx(16), dpToPx(2), dpToPx(16), dpToPx(4));
        typingIndicator.setVisibility(View.GONE);
        layout.addView(typingIndicator);

        // Bottom Input Row
        LinearLayout inputBar = new LinearLayout(this);
        inputBar.setOrientation(LinearLayout.HORIZONTAL);
        inputBar.setPadding(dpToPx(10), dpToPx(8), dpToPx(10), dpToPx(8));
        inputBar.setBackgroundColor(Color.WHITE);
        inputBar.setGravity(Gravity.CENTER_VERTICAL);

        editChatMessage = new EditText(this);
        editChatMessage.setHint(R.string.chat_hint);
        editChatMessage.setHintTextColor(Color.parseColor("#94A3B8"));
        editChatMessage.setTextColor(Color.parseColor("#0F172A"));
        editChatMessage.setTextSize(14f);
        editChatMessage.setBackgroundResource(R.drawable.bg_input);
        editChatMessage.setPadding(dpToPx(16), dpToPx(10), dpToPx(16), dpToPx(10));
        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        editParams.setMargins(dpToPx(4), 0, dpToPx(4), 0);
        editChatMessage.setLayoutParams(editParams);
        inputBar.addView(editChatMessage);

        Button btnSend = new Button(this);
        btnSend.setText(R.string.send);
        btnSend.setTextColor(Color.WHITE);
        btnSend.setTextSize(14f);
        btnSend.setTypeface(Typeface.DEFAULT_BOLD);
        btnSend.setBackgroundResource(R.drawable.btn_primary);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                dpToPx(72), dpToPx(44));
        btnSend.setLayoutParams(btnParams);
        btnSend.setOnClickListener(v -> {
            String text = editChatMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                editChatMessage.setText("");
                handleSendMessage(text);
            }
        });
        inputBar.addView(btnSend);

        layout.addView(inputBar);
        contentContainer.addView(layout);

        chatListView.setSelection(chatAdapter.getCount() - 1);
    }

    private void handleSendMessage(final String text) {
        chatMessages.add(new ChatMessage(text, ChatMessage.TYPE_USER));
        chatAdapter.notifyDataSetChanged();
        if (chatListView != null) {
            chatListView.smoothScrollToPosition(chatAdapter.getCount() - 1);
        }

        if (typingIndicator != null) {
            typingIndicator.setVisibility(View.VISIBLE);
        }

        // Realistic asynchronous thinking simulation on background thread
        new Thread(() -> {
            final String aiReply = aiEngine.generateResponse(text);
            try {
                // Short deliberate pause simulating neural processing
                Thread.sleep(400);
            } catch (InterruptedException ignored) {
            }

            mainHandler.post(() -> {
                if (typingIndicator != null) {
                    typingIndicator.setVisibility(View.GONE);
                }
                chatMessages.add(new ChatMessage(aiReply, ChatMessage.TYPE_AI));
                if (chatAdapter != null) {
                    chatAdapter.notifyDataSetChanged();
                }
                if (chatListView != null) {
                    chatListView.smoothScrollToPosition(chatAdapter.getCount() - 1);
                }
            });
        }).start();
    }

    // =========================================================================
    //  TAB 2: NLP SMART TOOLS (Sentiment, Summarizer, Idea Generator)
    // =========================================================================
    private void buildToolsTab() {
        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(24));

        // Tool 1: Sentiment Analysis Card
        root.addView(createSectionHeader("📊 ניתוח סנטימנט ורגשות בטקסט"));
        LinearLayout cardSentiment = createCard();

        final EditText editSentiment = new EditText(this);
        editSentiment.setHint("הקלד משפט או ביקורת (למשל: 'היה לי יום מושלם וכיפי')...");
        editSentiment.setTextSize(13f);
        editSentiment.setTextColor(Color.parseColor("#0F172A"));
        editSentiment.setBackgroundResource(R.drawable.bg_input);
        editSentiment.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));
        cardSentiment.addView(editSentiment);

        final TextView sentimentResult = new TextView(this);
        sentimentResult.setTextSize(13f);
        sentimentResult.setTextColor(Color.parseColor("#1E293B"));
        sentimentResult.setPadding(0, dpToPx(10), 0, 0);

        Button btnAnalyze = createCardButton("נתח רגש כעת", v -> {
            String input = editSentiment.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(this, "נא להקליד טקסט לניתוח", Toast.LENGTH_SHORT).show();
                return;
            }
            AiEngine.SentimentResult res = aiEngine.analyzeSentiment(input);
            sentimentResult.setText(String.format("תוצאה: %s\nציון קוטביות: %.2f | מילים חיוביות: %d | מילים שליליות: %d",
                    res.label, res.score, res.positiveCount, res.negativeCount));
        });
        cardSentiment.addView(btnAnalyze);
        cardSentiment.addView(sentimentResult);
        root.addView(cardSentiment);

        // Tool 2: Summarizer Card
        root.addView(createSectionHeader("📄 מסכם טקסטים אוטומטי (TextRank)"));
        LinearLayout cardSummary = createCard();

        final EditText editSummary = new EditText(this);
        editSummary.setHint("הדבק כאן פסקה ארוכה או מאמר לסיכום...");
        editSummary.setMinLines(3);
        editSummary.setTextSize(13f);
        editSummary.setTextColor(Color.parseColor("#0F172A"));
        editSummary.setBackgroundResource(R.drawable.bg_input);
        editSummary.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));
        cardSummary.addView(editSummary);

        final TextView summaryResult = new TextView(this);
        summaryResult.setTextSize(13f);
        summaryResult.setTextColor(Color.parseColor("#1E293B"));
        summaryResult.setPadding(0, dpToPx(10), 0, 0);

        Button btnSummarize = createCardButton("סכם טקסט בחילוץ חכם", v -> {
            String input = editSummary.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(this, "נא להדביק טקסט לסיכום", Toast.LENGTH_SHORT).show();
                return;
            }
            String result = aiEngine.summarize(input, 2);
            summaryResult.setText("📌 עיקרי הדברים שחולצו:\n" + result);
        });
        cardSummary.addView(btnSummarize);
        cardSummary.addView(summaryResult);
        root.addView(cardSummary);

        // Tool 3: Creative Idea Generator
        root.addView(createSectionHeader("✨ מחולל רעיונות ויצירתיות"));
        LinearLayout cardIdea = createCard();

        final TextView ideaResult = new TextView(this);
        ideaResult.setText("לחץ על הכפתור כדי שה-AI יחבר רעיון יצירתי וחדשני.");
        ideaResult.setTextSize(13f);
        ideaResult.setTextColor(Color.parseColor("#334155"));
        ideaResult.setPadding(0, 0, 0, dpToPx(10));
        cardIdea.addView(ideaResult);

        Button btnGenerateIdea = createCardButton("הפק רעיון יצירתי חדש", v -> {
            String idea = aiEngine.generateIdea("startup");
            ideaResult.setText(idea);
        });
        cardIdea.addView(btnGenerateIdea);
        root.addView(cardIdea);

        scroll.addView(root);
        contentContainer.addView(scroll);
    }

    // =========================================================================
    //  TAB 3: TEACH AI (Knowledge Base)
    // =========================================================================
    private void buildTeachTab() {
        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(24));

        root.addView(createSectionHeader("🧠 אימון והרחבת הזיכרון של ה-AI"));

        TextView desc = new TextView(this);
        desc.setText("הבינה המלאכותית לומדת באופן מקומי! תוכל ללמד אותה שאלות ותשובות פרטיות, עובדות או סיסמאות אישיות, והיא תדע לענות עליהן בצ'אט מיידית.");
        desc.setTextSize(13f);
        desc.setTextColor(Color.parseColor("#475569"));
        desc.setPadding(0, 0, 0, dpToPx(12));
        root.addView(desc);

        LinearLayout cardTeach = createCard();

        final EditText editQ = new EditText(this);
        editQ.setHint("שאלה או מילת מפתח (למשל: 'מי הכלב שלי?')");
        editQ.setTextSize(13f);
        editQ.setTextColor(Color.parseColor("#0F172A"));
        editQ.setBackgroundResource(R.drawable.bg_input);
        editQ.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));
        cardTeach.addView(editQ);

        final EditText editA = new EditText(this);
        editA.setHint("התשובה שה-AI יחזיר (למשל: 'הכלב שלך הוא רקסי החמוד!')");
        editA.setTextSize(13f);
        editA.setTextColor(Color.parseColor("#0F172A"));
        editA.setBackgroundResource(R.drawable.bg_input);
        editA.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));
        LinearLayout.LayoutParams aParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        aParams.setMargins(0, dpToPx(8), 0, 0);
        editA.setLayoutParams(aParams);
        cardTeach.addView(editA);

        final LinearLayout learnedListContainer = new LinearLayout(this);
        learnedListContainer.setOrientation(LinearLayout.VERTICAL);
        learnedListContainer.setPadding(0, dpToPx(12), 0, 0);

        Button btnSave = createCardButton("שמור בזיכרון ה-AI", v -> {
            String q = editQ.getText().toString().trim();
            String a = editA.getText().toString().trim();
            if (q.isEmpty() || a.isEmpty()) {
                Toast.makeText(this, "נא למלא גם שאלה וגם תשובה", Toast.LENGTH_SHORT).show();
                return;
            }
            aiEngine.learnFact(q, a);
            editQ.setText("");
            editA.setText("");
            Toast.makeText(this, "ה-AI למד את המידע בהצלחה!", Toast.LENGTH_SHORT).show();
            refreshLearnedList(learnedListContainer);
        });
        cardTeach.addView(btnSave);
        root.addView(cardTeach);

        root.addView(createSectionHeader("📚 ידע שמור בזיכרון המקומי:"));
        refreshLearnedList(learnedListContainer);
        root.addView(learnedListContainer);

        // Clear all button
        Button btnClear = new Button(this);
        btnClear.setText("איפוס כל הידע שנלמד");
        btnClear.setTextColor(Color.parseColor("#EF4444"));
        btnClear.setBackgroundResource(R.drawable.btn_quick);
        LinearLayout.LayoutParams clrParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(40));
        clrParams.setMargins(0, dpToPx(16), 0, 0);
        btnClear.setLayoutParams(clrParams);
        btnClear.setOnClickListener(v -> {
            aiEngine.clearKnowledge();
            refreshLearnedList(learnedListContainer);
            Toast.makeText(this, "זיכרון ה-AI אופס", Toast.LENGTH_SHORT).show();
        });
        root.addView(btnClear);

        scroll.addView(root);
        contentContainer.addView(scroll);
    }

    private void refreshLearnedList(LinearLayout container) {
        container.removeAllViews();
        Map<String, String> facts = aiEngine.getAllLearnedFacts();
        if (facts.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("עדיין לא נלמד מידע אישי. הוסף עובדה ראשונה למעלה!");
            empty.setTextColor(Color.parseColor("#94A3B8"));
            empty.setTextSize(13f);
            container.addView(empty);
            return;
        }

        for (Map.Entry<String, String> entry : facts.entrySet()) {
            LinearLayout item = createCard();
            item.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.setMargins(0, 0, 0, dpToPx(8));
            item.setLayoutParams(p);

            TextView qText = new TextView(this);
            qText.setText("❓ שאלה: " + entry.getKey());
            qText.setTypeface(Typeface.DEFAULT_BOLD);
            qText.setTextSize(13f);
            qText.setTextColor(Color.parseColor("#1E1B4B"));
            item.addView(qText);

            TextView aText = new TextView(this);
            aText.setText("💡 תשובה: " + entry.getValue());
            aText.setTextSize(12f);
            aText.setTextColor(Color.parseColor("#475569"));
            item.addView(aText);

            container.addView(item);
        }
    }

    // =========================================================================
    //  TAB 4: UNBEATABLE MINIMAX AI GAME (איקס עיגול חכם)
    // =========================================================================
    private void buildGameTab() {
        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(24));

        root.addView(createSectionHeader("🎮 אתגר חשיבה: שחק נגד אלגוריתם Minimax"));

        TextView gameDesc = new TextView(this);
        gameDesc.setText("ה-AI מחשב את כל עץ ההחלטות האפשרי בעזרת אלגוריתם Minimax קלאסי. האם תצליח להשיג תיקו מולו?");
        gameDesc.setTextSize(13f);
        gameDesc.setTextColor(Color.parseColor("#475569"));
        gameDesc.setGravity(Gravity.CENTER);
        gameDesc.setPadding(0, 0, 0, dpToPx(14));
        root.addView(gameDesc);

        gameStatusText = new TextView(this);
        gameStatusText.setText("תורך לשחק (אתה X, ה-AI הוא O)");
        gameStatusText.setTextSize(15f);
        gameStatusText.setTypeface(Typeface.DEFAULT_BOLD);
        gameStatusText.setTextColor(Color.parseColor("#4F46E5"));
        gameStatusText.setGravity(Gravity.CENTER);
        gameStatusText.setPadding(0, 0, 0, dpToPx(14));
        root.addView(gameStatusText);

        // 3x3 Game Board Grid
        LinearLayout boardContainer = new LinearLayout(this);
        boardContainer.setOrientation(LinearLayout.VERTICAL);
        boardContainer.setBackgroundResource(R.drawable.bg_card);
        boardContainer.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        int boardWidth = dpToPx(270);
        LinearLayout.LayoutParams bParams = new LinearLayout.LayoutParams(boardWidth, boardWidth);
        boardContainer.setLayoutParams(bParams);

        for (int r = 0; r < 3; r++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

            for (int c = 0; c < 3; c++) {
                final int idx = r * 3 + c;
                Button btn = new Button(this);
                LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                cellParams.setMargins(dpToPx(3), dpToPx(3), dpToPx(3), dpToPx(3));
                btn.setLayoutParams(cellParams);
                btn.setTextSize(24f);
                btn.setTypeface(Typeface.DEFAULT_BOLD);
                btn.setBackgroundResource(R.drawable.btn_quick);
                btn.setOnClickListener(v -> onGameCellClicked(idx));
                boardButtons[idx] = btn;
                row.addView(btn);
            }
            boardContainer.addView(row);
        }
        root.addView(boardContainer);

        // Reset game button
        Button btnReset = new Button(this);
        btnReset.setText("משחק חדש 🔄");
        btnReset.setTextColor(Color.WHITE);
        btnReset.setTextSize(14f);
        btnReset.setBackgroundResource(R.drawable.btn_primary);
        LinearLayout.LayoutParams rParams = new LinearLayout.LayoutParams(
                dpToPx(160), dpToPx(44));
        rParams.setMargins(0, dpToPx(18), 0, 0);
        btnReset.setLayoutParams(rParams);
        btnReset.setOnClickListener(v -> resetGame());
        root.addView(btnReset);

        scroll.addView(root);
        contentContainer.addView(scroll);

        resetGame();
    }

    private void onGameCellClicked(int idx) {
        if (!gameActive || board[idx] != 0) return;

        // User move (1 = X)
        board[idx] = 1;
        boardButtons[idx].setText("X");
        boardButtons[idx].setTextColor(Color.parseColor("#4F46E5"));

        int winner = checkWinner(board);
        if (winner != 0) {
            finishGame(winner);
            return;
        }

        if (isBoardFull(board)) {
            finishGame(0);
            return;
        }

        // AI Move (2 = O)
        gameStatusText.setText("ה-AI מחשב מהלך אופטימלי...");
        gameActive = false;

        mainHandler.postDelayed(() -> {
            int bestMove = findBestMove(board);
            if (bestMove != -1) {
                board[bestMove] = 2;
                boardButtons[bestMove].setText("O");
                boardButtons[bestMove].setTextColor(Color.parseColor("#EF4444"));
            }

            int win = checkWinner(board);
            if (win != 0) {
                finishGame(win);
            } else if (isBoardFull(board)) {
                finishGame(0);
            } else {
                gameStatusText.setText("תורך לשחק!");
                gameActive = true;
            }
        }, 300);
    }

    private void finishGame(int winner) {
        gameActive = false;
        if (winner == 1) {
            gameStatusText.setText("🎉 מדהים! ניצחת את ה-AI!");
            gameStatusText.setTextColor(Color.parseColor("#10B981"));
        } else if (winner == 2) {
            gameStatusText.setText("🤖 ה-AI ניצח! נסה שוב");
            gameStatusText.setTextColor(Color.parseColor("#EF4444"));
        } else {
            gameStatusText.setText("🤝 תיקו! חשיבה מצוינת מול ה-AI");
            gameStatusText.setTextColor(Color.parseColor("#64748B"));
        }
    }

    private void resetGame() {
        for (int i = 0; i < 9; i++) {
            board[i] = 0;
            if (boardButtons[i] != null) {
                boardButtons[i].setText("");
            }
        }
        gameActive = true;
        if (gameStatusText != null) {
            gameStatusText.setText("תורך לשחק (אתה X, ה-AI הוא O)");
            gameStatusText.setTextColor(Color.parseColor("#4F46E5"));
        }
    }

    private boolean isBoardFull(int[] b) {
        for (int cell : b) {
            if (cell == 0) return false;
        }
        return true;
    }

    private int checkWinner(int[] b) {
        int[][] lines = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
                {0, 4, 8}, {2, 4, 6}             // Diagonals
        };

        for (int[] line : lines) {
            if (b[line[0]] != 0 && b[line[0]] == b[line[1]] && b[line[1]] == b[line[2]]) {
                return b[line[0]];
            }
        }
        return 0; // No winner
    }

    /**
     * Complete Minimax Algorithm for unbeatable AI logic.
     */
    private int findBestMove(int[] b) {
        int bestVal = -1000;
        int bestMove = -1;

        for (int i = 0; i < 9; i++) {
            if (b[i] == 0) {
                b[i] = 2; // AI move
                int moveVal = minimax(b, 0, false);
                b[i] = 0; // backtrack
                if (moveVal > bestVal) {
                    bestMove = i;
                    bestVal = moveVal;
                }
            }
        }
        return bestMove;
    }

    private int minimax(int[] b, int depth, boolean isMax) {
        int winner = checkWinner(b);
        if (winner == 2) return 10 - depth;
        if (winner == 1) return depth - 10;
        if (isBoardFull(b)) return 0;

        if (isMax) {
            int best = -1000;
            for (int i = 0; i < 9; i++) {
                if (b[i] == 0) {
                    b[i] = 2;
                    best = Math.max(best, minimax(b, depth + 1, false));
                    b[i] = 0;
                }
            }
            return best;
        } else {
            int best = 1000;
            for (int i = 0; i < 9; i++) {
                if (b[i] == 0) {
                    b[i] = 1;
                    best = Math.min(best, minimax(b, depth + 1, true));
                    b[i] = 0;
                }
            }
            return best;
        }
    }

    // =========================================================================
    //  UI BUILDER HELPERS
    // =========================================================================
    private TextView createSectionHeader(String title) {
        TextView tv = new TextView(this);
        tv.setText(title);
        tv.setTextSize(15f);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextColor(Color.parseColor("#1E1B4B"));
        tv.setPadding(0, dpToPx(8), 0, dpToPx(8));
        return tv;
    }

    private LinearLayout createCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card);
        card.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dpToPx(14));
        card.setLayoutParams(params);
        return card;
    }

    private Button createCardButton(String text, View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(13f);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setBackgroundResource(R.drawable.btn_primary);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(42));
        params.setMargins(0, dpToPx(10), 0, 0);
        btn.setLayoutParams(params);
        btn.setOnClickListener(listener);
        return btn;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    // =========================================================================
    //  CHAT LIST ADAPTER
    // =========================================================================
    private class ChatAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return chatMessages.size();
        }

        @Override
        public Object getItem(int position) {
            return chatMessages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ChatMessage msg = chatMessages.get(position);
            LinearLayout row;

            if (convertView instanceof LinearLayout) {
                row = (LinearLayout) convertView;
                row.removeAllViews();
            } else {
                row = new LinearLayout(MainActivity.this);
                row.setOrientation(LinearLayout.HORIZONTAL);
            }

            TextView messageText = new TextView(MainActivity.this);
            messageText.setText(msg.getText());
            messageText.setTextSize(14f);
            messageText.setLineSpacing(dpToPx(2), 1.15f);

            int pHoriz = dpToPx(14);
            int pVert = dpToPx(10);
            messageText.setPadding(pHoriz, pVert, pHoriz, pVert);

            LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            bubbleParams.weight = 0;

            if (msg.getType() == ChatMessage.TYPE_USER) {
                row.setGravity(Gravity.RIGHT | Gravity.END);
                messageText.setBackgroundResource(R.drawable.bg_message_user);
                messageText.setTextColor(Color.WHITE);
                bubbleParams.setMargins(dpToPx(48), 0, 0, 0);
            } else {
                row.setGravity(Gravity.LEFT | Gravity.START);
                messageText.setBackgroundResource(R.drawable.bg_message_ai);
                messageText.setTextColor(Color.parseColor("#1E1B4B"));
                bubbleParams.setMargins(0, 0, dpToPx(48), 0);
            }

            messageText.setLayoutParams(bubbleParams);
            row.addView(messageText);

            return row;
        }
    }
}
