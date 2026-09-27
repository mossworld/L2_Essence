/*
==========================================================================
	이벤트 스크립트 종합판 (완전 참조용 템플릿)
==========================================================================
설명:
- 원본 코드는 절대 수정, 삭제, 줄임 금지
- 한글 주석만 추가 가능
- 초보자 친화적으로 코드 이해를 돕는 상세 한글 주석 포함
- 복사 가능한 인터페이스 제공
- Eclipse에서 바로 컴파일 가능하도록 작성
==========================================================================
   A. 이벤트 / 핸들러 메서드 (주요)
==========================================================================
   onEvent(String event, Npc npc, Player player)     : 일반 이벤트 처리 진입점
   onFirstTalk(Npc npc, Player player)             : NPC 첫 대화 처리
   onKill(Npc npc, Player killer, boolean summon)  : 몬스터 처치 이벤트
   onPlayerLogin(Player player), onPlayerLogout(Player player) : 접속/로그아웃 훅
   onSkillSee(Npc npc, Player caster, Skill skill): 스킬 사용 감지
   onAttack(Npc npc, Player attacker, int damage)  : 공격 이벤트
   onSpawn(Npc npc)                                : NPC/몬스터 스폰 시 처리
   onDailyReset()                                  : 일일 초기화
   startEvent(), stopEvent()                        : 이벤트 시작/종료
   spawnNext(), randomSpawn(), removeSpawn()        : 스폰 제어
   reloadRewards()                                 : 보상 데이터 리로드
   triggerCast()                                   : 스킬/효과 강제 발동
   load(), loadHTM(), parseDocument(), parseValue(), parseDoubleWithoutPoint() : 파일/HTML/XML 파싱
   main()                                          : 이벤트 진입(스크립트 메인)
   onMultisellBuyItem(...)                         : 상점 구매 훅
==========================================================================
   B. 아이템 / 인벤토리 처리
==========================================================================
   // 지급
   giveItems(Player player, int itemId, long count)
   player.addItem(itemId, count)

   // 회수 / 삭제
   takeItems(Player player, int itemId, long amount)
   takeItems(Player player, int amount, int... itemIds)
   takeItemsAndUpdateInventory(Player player, Map<Item,Long> feeItemsMap, long adenaAmount)
   destroyItem(ItemProcessType process, Item item, long count, WorldObject reference, boolean sendMessage)
   destroyItemByItemId(ItemProcessType process, int itemId, long count, ...)
   player.getInventory().destroyItemByItemId(...)
   player.getWarehouse().destroyItem(...)

   // 조회
   getQuestItemsCount(Player, int itemId)
   player.getInventory(), player.getWarehouse()

   // 유의사항
   - ItemProcessType으로 회수 이유 표시 가능 (DESTROY, ADENLAB 등)
   - takeItemsAndUpdateInventory 사용 시 UI 자동 갱신
==========================================================================
   C. 스폰 / 보상 / 진행 관련 상수
==========================================================================
   // 몬스터/그룹
   BOSSES, ELITE_MINIONS, NORMAL_MINIONS, BRAINWASHED_MINIONS
   // 카운터/한계
   BRAINWASHED_MONSTER_COUNT, ELITE_MONSTER_COUNT, MINION_MAX_COUNT, MAX_DROP_COUNT, MINION_KILL_COUNTER
   // 아이템/보상
   REWARD, REWARD_VAR, REWARD_LOCK, RED_BEADS, GOLDEN_WHEEL_COIN, SANTA_GIFT, FORTUNE_READING_TICKET, LUXURY_FORTUNE_READING_TICKET
   // NPC / 위치 / 템플릿
   SCHEDULE_NPCS, SPAWN_TEMPLATE, PLAINS_OF_GLORY_TELEPORT, WAR_TORN_PLAINS_TELEPORT, SILENT_VALLEY_TELEPORT
   SANTA_CLAUS_NPC_ID, RUDOLPH_HUMANIZED_NPC_ID, LARGE_CHRISTMAS_TREE_NPC_ID
   // 메시지
   EVENT_MESSAGE_START, EVENT_MESSAGE_KILL, EVENT_MESSAGE_TO_KILLER, EVENT_MESSAGE_WEAK
   // 기타
   BUFF_ITEM_ID, BUFF_COOLDOWN, MIN_LEVEL, REQUIRED_AMOUNT, _initialized, _random, _multisells, _santaActive, _santaLocation
==========================================================================
   D. 이벤트 진행 상태 변수
==========================================================================
   // 카운터/타이머/시간
   count, gameCount, score, startHour, startMinute, endHour, endMinute, currentTime, currentDay, endTimeParts
   // 플레이어/대상 참조
   player, playerName, playerObjectId, npcId, aroundPlayers, locations
   // 보상/아이템
   rewardAmount, rewardId, rewardList, rewards, itemId, itemCount, itemChance, itemAttrs
   // 조건/확률
   chance, chanceSum, chanceToNextGame, chanceToObtainByPlayer, needToSumAllChance
   // 제약/레벨
   level, MIN_LEVEL, maximumLevel, minimumLevel
   // 기타
   multisells, _santaActive, _santaLocation, dropCount, lastReceived, redBeadsCount, redeemInAnyCase, respawnTimer
==========================================================================
   E. 공통 사용 클래스 / API
==========================================================================
   // 핵심 클래스
   Player, Npc, Item, Location, WorldObject
   // 퀘스트/이벤트
   Quest, QuestState, NewQuest, QuestCondType, QuestDialogType
   // QuestState API
   getQuestState(player, boolean), getCount(), setCount(int), isStarted(), isCompleted(), setCond(...), isCond(...), getGoal(), getItemId(), getLocation()
   // 퀘스트 제어
   startQuest(), exitQuest(boolean, boolean), startQuestTimer(name, ms), cancelQuestTimer(name)
   // 문서/IO
   IXmlReader, Document (org.w3c), parseDatapackFile()
   // 스케줄/타이밍
   SchedulingPattern, System.currentTimeMillis()
   // 유틸
   AtomicBoolean, SimpleEntry<K,V>, StatSet, ItemHolder, BlackCouponManager
==========================================================================
   F. NPC 대화 / 메시지
==========================================================================
   npc.showChatWindow(player, "file.htm")   : HTML 대화창 표시
   showChatWindow(player)                   : 간단한 대화창
   sendAcceptDialog(player), sendEndDialog(player) : 퀘스트 수락/종료 UI
   player.sendMessage(String) 또는 sendPacket(ServerPacket) : 시스템 메시지/패킷 전송
   HTML 템플릿: loadHTM("..."), htm, html, htmltext
==========================================================================
   G. 이벤트 등록 / XML / 유틸
==========================================================================
   // 이벤트 등록
   addStartNpc(npcId), addFirstTalkId(npcId), addTalkId(npcId), addSpawnId(npcId), addKillId(npcId), addAttackId(npcId)
   // XML/데이터 처리
   parseDatapackFile(path), parseAttributes(node), forEach(...)
   // 아이템 처리
   player.getInventory().addItem(), player.getInventory().destroyItemByItemId(...)
   // 퀘스트 상태/변수
   player.getVariables().getLong(key), .set(key, value)
==========================================================================
   H. 일일 1회 / 멀티 스테이지 이벤트 예제
==========================================================================
   - PlayerVariables 활용해 일일 1회 보상 지급
   - stageRewards Map으로 스테이지별 보상 관리
   - showChatWindow / npc.showChatWindow 사용
   - giveItems, takeItemsAndUpdateInventory 등으로 아이템 지급/회수
   - startEvent / stopEvent로 이벤트 시작/종료
   - onFirstTalk, onEvent에서 진행 흐름 제어
==========================================================================
   I. 사용 예시 (템플릿)
==========================================================================
   DailyStageEvent extends DailyStageRankingEventV3
   - addStartNpc(EVENT_NPC_ID)
   - onFirstTalk → HTML 안내
   - onEvent("next_stage") → 보상 지급 + 다음 단계 진행
   - giveItems(player, itemId, count)
   - takeItemsAndUpdateInventory(player, feeItemsMap, adenaAmount)
   - getPlayerVariables().get/set(...)로 DB/메모리 동기화
   - showStageHtml(player, stage) → NPC 대화창 표시
==========================================================================
spawnNext(), randomSpawn(), removeSpawn()         // NPC/몬스터 스폰 제어
reloadRewards()                                   // 보상 데이터 리로드
triggerCast()                                     // 스킬/효과 강제 발동
load(), loadHTM(), parseDocument(), parseValue(), parseDoubleWithoutPoint() // 파일/HTML/XML 파싱
main()                                            // 이벤트 진입점
onMultisellBuyItem(...)                           // 멀티셀(상점) 구매 훅

B. 아이템 / 인벤토리 처리
==========================================================================
지급
giveItems(Player player, int itemId, long count)
player.addItem(...)

회수 / 삭제
takeItems(Player player, int itemId, long amount)
takeItems(Player player, int amount, int... itemIds)
takeItemsAndUpdateInventory(Player player, Map<Item,Long> feeItemsMap, long adenaAmount)
destroyItem(ItemProcessType process, Item item, long count, WorldObject reference, boolean sendMessage)
player.getInventory().destroyItemByItemId(ItemProcessType..., itemId, count, player, sendMessage)
player.getWarehouse().destroyItem(...)

조회
getQuestItemsCount(Player, int itemId)
player.getInventory(), player.getWarehouse()

C. 스폰 / 보상 / 진행 관련 상수
==========================================================================
몬스터/그룹: BOSSES, ELITE_MINIONS, NORMAL_MINIONS, BRAINWASHED_MINIONS
카운터/한계: BRAINWASHED_MONSTER_COUNT, ELITE_MONSTER_COUNT, MINION_MAX_COUNT, MAX_DROP_COUNT, MINION_KILL_COUNTER
아이템/보상: REWARD, REWARD_VAR, REWARD_LOCK, RED_BEADS, GOLDEN_WHEEL_COIN, SANTA_GIFT, FORTUNE_READING_TICKET, LUXURY_FORTUNE_READING_TICKET
NPC / 위치 / 템플릿: SCHEDULE_NPCS, SPAWN_TEMPLATE, PLAINS_OF_GLORY_TELEPORT, WAR_TORN_PLAINS_TELEPORT, SILENT_VALLEY_TELEPORT, SANTA_CLAUS_NPC_ID, RUDOLPH_HUMANIZED_NPC_ID, LARGE_CHRISTMAS_TREE_NPC_ID
메시지: EVENT_MESSAGE_START, EVENT_MESSAGE_KILL, EVENT_MESSAGE_TO_KILLER, EVENT_MESSAGE_WEAK
기타: BUFF_ITEM_ID, BUFF_COOLDOWN, MIN_LEVEL, REQUIRED_AMOUNT, _initialized, _random, _multisells, _santaActive, _santaLocation

D. 이벤트 진행 상태 변수
==========================================================================
카운터/타이머/시간: count, gameCount, score, startHour, startMinute, endHour, endMinute, currentTime, currentDay, endTimeParts
플레이어/대상 참조: player, playerName, playerObjectId, npcId, aroundPlayers, locations
보상/아이템: rewardAmount, rewardId, rewardList, rewards, itemId, itemCount, itemChance, itemAttrs
조건/확률: chance, chanceSum, chanceToNextGame, chanceToObtainByPlayer, needToSumAllChance
제약/레벨: level, MIN_LEVEL, maximumLevel, minimumLevel
기타: multisells, _santaActive, _santaLocation, dropCount, lastReceived, redBeadsCount, redeemInAnyCase, respawnTimer

E. 공통 사용 클래스 / API
==========================================================================
핵심 클래스: Player, Npc, Item, Location, WorldObject
퀘스트/이벤트: Quest, QuestState, NewQuest, QuestCondType, QuestDialogType
QuestState API: getQuestState(player, boolean), getCount(), setCount(int), isStarted(), isCompleted(), setCond(...), isCond(...), getGoal(), getItemId(), getLocation()
퀘스트 제어: startQuest(), exitQuest(boolean, boolean), startQuestTimer(name, ms), cancelQuestTimer(name)
문서/IO: IXmlReader, Document (org.w3c), parseDatapackFile()
스케줄/타이밍: SchedulingPattern, System.currentTimeMillis()
유틸: AtomicBoolean, SimpleEntry<K,V>, StatSet, ItemHolder, BlackCouponManager

F. NPC 대화 / 메시지 관련
==========================================================================
npc.showChatWindow(player, "file.htm") — HTML 대화창 표시
showChatWindow(player) — 간단한 대화창
sendAcceptDialog(player), sendEndDialog(player) — 퀘스트 수락/종료 UI
player.sendMessage(String), sendPacket(ServerPacket) — 시스템 메시지/패킷 전송
HTML 템플릿 관리: loadHTM("..."), htm, html, htmltext

G. 흔히 쓰는 유틸 메서드 / XML 파싱 / 이벤트 등록
==========================================================================
addStartNpc(npcId), addFirstTalkId(npcId), addTalkId(npcId), addSpawnId(npcId), addKillId(npcId), addAttackId(npcId)
parseDatapackFile(path), parseAttributes(node), forEach(...)
player.getInventory().addItem(), player.getInventory().destroyItemByItemId(...)
player.getVariables().getLong(key), player.getVariables().set(key, value)

==========================================================================
==========================================================================
//////////////////////////////////////////////////////////////////////////
	추가 – 이벤트 스크립트 작성용 자료 항목
//////////////////////////////////////////////////////////////////////////
==========================================================================
==========================================================================
1. 이벤트 기본 정보
==========================================================================
eventName        : "SampleEvent"        // 이벤트 이름
eventStartTime   : "2025-09-10 12:00"   // 시작 시각
eventEndTime     : "2025-09-10 18:00"   // 종료 시각
repeatType       : DAILY / WEEKLY / ONCE // 반복 타입

2. 보상 정보
==========================================================================
rewardItems      : (57, 1001, 1), (57, 1002, 5)
                    // (itemTypeId, itemId, count)
rewardAdena      : 5000                 // 아데나 보상
rewardCondition  : "level >= 20"        // 수령 조건
rewardLimit      : 1                     // 제한 횟수 (일/주)

3. 몬스터 / 스폰 정보
==========================================================================
monsterIds       : (29001, 29002, 29003, 29004, 29005)
npcId            : 9000                  // 스폰될 NPC ID
spawnCount       : 3                     // 한 스테이지 등장 수
spawnInterval    : 5000                  // 스폰 간격(ms)
spawnLocations   : (x1,y1,z1), (x2,y2,z2) // 스폰 좌표

4. 스테이지 / 진행 정보
==========================================================================
stageCount       : 3
stageDetails     : "Stage1: 3 monsters, Stage2: 5 monsters, Stage3: Boss"
stageTimeLimit   : 600000                // 각 스테이지 시간 제한(ms)

5. 이벤트 조건 / 카운터
==========================================================================
minLevel         : 20
maxLevel         : 85
maxParticipants  : 50
eventCounters    : count, score, currentStage, playerProgress
eventFlags       : isActive, _santaActive

6. NPC 대화 / 메시지
==========================================================================
npcDialogFile    : "sample_event.htm"
acceptDialog     : true
endDialog        : true
customMessages   : "축하합니다! 보상을 받으셨습니다.",
                   "다음 단계로 진행하세요."

7. 아이템 회수 / 사용
==========================================================================
takeItemsMap     : {itemId: count, ...}  // 회수 아이템 목록
destroyType      : DESTROY / ADENLAB
updateInventoryUI: true / false

8. 기타 변수 / 상태
==========================================================================
randomSeed       : //
currentDay       : 10
currentTime      : 43200000
playerReferences : aroundPlayers, playerObjectId

==========================================================================
              (취합 후 실제 값 입력 가능)
   ※ 이 블럭은 "참조용 템플릿"이며 필요 시 삭제/수정 가능
==========================================================================
*/

package events.ThePowerOfLove;

import java.util.Calendar;
import net.sf.l2jdev.gameserver.model.actor.Npc;
import net.sf.l2jdev.gameserver.model.actor.Player;
import net.sf.l2jdev.gameserver.model.script.LongTimeEvent;
import net.sf.l2jdev.gameserver.model.skill.SkillCaster;
import net.sf.l2jdev.gameserver.model.skill.holders.SkillHolder;
import net.sf.l2jdev.gameserver.network.serverpackets.ExShowScreenMessage;

public class ThePowerOfLove extends LongTimeEvent
{
    // ==========================================
    // 아이템
    // ==========================================
    // = 8765;         // 붉은 구슬
    // = 91408;         // 엘모아덴 주화
    // = 90143;         // 아인하사드의 가호
    // = 4037;         // 행운의 동전
    // = 95570;         // 명예 주화
    // = 100255;         // 로열 쿠폰
    // = 102401;         // 해적의 피
    // = 103817;         // 영웅의 정수
    // = 104286;         // 황금 장미
    // = 104580;         // 발라카스의 영혼
    // = 102528;         // 플레이 리워드
    // ==========================================
    // [상수 설정] 아이템 ID 및 교환 설정
    // ==========================================
    private static final int COCO = 34424; // 이벤트 NPC ID
    
    // 무료 데일리 이벤트 보상
    private static final int FREE_REWARD_DAY_ONE = 104286; // 황금 장미
    private static final int FREE_REWARD_DAY_TWO = 90143; // 아인하사드의 가호

    // 유료 교환 인벤토리 체크 (붉은 구슬,엘모아덴 주화)
    private static final int AMULET_LOVE = 8765; // 붉은 구슬
    private static final int LUCKY_COIN = 4037; // 행운의 동전

    private static final int GOLDEN_ROSE = 91408; // 엘모아덴 주화
    private static final int EINHASAD_PROTECTION = 90143; // 아인하사드의 가호

    // 유료 교환 1 설정 (붉은 구슬 -> 행운의 동전)
    private static final int REQ_AMULET_AMOUNT = 10; // 붉은 구슬 수량
    private static final int REW_COIN_AMOUNT = 1; // 지급할 행운의 동전 수량

    // 유료 교환 2 설정 (엘모아덴 주화 -> 아인하사드의 가호)
    private static final int REQ_ROSE_AMOUNT = 10; // 엘모아덴 주화 수량
    private static final int REW_EINHASAD_AMOUNT = 1; // 지급할 아인하사드의 가호 수량

    // 무료 버프 설정
    private static final SkillHolder FREE_BUFF = new SkillHolder(48639, 1); // 리오나의 권능
    
    // 유저 변수 키 (매일 초기화 체크용)
    private static final String VAR_FREE_REWARD_DAY = "ThePowerOfLove_FreeReward_V2";
    private static final String VAR_FREE_BUFF_DAY = "ThePowerOfLove_FreeBuff_V2";

    private ThePowerOfLove()
    {
        addStartNpc(COCO);
        addFirstTalkId(COCO);
        addTalkId(COCO);
    }

    // ==========================================
    // NPC 클릭 시 호출 (무료 보상 자동 지급 + 조건별 HTML 이동)
    // ==========================================
    @Override
    public String onFirstTalk(Npc npc, Player player)
    {
        // 1. 현재 날짜 계산 (YYYYMMDD 형식)
        Calendar cal = Calendar.getInstance();
        int today = (cal.get(Calendar.YEAR) * 10000) + ((cal.get(Calendar.MONTH) + 1) * 100) + cal.get(Calendar.DAY_OF_MONTH);

        // --------------------------------------
        // [조건 1] 매일 1회 무료 보상 자동 지급
        // --------------------------------------
        if (today > player.getVariables().getInt(VAR_FREE_REWARD_DAY, 0))
        {
            giveItems(player, FREE_REWARD_DAY_ONE, 100); // 황금 장미 100개)
            giveItems(player, FREE_REWARD_DAY_TWO, 10); // 아인하사드의 가호 10개)
            player.getVariables().set(VAR_FREE_REWARD_DAY, today);
            player.sendMessage("오늘의 무료 데일리 이벤트 보상이 인벤토리에 지급되었습니다.");
        }

        // --------------------------------------
        // [조건 2] 매일 1회 무료 버프 자동 지급
        // --------------------------------------
        if (today > player.getVariables().getInt(VAR_FREE_BUFF_DAY, 0))
        {
            SkillCaster.triggerCast(npc, player, FREE_BUFF.getSkill());
            player.getVariables().set(VAR_FREE_BUFF_DAY, today);
            player.sendPacket(new ExShowScreenMessage("오늘의 무료 버프(리오나의 권능)가 적용되었습니다!", 5000));
        }

        // --------------------------------------
        // [조건 3] 유료 교환 요구 조건 부합 여부 체크
        // --------------------------------------
        long currentAmulets = player.getInventory().getInventoryItemCount(AMULET_LOVE, -1);
        long currentRoses = player.getInventory().getInventoryItemCount(GOLDEN_ROSE, -1);

        // 두 가지 교환 조건 중 하나라도 만족하는 경우 수동 교환 대화창 오픈
        if (currentAmulets >= REQ_AMULET_AMOUNT || currentRoses >= REQ_ROSE_AMOUNT)
        {
            return "34424-1.htm"; // 유료 교환 버튼이 있는 대화창으로 유도
        }
        else
        {
            player.sendMessage("인벤토리에 유료 교환이 가능한 아이템이 부족합니다.");
            return "34424-main.htm"; // 조건 미달 시 보여줄 기본 안내 대화창
        }
    }

    // ==========================================
    // 대화창 버튼 클릭 시 이벤트 (일괄 수동 교환 처리)
    // ==========================================
    @Override
    public String onEvent(String event, Npc npc, Player player)
    {
        if ("coco_takeAmulet".equals(event))
        {
            boolean exchanged = false;
            StringBuilder msg = new StringBuilder();

            // 1. 유료 교환 1 처리 (붉은 구슬 -> 행운의 동전)
            long amulets = player.getInventory().getInventoryItemCount(AMULET_LOVE, -1);
            if (amulets >= REQ_AMULET_AMOUNT)
            {
                int sets = (int) (amulets / REQ_AMULET_AMOUNT);
                long consumedAmulets = (long) sets * REQ_AMULET_AMOUNT;
                long rewardedCoins = (long) sets * REW_COIN_AMOUNT;
                
                if (takeItems(player, AMULET_LOVE, consumedAmulets)) {
                    giveItems(player, LUCKY_COIN, rewardedCoins);
                    msg.append("붉은 구슬 ").append(consumedAmulets).append("개");
                    exchanged = true;
                }
            }

            // 2. 유료 교환 2 처리 (엘모아덴 주화 -> 아인하사드의 가호)
            long roses = player.getInventory().getInventoryItemCount(GOLDEN_ROSE, -1);
            if (roses >= REQ_ROSE_AMOUNT)
            {
                int sets = (int) (roses / REQ_ROSE_AMOUNT);
                long consumedRoses = (long) sets * REQ_ROSE_AMOUNT;
                long rewardedEinhasad = (long) sets * REW_EINHASAD_AMOUNT;
                
                if (takeItems(player, GOLDEN_ROSE, consumedRoses)) {
                    giveItems(player, EINHASAD_PROTECTION, rewardedEinhasad);
                    if (exchanged) msg.append(", ");
                    msg.append("엘모아덴 주화 ").append(consumedRoses).append("개");
                    exchanged = true;
                }
            }

            // 교환 결과에 따른 패킷 및 시스템 메시지 처리
            if (exchanged)
            {
                msg.append("를 성공적으로 교환하였습니다.");
                player.sendMessage(msg.toString());
                return null; // 교환 완료 후 깔끔하게 대화창 닫기
            }
            else
            {
                // 사용자가 버튼을 눌렀으나 그 사이 재화가 부족해진 경우 예외 처리
                return "34424-2.htm"; // 재화 부족 안내 HTML 창 출력
            }
        }

        // 기타 .htm 호출 대응
        return event.endsWith(".htm") ? event : null;
    }

    public static void main(String[] args)
    {
        new ThePowerOfLove();
    }
}
