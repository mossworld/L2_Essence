package quests.Q10999_SilverCollection;

import net.sf.l2jdev.gameserver.model.actor.Npc;
import net.sf.l2jdev.gameserver.model.actor.Player;
import net.sf.l2jdev.gameserver.model.script.Quest;
import net.sf.l2jdev.gameserver.model.script.QuestSound;
import net.sf.l2jdev.gameserver.model.script.QuestState;
import net.sf.l2jdev.gameserver.model.script.QuestType;

public class Q10999_SilverCollection extends Quest
{
	private static final int QUEST_ID = 10999;
	private static final int QUEST_NPC = 9099;
	private static final int SILVER = 1873;
	private static final int REQUIRED_ITEMS = 10;
	private static final int ADENA = 57;
	private static final int REWARD_ADENA = 20000;
	private static final int[] MONSTERS = { 20001, 20002, 20003, 20004, 20005, 20006 };

	public Q10999_SilverCollection()
	{
		super(QUEST_ID);
		addStartNpc(QUEST_NPC);
		addTalkId(QUEST_NPC);
		addKillId(MONSTERS);
		addSpawn(QUEST_NPC, 83400, 147900, -3400, 0, false, 0);
	}

	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState questState = getQuestState(player, true);
		if (questState.isCreated())
		{
			return "9099-01.htm";
		}

		if (questState.getCond() == 1 && getQuestItemsCount(player, SILVER) >= REQUIRED_ITEMS)
		{
			questState.setCond(2);
		}

		if (questState.getCond() == 2)
		{
			if (getQuestItemsCount(player, SILVER) < REQUIRED_ITEMS)
			{
				questState.setCond(1);
				return "9099-progress.htm";
			}

			takeItems(player, SILVER, REQUIRED_ITEMS);
			giveItems(player, ADENA, REWARD_ADENA);
			questState.exitQuest(QuestType.REPEATABLE);
			playSound(player, QuestSound.ITEMSOUND_QUEST_FINISH);
			return "9099-complete.htm";
		}

		return "9099-progress.htm";
	}

	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if ("accept".equalsIgnoreCase(event))
		{
			final QuestState questState = getQuestState(player, true);
			if (questState.isCreated())
			{
				questState.startQuest();
				questState.setCond(1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ACCEPT);
			}
			return "9099-progress.htm";
		}

		return event.endsWith(".htm") ? event : null;
	}

	@Override
	public void onKill(Npc npc, Player killer, boolean summon)
	{
		final QuestState questState = getQuestState(killer, false);
		if ((questState != null) && questState.isStarted() && (questState.getCond() == 1))
		{
			final long itemCount = getQuestItemsCount(killer, SILVER);
			if (itemCount < REQUIRED_ITEMS)
			{
				giveItems(killer, SILVER, 1);
				final long newItemCount = getQuestItemsCount(killer, SILVER);
				if (newItemCount >= REQUIRED_ITEMS)
				{
					questState.setCond(2);
					playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					killer.sendMessage("은 10개를 모았습니다. 수집 의뢰인에게 돌아가 보상을 받으세요.");
				}
				else
				{
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
		}
	}
}