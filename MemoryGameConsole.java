// 引入所需要的模組
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

// 定義主要的遊戲類別
public class MemoryGameConsole {

    // 主程序
    public static void main(String[] args) {
        // 建立一個Scanner來讀取使用者輸入
        Scanner scanner = new Scanner(System.in);

        // 歡迎訊息
        System.out.println("歡迎來到記憶力挑戰遊戲！");
        // 請求輸入配對數量
        System.out.print("請輸入卡片配對數量：");
        int numPairs = scanner.nextInt();
        // 請求輸入時間限制
        System.out.print("請輸入遊戲時間限制（秒）：");
        int timeLimitSeconds = scanner.nextInt();

        // 建立遊戲實例並啟動
        MemoryGame game = new MemoryGame(numPairs, timeLimitSeconds);
        game.startGame();

        // 當遊戲未結束時持續遊戲
        while (!game.isGameOver()) {
            // 顯示卡片狀態
            System.out.println("卡片狀態：");
            printCards(game.getCards());

            // 請求玩家翻開卡片
            System.out.print("請輸入卡片索引（0-" + (game.getCards().size() - 1) + "）：");
            int index = scanner.nextInt();
            if(index > (game.getCards().size() - 1)){
                System.out.println("輸入數值超出卡片索引值");
                continue;
            }
            // 如果翻開成功則顯示訊息，否則提示錯誤
            if (game.flipCard(index)) {
                System.out.println("成功翻開卡片！");
            } else {
                System.out.println("無法翻開此卡片，請重試。");
            }
        }

        // 如果時間到則顯示遊戲結束，否則顯示挑戰成功
        if (game.isTimeUp()) {
            System.out.println("時間到！遊戲結束。");
        } else {
            System.out.println("恭喜你完成挑戰！");
        }

        // 關閉輸入流
        scanner.close();
    }

    // 打印卡片的方法
    private static void printCards(List<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            // 如果卡片是翻面狀態，則顯示其ID，否則顯示X
            System.out.print(card.isFaceUp() ? card.getId() : "X");
            System.out.print(" ");
            if ((i + 1) % 4 == 0) {
                System.out.println();
            }
        }
    }

    // 定義卡片類別
    static class Card {
        private int id;  // 卡片的ID，配對的卡片擁有相同的ID
        private boolean isFaceUp;  // 表示卡片是否翻面的狀態

        public Card(int id) {  // 建構子
            this.id = id;
            this.isFaceUp = false;  // 初始狀態為未翻開
        }

        // 取得ID的方法
        public int getId() {
            return id;
        }

        // 檢查卡片是否翻面的方法
        public boolean isFaceUp() {
            return isFaceUp;
        }

        // 翻卡的方法
        public void flip() {
            isFaceUp = !isFaceUp;
        }
    }

    // 定義記憶力遊戲類別
    static class MemoryGame {
        private List<Card> cards;  // 卡片的列表
        private Card previousCard;  // 前一張翻開的卡片
        private long startTime;  // 遊戲開始的時間
        private int timeLimitSeconds;  // 遊戲的時間限制
        private int pairsFound;  // 已找到的配對數量

        // 建構子
        public MemoryGame(int numPairs, int timeLimitSeconds) {
            this.cards = createCards(numPairs);  // 創建卡片
            this.previousCard = null;  // 初始化前一張翻開的卡片
            this.timeLimitSeconds = timeLimitSeconds;  // 設定時間限制
            this.pairsFound = 0;  // 初始化已找到的配對數量
        }

        // 取得卡片的方法
        public List<Card> getCards() {
            return cards;
        }

        // 創建卡片的方法
        private List<Card> createCards(int numPairs) {
            List<Card> cards = new ArrayList<>();
            for (int i = 0; i < numPairs; i++) {
                // 對於每個配對，創建兩張具有相同ID的卡片
                cards.add(new Card(i));
                cards.add(new Card(i));
            }
            // 將卡片打亂順序
            Collections.shuffle(cards);
            return cards;
        }

        // 開始遊戲的方法，紀錄開始時間
        public void startGame() {
            startTime = System.currentTimeMillis();
        }

        // 檢查時間是否到達限制的方法
        public boolean isTimeUp() {
            long elapsedTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);
            return elapsedTime >= timeLimitSeconds;
        }

        // 檢查遊戲是否結束的方法，當找到的配對數量等於卡片數量的一半或者時間已到，則遊戲結束
        public boolean isGameOver() {
            return pairsFound == cards.size() / 2 || isTimeUp();
        }

        // 翻卡的方法，如果卡片已經翻開或遊戲已結束則無法翻開，否則翻開並檢查是否配對成功
        public boolean flipCard(int index) {
            Card card = cards.get(index);
            if (card.isFaceUp() || isGameOver()) {
                return false;
            }

            card.flip();

            if (previousCard == null) {
                previousCard = card;
            } else {
                if (previousCard.getId() == card.getId()) {
                    pairsFound++;
                    previousCard = null;
                } else {
                    printCards(cards);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    previousCard.flip();
                    card.flip();
                    previousCard = null;
                }
            }

            return true;
        }
    }
}
