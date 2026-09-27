/*
 * Copyright (c) 2013 L2jBAN-JDEV
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package quests.Q10510_MoreExperience;

import net.sf.l2jdev.gameserver.data.xml.TeleportListData;
import net.sf.l2jdev.gameserver.model.Location;
import net.sf.l2jdev.gameserver.model.actor.Npc;
import net.sf.l2jdev.gameserver.model.actor.Player;
import net.sf.l2jdev.gameserver.model.script.Quest;
import net.sf.l2jdev.gameserver.model.script.QuestDialogType;
import net.sf.l2jdev.gameserver.model.script.QuestState;
import net.sf.l2jdev.gameserver.model.script.newquestdata.NewQuest;
import net.sf.l2jdev.gameserver.model.script.newquestdata.NewQuestLocation;
import net.sf.l2jdev.gameserver.model.script.newquestdata.QuestCondType;
import net.sf.l2jdev.gameserver.network.serverpackets.quest.ExQuestDialog;
import net.sf.l2jdev.gameserver.network.serverpackets.quest.ExQuestNotification;

import quests.Q10511_SecretGarden.Q10511_SecretGarden;

/**
 * @author Galagard
 */
public class Q10510_MoreExperience extends Quest
{
	private static final int QUEST_ID = 10510;
	private static final int[] MONSTERS =
	{
		20063, // 올 마훔 사수
		20066, // 올 마훔 장교
		20076, // 올 마훔 지휘관

		20436, // 올 마훔 보급병
		20437, // 올 마훔 신병
		20438, // 올 마훔 장성
		20439, // 올 마훔 하사관

		20199, // 앰버 바실리스크
		20145, // 하피
		20158, // 메두사
		20176, // 웜
		20248, // 투랙 버그베어
		20249, // 투랙 버그베어 전사

		20176, // 웜
		20550, // 가디언 바실리스크
		20551, // 로드 스캐빈져
		20552, // 크림슨 바인드
		20553, // 윈드수스
		20554, // 그란디스
		20555, // 거대 펑거스
		20556, // 거대 괴물눈
		20557, // 다이어 웜
		20558, // 부식수
		20559, // 부해의 골렘
		20560, // 트리살림
		20561, // 트리살림 토트
		20562, // 포자 좀비
		20563, // 마나센
	};

	public Q10510_MoreExperience()
	{
		super(QUEST_ID);
		addKillId(MONSTERS);
	}

	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "ACCEPT":
			{
				if (!canStartQuest(player))
				{
					break;
				}

				final QuestState questState = getQuestState(player, true);
				if (!questState.isStarted() && !questState.isCompleted())
				{
					questState.startQuest();
					onEvent("TELEPORT", npc, player);
				}
				break;
			}
			case "TELEPORT":
			{
				QuestState questState = getQuestState(player, false);
				if (questState == null)
				{
					if (!canStartQuest(player))
					{
						break;
					}

					questState = getQuestState(player, true);

					final NewQuestLocation questLocation = getQuestData().getLocation();
					if (questLocation.getStartLocationId() > 0)
					{
						final Location location = TeleportListData.getInstance().getTeleport(questLocation.getStartLocationId()).getLocation();
						if (teleportToQuestLocation(player, location) && (questLocation.getQuestLocationId() == questLocation.getStartLocationId()))
						{
							questState.setCond(QuestCondType.ACT);
							sendAcceptDialog(player);
						}
					}
					break;
				}

				final NewQuestLocation questLocation = getQuestData().getLocation();
				if (questState.isCond(QuestCondType.STARTED))
				{
					if (questLocation.getQuestLocationId() > 0)
					{
						final Location location = TeleportListData.getInstance().getTeleport(questLocation.getQuestLocationId()).getLocation();
						if (teleportToQuestLocation(player, location) && (questLocation.getQuestLocationId() == questLocation.getEndLocationId()))
						{
							questState.setCond(QuestCondType.DONE);
							sendEndDialog(player);
						}
					}
				}
				else if (questState.isCond(QuestCondType.DONE) && !questState.isCompleted())
				{
					if (questLocation.getEndLocationId() > 0)
					{
						final Location location = TeleportListData.getInstance().getTeleport(questLocation.getEndLocationId()).getLocation();
						if (teleportToQuestLocation(player, location))
						{
							sendEndDialog(player);
						}
					}
				}
				break;
			}
			case "COMPLETE":
			{
				final QuestState questState = getQuestState(player, false);
				if (questState == null)
				{
					break;
				}

				if (questState.isCond(QuestCondType.DONE) && !questState.isCompleted())
				{
					questState.exitQuest(false, true);
					rewardPlayer(player);
				}

				final QuestState nextQuestState = player.getQuestState(Q10511_SecretGarden.class.getSimpleName());
				if (nextQuestState == null)
				{
					player.sendPacket(new ExQuestDialog(10511, QuestDialogType.ACCEPT));
				}
				break;
			}
		}

		return null;
	}

	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final QuestState questState = getQuestState(player, false);
		if ((questState != null) && !questState.isCompleted())
		{
			if ((questState.getCount() == 0) && questState.isCond(QuestCondType.NONE))
			{
				player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.START));
			}
			else if (questState.isCond(QuestCondType.DONE))
			{
				player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.END));
			}
		}

		npc.showChatWindow(player);
		return null;
	}

	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState questState = getQuestState(killer, false);
		if ((questState != null) && questState.isCond(QuestCondType.STARTED))
		{
			final NewQuest data = getQuestData();
			if (data.getGoal().getItemId() > 0)
			{
				final int itemCount = (int) getQuestItemsCount(killer, data.getGoal().getItemId());
				if (itemCount < data.getGoal().getCount())
				{
					giveItems(killer, data.getGoal().getItemId(), 1);
					final int newItemCount = (int) getQuestItemsCount(killer, data.getGoal().getItemId());
					questState.setCount(newItemCount);
				}
			}
			else
			{
				final int currentCount = questState.getCount();
				if (currentCount < data.getGoal().getCount())
				{
					questState.setCount(currentCount + 1);
				}
			}

			if (questState.getCount() >= data.getGoal().getCount())
			{
				questState.setCond(QuestCondType.DONE);
				killer.sendPacket(new ExQuestNotification(questState));
			}
		}
	}
}
